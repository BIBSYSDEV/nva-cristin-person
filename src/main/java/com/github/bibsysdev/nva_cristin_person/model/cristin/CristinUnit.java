package com.github.bibsysdev.nva_cristin_person.model.cristin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.net.URI;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CristinUnit {

    public String cristinUnitId;
    public URI url;
}

