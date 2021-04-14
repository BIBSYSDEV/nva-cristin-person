package com.github.bibsysdev.nva_cristin_person;

import java.io.InputStreamReader;
import java.net.URL;

public class CristinApiClientStub extends CristinApiClient {

    private static final String CRISTIN_PERSONS_RESPONSE_JSON_FILE = "/cristin_persons_response.json";
    private static final String CRISTIN_ONE_PERSON_RESPONSE_JSON_FILE = "/cristin_one_person_response.json";

    @Override
    protected long calculateProcessingTime(long startRequestTime, long endRequestTime) {
        return 1000;
    }

    @Override
    protected InputStreamReader getQueryResponse(URL url) {
        return mockQueryResponseReader();
    }

    @Override
    protected InputStreamReader getResponse(URL url) {
        return mockGetResponseReader();
    }

    private InputStreamReader mockGetResponseReader() {
        return new InputStreamReader(
            CristinApiClientStub.class.getResourceAsStream(CRISTIN_ONE_PERSON_RESPONSE_JSON_FILE));
    }

    private InputStreamReader mockQueryResponseReader() {
        return new InputStreamReader(
            CristinApiClientStub.class.getResourceAsStream(CRISTIN_PERSONS_RESPONSE_JSON_FILE));
    }
}