package com.github.bibsysdev.nva.cristin.person.model.nva;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;
import java.util.Map;

public class NvaAffiliation {

    public URI id;
    @JsonInclude(ALWAYS)
    public Map<String, String> role;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public Map<String, String> getRole() {
        return role;
    }

    public void setRole(Map<String, String> role) {
        this.role = role;
    }
}