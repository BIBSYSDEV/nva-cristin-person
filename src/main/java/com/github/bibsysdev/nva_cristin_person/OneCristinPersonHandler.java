package com.github.bibsysdev.nva_cristin_person;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.lambda.runtime.Context;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaPerson;
import java.net.HttpURLConnection;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

@JacocoGenerated
public class OneCristinPersonHandler extends CristinHandler<Void, NvaPerson> {

    public static final String INVALID_PATH_PARAMETER_FOR_ID_EXCEPTION_MESSAGE = "Path parameter for id must be number";
    public static final String ID = "id";
    private final transient CristinApiClient cristinApiClient;

    public OneCristinPersonHandler() {
        this(new Environment());
    }

    public OneCristinPersonHandler(Environment environment) {
        this(new CristinApiClient(), environment);
    }

    public OneCristinPersonHandler(CristinApiClient cristinApiClient, Environment environment) {
        super(Void.class, environment);
        this.cristinApiClient = cristinApiClient;
    }

    @Override
    protected NvaPerson processInput(Void input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        String language = getValidLanguage(requestInfo);
        String id = getValidId(requestInfo);
        return getTransformedPersonFromCristin(id, language);
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, NvaPerson output) {
        return HttpURLConnection.HTTP_OK;
    }

    private String getValidId(RequestInfo requestInfo) throws BadRequestException {
        attempt(() -> Integer.parseInt(requestInfo.getPathParameter(ID))).orElseThrow(
            failure -> new BadRequestException(INVALID_PATH_PARAMETER_FOR_ID_EXCEPTION_MESSAGE));
        return requestInfo.getPathParameter(ID);
    }

    private NvaPerson getTransformedPersonFromCristin(String id, String language) throws BadGatewayException {
        return cristinApiClient.queryOneCristinPerson(id, language);
    }
}
