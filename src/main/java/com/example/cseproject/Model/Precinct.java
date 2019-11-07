package com.example.cseproject.Model;

import javax.persistence.*;
import java.util.List;
import java.util.Map;


@Entity
public class Precinct {

    @Id
    @Column(name = "precinct_id")
    private Integer id;
    private String name;
    private Integer population;
    private String party;
    private Integer districtId;
    private Integer countyId;

    // This object should be created during phase I, so it may probably in service
    //private DemographicAnalysisData dad;

    @JoinColumn(name = "precinct_id", referencedColumnName = "precinct_id")
    private List<Votes> votes;

    @OneToMany
    @JoinColumn(name = "precinct_id", referencedColumnName = "precinct_id")
    private List<PrecinctEdge> precinctEdges;

    @ElementCollection
    @CollectionTable(name = "groupName_groupPopulation",
                        joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "groupName")
    @Column(name = "groupPopulation")
    private Map<String, Integer> demographicGroups;

    private String geoJson;

    @ElementCollection
    @CollectionTable(name = "minorityName_groupPopulation",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "minorityName")
    @Column(name = "groupPopulation")
    private Map<String, Integer> minorityGroupPopulation;

    @ElementCollection
    @CollectionTable(name = "countyName_area",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "countyName")
    @Column(name = "area")
    private Map<Integer, Float> CountyAreas;

}
