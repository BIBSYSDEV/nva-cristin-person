package com.github.bibsysdev.nva.cristin.person;

import static com.github.bibsysdev.nva.cristin.person.CristinApiClient.BASE_URL;
import static com.github.bibsysdev.nva.cristin.person.UriUtils.buildUri;
import com.github.bibsysdev.nva.cristin.person.model.cristin.CristinAffiliation;
import com.github.bibsysdev.nva.cristin.person.model.cristin.CristinPerson;
import com.github.bibsysdev.nva.cristin.person.model.nva.NvaAffiliation;
import com.github.bibsysdev.nva.cristin.person.model.nva.NvaIdentifier;
import com.github.bibsysdev.nva.cristin.person.model.nva.NvaPerson;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nva.commons.core.JacocoGenerated;

@JacocoGenerated
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

        if (cristinPerson.getPictureUrl() != null) {
            nvaPerson.setImageUrl(URI.create(cristinPerson.getPictureUrl()));
        }

        nvaPerson.setVerified(cristinPerson.getIdentifiedCristinPerson());
        return nvaPerson;
    }

    private NvaIdentifier createNvaIdentifierFromCristinPersonId(CristinPerson cristinPerson) {
        NvaIdentifier nvaIdentifier = new NvaIdentifier();
        nvaIdentifier.setType(CRISTIN_IDENTIFIER_TYPE);
        nvaIdentifier.setValue(cristinPerson.getCristinPersonId());
        return nvaIdentifier;
    }

    private List<NvaAffiliation> createNvaAffiliationList(CristinPerson cristinPerson) {
        return Optional.ofNullable(cristinPerson.getAffiliations())
            .orElseGet(Collections::emptyList)
            .stream()
            .filter(CristinAffiliation::getActive)
            .map(this::convertCristinAffiliationToNvaAffiliation)
            .collect(Collectors.toList());
    }

    private NvaAffiliation convertCristinAffiliationToNvaAffiliation(CristinAffiliation cristinAffiliation) {
        NvaAffiliation nvaAffiliation = new NvaAffiliation();
        nvaAffiliation.setId(cristinAffiliation.getUnit().getUrl());
        if (cristinAffiliation.getPosition() != null && !cristinAffiliation.getPosition().isEmpty()) {
            nvaAffiliation.setRole(cristinAffiliation.getPosition());
        }

        return nvaAffiliation;
    }
}
