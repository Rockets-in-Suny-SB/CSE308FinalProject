package com.example.cseproject.Model;

import javax.persistence.*;

public class PrecinctEdge {
    @Id
    @Column(name = "edge_id")
    private Integer id;
    private Integer adjacentCluster1;
    private Integer adjacentCluster2;
    private Boolean sameCounty;
    private Float demographicSimilarity;
    private Integer precinct_id;


}
