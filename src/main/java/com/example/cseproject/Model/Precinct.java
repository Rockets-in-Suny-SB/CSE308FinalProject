package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.Cluster;
import com.example.cseproject.DataClasses.EligibleBloc;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.interfaces.PrecinctInterface;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.locationtech.jts.geom.*;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.util.Pair;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Precinct
        implements PrecinctInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "precinct_id")
    private Integer id;
    private String name;
    private Integer population;
    @ManyToOne(targetEntity = County.class)
    private County countyId;
    @Transient
    private Integer parentCluster;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "precinct_votes",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "election")
    @Column(name = "vote")
    private Map<Election, Vote> votes;
    @OneToMany(fetch = FetchType.EAGER, targetEntity = Edge.class)
//    @JsonIgnore
    private Set<Edge> precinctEdges;
    private String geoJson;
    @ElementCollection( fetch = FetchType.EAGER)
    @CollectionTable(name = "minorityName_groupPopulation",
            joinColumns = @JoinColumn(name = "precinct_id"))
    @MapKeyColumn(name = "minorityName")
    @Column(name = "groupPopulation")
    private Map<DemographicGroup, Integer> minorityGroupPopulation;

    @Transient
    private Double populationDensity;
    @Transient
    private Integer originalDistrictID;
    private Integer gopVote;
    private Integer demVote;
    @Transient
    private Set<Integer> neighborIds;
    @Transient
    @JsonIgnore
    private Geometry geometry;

    public Precinct() {
        this.geometry = new Geometry(new GeometryFactory(new PrecisionModel())) {
            @Override
            public String getGeometryType() {
                return null;
            }

            @Override
            public Coordinate getCoordinate() {
                return null;
            }

            @Override
            public Coordinate[] getCoordinates() {
                return new Coordinate[0];
            }

            @Override
            public int getNumPoints() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public int getDimension() {
                return 0;
            }

            @Override
            public Geometry getBoundary() {
                return null;
            }

            @Override
            public int getBoundaryDimension() {
                return 0;
            }

            @Override
            public Geometry reverse() {
                return null;
            }

            @Override
            public boolean equalsExact(Geometry geometry, double v) {
                return false;
            }

            @Override
            public void apply(CoordinateFilter coordinateFilter) {

            }

            @Override
            public void apply(CoordinateSequenceFilter coordinateSequenceFilter) {

            }

            @Override
            public void apply(GeometryFilter geometryFilter) {

            }

            @Override
            public void apply(GeometryComponentFilter geometryComponentFilter) {

            }

            @Override
            protected Geometry copyInternal() {
                return null;
            }

            @Override
            public void normalize() {

            }

            @Override
            protected Envelope computeEnvelopeInternal() {
                return null;
            }

            @Override
            protected int compareToSameClass(Object o) {
                return 0;
            }

            @Override
            protected int compareToSameClass(Object o, CoordinateSequenceComparator coordinateSequenceComparator) {
                return 0;
            }

            @Override
            protected int getSortIndex() {
                return 0;
            }
        };
    }


    @Override
    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    @Override
    public Integer getGopVote() {
        return gopVote;
    }

    public void setGopVote(Integer gopVote) {
        this.gopVote = gopVote;
    }

    @Override
    public Integer getDemVote() {
        return demVote;
    }

    public void setDemVote(Integer demVote) {
        this.demVote = demVote;
    }

    @Override
    public Integer getOriginalDistrictID() {
        return this.originalDistrictID;
    }

    public void setOriginalDistrictID(Integer originalDistrictID) {
        this.originalDistrictID = originalDistrictID;
    }

    @Override
    public Set<Integer> getNeighborIds() {
        return neighborIds;
    }

    public void setNeighborIds(Set<Integer> neighborIds) {
        this.neighborIds = neighborIds;
    }

    public Integer getParentCluster() {
        return parentCluster;
    }

    public void setParentCluster(Integer parentCluster) {
        this.parentCluster = parentCluster;
    }


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

    public County getCountyId() {
        return countyId;
    }

    public void setCountyId(County countyId) {
        this.countyId = countyId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }


    public Map<Election, Vote> getVotes() {
        return votes;
    }

    public void setVotes(Map<Election, Vote> votes) {
        this.votes = votes;
    }

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


    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }




    @Transient
    public Double getPopulationDensity() {
        if (geometry !=null && geometry.getArea() != 0)
            return getPopulation() / geometry.getArea();
        return (double) -1;
    }

    public void setPopulationDensity(Double populationDensity) {
        this.populationDensity = populationDensity;
    }

    public EligibleBloc doBlocAnalysis(Threshold threshold, Election election) {
        Pair<Boolean, DemographicGroup> populationResult = findLargestDemographicGroup(threshold);
        Pair<Boolean, EligibleBloc> votingResult = this.checkBlocThreshold(threshold, election);
        Boolean isEligible = populationResult.getFirst() && votingResult.getFirst();
        EligibleBloc eligibleBloc = votingResult.getSecond();
        eligibleBloc.setDemographicGroup(populationResult.getSecond().toString());
        eligibleBloc.setEligible(isEligible);
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
        String winningPartyResult = winningPartyName.substring(0, 1).toUpperCase() + winningPartyName.substring(1);
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

    public Integer calculateGopVotes(Election election) {
        Integer totalGopVotes = 0;
        Vote vote = votes.get(election);
        return  vote.getPartyVotes().get(PartyName.REPUBLICAN);
    }

    public Integer calculateDEmVotes(Election election) {
        Integer totalGopVotes = 0;
        Vote vote = votes.get(election);
        return  vote.getPartyVotes().get(PartyName.DEMOCRATIC);
    }

    public void calculateNeighborId() {
        this.neighborIds = new HashSet<>();
        for (Edge edge : this.precinctEdges) {
            neighborIds.add(edge.getAdjacentPrecinctId());
        }
    }
}
