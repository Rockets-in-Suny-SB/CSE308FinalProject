package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.JoinFactor;
import com.example.cseproject.Model.County;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

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
    //public boolean removed;
    private Set<Precinct> precincts;
    private Set<Integer> neighbors;
    private int population;
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    private Map<String, Integer> countyCount;
    public boolean paired;

    public int getGopVotes() {
        return gopVotes;
    }

    public void setGopVotes(int gopVotes) {
        this.gopVotes = gopVotes;
    }

    public int getDemVotes() {
        return demVotes;
    }

    public void setDemVotes(int demVotes) {
        this.demVotes = demVotes;
    }

    private int gopVotes;
    private int demVotes;
    public Cluster(){}
    //constructor
    public Cluster(Precinct precinct) {
        this.id = precinct.getId();
        precinct.setParentCluster(this.id);
        this.precincts = new HashSet<>();
        this.neighbors = new HashSet<>();
        Set<Edge> edges= precinct.getPrecinctEdges();
        for(Edge e: edges){
            this.neighbors.add(e.getAdjacentPrecinctId());
        }
        this.minorityGroupPopulation = new HashMap<>(precinct.getMinorityGroupPopulation());
        this.countyCount = new HashMap<>();
        this.population = precinct.getPopulation();
        //this.countyCount.put(precinct.getCountyId(),1);
        this.precincts.add(precinct);
        this.paired = false;
        this.demVotes+=precinct.getDemVote();
        this.gopVotes+=precinct.getGopVote();

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

    public Set<Integer> getNeighbors() {
        return this.neighbors;
    }

    public void addClusterData(Cluster c) {
        addAllPopulation(c);
        addAllMinorityPopulation(c);
        addAllVotes(c);
    }
    public void addAllVotes(Cluster c){
        for(Precinct p:c.getPrecincts()){
            demVotes+=p.getDemVote();
            gopVotes+=p.getGopVote();
        }
    }

    public void combine(Cluster c2, Map<Integer, Cluster> clusters) {
        //Combine Precincts
        Set<Precinct> precincts=c2.getPrecincts();
        for (Precinct p : precincts) {
            //if (!this.precincts.contains(p)) {
            p.setParentCluster(this.id);
            this.precincts.add(p);
            //}
        }

    }


    private void addEdges(List<Edge> edges) {
    }

    public Pair<Cluster, Cluster> findBestMajorityMinorityPair(DemographicGroup d, Map<Integer,Cluster> clusters) {
        double bestScore = 0;
        Cluster bestNeighbor = null;
        double candidateScore = 0;
        Set<Integer> neighbors=getNeighbors();
        for (Integer nId : neighbors) {
            Cluster n=clusters.get(nId);
            if(!n.paired) {
                candidateScore = n.calculateMajorityMinorityScore(n, d);

                if (candidateScore > bestScore) {
                    //System.out.println("MM5:"+candidateScore);
                    bestScore = candidateScore;
                    bestNeighbor = n;
                }

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

    public Pair<Cluster, Cluster> findBestPairBasedOnFactor(JoinFactor factor, Map<Integer,Cluster> clusters) {
        double bestScore = 0;
        Cluster bestNeighbor = null;
        double candidateScore = 0;
        for (Integer nId : getNeighbors()) {
            Cluster n=clusters.get(nId);
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




    public double calculateMajorityMinorityScore(Cluster c, DemographicGroup d) {
        //MM Score
        int totalPopulation=c.getPopulation() + this.getPopulation();
        int totalMinorityPopulation=(c.getMinorityGroupPopulation().get(d) + this.getMinorityGroupPopulation().get(d));
        double score= totalPopulation==0?0: totalMinorityPopulation / (totalPopulation*1.0);
        /*double mmScore = totalPopulation==0?0: totalMinorityPopulation / (totalPopulation*1.0);
        //County Score
        Map<Integer,Integer> countyMap=new HashMap<>();
        List<County> thisCountyList=this.getPrecincts().stream().map(precinct -> precinct.getCountyId()).collect(Collectors.toList());
        List<County> mergeCountyList=c.getPrecincts().stream().map(precinct -> precinct.getCountyId()).collect(Collectors.toList());

        for(County county: thisCountyList){
            if(countyMap.containsKey(county.getId())){
                countyMap.put(county.getId(),countyMap.get(county.getId())+1);
            }else{
                countyMap.put(county.getId(),1);
            }
        }
        for(County county: mergeCountyList){
            if(countyMap.containsKey(county.getId())){
                countyMap.put(county.getId(),countyMap.get(county.getId())+1);
            }else{
                countyMap.put(county.getId(),1);
            }
        }
        int largestCounty=0;
        for(Map.Entry<Integer,Integer> e :countyMap.entrySet()){
            int eValue=e.getValue();
            if(eValue>largestCounty){
                largestCounty=eValue;
            }
        }
        double countyScore=largestCounty*1.0/(thisCountyList.size()+mergeCountyList.size());
        //Political Fairness
        int demVote=getDemVotes();
        int gopVote=getGopVotes();
        double ppScore=Math.abs(demVote-gopVote)*1.0/(demVote+gopVote);*/
        //Compactness
        //Equal pop
       return score;
    }

    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

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

}
