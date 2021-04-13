package com.github.bibsysdev.nva_cristin_person;

import static com.github.bibsysdev.nva_cristin_person.CristinApiClient.BASE_URL;
import static com.github.bibsysdev.nva_cristin_person.UriUtils.buildUri;
import com.github.bibsysdev.nva_cristin_person.model.cristin.CristinAffiliation;
import com.github.bibsysdev.nva_cristin_person.model.cristin.CristinPerson;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaAffiliation;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaIdentifier;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaPerson;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NvaPersonBuilder {

    private static final String PERSON_TYPE = "Person";
    private static final String CRISTIN_IDENTIFIER_TYPE = "CristinIdentifier";
    private final transient CristinPerson cristinPerson;
    private final transient NvaPerson nvaPerson;

    public NvaPersonBuilder(CristinPerson cristinPerson) {
        this.cristinPerson = cristinPerson;
        nvaPerson = new NvaPerson();
    }

    /**
     * Build a NVA person datamodel from a Cristin person datamodel.
     *
     * @return a NvaPerson converted from a CristinPerson
     */
    public NvaPerson build() {
        nvaPerson.setId(buildUri(BASE_URL, cristinPerson.getCristinPersonId()));
        nvaPerson.setType(PERSON_TYPE);
        nvaPerson.setIdentifier(List.of(createNvaIdentifierFromCristinPersonId(cristinPerson)));
        nvaPerson.setName(cristinPerson.getSurname() + ", " + cristinPerson.getFirstName());
        nvaPerson.setAffiliation(createNvaAffiliationList(cristinPerson));
        nvaPerson.setImageUrl(URI.create(cristinPerson.getPictureUrl()));
        nvaPerson.setVerified(cristinPerson.getIdentifiedCristinPerson());
        return nvaPerson;
    }

    private static NvaIdentifier createNvaIdentifierFromCristinPersonId(CristinPerson cristinPerson) {
        NvaIdentifier nvaIdentifier = new NvaIdentifier();
        nvaIdentifier.setType(CRISTIN_IDENTIFIER_TYPE);
        nvaIdentifier.setValue(cristinPerson.getCristinPersonId());
        return nvaIdentifier;
    }

    private static List<NvaAffiliation> createNvaAffiliationList(CristinPerson cristinPerson) {
        List<NvaAffiliation> nvaAffiliations = new ArrayList<>();
        cristinPerson.getAffiliations().stream().filter(CristinAffiliation::getActive).forEach(cristinAffiliation -> {
            NvaAffiliation nvaAffiliation = new NvaAffiliation();
            nvaAffiliation.setId(cristinAffiliation.getUnit().getUrl());
            if (cristinAffiliation.getPosition() != null) {
                cristinAffiliation.getPosition()
                    .entrySet()
                    .stream()
                    .findFirst()
                    .ifPresent(stringStringEntry -> nvaAffiliation.setRole(stringStringEntry.getValue()));
            }

            nvaAffiliations.add(nvaAffiliation);
        });
        return nvaAffiliations;
    }
}
