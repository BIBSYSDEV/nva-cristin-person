package com.github.bibsysdev.nva.cristin.person.model.cristin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.StringUtils;

@JacocoGenerated
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CristinPerson {

    public String cristinPersonId;
    public String firstName;
    public String surname;
    public Boolean identifiedCristinPerson;
    public String pictureUrl;
    public List<CristinAffiliation> affiliations;

    public String getCristinPersonId() {
        return cristinPersonId;
    }

    public void setCristinPersonId(String cristinPersonId) {
        this.cristinPersonId = cristinPersonId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getIdentifiedCristinPerson() {
        return identifiedCristinPerson;
    }

    public void setIdentifiedCristinPerson(Boolean identifiedCristinPerson) {
        this.identifiedCristinPerson = identifiedCristinPerson;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<CristinAffiliation> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(
        List<CristinAffiliation> affiliations) {
        this.affiliations = affiliations;
    }

    /**
     * Returns whether or not the minimum required fields for a valid Cristin Person are present.
     */
    @JsonIgnore
    public boolean hasRequiredFields() {
        return StringUtils.isNotBlank(cristinPersonId)
            && StringUtils.isNotBlank(firstName)
            && StringUtils.isNotBlank(surname);
    }
}