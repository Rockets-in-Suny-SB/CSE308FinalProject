package com.example.cseproject.Model;

import javax.persistence.*;

@Entity
public class Edge {

    private Integer id;
    private Integer selfPrecinctId;
    private Integer adjacentPrecinctId;
    private Boolean sameCounty;
    private Float demographicSimilarity;

    @Id
    @Column(name = "edge_id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSelfPrecinctId() {
        return selfPrecinctId;
    }

    public void setSelfPrecinctId(Integer selfPrecinctId) {
        this.selfPrecinctId = selfPrecinctId;
    }

    public Integer getAdjacentPrecinctId() {
        return adjacentPrecinctId;
    }

    public void setAdjacentPrecinctId(Integer adjacentPrecinctId) {
        this.adjacentPrecinctId = adjacentPrecinctId;
    }

    public Boolean getSameCounty() {
        return sameCounty;
    }

    public void setSameCounty(Boolean sameCounty) {
        this.sameCounty = sameCounty;
    }

    public Float getDemographicSimilarity() {
        return demographicSimilarity;
    }

    public void setDemographicSimilarity(Float demographicSimilarity) {
        this.demographicSimilarity = demographicSimilarity;
    }
}
