package com.github.bibsysdev.nva_cristin_person.model.nva;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

public class NvaAffiliation {

    public URI id;
    @JsonInclude(NON_NULL)
    public String role;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}