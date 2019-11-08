package com.example.cseproject.Model;

import javax.persistence.*;

public class Edge {
    @Id
    @Column(name = "edge_id")
    private Integer id;
    private Precinct adjacentPrecinct1;
    private Precinct adjacentPrecinct2;
    private Boolean sameCounty;
    private Float demographicSimilarity;
    private Integer precinct_id;


    public Precinct getAdjacentPrecinct1() {
        return adjacentPrecinct1;
    }

    public void setAdjacentPrecinct1(Precinct adjacentPrecinct1) {
        this.adjacentPrecinct1 = adjacentPrecinct1;
    }

    public Precinct getAdjacentPrecinct2() {
        return adjacentPrecinct2;
    }

    public void setAdjacentPrecinct2(Precinct adjacentPrecinct2) {
        this.adjacentPrecinct2 = adjacentPrecinct2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getPrecinct_id() {
        return precinct_id;
    }

    public void setPrecinct_id(Integer precinct_id) {
        this.precinct_id = precinct_id;
    }
}
