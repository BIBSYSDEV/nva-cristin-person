package com.github.bibsysdev.nva_cristin_person.model.cristin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CristinPerson {

    public String cristinPersonId;
    public String firstName;
    public String surname;
    public Boolean identifiedCristinPerson;
    public String pictureUrl;
    public List<CristinAffiliation> affiliations;
}