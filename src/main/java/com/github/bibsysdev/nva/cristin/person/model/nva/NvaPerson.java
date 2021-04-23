package com.github.bibsysdev.nva.cristin.person.model.nva;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.github.bibsysdev.nva.cristin.person.PersonsWrapper.CONTEXT;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.bibsysdev.nva.cristin.person.NvaPersonBuilder;
import com.github.bibsysdev.nva.cristin.person.model.cristin.CristinPerson;
import java.net.URI;
import java.util.List;

@JsonInclude(ALWAYS)
@JsonPropertyOrder(CONTEXT)
public class NvaPerson {

    @JsonInclude(NON_NULL)
    @JsonProperty(CONTEXT)
    private String context;
    private URI id;
    private String type;
    private List<NvaIdentifier> identifier;
    private String name;
    private List<NvaAffiliation> affiliation;
    private URI imageUrl;
    private Boolean verified;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<NvaIdentifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<NvaIdentifier> identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NvaAffiliation> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<NvaAffiliation> affiliation) {
        this.affiliation = affiliation;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public static NvaPerson fromCristinPerson(CristinPerson person) {
        return new NvaPersonBuilder(person).build();
    }
}
