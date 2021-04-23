package com.github.bibsysdev.nva.cristin.person;

import static com.github.bibsysdev.nva.cristin.person.CristinApiClient.PERSON_LOOKUP_CONTEXT_URL;
import static com.github.bibsysdev.nva.cristin.person.CristinHandler.LANGUAGE_INVALID_ERROR_MESSAGE;
import static com.github.bibsysdev.nva.cristin.person.CristinPersonsHandler.LANGUAGE_QUERY_PARAMETER;
import static com.github.bibsysdev.nva.cristin.person.CristinPersonsHandler.NAME_QUERY_PARAMETER;
import static nva.commons.apigateway.ApiGatewayHandler.APPLICATION_PROBLEM_JSON;
import static nva.commons.core.attempt.Try.attempt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bibsysdev.nva.cristin.person.model.cristin.CristinPerson;
import com.github.bibsysdev.nva.cristin.person.model.nva.NvaPerson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.core.ioutils.IoUtils;
import org.apache.commons.codec.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;

public class CristinPersonsHandlerTest {

    private static final String LANGUAGE_KEY = "language";
    private static final String EMPTY_STRING = "";
    private static final String LANGUAGE_NB = "nb";
    private static final String LANGUAGE_INVALID = "invalid";
    private static final String NAME_MATTIAS = "Mattias";
    private static final String INVALID_JSON = "This is not valid JSON!";
    private static final String EMPTY_LIST_STRING = "[]";
    private static final String ALLOW_ALL_ORIGIN = "*";
    private static final String NVA_ONE_PERSON_JSON_FILE = "nva_one_person.json";
    private static final String NVA_PERSONS_NON_ENRICHED_JSON_FILE = "nva_persons_non_enriched.json";
    private static final String NVA_PERSONS_NO_HITS_JSON_FILE = "nva_persons_no_hits.json";
    private static final String CRISTIN_ONE_PERSON_JSON_FILE = "cristin_one_person.json";
    private final ObjectMapper objectMapper = JsonUtils.objectMapper;
    private final Environment environment = new Environment();
    private CristinApiClient cristinApiClientStub;
    private Context context;
    private ByteArrayOutputStream output;
    private CristinPersonsHandler handler;

    @BeforeEach
    void setUp() {
        cristinApiClientStub = new CristinApiClientStub();
        context = mock(Context.class);
        output = new ByteArrayOutputStream();
        handler = new CristinPersonsHandler(cristinApiClientStub, environment);
    }

    @ParameterizedTest
    @ArgumentsSource(TestPairProvider.class)
    void handlerReturnsExpectedBodyWhenRequestInputIsValid(String expected) throws IOException {
        var actual = sendDefaultQuery().getBody();
        assertEquals(objectMapper.readTree(expected), objectMapper.readTree(actual));
    }

    @Test
    void handlerReturnsNonEnrichedBodyWhenEnrichingFails() throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        doThrow(new IOException()).when(cristinApiClientStub).getPerson(any(), any());
        handler = new CristinPersonsHandler(cristinApiClientStub, environment);
        GatewayResponse<PersonsWrapper> response = sendDefaultQuery();
        var expected = getReader(NVA_PERSONS_NON_ENRICHED_JSON_FILE);

