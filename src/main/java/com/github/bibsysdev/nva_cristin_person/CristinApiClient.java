package com.github.bibsysdev.nva_cristin_person;

import static com.github.bibsysdev.nva_cristin_person.UriUtils.buildUri;
import static java.util.Arrays.asList;
import static nva.commons.core.attempt.Try.attempt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bibsysdev.nva_cristin_person.model.cristin.CristinPerson;
import com.github.bibsysdev.nva_cristin_person.model.nva.EmptyNvaPerson;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaPerson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.core.attempt.Failure;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CristinApiClient {

    static final String PERSON_LOOKUP_CONTEXT_URL = "https://example.org/person-context.json";
    private static final Logger logger = LoggerFactory.getLogger(CristinApiClient.class);
    private static final Environment ENVIRONMENT = new Environment();
    static final String BASE_URL = ENVIRONMENT.readEnv("BASE_URL");
    private static final String CRISTIN_API_HOST = ENVIRONMENT.readEnv("CRISTIN_API_HOST");
    private static final ObjectMapper OBJECT_MAPPER = JsonUtils.objectMapper;

    private static final String NAME = "name";
    private static final String CHARACTER_EQUALS = "=";
    private static final String HTTPS = "https";
    private static final String CRISTIN_API_PERSONS_PATH = "/v2/persons/";
    private static final String SEARCH_PATH = "search?QUERY_PARAMS"; // TODO: NP-2412: Replace QUERY_PARAMS
    private static final String ERROR_MESSAGE_FETCHING_CRISTIN_PERSON_WITH_ID = "Error fetching cristin person with "
        + "id: %s . Exception Message: %s";
    private static final String ERROR_MESSAGE_BACKEND_FETCH_FAILED = "Your request cannot be processed at this time "
        + "due to an upstream error";

    /**
     * Creates a wrapper object containing CristinPersons transformed to NvaPersons. Is used for serialization to the
     * client.
     *
     * @param parameters The query params
     * @param language   Language used for some properties in Cristin API response
     * @return a PersonsWrapper with transformed CristinPersons
     * @throws IOException        for connection errors
     * @throws URISyntaxException if URI is malformed
     */
    public PersonsWrapper queryCristinPersonsIntoWrapperObject(Map<String, String> parameters, String language)
        throws IOException, URISyntaxException {

        long startRequestTime = System.currentTimeMillis();
        List<CristinPerson> enrichedPersons = queryAndEnrichPersons(parameters, language);
        long endRequestTime = System.currentTimeMillis();

        PersonsWrapper personsWrapper = new PersonsWrapper();
        personsWrapper.setId(buildUri(BASE_URL, SEARCH_PATH));
        personsWrapper.setSize(0); // TODO: NP-2385: X-Total-Count header from Cristin response
        personsWrapper.setSearchString(extractNameSearchString(parameters));
        personsWrapper.setProcessingTime(calculateProcessingTime(startRequestTime, endRequestTime));
        // TODO: NP-2385: Use Link header / Pagination data from Cristin response in the next two values
        personsWrapper.setFirstRecord(0);
        personsWrapper.setNextResults(null); // TODO: Change to URI
        personsWrapper.setHits(transformCristinPersonsToNvaPersons(enrichedPersons));
        // TODO: NP-2424: Return fields with empty values instead of null to avoid "undefined" in frontend
        return personsWrapper;
    }

    /**
     * Creates a NvaPerson object containing a single transformed CristinPerson. Is used for serialization to the
     * client.
     *
     * @param id       The CristinPersonId of the person to query
     * @param language Language used for some properties in the Cristin API response
     * @return a NvaPerson filled with one transformed CristinPerson
     * @throws BadGatewayException when there is a problem with fetch from backend
     */
    public NvaPerson queryOneCristinPerson(String id, String language) throws BadGatewayException {
        CristinPerson cristinPerson = attemptToGetCristinPerson(id, language);
        if (cristinPerson == null || !cristinPerson.hasRequiredFields()) {
            return new EmptyNvaPerson();
        } else {
            NvaPerson nvaPerson = new NvaPersonBuilder(cristinPerson).build();
            nvaPerson.setContext(PERSON_LOOKUP_CONTEXT_URL);
            return nvaPerson;
        }
    }

    protected static <T> T fromJson(InputStreamReader reader, Class<T> classOfT) throws IOException {
        return OBJECT_MAPPER.readValue(reader, classOfT);
    }

    // TODO: throw BadGatewayException if this fails as well?
    protected List<CristinPerson> queryPersons(Map<String, String> parameters) throws IOException,
                                                                                      URISyntaxException {
        URL url = generateQueryPersonsUrl(parameters);
        try (InputStreamReader streamReader = getQueryResponse(url)) {
            return asList(fromJson(streamReader, CristinPerson[].class));
        }
    }

    protected long calculateProcessingTime(long startRequestTime, long endRequestTime) {
        return endRequestTime - startRequestTime;
    }

    protected List<CristinPerson> queryAndEnrichPersons(Map<String, String> parameters,
                                                        String language) throws IOException,
                                                                                URISyntaxException {
        List<CristinPerson> persons = queryPersons(parameters);
        return enrichPersons(language, persons);
    }

    protected CristinPerson getPerson(String id, String language) throws IOException, URISyntaxException {
        URL url = generateGetPersonUrl(id, language);
        try (InputStreamReader streamReader = getResponse(url)) {
            return fromJson(streamReader, CristinPerson.class);
        }
    }

    protected InputStreamReader getQueryResponse(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected InputStreamReader getResponse(URL url) throws IOException {
        return new InputStreamReader(url.openStream());
    }

    protected URL generateQueryPersonsUrl(Map<String, String> parameters) throws MalformedURLException,
                                                                                 URISyntaxException {
        URIBuilder uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(CRISTIN_API_HOST)
            .setPath(CRISTIN_API_PERSONS_PATH);
        if (parameters != null) {
            parameters.keySet().forEach(s -> uri.addParameter(s, parameters.get(s)));
        }
        return uri.build().toURL();
    }

    protected URL generateGetPersonUrl(String id, String language) throws MalformedURLException, URISyntaxException {
        URI uri = new URIBuilder()
            .setScheme(HTTPS)
            .setHost(CRISTIN_API_HOST)
            .setPath(CRISTIN_API_PERSONS_PATH + id)
            .addParameter("lang", language)
            .build();
        return uri.toURL();
    }

    private String extractNameSearchString(Map<String, String> parameters) {
        return NAME + CHARACTER_EQUALS + parameters.get(NAME);
    }

    private List<NvaPerson> transformCristinPersonsToNvaPersons(List<CristinPerson> cristinPersons) {
        return cristinPersons.stream()
            .filter(CristinPerson::hasRequiredFields)
            .map(cristinPerson -> new NvaPersonBuilder(cristinPerson).build())
            .collect(Collectors.toList());
    }

    private CristinPerson attemptToGetCristinPerson(String id, String language) throws BadGatewayException {
        return attempt(() -> getPerson(id, language)).orElseThrow(failure -> {
            logError(id, failure);
            return new BadGatewayException(ERROR_MESSAGE_BACKEND_FETCH_FAILED);
        });
    }

    private List<CristinPerson> enrichPersons(String language, List<CristinPerson> persons) {
        return persons.stream().map(person -> enrichOnePerson(language, person)).collect(Collectors.toList());
    }

    private CristinPerson enrichOnePerson(String language, CristinPerson person) {
        return attempt(() -> getPerson(person.getCristinPersonId(), language))
            .toOptional(failure -> logError(person.getCristinPersonId(), failure))
            .orElse(person);
    }

    private void logError(String id, Failure<CristinPerson> failure) {
        logger.error(String.format(ERROR_MESSAGE_FETCHING_CRISTIN_PERSON_WITH_ID,
            id, failure.getException().getMessage()));
    }
}
