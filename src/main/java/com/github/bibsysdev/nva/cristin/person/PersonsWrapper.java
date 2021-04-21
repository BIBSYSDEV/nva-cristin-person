package com.github.bibsysdev.nva.cristin.person;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.bibsysdev.nva.cristin.person.model.nva.NvaPerson;
import java.net.URI;
import java.util.List;
import nva.commons.core.JacocoGenerated;

@JsonInclude(ALWAYS)
@JsonPropertyOrder("@context")
@JacocoGenerated
public class PersonsWrapper {

    public static final String PERSON_SEARCH_CONTEXT_URL = "https://example.org/person-search-context.json";
    public static final String CONTEXT = "@context";

    @JsonProperty(CONTEXT)
    private String context = PERSON_SEARCH_CONTEXT_URL;
    private URI id;
    private Integer size;
    private String searchString;
    private Long processingTime;
    private Integer firstRecord;
    private String nextResults;
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
