package com.github.bibsysdev.nva_cristin_person;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;

public class CristinPersonsHandler extends CristinHandler<Void, PersonsWrapper> {

    protected static final String NAME_MISSING_EXCEPTION_MESSAGE = "Parameter 'name' is missing.";
    protected static final String NAME_QUERY_PARAMETER = "name";
    private static final String CRISTIN_QUERY_PARAMETER_NAME_KEY = "name";
    private static final String CRISTIN_QUERY_PARAMETER_LANGUAGE_KEY = "lang";
    private static final String CRISTIN_QUERY_PARAMETER_PAGE_KEY = "page";
    private static final String CRISTIN_QUERY_PARAMETER_PAGE_VALUE = "1";
    private static final String CRISTIN_QUERY_PARAMETER_PER_PAGE_KEY = "per_page";
    private static final String CRISTIN_QUERY_PARAMETER_PER_PAGE_VALUE = "5";
    private final transient CristinApiClient cristinApiClient;

    public CristinPersonsHandler() {
        this(new Environment());
    }

    public CristinPersonsHandler(Environment environment) {
        this(new CristinApiClient(), environment);
    }

    protected CristinPersonsHandler(CristinApiClient cristinApiClient, Environment environment) {
        super(Void.class, environment);
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    protected PersonsWrapper processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        String language = getValidLanguage(requestInfo);
        String name = getValidName(requestInfo);
        return getTransformedCristinPersonsUsingWrapperObject(language, name);
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, PersonsWrapper output) {
        return HttpURLConnection.HTTP_OK;
    }

    private String getValidName(RequestInfo requestInfo) throws BadRequestException {
        return getQueryParam(requestInfo, NAME_QUERY_PARAMETER).orElseThrow(
            () -> new BadRequestException(NAME_MISSING_EXCEPTION_MESSAGE));
    }

    private PersonsWrapper getTransformedCristinPersonsUsingWrapperObject(String language, String name) {
        Map<String, String> cristinQueryParameters = createCristinQueryParameters(name, language);
        return attempt(() -> cristinApiClient.queryCristinPersonsIntoWrapperObject(cristinQueryParameters,
            language)).orElseThrow();
    }

    private Map<String, String> createCristinQueryParameters(String name, String language) {
        Map<String, String> queryParameters = new ConcurrentHashMap<>();
        queryParameters.put(CRISTIN_QUERY_PARAMETER_NAME_KEY, name);
        queryParameters.put(CRISTIN_QUERY_PARAMETER_LANGUAGE_KEY, language);
        queryParameters.put(CRISTIN_QUERY_PARAMETER_PAGE_KEY, CRISTIN_QUERY_PARAMETER_PAGE_VALUE);
        queryParameters.put(CRISTIN_QUERY_PARAMETER_PER_PAGE_KEY, CRISTIN_QUERY_PARAMETER_PER_PAGE_VALUE);
        return queryParameters;
    }
}
