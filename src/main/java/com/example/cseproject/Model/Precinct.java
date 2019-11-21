package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.EligibleBloc;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import org.springframework.data.util.Pair;


import javax.persistence.*;
import java.util.*;


@Entity
public class Precinct {

    private Integer id;

    private String name;

    private Integer population;

    private String party;

    private Integer districtId;

    private Integer countyId;

    private Set<Vote> votes;

    private Set<Edge> precinctEdges;

    private Map<DemograpicGroup, Integer> demographicGroups;

    private String geoJson;

    private Map<DemograpicGroup, Integer> minorityGroupPopulation;

    private Map<Integer, Float> CountyAreas;

    @Id
    @GeneratedValue()
    @Column(name = "precinct_id")
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

    @OneToMany(targetEntity = Vote.class)
    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    @OneToMany(targetEntity = Edge.class)
    public Set<Edge> getPrecinctEdges() {
        return precinctEdges;
    }

    public void setPrecinctEdges(Set<Edge> precinctEdges) {
        this.precinctEdges = precinctEdges;
    }

    @ElementCollection
    @CollectionTable(name = "groupName_groupPopulation",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "groupName")
    @Column(name = "groupPopulation")
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

    @ElementCollection
    @CollectionTable(name = "minorityName_groupPopulation",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "minorityName")
    @Column(name = "groupPopulation")
    public Map<DemograpicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemograpicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    @ElementCollection
    @CollectionTable(name = "countyName_area",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "countyName")
    @Column(name = "area")
    public Map<Integer, Float> getCountyAreas() {
        return CountyAreas;
    }

    public void setCountyAreas(Map<Integer, Float> countyAreas) {
        CountyAreas = countyAreas;
    }


    public EligibleBloc doBlocAnalysis(Threshold threshold, Election election){
        DemograpicGroup populationResult = findLargestDemographicGroup(threshold);
        if (populationResult == null){
            return null;
        }
        EligibleBloc eligibleBloc = this.checkBlocThreshold(threshold, election);
        eligibleBloc.setDemographicGroup(populationResult);
        return eligibleBloc;
    }

    /* Use case 23: check whether it meets populution threshold or not*/
    public DemograpicGroup findLargestDemographicGroup(Threshold threshold){
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
        if (dominate != DemograpicGroup.WHITE){
            return null;
        }
        return dominate;
    }

    /* Use case 24: whether the vote for a party candidate exceeded the user supplied threshold */
    public EligibleBloc checkBlocThreshold(Threshold threshold, Election election){
        Set<Vote> votes = this.getVotes();
        Vote targetVote = null;
        for (Vote vote : votes) {
            if (vote.getElection() == election){
                targetVote = vote;
                break;
            }
        }
        Integer totalVotes = targetVote.getTotalVotes();
        Integer winningVotes = targetVote.getWinningVotes();
        PartyName winningPartyName = targetVote.getWinningPartyName();
        Float percentage = (float) winningVotes / totalVotes;
        if (percentage < threshold.getBlocThreshold()){
            return null;
        }
        EligibleBloc eligibleBloc = new EligibleBloc();
        eligibleBloc.setWinningParty(winningPartyName);
        eligibleBloc.setWinningVotes(winningVotes);
        eligibleBloc.setTotalVotes(totalVotes);
        eligibleBloc.setPopulation(this.population);
        eligibleBloc.setPrecinctName(this.name);
        eligibleBloc.setPercentage(percentage);
        return eligibleBloc;
    }

}
