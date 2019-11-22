package com.example.cseproject.DataClasses;

import com.example.cseproject.Algorithm.SetLib;
import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.JoinFactor;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import org.springframework.data.util.Pair;

import java.util.*;

public class Cluster {
    private Integer id;
    //private Vote vote;
    //private List<Edge> edges;
    private Set<Precinct> precincts;
    private Set<Cluster> neighbors;
    private int population;
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    private Map<String, Integer> countyCount;
    public boolean paired;

    //constructor
    public Cluster(Precinct precinct) {
        this.id = precinct.getId();
        this.precincts = new HashSet<>();
        this.neighbors = new HashSet<>();
        this.minorityGroupPopulation = precinct.getMinorityGroupPopulation();
        this.countyCount = new HashMap<>();
        this.population = precinct.getPopulation();
        //this.countyCount.put(precinct.getCountyId(),1);
        this.precincts.add(precinct);
        this.paired = false;
    }

    public Set<Precinct> getPrecincts() {
        return precincts;
    }

    public void setPrecincts(Set<Precinct> precincts) {
        this.precincts = precincts;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Set<Cluster> getNeighbors() {
        return this.neighbors;
    }

    public void addClusterData(Cluster c) {
        addAllPopulation(c);
        addAllMinorityPopulation(c);
    }

    public void combine(Cluster c2) {
        //Combine Precincts
        for (Precinct p : c2.getPrecincts()) {
            if (!precincts.contains(p)) {
                precincts.add(p);
            }
        }
        //Combine Neighbors
        for (Cluster n : c2.getNeighbors()) {
            if (!neighbors.contains(n) && n != this) {
                neighbors.add(n);
            }
        }
    }


    private void addEdges(List<Edge> edges) {
    }

    public Pair<Cluster, Cluster> findBestMajorityMinorityPair(DemographicGroup d) {
        double bestScore = 0;
        Cluster bestNeighbor = null;
        double candidateScore = 0;
        for (Cluster n : getNeighbors()) {
            candidateScore = n.calculateMajorityMinorityScore(n, d);
            if (candidateScore > bestScore) {
                bestScore = candidateScore;
                bestNeighbor = n;
            }
        }
        Threshold t = new Threshold();

        double threshold = t.getMajorityMinorityThreshold();
        if (bestScore > threshold && bestNeighbor != null) {
            this.paired = true;
            bestNeighbor.paired = true;
            return Pair.of(this, bestNeighbor);
        } else {
            return null;
        }
    }

    public Pair<Cluster, Cluster> findBestPairBasedOnFactor(JoinFactor factor) {
        double bestScore = 0;
        Cluster bestNeighbor = null;
        double candidateScore = 0;
        for (Cluster n : getNeighbors()) {
            candidateScore = n.calculateFactorScore(n, factor);
            if (candidateScore > bestScore) {
                bestScore = candidateScore;
                bestNeighbor = n;
            }
        }
        Threshold t = new Threshold();

        double threshold = t.getMajorityMinorityThreshold();
        if (bestScore > threshold && bestNeighbor != null) {
            this.paired = true;
            bestNeighbor.paired = true;
            return Pair.of(this, bestNeighbor);
        } else {
            return null;
        }
    }

   /* private List<Edge> getEdges(){
        return this.edges;
    }*/


    public double calculateMajorityMinorityScore(Cluster c, DemographicGroup d) {
        double score = (c.getMinorityGroupPopulation().get(d) + this.getMinorityGroupPopulation().get(d))
                / (c.getPopulation() + this.getPopulation());
        return new Random().nextDouble();
    }

    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    /*private void addClusters(Set<Cluster> clusters){
        for(Cluster c:clusters){
            this.clusters.add(c);
        }
    }*/

    public void addAllPopulation(Cluster c) {
        this.population += c.population;
    }

    public void addAllMinorityPopulation(Cluster c) {
        for (Precinct p : c.getPrecincts()) {
            for (DemographicGroup k : this.minorityGroupPopulation.keySet()) {
                this.minorityGroupPopulation.put(k,
                        p.getMinorityGroupPopulation().get(k)
                                + this.minorityGroupPopulation.get(k));
            }
        }
    }

    ;

    public double calculateFactorScore(Cluster c, JoinFactor factor) {
        //Todo:Calculate the combine score based on factor
        return new Random().nextDouble();
    }
    /*public Set<Edge> getEdges(Set<Cluster> clusters){
        return null;
    }*/
}