        assertEquals(objectMapper.readTree(expected), objectMapper.readTree(response.getBody()));
    }

    @Test
    void returnNvaPersonWhenCallingNvaPersonBuilderMethodWithValidCristinPerson() throws Exception {
        var expected = getReader(NVA_ONE_PERSON_JSON_FILE);
        var cristinGetPerson = getReader(CRISTIN_ONE_PERSON_JSON_FILE);
        CristinPerson cristinPerson = attempt(
            () -> objectMapper.readValue(cristinGetPerson, CristinPerson.class)).get();
        NvaPerson nvaPerson = new NvaPersonBuilder(cristinPerson).build();
        nvaPerson.setContext(PERSON_LOOKUP_CONTEXT_URL);
        var actual = attempt(() -> objectMapper.writeValueAsString(nvaPerson)).get();
        assertEquals(objectMapper.readTree(expected), objectMapper.readTree(actual));
    }

    @Test
    void handlerReturnsPersonsWrapperWithAllMetadataButEmptyHitsArrayWhenNoMatchesAreFoundInCristin()
        throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        var emptyArray = new InputStreamReader(IoUtils.stringToStream(EMPTY_LIST_STRING), Charsets.UTF_8);
        doReturn(emptyArray).when(cristinApiClientStub).getQueryResponse(any());
        var expected = getReader(NVA_PERSONS_NO_HITS_JSON_FILE);
        handler = new CristinPersonsHandler(cristinApiClientStub, environment);
        GatewayResponse<PersonsWrapper> response = sendDefaultQuery();
        assertEquals(objectMapper.readTree(expected), objectMapper.readTree(response.getBody()));
    }

    @Test
    void handlerReturnsOkWhenInputContainsName() throws Exception {
        GatewayResponse<PersonsWrapper> response = sendDefaultQuery();
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusCode());
    }

    @Test
    void handlerIgnoresErrorsWhenTryingToEnrichPersonInformation() throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        doThrow(new IOException()).when(cristinApiClientStub).getPerson(any(), any());
        handler = new CristinPersonsHandler(cristinApiClientStub, environment);
        GatewayResponse<PersonsWrapper> response = sendDefaultQuery();

        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    void handlerThrowsInternalErrorWhenQueryingPersonsFails() throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        doThrow(new IOException()).when(cristinApiClientStub).queryAndEnrichPersons(any(), any());
        handler = new CristinPersonsHandler(cristinApiClientStub, environment);
        GatewayResponse<PersonsWrapper> response = sendDefaultQuery();

        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    void handlerThrowsBadRequestWhenMissingNameQueryParameter() throws IOException {
        InputStream input = requestWithQueryParameters(Map.of(LANGUAGE_QUERY_PARAMETER, LANGUAGE_NB));
        handler.handleRequest(input, output, context);
        GatewayResponse<PersonsWrapper> response = GatewayResponse.fromOutputStream(output);

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    void handlerSetsDefaultValueForMissingOptionalLanguageParameterAndReturnOk() throws Exception {
        InputStream input = requestWithQueryParameters(Map.of(NAME_QUERY_PARAMETER, NAME_MATTIAS));
        handler.handleRequest(input, output, context);
        GatewayResponse<PersonsWrapper> response = GatewayResponse.fromOutputStream(output);

        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    void handlerReceivesAllowOriginHeaderValueFromEnvironmentAndPutsItOnResponse() throws Exception {
        GatewayResponse<PersonsWrapper> response = sendDefaultQuery();
        assertEquals(ALLOW_ALL_ORIGIN, response.getHeaders().get(ApiGatewayHandler.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void handlerReturnsBadRequestWhenNameQueryParamIsEmpty() throws Exception {
        InputStream input = requestWithQueryParameters(Map.of(NAME_QUERY_PARAMETER, EMPTY_STRING));
        handler.handleRequest(input, output, context);
        GatewayResponse<PersonsWrapper> response = GatewayResponse.fromOutputStream(output);

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getBody().contains(CristinPersonsHandler.NAME_MISSING_EXCEPTION_MESSAGE));
    }

    @Test
    void handlerReturnsBadRequestWhenReceivingInvalidLanguageQueryParam() throws Exception {
        InputStream input = requestWithQueryParameters(
            Map.of(NAME_QUERY_PARAMETER, NAME_MATTIAS, LANGUAGE_KEY, LANGUAGE_INVALID));
        handler.handleRequest(input, output, context);
        GatewayResponse<PersonsWrapper> response = GatewayResponse.fromOutputStream(output);

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getBody().contains(LANGUAGE_INVALID_ERROR_MESSAGE));
    }

    @Test
    void cristinApiClientWillStillGenerateQueryPersonsUrlEvenWithoutParameters()
        throws IOException, URISyntaxException {
        cristinApiClientStub.generateQueryPersonsUrl(null);
    }

    @Test
    void readerThrowsIoExceptionWhenReadingInvalidJson() {
        InputStream inputStream = new ByteArrayInputStream(INVALID_JSON.getBytes(StandardCharsets.UTF_8));
        InputStreamReader reader = new InputStreamReader(inputStream);
        Executable action = () -> CristinApiClient.fromJson(reader, CristinPerson.class);
        assertThrows(IOException.class, action);
    }

    private GatewayResponse<PersonsWrapper> sendDefaultQuery() throws IOException {
        InputStream input = requestWithQueryParameters(
            Map.of(NAME_QUERY_PARAMETER, NAME_MATTIAS, LANGUAGE_QUERY_PARAMETER, LANGUAGE_NB));
        handler.handleRequest(input, output, context);
        return GatewayResponse.fromOutputStream(output);
    }

    private InputStream requestWithQueryParameters(Map<String, String> map) throws JsonProcessingException {
        return new HandlerRequestBuilder<Void>(objectMapper)
            .withBody(null)
            .withQueryParameters(map)
            .build();
    }

    private InputStreamReader getReader(String resource) {
        InputStream queryResultsAsStream = IoUtils.inputStreamFromResources(resource);
        return new InputStreamReader(queryResultsAsStream, Charsets.UTF_8);
    }
}