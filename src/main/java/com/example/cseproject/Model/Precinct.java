package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;


import javax.persistence.*;
import java.util.ArrayList;
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
    private Map<DemograpicGroup, Integer> minorityGroupPopulation;

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

    public Vote getVote(Election election){
        for (Vote vote : this.votes){
            if (vote.getElection() == election)
                return vote;
        }
        return null;
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

    public Map<DemograpicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemograpicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    public Map<Integer, Float> getCountyAreas() {
        return CountyAreas;
    }

    public void setCountyAreas(Map<Integer, Float> countyAreas) {
        CountyAreas = countyAreas;
    }


    public List<Object> doBlocAnalysis(Threshold threshold, Election election){
        List<Object> populationResult = this.findLargestDemographicGroup(threshold);
        if (populationResult.get(0) == Boolean.FALSE){
            return null;
        }
        return this.checkBlocThreshold(threshold, election);
    }

    public List<Object> findLargestDemographicGroup(Threshold threshold){
        DemograpicGroup dominate = DemograpicGroup.WHITE;
        Float maxPercent = (float) 0;
        Float populationThreshold = threshold.getPopulationThreshold();
        for(Map.Entry<DemograpicGroup,Integer> entry : this.minorityGroupPopulation.entrySet()){
            Float percentage = (float) entry.getValue()/this.population;
            if (percentage > populationThreshold && percentage > maxPercent){
                dominate = entry.getKey();
                maxPercent = percentage;
            }
        }
        List<Object> result = new ArrayList<>();
        if (dominate != DemograpicGroup.WHITE)
            result.add(Boolean.FALSE);
        else
            result.add(Boolean.TRUE);
        result.add(dominate);
        result.add(maxPercent);
        return result;


    }

    public List<Object> checkBlocThreshold(Threshold threshold, Election election){
        Vote targetVote = this.getVote(election);
        Integer totalVotes = targetVote.getTotalVotes();
        Integer winningVotes = targetVote.getWinningVotes();
        PartyName winningPartyName = targetVote.getWinningPartyName();
        Float percentage = (float) winningVotes / totalVotes;
        if (percentage < threshold.getBlocThreshold()){
            return null;
        }
        List<Object> result = new ArrayList<>();
        result.add(this.name);
        result.add(this.population);
        result.add(winningVotes);
        result.add(totalVotes);
        result.add(percentage);
        return result;
    }

}
