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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
        this.minorityGroupPopulation = new HashMap<>(precinct.getMinorityGroupPopulation());
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
        Set<Precinct> precincts=c2.getPrecincts();
        for (Precinct p : precincts) {
            if (!precincts.contains(p)) {
                p.setParentCluster(this.id);
                precincts.add(p);
            }
        }
        //Combine Neighbors
        Set<Cluster> neighbors=c2.getNeighbors();
        for (Cluster n : neighbors) {
            if (!neighbors.contains(n) && n != this) {
                neighbors.add(n);
            }

        }
        try {
            neighbors=c2.getNeighbors();
            for(Cluster n : neighbors){
                //Remove c2 from c2's neighbors
                n.getNeighbors().remove(c2);
            }
        }catch (Exception e1){
            System.out.println("First failed:"+e1);
            try {
                boolean itSelf=false;
                for(Cluster n : neighbors){
                    //Remove c2 from c2's neighbors
                    if(n.getNeighbors()!=neighbors) {
                        n.getNeighbors().remove(c2);
                    }else{
                        System.out.println("Error: C2 neighbors contains itself!!");
                        itSelf=true;
                    }
                }
                if(itSelf){
                    System.out.println("Error: C2 neighbors contains itself!!2");
                    c2.getNeighbors().remove(c2);
                }
            }catch (Exception e2){
                System.out.println("Second failed also:"+e2);
            }
        }

        //neighbors.remove(c2);
    }


    private void addEdges(List<Edge> edges) {
    }

    public Pair<Cluster, Cluster> findBestMajorityMinorityPair(DemographicGroup d) {
        double bestScore = 0;
        Cluster bestNeighbor = null;
        double candidateScore = 0;
        Set<Cluster> neighbors=getNeighbors();
        //System.out.println("MM4:"+neighbors.size());
        for (Cluster n : neighbors) {
            if(!n.paired) {
                candidateScore = n.calculateMajorityMinorityScore(n, d);
                //System.out.println(candidateScore);
                if (candidateScore > bestScore) {
                    System.out.println("MM5:"+candidateScore);
                    bestScore = candidateScore;
                    bestNeighbor = n;
                }
            }
        }
        Threshold t = new Threshold();

        double threshold = t.getMajorityMinorityThreshold();
        //System.out.println("Th:"+threshold);
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
            if(!n.paired) {
                candidateScore = n.calculateFactorScore(n, factor);
                if (candidateScore > bestScore) {
                    bestScore = candidateScore;
                    bestNeighbor = n;
                }
            }
        }
        Threshold t = new Threshold();

        double threshold = t.getMajorityMinorityThreshold();
        if (bestScore >= threshold && bestNeighbor != null) {
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
        int totalPopulation=c.getPopulation() + this.getPopulation();
        int totalMinorityPopulation=(c.getMinorityGroupPopulation().get(d) + this.getMinorityGroupPopulation().get(d));
        //System.out.println("TT:"+totalPopulation);
        //System.out.println(("TM:")+totalMinorityPopulation);
        double score = totalPopulation==0?0: totalMinorityPopulation / (totalPopulation*1.0);
        //System.out.println("MM:"+score);
        return score;
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



    public double calculateFactorScore(Cluster c, JoinFactor factor) {
        //Todo:Calculate the combine score based on factor
        double score=0;
        switch (factor){
            case COMPACTNESS:
                score=0;
                break;
            case SINGLECOUNTY:
                score=0.25;
                break;
            case EQUALPOPULATION:
                score=0.5;
                break;
            case POLITICALFAIRNESS:
                score=0.75;
                break;
        }
        return score;
    }
    /*public Set<Edge> getEdges(Set<Cluster> clusters){
        return null;
    }*/
}
