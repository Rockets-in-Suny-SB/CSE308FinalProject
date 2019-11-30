package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.Cluster;
import com.example.cseproject.DataClasses.EligibleBloc;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemographicGroup;
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
    @ManyToOne(targetEntity = County.class)
    private Integer countyId;
    private Cluster parentCluster;
    private Map<Election, Vote> votes;
    private Set<Edge> precinctEdges;
    private String geoJson;
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    private DemographicGroup largestPopulationGroup;

    public Precinct() {

    }

    @Transient
    public Cluster getParentCluster() {
        return parentCluster;
    }

    public void setParentCluster(Cluster parentCluster) {
        this.parentCluster = parentCluster;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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


    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    @ElementCollection
    @CollectionTable(name = "precinct_votes",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "election")
    @Column(name = "vote")
    public Map<Election, Vote> getVotes() {
        return votes;
    }

    public void setVotes(Map<Election, Vote> votes) {
        this.votes = votes;
    }

    @OneToMany(targetEntity = Edge.class)
    public Set<Edge> getPrecinctEdges() {
        return precinctEdges;
    }

    public void setPrecinctEdges(Set<Edge> precinctEdges) {
        this.precinctEdges = precinctEdges;
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
    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    public EligibleBloc doBlocAnalysis(Threshold threshold, Election election) {
        Pair<Boolean, DemographicGroup> populationResult = findLargestDemographicGroup(threshold);
        Pair<Boolean, EligibleBloc> votingResult = this.checkBlocThreshold(threshold, election);
        Boolean isEligible = populationResult.getFirst() && votingResult.getFirst();
        EligibleBloc eligibleBloc = votingResult.getSecond();
        eligibleBloc.setDemographicGroup(populationResult.getSecond());
        eligibleBloc.setEligibleBloc(isEligible);
        return eligibleBloc;
    }

    /* Use case 23: check whether it meets populution threshold or not*/
    public Pair<Boolean, DemographicGroup> findLargestDemographicGroup(Threshold threshold) {
        DemographicGroup mostPopulationGroup = DemographicGroup.WHITE;
        Boolean isEligible = false;
        Float maxPercent = (float) 0;
        Float populationThreshold = threshold.getPopulationThreshold();
        for (Map.Entry<DemographicGroup, Integer> entry : this.minorityGroupPopulation.entrySet()) {
            Float percentage = (float) entry.getValue() / this.population;
            if (percentage > maxPercent) {
                mostPopulationGroup = entry.getKey();
                maxPercent = percentage;
            }
        }
        if (maxPercent > populationThreshold) {
            isEligible = true;
        }
        return Pair.of(isEligible, mostPopulationGroup);
    }

    /* Use case 24: whether the vote for a party candidate exceeded the user supplied threshold */
    public Pair<Boolean, EligibleBloc> checkBlocThreshold(Threshold threshold, Election election) {
        Boolean isEligible = false;
        Map<Election, Vote> votes = this.getVotes();
        Vote targetVote = votes.get(election);
        Integer totalVotes = targetVote.getTotalVotes();
        Integer winningVotes = targetVote.getWinningVotes();
        PartyName winningPartyValue = targetVote.getWinningPartyName();
        Float percentage = (float) winningVotes / totalVotes;
        if (percentage > threshold.getBlocThreshold()) {
            isEligible = true;
        }
        EligibleBloc eligibleBloc = new EligibleBloc();
        String winningPartyName = winningPartyValue.name();
        String winningPartyResult = winningPartyName.substring(0,1).toUpperCase() + winningPartyName.substring(1);
        eligibleBloc.setWinningParty(winningPartyResult);
        eligibleBloc.setWinningVotes(winningVotes);
        eligibleBloc.setTotalVotes(totalVotes);
        eligibleBloc.setPopulation(this.population);
        eligibleBloc.setPrecinctName(this.name);
        eligibleBloc.setPercentage(percentage);
        if (winningVotes > this.population) {
            isEligible = false;
        }
        return Pair.of(isEligible, eligibleBloc);
    }

}
