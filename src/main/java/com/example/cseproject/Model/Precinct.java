package com.example.cseproject.Model;

import com.example.cseproject.Enum.DemograpicGroup;

import javax.persistence.*;
import java.util.List;
import java.util.Map;


@Entity
public class Precinct {

    @Id
    @GeneratedValue()
    @Column(name = "precinct_id")
    private Integer id;
    private String name;
    private Integer population;
    private String party;
    private Integer districtId;
    private Integer countyId;

    // This object should be created during phase I, so it may probably in service
    //private DemographicAnalysisData dad;

    @OneToMany(targetEntity = Vote.class)
    private List<Vote> votes;

    @OneToMany(targetEntity = Edge.class)
    private List<Edge> precinctEdges;

    @ElementCollection
    @CollectionTable(name = "groupName_groupPopulation",
                        joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "groupName")
    @Column(name = "groupPopulation")
    private Map<DemograpicGroup, Integer> demographicGroups;

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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public List<Edge> getPrecinctEdges() {
        return precinctEdges;
    }

    public void setEdges(List<Edge> edges) {
        this.precinctEdges = edges;
    }

    public Map<DemograpicGroup, Integer> getDemographicGroups() {
        return demographicGroups;
    }

    public void setDemographicGroups(Map<DemograpicGroup, Integer> demographicGroups) {
        this.demographicGroups = demographicGroups;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }

    public Map<String, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<String, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    public Map<Integer, Float> getCountyAreas() {
        return CountyAreas;
    }

    public void setCountyAreas(Map<Integer, Float> countyAreas) {
        CountyAreas = countyAreas;
    }
}
