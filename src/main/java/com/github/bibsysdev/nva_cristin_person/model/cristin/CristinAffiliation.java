package com.github.bibsysdev.nva_cristin_person.model.cristin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CristinAffiliation {

    public CristinUnit unit;
    public Map<String, String> position;

    public CristinUnit getUnit() {
        return unit;
    }

    public void setUnit(CristinUnit unit) {
        this.unit = unit;
    }

    public Map<String, String> getPosition() {
        return position;
    }

    public void setPosition(Map<String, String> position) {
        this.position = position;
    }
}