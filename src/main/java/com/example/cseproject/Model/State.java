package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.*;
import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.CompositeKeys.StateId;
import com.example.cseproject.interfaces.StateInterface;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@IdClass(StateId.class)
public class State
    implements StateInterface<Precinct, District> {

    @Id
    private StateName name;
    @Id
    private State_Status status;
    @ManyToMany(targetEntity = Precinct.class)
    private Map<Integer, Precinct> precincts;
    @Transient
    private Threshold threshold;
    private int population;
    @Transient
    private Set<Cluster> clusters;
    @Enumerated
    @Transient
    private Election election;
    @ManyToMany(targetEntity = District.class)
    private Map<Integer, District> districts;

    @ElementCollection
    @CollectionTable(name = "state_demographicGroup",
            joinColumns = {@JoinColumn(name = "state_name"),
                    @JoinColumn(name = "state_status")})
    @MapKeyColumn(name = "demographic_group")
    @Column(name = "population")
    private Map<DemographicGroup, Integer> demographicGroups;

    public State() {
        this.clusters=new HashSet<>();
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

    public Set<Precinct> getPrecincts() {
        return new HashSet<>(precincts.values());
    }

    public Set<District> getDistricts() {
        return new HashSet<>(districts.values());
    }


    @Override
    public Precinct getPrecinct(Integer precinctId) {
        return this.precincts.get(precinctId);
    }

    @Override
    public District getDistrict(Integer districtId) {
        return this.districts.get(districtId);
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

    public int getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
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
        float minimumPercentage = parameter.getMinimumPercentage();
        float maximumPercentage = parameter.getMaximumPercentage();
        Set<DemographicGroup> demographicGroups = parameter.getMinorityPopulations();
        demographicGroups.add(DemographicGroup.WHITE);
        Set<MinorityPopulation> minorityPopulations = new HashSet<>();
        Boolean isCombined  = parameter.getCombined();
        Set<Set<DemographicGroup>> combinedGroup = parameter.getCombinedGroup();
        for (DemographicGroup demographicGroup : demographicGroups) {
            Integer population = this.demographicGroups.get(demographicGroup);
            System.out.println(population);
            if (population != null) {
                Float percentage = (float) population / this.population;
                if (population >= minimumPercentage && population <= maximumPercentage) {
                    MinorityPopulation minorityPopulation = new MinorityPopulation(demographicGroup.toString(),
                                                                percentage, population);
                    minorityPopulations.add(minorityPopulation);
                }
            }
        }
        if (!isCombined) {
            return minorityPopulations;
        }
        for (Set<DemographicGroup> group : combinedGroup) {
            Integer groupPopulation = 0;
            String groupNames = "";
            for (DemographicGroup demographicGroup : group) {
                Integer population = this.demographicGroups.get(demographicGroup);
                if (population != null) {
                    groupPopulation += population;
                }
                groupNames += demographicGroup.toString()+", ";
            }
            Float percentage = (float) population / this.population;
            if (population >= minimumPercentage && population <= maximumPercentage) {
                MinorityPopulation minorityPopulation = new MinorityPopulation(
                        groupNames.substring(0, groupNames.length()-2), percentage, population);
                minorityPopulations.add(minorityPopulation);
            }
        }
        return minorityPopulations;
    }

    public void combine(Cluster c1, Cluster c2) {
        c1.addClusterData(c2);
        c1.combine(c2);
        clusters.remove(c2);
    }


}


