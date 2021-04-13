package com.github.bibsysdev.nva_cristin_person;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.bibsysdev.nva_cristin_person.model.nva.NvaPerson;
import java.net.URI;
import java.util.List;

@JsonPropertyOrder({"@context"})
public class PersonsWrapper {

    public static final String PERSON_SEARCH_CONTEXT_URL = "https://example.org/person-search-context.json";

    @JsonProperty("@context")
    private String context = PERSON_SEARCH_CONTEXT_URL;
    @JsonProperty
    private URI id;
    @JsonProperty
    private Integer size;
    @JsonProperty
    private String searchString;
    @JsonProperty
    private Long processingTime;
    @JsonProperty
    private Integer firstRecord;
    @JsonProperty
    private String nextResults;
    @JsonProperty
    private List<NvaPerson> hits;

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }

    public Integer getFirstRecord() {
        return firstRecord;
    }

    public void setFirstRecord(Integer firstRecord) {
        this.firstRecord = firstRecord;
    }

    public String getNextResults() {
        return nextResults;
    }

    public void setNextResults(String nextResults) {
        this.nextResults = nextResults;
    }

    public List<NvaPerson> getHits() {
        return hits;
    }

    public void setHits(List<NvaPerson> hits) {
        this.hits = hits;
    }
}
