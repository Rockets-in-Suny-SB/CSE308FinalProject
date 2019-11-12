package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import java.util.List;
import java.util.Map;

public class Cluster {
    private Integer id;
    private Vote vote;
    private List<Edge> edges;
    private List<Precinct> precincts;
    private List<Cluster> neighbors;
    private Map<DemograpicGroup,Integer> minorityGroupPopulation;
    private Map<String,Integer> countyCount;

    //constructor
    public Cluster(Integer id, Vote vote, List<Edge> edges, List<Precinct> precincts,
                   List<Cluster> neighbors, Map<DemograpicGroup, Integer> minorityGroupPopulation,
                   Map<String, Integer> countyCount) {
        this.id = id;
        this.vote = vote;
        this.edges = edges;
        this.precincts = precincts;
        this.neighbors = neighbors;
        this.minorityGroupPopulation = minorityGroupPopulation;
        this.countyCount = countyCount;
    }

    // Methods need to be implemented
//    private void init(Precinct p){}
//
//    private Pair<Cluster,Cluster> findBestPairBasedOnFactor(Cluster c, JoinFactor j){}

//    private Pair<Cluster,Cluster> findBestMajorityMinorityPair(Cluster c){}

//
//    private float calculateFactorScore(Cluster c, JoinFactor j){}
    private List<Cluster> getNeighbors(){return this.neighbors;}

    private void updateClusterData(Cluster c){}

    private void combine(List<Cluster> intersectingClusters){}

    private List<Edge> getEdges(){return this.edges;}

    private void addEdges(List<Edge> edges){}

//    private Boolean removeEdgeAndCluster(Cluster c){}

//    private Boolean addClusters(List<Cluster> c2OnlyClusters){}


}
