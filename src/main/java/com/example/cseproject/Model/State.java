package com.example.cseproject.Model;

import com.example.cseproject.Algorithm.SetLib;
import com.example.cseproject.DataClasses.Cluster;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.CompositeKeys.StateId;

import javax.persistence.*;
import java.util.*;

@Entity
@IdClass(StateId.class)
public class State {
    @Id
    private StateName name;
    @Id
    private State_Status status;

    @OneToMany
    private List<District> districts;

    @Transient
    private Threshold threshold;

    @Transient
    private List<String> EligibleBlocs;
    @Transient
    private Set<Cluster> clusters;

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    public StateName getName() {
        return name;
    }

    public void setName(StateName name) {
        this.name = name;
    }

    public State_Status getStatus() {
        return status;
    }

    public void setStatus(State_Status status) {
        this.status = status;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public List<Precinct> getPrecinct(){
        List<Precinct> precincts = new ArrayList<>();
        for (District district:districts){
            precincts.addAll(district.getPrecincts());
        }
        return precincts;
    }

    public List<List<Object>> findEligibleBlocs(){
        List<List<Object>> result = new ArrayList<>();

        for (Precinct precinct:this.getPrecinct()){
            List<Object> eligibleBloc = precinct.findLargestDemographicGroup(this.threshold);
            if (eligibleBloc != null)
                result.add(eligibleBloc);
        }
        return result;
    }

    public List<List<Object>> getPopulationDistribution(Integer districtId, List<DemograpicGroup> demograpicGroups){
        District district = null;
        for (District d : this.districts){
            if (d.getId().equals(districtId)){
                district = d;
                break;
            }
        }
        if (district == null){
            System.out.println("District do not exist");
            return null;
        }
        Map<DemograpicGroup,Integer> distributionResult =new HashMap<>();
        Integer districtPopulation = district.getPopulation();
        for (Precinct precinct : district.getPrecincts()){
            Map<DemograpicGroup, Integer> demographicGroupMap = precinct.getDemographicGroups();
            for (DemograpicGroup dp : demograpicGroups){
                Integer dpPopulation = demographicGroupMap.get(dp);
                if (dpPopulation != null){
                    Integer ddp = distributionResult.get(dp);
                    if (ddp == null)
                        distributionResult.put(dp,dpPopulation);
                    else
                        distributionResult.put(dp,ddp+dpPopulation);
                }
            }
        }
        List<List<Object>> result = new ArrayList<>();
        for (Map.Entry<DemograpicGroup,Integer> entry : distributionResult.entrySet()){
            List<Object> demographicData = new ArrayList<>();
            demographicData.add(entry.getKey());
            demographicData.add(entry.getValue());
            demographicData.add((float)entry.getValue()/districtPopulation);
            result.add(demographicData);
        }
        return result;

    }
    public Set<Cluster> getClusters(){
        return this.clusters;
    }
    public void setClusters(Set<Cluster> clusters){
        this.clusters=clusters;
    }

    public void combine(Cluster c1, Cluster c2){
        c1.updateClusterData(c2);
        Set<Cluster> c1Neighbors=c1.getNeighbors();
        Set<Cluster> c2Neighbors=c2.getNeighbors();
        Set<Cluster> intersectingClusters= SetLib.intersection(c1Neighbors,c2Neighbors);
        c1.combine(intersectingClusters,c2);
        clusters.remove(c2);
    }
}


