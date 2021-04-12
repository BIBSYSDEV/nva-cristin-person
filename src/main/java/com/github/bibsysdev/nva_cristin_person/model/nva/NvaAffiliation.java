package com.github.bibsysdev.nva_cristin_person.model.nva;

import java.net.URI;

public class NvaAffiliation {

    public URI id;
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