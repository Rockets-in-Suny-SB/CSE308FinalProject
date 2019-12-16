package com.example.cseproject.Model;

import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.interfaces.DistrictInterface;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import javax.persistence.*;
import java.util.*;

@Entity
public class District
    implements DistrictInterface<Precinct> {

    @Id
    @GeneratedValue
    @Column(name = "district_id")
    private Integer id;
    @Transient @JsonIgnore
    private String color;
    private String name;
    private Integer population;
    @ElementCollection
    @CollectionTable(name = "district_partyVotes",
            joinColumns = @JoinColumn(name = "district_id"))
    private Map<PartyName, Integer> partyVotes;
    @ElementCollection
    @CollectionTable(name = "district_minorityGroupPopulation",
            joinColumns = @JoinColumn(name = "district_id"))
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    private String geoJson;
    @Transient @JsonIgnore
    private Map<Integer, Precinct> precincts;
    @Transient @JsonIgnore
    private Election election;

    @Transient @JsonIgnore
    private static final int MAXX = 0;
    @Transient @JsonIgnore
    private static final int MAXY = 1;
    @Transient @JsonIgnore
    private static final int MINX = 2;
    @Transient @JsonIgnore
    private static final int MINY = 3;
    @Transient @JsonIgnore
    private int gopVote;
    @Transient @JsonIgnore
    private int demVote;
    @Transient @JsonIgnore
    private int internalEdges = 0;
    @Transient @JsonIgnore
    private int externalEdges = 0;
    @Transient @JsonIgnore
    private Set<Precinct> borderPrecincts;
    @Transient @JsonIgnore
    private MultiPolygon multiPolygon;
    @Transient @JsonIgnore
    private Geometry boundingCircle;
    @Transient @JsonIgnore
    private Geometry convexHull;
    @Transient @JsonIgnore
    private boolean boundingCircleUpdated=false;
    @Transient @JsonIgnore
    private boolean multiPolygonUpdated=false;
    @Transient @JsonIgnore
    private boolean convexHullUpdated=false;
    @Transient @JsonIgnore
    private State state;
    /*
    private String districtAttributeJSON;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> districtAttributes;

    public void serializeDistrictAttributes() throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        this.districtAttributeJSON = objectMapper.writeValueAsString(this.districtAttributes);
    }

    public void deserializeCustomerAttributes() throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        this.districtAttributes = objectMapper.readValue(this.districtAttributeJSON, HashMap.class);
    }
    */

    public Integer getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public Set<Precinct> getPrecincts() {
        return new HashSet<>(precincts.values());
    }

    @Override
    public void removePrecinct(Precinct p) {

    }

    public void addPrecinct(Precinct p) {
        precincts.put(p.getId(), p);
        population += p.getPopulation();

        gopVote += p.calculateGopVotes(this.election);
        demVote += p.calculateDEmVotes(this.election);

        borderPrecincts.add(p);
        Set<Precinct> newInternalNeighbors = getInternalNeighbors(p);
        int newInternalEdges = newInternalNeighbors.size();
        internalEdges += newInternalEdges;
        externalEdges -= newInternalEdges;
        externalEdges += (p.getNeighborIds().size() - newInternalEdges);
        newInternalNeighbors.removeIf(
                this::isBorderPrecinct
        );
        borderPrecincts.removeAll(newInternalNeighbors);

        this.multiPolygonUpdated = false;
        this.convexHullUpdated = false;
        this.boundingCircleUpdated = false;
    }

    private boolean isBorderPrecinct(Precinct precinct) {
        for (Integer neighborID : precinct.getNeighborIds()) {
            //if the neighbor's neighbor is not in the district, then it is outer
            if (!precincts.containsKey(neighborID))  {
                return true;
            }
        }
        return false;
    }

    public Map<DemographicGroup, Integer> demographicGroups(Parameter parameter) {
        if (this.minorityGroupPopulation == null)
            return null;
        Set<DemographicGroup> demographicGroups = parameter.getMinorityPopulations();
        Map<DemographicGroup,Integer> resultMap = new HashMap<>();
        for (DemographicGroup demographicGroup : demographicGroups) {
            Integer demoPopulation = this.minorityGroupPopulation.get(demographicGroup);
            if (demoPopulation == null) {
                resultMap.put(demographicGroup, 0);
            }
            else {
                resultMap.put(demographicGroup, demoPopulation);
            }
        }
        return resultMap;
    }


    @Override
    public Set<Precinct> getBorderPrecincts() {
        return this.borderPrecincts;
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

    public Map<PartyName, Integer> getPartyVotes() {
        return partyVotes;
    }

    public void setPartyVotes(Map<PartyName, Integer> partyVotes) {
        this.partyVotes = partyVotes;
    }

    @JsonIgnore
    public void setPrecincts(Map<Integer, Precinct> precincts) {
        this.precincts = precincts;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public void setBorderPrecincts(Set<Precinct> borderPrecincts) {
        this.borderPrecincts = borderPrecincts;
    }

    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }


    public Set<Precinct> getInternalNeighbors(Precinct p) {
        Set<Precinct> neighborsInternal = new HashSet<>();
        for (Integer nid : p.getNeighborIds()) {
            if (precincts.containsKey(nid)) {
                Precinct neighbor = precincts.get(nid);
                neighborsInternal.add(neighbor);
            }
        }
        return neighborsInternal;
    }

    public Precinct getPrecinct(Integer PrecID) {
        return precincts.get(PrecID);
    }

    public MultiPolygon computeMulti() {
        List<Polygon> polygonList = new ArrayList<>();
        for (Precinct precinct : getPrecincts()) {
            Geometry poly = precinct.getGeometry();
            if (poly == null)
                continue;
            if (poly instanceof Polygon)
                polygonList.add((Polygon) poly);
            else {

                Integer numbers = poly.getNumGeometries();
                for (int i = 0; i < numbers; i++) {
                    Polygon polygon = (Polygon) poly.getGeometryN(i);
                    polygonList.add(polygon);
                }
            }
        }
        Polygon[] polygons = new Polygon[polygonList.size()];
        for (int i = 0; i < polygonList.size(); i++) {
            polygons[i] = polygonList.get(i);
        }
        MultiPolygon mp = new MultiPolygon(polygons,new GeometryFactory());
        this.multiPolygon = mp;
        this.multiPolygonUpdated = true;
        return mp;
    }

    @JsonIgnore
    public MultiPolygon getMulti() {
        if (this.multiPolygonUpdated && this.multiPolygon != null)
            return this.multiPolygon;
        return computeMulti();
    }
    @JsonIgnore
    public Geometry getConvexHull() {
        if (convexHullUpdated && convexHull !=null)
            return convexHull;
        convexHull = multiPolygon.convexHull();
        this.convexHullUpdated = true;
        return convexHull;
    }
    @JsonIgnore
    public Geometry getBoundingCircle() {
        if (boundingCircleUpdated && boundingCircle !=null)
            return boundingCircle;
        boundingCircle = new MinimumBoundingCircle(getMulti()).getCircle();
        this.boundingCircleUpdated = true;
        return boundingCircle;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }
    @JsonIgnore
    public int getGOPVote() {
        return this.gopVote;
    }
    @JsonIgnore
    public int getDEMVote() {
        return this.demVote;
    }

    public int getInternalEdges() {
        return this.internalEdges;
    }

    public int getExternalEdges() {
        return this.externalEdges;
    }

    @Override
    public String toString() {
        return "District{" +
                "minorityGroupPopulation=" + minorityGroupPopulation +
                '}';
    }
}
