package com.github.bibsysdev.nva_cristin_person.model.cristin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CristinAffiliation {

    public CristinUnit unit;
    public Map<String, String> position;
}