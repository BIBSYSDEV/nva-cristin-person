package com.github.bibsysdev.nva_cristin_person;

import static com.github.bibsysdev.nva_cristin_person.GetCristinPerson.DEFAULT_LANGUAGE_CODE;
import static com.github.bibsysdev.nva_cristin_person.GetCristinPerson.LANGUAGE_QUERY_PARAMETER;
import static com.github.bibsysdev.nva_cristin_person.model.cristin.CristinPerson.hasRequiredContent;
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
import com.github.bibsysdev.nva_cristin_person.model.cristin.CristinPerson;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaPerson;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.core.Environment;
import nva.commons.core.JsonUtils;
import nva.commons.core.ioutils.IoUtils;
import org.apache.commons.codec.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CristinPersonTest {

    private static final String EMPTY_JSON = "{}";
    private static final String CRISTIN_PERSON_RESPONSE_INVALID_ID_JSON = "cristin_person_response_invalid_id.json";
    private static final String CRISTIN_PERSON_RESPONSE_JSON = "cristin_person_response.json";
    private static final String INVALID_ID = "Not an ID";
    private static final String DEFAULT_ID = "9999";
    private CristinApiClient cristinApiClientStub;
    private final Environment environment = new Environment();
    private Context context;
    private ByteArrayOutputStream output;
    private GetCristinPerson handler;
    private final ObjectMapper objectMapper = JsonUtils.objectMapper;

    @BeforeEach
    void setUp() {
        cristinApiClientStub = new CristinApiClientStub();
        context = mock(Context.class);
        output = new ByteArrayOutputStream();
        handler = new GetCristinPerson(cristinApiClientStub, environment);
    }

    @Test
    void handlerReturnsEmptyJsonWhenIdIsNotFound() throws Exception {
        cristinApiClientStub = spy(cristinApiClientStub);

        doReturn(getReader(CRISTIN_PERSON_RESPONSE_INVALID_ID_JSON)).when(cristinApiClientStub).getResponse(any());
        handler = new GetCristinPerson(cristinApiClientStub, environment);
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);
        assertEquals(objectMapper.readTree(EMPTY_JSON), objectMapper.readTree(response.getBody()));
    }

    @Test
    void handlerReturnsBadRequestWhenIdIsNotANumber() throws Exception {
        GatewayResponse<NvaPerson> response = sendQueryWithId(INVALID_ID);
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handlerReturnsNvaPersonFromTransformedCristinPersonWhenIdIsFound() throws Exception {
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);
        assertEquals(
            objectMapper.readTree(getReader(CRISTIN_PERSON_RESPONSE_JSON)),
            objectMapper.readTree(response.getBody())
        );
    }

    @Test
    void handlerReturnsEmptyJsonWhenGetFromBackendFails() throws Exception {
        cristinApiClientStub = spy(cristinApiClientStub);

        doThrow(new IOException()).when(cristinApiClientStub).getPerson(any(), any());
        handler = new GetCristinPerson(cristinApiClientStub, environment);
        GatewayResponse<NvaPerson> response = sendQueryWithId(DEFAULT_ID);
        assertEquals(objectMapper.readTree(EMPTY_JSON), objectMapper.readTree(response.getBody()));

        doThrow(new FileNotFoundException()).when(cristinApiClientStub).getPerson(any(), any());
        handler = new GetCristinPerson(cristinApiClientStub, environment);
        GatewayResponse<NvaPerson> nextResponse = sendQueryWithId(DEFAULT_ID);
        assertEquals(objectMapper.readTree(EMPTY_JSON), objectMapper.readTree(nextResponse.getBody()));
    }

    @Test
    void callingHasValidContentOnCristinPersonOnlyReturnsTrueWhenAllRequiredDataArePresent() {
        assertFalse(hasRequiredContent(null));

        CristinPerson cristinPerson = new CristinPerson();
        assertFalse(hasRequiredContent(cristinPerson));

        cristinPerson.cristinPersonId = "1234";
        assertTrue(hasRequiredContent(cristinPerson));
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
