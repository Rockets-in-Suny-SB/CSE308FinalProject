package com.example.cseproject.Model;

import javax.persistence.*;

public class PrecinctEdge {
    @Id
    @Column(name = "edge_id")
    private Integer id;
    private Precinct adjacentPrecinct1;
    private Precinct adjacentPrecinct2;
    private Boolean sameCounty;
    private Float demographicSimilarity;
    private Integer precinct_id;





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
