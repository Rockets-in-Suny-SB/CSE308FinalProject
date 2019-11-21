package com.example.cseproject.Model;

import javax.persistence.*;

@Entity
public class Edge {

    private Integer id;
    private Integer adjacentPrecinct1_id;
    private Integer adjacentPrecinct2_id;
    private Boolean sameCounty;
    private Float demographicSimilarity;
    private Integer precinct_id;

    @Id
    @Column(name = "edge_id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdjacentPrecinct1_id() {
        return adjacentPrecinct1_id;
    }

    public void setAdjacentPrecinct1_id(Integer adjacentPrecinct1_id) {
        this.adjacentPrecinct1_id = adjacentPrecinct1_id;
    }

    public Integer getAdjacentPrecinct2_id() {
        return adjacentPrecinct2_id;
    }

    public void setAdjacentPrecinct2_id(Integer adjacentPrecinct2_id) {
        this.adjacentPrecinct2_id = adjacentPrecinct2_id;
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
