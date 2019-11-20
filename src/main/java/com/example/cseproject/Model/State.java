package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.Cluster;
import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.CompositeKeys.StateId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@IdClass(StateId.class)
public class State {
    @Id
    private StateName name;
    @Id
    private State_Status status;
    @Id
    private Election election;

    @OneToMany
    private List<District> districts;

    @Transient
    private Threshold threshold;

    @Transient
    private List<String> EligibleBlocs;
    @Transient
    private List<Cluster> clusters;

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

    public List<List<Object>> getPopulationDistribution(Parameter parameter){
        List<DemograpicGroup> demograpicGroups = parameter.getMinorityPopulations();
        Map<DemograpicGroup,Integer> demographicResult =new HashMap<>();
        Integer statePopulation = 0;
        for(District district : this.districts){
            statePopulation += district.getPopulation();
            for (Precinct precinct : district.getPrecincts()){
                Map<DemograpicGroup, Integer> demographicGroupMap = precinct.getDemographicGroups();
                for (DemograpicGroup dp : demograpicGroups){
                    Integer dpPopulation = demographicGroupMap.get(dp);
                    if (dpPopulation != null){
                        Integer ddp = demographicResult.get(dp);
                        if (ddp == null)
                            demographicResult.put(dp,dpPopulation);
                        else
                            demographicResult.put(dp,ddp+dpPopulation);
                    }
                }
            }
        }
        List<List<Object>> result = new ArrayList<>();
        for (Map.Entry<DemograpicGroup,Integer> entry : demographicResult.entrySet()){
            Float percentage = (float) entry.getValue()/statePopulation;
            if ( percentage <= parameter.getMaximumPercentage() && percentage >= parameter.getMinimumPercentage()){
                List<Object> demographicData = new ArrayList<>();
                demographicData.add(entry.getKey());
                demographicData.add(entry.getValue());
                demographicData.add(percentage);
                result.add(demographicData);
            }
        }
        return result;

    }
    public void initializeClusters(){

    }
    public List<Cluster> getClusters(){
        return this.clusters;
    }
    public void setClusters(List<Cluster> clusters){
        this.clusters=clusters;
    }
}


