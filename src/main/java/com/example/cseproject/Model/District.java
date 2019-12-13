package com.example.cseproject.Model;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.interfaces.DistrictInterface;
import org.locationtech.jts.algorithm.MinimumBoundingCircle;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Entity
public class District
    implements DistrictInterface<Precinct> {

    @Id
    @GeneratedValue
    @Column(name = "district_id")
    private Integer id;
    @Transient
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
    @Transient
    private Map<Integer, Precinct> precincts;
    @Transient
    private Election election;



    @Transient
    private static final int MAXX = 0;
    @Transient
    private static final int MAXY = 1;
    @Transient
    private static final int MINX = 2;
    @Transient
    private static final int MINY = 3;
    @Transient
    private int gop_vote;
    @Transient
    private int dem_vote;
    @Transient
    private int internalEdges = 0;
    @Transient
    private int externalEdges = 0;
    @Transient
    private Set<Precinct> borderPrecincts;
    @Transient
    private MultiPolygon multiPolygon;
    @Transient
    private Geometry boundingCircle;
    @Transient
    private Geometry convexHull;
    @Transient
    private boolean boundingCircleUpdated=false;
    @Transient
    private boolean multiPolygonUpdated=false;
    @Transient
    private boolean convexHullUpdated=false;
    @Transient
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
    public Set<Precinct> getPrecincts() {
        return new HashSet<>(precincts.values());
    }

    @Override
    public void removePrecinct(Precinct p) {

    }

    public void addPrecinct(Precinct p) {
        precincts.put(p.getId(), p);
        population += p.getPopulation();
        gop_vote += p.calculateGopVotes(this.election);
        dem_vote += p.calculateDEmVotes(this.election);
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
        Polygon[] polygons = new Polygon[getPrecincts().size()];

        Iterator<Precinct> piter = getPrecincts().iterator();
        for(int ii = 0; ii < polygons.length; ii++) {
            Geometry poly = piter.next().getGeometry();
            if (poly instanceof Polygon)
                polygons[ii] = (Polygon) poly;
            else
                polygons[ii] = (Polygon) poly.convexHull();
        }
        MultiPolygon mp = new MultiPolygon(polygons,new GeometryFactory());
        this.multiPolygon = mp;
        this.multiPolygonUpdated = true;
        return mp;
    }

    public MultiPolygon getMulti() {
        if (this.multiPolygonUpdated && this.multiPolygon != null)
            return this.multiPolygon;
        return computeMulti();
    }

    public Geometry getConvexHull() {
        if (convexHullUpdated && convexHull !=null)
            return convexHull;
        convexHull = multiPolygon.convexHull();
        this.convexHullUpdated = true;
        return convexHull;
    }

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

    public int getGOPVote() {
        return this.gop_vote;
    }

    public int getDEMVote() {
        return this.dem_vote;
    }

    public int getInternalEdges() {
        return this.internalEdges;
    }

    public int getExternalEdges() {
        return this.externalEdges;
    }
}
