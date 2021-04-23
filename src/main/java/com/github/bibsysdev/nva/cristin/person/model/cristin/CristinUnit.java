package com.github.bibsysdev.nva.cristin.person.model.cristin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.net.URI;
import nva.commons.core.JacocoGenerated;

@JacocoGenerated
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CristinUnit {

    public String cristinUnitId;
    public URI url;

    public String getCristinUnitId() {
        return cristinUnitId;
    }

    public void setCristinUnitId(String cristinUnitId) {
        this.cristinUnitId = cristinUnitId;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }
}

