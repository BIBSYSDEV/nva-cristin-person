package com.github.bibsysdev.nva.cristin.person;

import static com.github.bibsysdev.nva.cristin.person.OneCristinPersonHandler.DEFAULT_LANGUAGE_CODE;
import static com.github.bibsysdev.nva.cristin.person.OneCristinPersonHandler.LANGUAGE_QUERY_PARAMETER;
import static nva.commons.apigateway.ApiGatewayHandler.APPLICATION_PROBLEM_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bibsysdev.nva.cristin.person.model.cristin.CristinPerson;
import com.github.bibsysdev.nva.cristin.person.model.nva.NvaPerson;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.apigateway.HttpHeaders;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.core.ioutils.IoUtils;
import org.apache.commons.codec.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class OneCristinPersonHandlerTest {

    private static final String EMPTY_JSON = "{}";
    private static final String CRISTIN_ONE_PERSON_INVALID_ID_JSON_FILE = "cristin_one_person_invalid_id.json";
    private static final String NVA_ONE_PERSON_JSON_FILE = "nva_one_person.json";
    private static final String CRISTIN_ONE_PERSON_MISSING_FIELDS_JSON_FILE = "cristin_one_person_missing_fields.json";
    private static final String NVA_ONE_PERSON_MISSING_FIELDS_JSON_FILE = "nva_one_person_missing_fields.json";
    private static final String INVALID_ID = "Not an ID";
    private static final String DEFAULT_ID = "9999";

    private final Environment environment = new Environment();
    private final ObjectMapper objectMapper = JsonUtils.objectMapper;
    private CristinApiClient cristinApiClientStub;
    private Context context;
    private ByteArrayOutputStream output;
    private OneCristinPersonHandler handler;

    @BeforeEach
    void setUp() {
        cristinApiClientStub = new CristinApiClientStub();
        context = mock(Context.class);
        output = new ByteArrayOutputStream();
        handler = new OneCristinPersonHandler(cristinApiClientStub, environment);
    }

    @Test
    void handlerReturnsEmptyJsonWhenIdIsNotFound() throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        doReturn(getReader(CRISTIN_ONE_PERSON_INVALID_ID_JSON_FILE)).when(cristinApiClientStub).getResponse(any());
        handler = new OneCristinPersonHandler(cristinApiClientStub, environment);
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);

        assertEquals(objectMapper.readTree(EMPTY_JSON), objectMapper.readTree(response.getBody()));
    }

    @Test
    void handlerReturnsBadRequestWhenIdIsNotANumber() throws Exception {
        GatewayResponse<NvaPerson> response = sendQueryWithId(INVALID_ID);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handlerReturnsNvaPersonWithNulledFieldsIfTheyAreMissingFromCristinResponse() throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        doReturn(getReader(CRISTIN_ONE_PERSON_MISSING_FIELDS_JSON_FILE)).when(cristinApiClientStub).getResponse(any());
        handler = new OneCristinPersonHandler(cristinApiClientStub, environment);
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);
        var expected = getReader(NVA_ONE_PERSON_MISSING_FIELDS_JSON_FILE);

        assertEquals(objectMapper.readTree(expected), objectMapper.readTree(response.getBody()));
    }

    @Test
    void handlerReturnsNvaPersonFromTransformedCristinPersonWhenIdIsFound() throws Exception {
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);
        assertEquals(
            objectMapper.readTree(getReader(NVA_ONE_PERSON_JSON_FILE)),
            objectMapper.readTree(response.getBody())
        );
    }

    @Test
    void handlerReturnsBadGatewayExceptionWhenFetchFromBackendFails() throws Exception {
        cristinApiClientStub = Mockito.spy(cristinApiClientStub);
        doThrow(new IOException()).when(cristinApiClientStub).getPerson(any(), any());
        handler = new OneCristinPersonHandler(cristinApiClientStub, environment);
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);

        assertEquals(HttpURLConnection.HTTP_BAD_GATEWAY, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));

        doThrow(new FileNotFoundException()).when(cristinApiClientStub).getPerson(any(), any());
        handler = new OneCristinPersonHandler(cristinApiClientStub, environment);
        response = sendQueryWithId(DEFAULT_ID);

        assertEquals(HttpURLConnection.HTTP_BAD_GATEWAY, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));

        doThrow(new BadGatewayException("")).when(cristinApiClientStub).queryOneCristinPerson(any(), any());
        handler = new OneCristinPersonHandler(cristinApiClientStub, environment);
        response = sendQueryWithId(DEFAULT_ID);

        assertEquals(HttpURLConnection.HTTP_BAD_GATEWAY, response.getStatusCode());
        assertEquals(APPLICATION_PROBLEM_JSON, response.getHeaders().get(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    void callingHasValidContentOnCristinPersonOnlyReturnsTrueWhenAllRequiredDataArePresent() {
        CristinPerson cristinPerson = new CristinPerson();
        assertFalse(cristinPerson.hasRequiredFields());
        cristinPerson.setCristinPersonId("1234");
        cristinPerson.setFirstName("Mattias");
        cristinPerson.setSurname("Testesen");
        assertTrue(cristinPerson.hasRequiredFields());
    }

    private GatewayResponse<NvaPerson> sendQueryWithId(String id) throws IOException {
        InputStream input = requestWithLanguageAndId(
            Map.of(LANGUAGE_QUERY_PARAMETER, DEFAULT_LANGUAGE_CODE),
            Map.of("id", id)
        );

        handler.handleRequest(input, output, context);
        return GatewayResponse.fromOutputStream(output);
    }

    private InputStream requestWithLanguageAndId(Map<String, String> languageQueryParam,
                                                 Map<String, String> idPathParam) throws JsonProcessingException {
        return new HandlerRequestBuilder<Void>(objectMapper)
            .withBody(null)
            .withQueryParameters(languageQueryParam)
            .withPathParameters(idPathParam)
            .build();
    }

    private InputStreamReader getReader(String resource) {
        InputStream queryResultsAsStream = IoUtils.inputStreamFromResources(resource);
        return new InputStreamReader(queryResultsAsStream, Charsets.UTF_8);
    }
}
