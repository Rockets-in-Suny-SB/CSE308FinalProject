package com.example.cseproject.DataClasses;

import com.example.cseproject.Algorithm.SetLib;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.JoinFactor;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Cluster {
    private Integer id;
    private Vote vote;
    private List<Edge> edges;
    private Set<Cluster> clusters;
    private Set<Cluster> neighbors;
    private int population;
    private Map<DemograpicGroup,Integer> minorityGroupPopulation;
    private Map<String,Integer> countyCount;
    public boolean paired;
    //constructor
    public Cluster(Integer id, Vote vote, List<Edge> edges, Set<Cluster> clusters,
                   Set<Cluster> neighbors, Map<DemograpicGroup, Integer> minorityGroupPopulation,
                   Map<String, Integer> countyCount) {
        this.id = id;
        this.vote = vote;
        this.edges = edges;
        this.clusters = clusters;
        this.neighbors = neighbors;
        this.minorityGroupPopulation = minorityGroupPopulation;
        this.countyCount = countyCount;
        this.paired = false;
    }

    public Set<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(Set<Cluster> clusters) {
        this.clusters = clusters;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
    // Methods need to be implemented
//    private void init(Precinct p){}
//
//    private Pair<Cluster,Cluster> findBestPairBasedOnFactor(Cluster c, JoinFactor j){}

//    private Pair<Cluster,Cluster> findBestMajorityMinorityPair(Cluster c){}
//
//    private float calculateFactorScore(Cluster c, JoinFactor j){}
    public Set<Cluster> getNeighbors(){return this.neighbors;}

    public void updateClusterData(Cluster c){}

    public void combine(Set<Cluster> intersectingClusters, Cluster c2){
        Set<Cluster> c2Clusters=c2.getNeighbors();
        Set<Cluster> c2OnlyClusters= SetLib.setDifference(c2Clusters,intersectingClusters);
        addClusters(c2OnlyClusters);
        Set<Edge> c2OnlyEdges=getEdges(c2Clusters);
        addEdges(c2OnlyEdges);
        removeEdgeAndCluster(c2);
    }



    private void addEdges(List<Edge> edges){}
    public Pair<Cluster,Cluster> findBestMajorityMinorityPair(){
        double bestScore=0;
        Cluster bestNeighbor=null;
        double candidateScore=0;
        for(Cluster n:getNeighbors()){
            candidateScore=n.calculateMajorityMinorityScore(n);
            if(candidateScore>bestScore){
                bestScore=candidateScore;
                bestNeighbor=n;
            }
        }
        Threshold t=new Threshold();

        double threshold=t.getMajorityMinorityThreshold();
        if(bestScore>threshold&&bestNeighbor!=null){
            this.paired=true;
            bestNeighbor.paired=true;
            return Pair.of(this,bestNeighbor);
        }else {
            return null;
        }
    }
    public Pair<Cluster,Cluster> findBestPairBasedOnFactor(JoinFactor factor){
        double bestScore=0;
        Cluster bestNeighbor=null;
        double candidateScore=0;
        for(Cluster n:getNeighbors()){
            candidateScore=n.calculateFactorScore(n,factor);
            if(candidateScore>bestScore){
                bestScore=candidateScore;
                bestNeighbor=n;
            }
        }
        Threshold t=new Threshold();

        double threshold=t.getMajorityMinorityThreshold();
        if(bestScore>threshold&&bestNeighbor!=null){
            this.paired=true;
            bestNeighbor.paired=true;
            return Pair.of(this,bestNeighbor);
        }else {
            return null;
        }
    }

    private List<Edge> getEdges(){
        return this.edges;
    }

    public double calculateFactorScore(Cluster c,JoinFactor factor){

        return 0;
    }
    public double calculateMajorityMinorityScore(Cluster c){

        return 0;
    }

    private void addClusters(Set<Cluster> clusters){

    }
    public Set<Edge> getEdges(Set<Cluster> clusters){
        return null;
    }
    public void addEdges(Set<Edge> edges){

    }
    public void removeEdgeAndCluster(Cluster c){

    }
}
