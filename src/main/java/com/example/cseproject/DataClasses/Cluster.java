package com.example.cseproject.DataClasses;

import com.example.cseproject.Algorithm.SetLib;
import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.JoinFactor;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import org.springframework.data.util.Pair;

import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    public Set<Cluster> getNeighbors(){return this.neighbors;}

    public void updateClusterData(Cluster c){
        addAllPopulation(c);
        addAllMinorityPopulation(c);
    }

    public void combine(Set<Cluster> intersectingClusters, Cluster c2){
        Set<Cluster> c2Clusters=c2.getClusters();
        Set<Cluster> c2OnlyClusters= SetLib.setDifference(c2Clusters,intersectingClusters);
        addClusters(c2OnlyClusters);
    }



    private void addEdges(List<Edge> edges){}
    public Pair<Cluster,Cluster> findBestMajorityMinorityPair(DemograpicGroup d){
        double bestScore=0;
        Cluster bestNeighbor=null;
        double candidateScore=0;
        for(Cluster n:getNeighbors()){
            candidateScore=n.calculateMajorityMinorityScore(n,d);
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


    public double calculateMajorityMinorityScore(Cluster c,DemograpicGroup d){
        double score= (c.getMinorityGroupPopulation().get(d)+this.getMinorityGroupPopulation().get(d))
                /(c.getPopulation()+this.getPopulation());
        return new Random().nextDouble();
    }

    public Map<DemograpicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemograpicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    private void addClusters(Set<Cluster> clusters){
        for(Cluster c:clusters){
            this.clusters.add(c);
        }
    }

    public void addAllPopulation(Cluster c){
        for(Cluster innerCluster:c.getClusters()){
            this.population+=innerCluster.population;
        }
    }
    public void addAllMinorityPopulation(Cluster c){
        for(Cluster innerCluster:c.getClusters()){
            for(DemograpicGroup k:this.minorityGroupPopulation.keySet()){
                this.minorityGroupPopulation.put(k,
                        innerCluster.minorityGroupPopulation.get(k)
                        + this.minorityGroupPopulation.get(k));
            }
        }
    };
    public double calculateFactorScore(Cluster c,JoinFactor factor){
        //Todo:Calculate the combine score based on factor
        return new Random().nextDouble();
    }
    public Set<Edge> getEdges(Set<Cluster> clusters){
        return null;
    }
}
