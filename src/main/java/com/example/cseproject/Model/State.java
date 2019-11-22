package com.example.cseproject.Model;

import com.example.cseproject.Algorithm.SetLib;
import com.example.cseproject.DataClasses.*;
import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.CompositeKeys.StateId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@IdClass(StateId.class)
public class State {

    @Id
    private StateName name;

    @Id
    private State_Status status;

    @OneToMany
    private Set<District> districts;
    @Transient
    private Threshold threshold;

    private Integer population;
    @Transient
    private Set<Cluster> clusters;
    @Enumerated
    private Election election;
    @OneToMany(targetEntity = Precinct.class)
    private Set<Precinct> precincts;

    @ElementCollection
    @CollectionTable(name = "state_demographicGroup",
            joinColumns = {@JoinColumn(name = "state_name"),
                    @JoinColumn(name = "state_status")})
    @MapKeyColumn(name = "demographic_group")
    @Column(name = "population")
    private Map<DemographicGroup, Integer> demographicGroups;

    public State() {
    }

    public StateName getName() {
        return name;
    }

    @Column (name="state_name")
    public void setName(StateName name) {
        this.name = name;
    }

    @Column ( name="state_status")
    public State_Status getStatus() {
        return status;
    }

    public void setStatus(State_Status status) {
        this.status = status;
    }

    public Set<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Set<Precinct> getPrecincts() {
        return precincts;
    }

    public void setPrecincts(Set<Precinct> precincts) {
        this.precincts = precincts;
    }

    public Set<Cluster> getClusters() {
        return this.clusters;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    public Map<DemographicGroup, Integer> getDemographicGroups() {
        return demographicGroups;
    }

    public void setDemographicGroups(Map<DemographicGroup, Integer> demographicGroups) {
        this.demographicGroups = demographicGroups;
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
    public Set<MinorityPopulation> getPopulationDistribution(Parameter parameter) {
        Float minimumPercentage = parameter.getMinimumPercentage();
        Float maximumPercentage = parameter.getMaximumPercentage();
        Set<DemographicGroup> demographicGroups = parameter.getMinorityPopulations();
        Set<MinorityPopulation> minorityPopulations = new HashSet<>();
        /* add white population */
        Integer whitePopulation = this.getDemographicGroups().get(DemographicGroup.WHITE);
        Float whitePercentage = (float) whitePopulation / this.population;
        MinorityPopulation whitePopulationData = new MinorityPopulation(DemographicGroup.WHITE,
                                                        whitePercentage, whitePopulation);
        minorityPopulations.add(whitePopulationData);
        for (DemographicGroup demographicGroup : demographicGroups) {
            Integer population = this.demographicGroups.get(demographicGroup);
            if (population != null) {
                Float percentage = (float) population / this.population;
                if (population >= minimumPercentage && population <= maximumPercentage) {
                    MinorityPopulation minorityPopulation = new MinorityPopulation(demographicGroup,
                                                                percentage, population);
                    minorityPopulations.add(minorityPopulation);
                }
            }
        }
        return minorityPopulations;
    }

    public void combine(Cluster c1, Cluster c2) {
        c1.addClusterData(c2);
        c1.combine(c2);
        clusters.remove(c2);
    }

    public void initializeClusters(){

    }
}


