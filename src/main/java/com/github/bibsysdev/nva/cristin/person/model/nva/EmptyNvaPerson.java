package com.github.bibsysdev.nva.cristin.person.model.nva;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(NON_NULL)
public class EmptyNvaPerson extends NvaPerson {
    /*
    TODO: NP-2315: Here we can return custom fields like "status": 404 to show that lookup of
     person with given ID did not match any results or that person contained invalid/insufficient data
    */
}