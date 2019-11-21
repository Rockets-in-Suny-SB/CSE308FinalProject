package com.example.cseproject.Model;

import com.example.cseproject.Algorithm.SetLib;
import com.example.cseproject.DataClasses.Cluster;
import com.example.cseproject.DataClasses.EligibleBloc;
import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.CompositeKeys.StateId;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@IdClass(StateId.class)
public class State {

    private StateName name;

    private State_Status status;

    private Set<District> districts;

    private Threshold threshold;

    private Set<Cluster> clusters;

    private Election election;

    public State() {
    }

    @Id
    public StateName getName() {
        return name;
    }

    public void setName(StateName name) {
        this.name = name;
    }

    @Id
    public State_Status getStatus() {
        return status;
    }

    public void setStatus(State_Status status) {
        this.status = status;
    }

    @OneToMany
    public Set<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }

    @Transient
    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    @Enumerated
    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Set<Precinct> getPrecincts() {
        Set<Precinct> precincts = new HashSet<>();
        for (District district : districts) {
            precincts.addAll(district.getPrecincts());
        }
        return precincts;
    }

    @Transient
    public Set<Cluster> getClusters() {
        return this.clusters;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    /* phase 0 */
    public Set<EligibleBloc> findEligibleBlocs() {
        Set<EligibleBloc> result = new HashSet<>();
        for (Precinct precinct : this.getPrecincts()) {
            EligibleBloc eligibleBloc = precinct.doBlocAnalysis(this.threshold, this.election);
            if (eligibleBloc != null)
                result.add(eligibleBloc);
        }
        return result;
    }

    /* Use case 43*/
    public Set<Set<Object>> getPopulationDistribution(Parameter parameter) {
        Set<DemograpicGroup> demograpicGroups = parameter.getMinorityPopulations();
        Map<DemograpicGroup, Integer> demographicResult = new HashMap<>();
        Integer statePopulation = 0;
        for (District district : this.districts) {
            statePopulation += district.getPopulation();
            for (Precinct precinct : district.getPrecincts()) {
                Map<DemograpicGroup, Integer> demographicGroupMap = precinct.getDemographicGroups();
                for (DemograpicGroup dp : demograpicGroups) {
                    Integer dpPopulation = demographicGroupMap.get(dp);
                    if (dpPopulation != null) {
                        Integer ddp = demographicResult.get(dp);
                        if (ddp == null) {
                            /* if key is not in the map, create an entry*/
                            demographicResult.put(dp, dpPopulation);
                        } else {
                            demographicResult.put(dp, ddp + dpPopulation);
                        }
                    }
                }
            }
        }
        Set<Set<Object>> result = new HashSet<>();
        for (Map.Entry<DemograpicGroup, Integer> entry : demographicResult.entrySet()) {
            Float percentage = (float) entry.getValue() / statePopulation;
            if (percentage <= parameter.getMaximumPercentage() && percentage >= parameter.getMinimumPercentage()) {
                Set<Object> demographicData = new HashSet<>();
                demographicData.add(entry.getKey());
                demographicData.add(entry.getValue());
                demographicData.add(percentage);
                result.add(demographicData);
            }
        }
        return result;

    }

    public void combine(Cluster c1, Cluster c2) {
        c1.updateClusterData(c2);
        Set<Cluster> c1Neighbors = c1.getNeighbors();
        Set<Cluster> c2Neighbors = c2.getNeighbors();
        Set<Cluster> intersectingClusters = SetLib.intersection(c1Neighbors, c2Neighbors);
        c1.combine(intersectingClusters, c2);
        clusters.remove(c2);
    }
}


