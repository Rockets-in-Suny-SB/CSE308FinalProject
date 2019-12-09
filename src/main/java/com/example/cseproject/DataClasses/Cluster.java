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
    //public boolean removed;
    private Set<Precinct> precincts;
    private Set<Integer> neighbors;
    private int population;
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    private Map<String, Integer> countyCount;
    public boolean paired;
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
    }

    public void combine(Cluster c2, Map<Integer, Cluster> clusters) {
        //Combine Precincts
        Set<Precinct> precincts=c2.getPrecincts();
        for (Precinct p : precincts) {
            if (!this.precincts.contains(p)) {
                p.setParentCluster(this.id);
                this.precincts.add(p);
            }
        }
        //Combine Neighbors
        Set<Integer> c2Neighbors=c2.getNeighbors();
        Set<Integer> c2OnlyNeighbors=new HashSet<>();
        for (Integer nId : c2Neighbors) {
            if (!this.neighbors.contains(nId) && nId != this.id && nId !=c2.getId()) {
                c2OnlyNeighbors.add(nId);
            }
        }
        this.neighbors.addAll(c2OnlyNeighbors);

        for(Integer i:c2OnlyNeighbors){
            if(clusters.containsKey(i)) {
                clusters.get(i).getNeighbors().add(this.getId());
            }
        }
        for(Integer i:c2.getNeighbors()){
            if(clusters.containsKey(i)&&clusters.get(i)!=c2) {
                clusters.get(i).getNeighbors().remove(c2.getId());
            }
        }
        clusters.remove(c2.getId());
        /*clusters.get(c2.getId()).getNeighbors().forEach(neighbor->{
            if(clusters.get(neighbor)!=null&&clusters.get(neighbor).getNeighbors()!=c2.getNeighbors())
                clusters.get(neighbor).getNeighbors().add(this.getId());
        });*/
        //clusters.get(c2.getId()).getNeighbors().add(this.getId());
        //clusters.remove(c2.getId());
       /* Set<Integer> removeAfter=new HashSet<>();
        for(Integer c2N:c2Neighbors){
            Cluster c2NC=clusters.get(c2N);
            if(c2NC!=null&&c2Neighbors!= c2NC.getNeighbors()) {
                if (c2NC != null) {
                    c2NC.getNeighbors().remove(c2.getId());
                } else {
                    System.out.println("C2N is null remove! Id:" + c2N + " c2Id:" + c2.getId());
                }
            }else{
                removeAfter.add(c2N);
            }
        }
        for(Integer c2N:removeAfter){
            Cluster c2NC=clusters.get(c2N);
            if(c2NC!=null&&c2Neighbors!= c2NC.getNeighbors()) {
                if (c2NC != null) {
                    c2NC.getNeighbors().remove(c2.getId());
                } else {
                    System.out.println("C2N is null remove2! Id:" + c2N + " c2Id:" + c2.getId());
                }
            }else{
                removeAfter.add(c2N);
            }
        }
        Set<Integer> addAfter=new HashSet<>();
        for(Integer c2N:c2Neighbors){
            Cluster c2NC=clusters.get(c2N);
            if(c2NC!=null&&c2Neighbors!= c2NC.getNeighbors()) {
                if(c2NC!=null&&c2Neighbors!= c2NC.getNeighbors()) {
                    c2NC.getNeighbors().add(this.getId());

                }else{
                    System.out.println("C2N is null add! Id:"+c2N+" c2Id:"+c2.getId());
                }
            }else {
                addAfter.add(c2N);
            }
        }
        for(Integer c2N:addAfter){
            Cluster c2NC=clusters.get(c2N);
            if(c2NC!=null&&c2Neighbors!= c2NC.getNeighbors()) {
                if(c2NC!=null&&c2Neighbors!= c2NC.getNeighbors()) {
                    c2NC.getNeighbors().add(this.getId());

                }else{
                    System.out.println("C2N is null add2! Id:"+c2N+" c2Id:"+c2.getId());
                }
            }else {
                addAfter.add(c2N);
            }
        }
        this.neighbors.addAll(c2OnlyNeighbors);
        this.neighbors.remove(c2);
        clusters.remove(c2.getId());*/
        /*neighbors.remove(this.getId());
        for(Integer nId : neighbors){
            Cluster n=clusters.get(nId);
            if(n!=null) {
                Set<Integer> nNeighbors = n.getNeighbors();

                if (nNeighbors!=neighbors&&this.getNeighbors().contains(c2.getId())) {
                    //nNeighbors.remove(c2.getId());
                    n.removeNeighbor(c2.getId());
                    System.out.println("This Id:" + nId + "Removed:" + c2.getId());
                    System.out.println("Contains:" + nNeighbors.contains(c2.getId()));
                    n.getNeighbors().add(this.id);
                }
            }else{
                System.out.println("No neighbor! ThisId"+this.id+"ID:"+nId);
            }
        }*/
        /*if(this.neighbors.contains(c2.getId())){
            this.neighbors.remove(c2.getId());
            System.out.println("contains c2.getid");
        }*/

        //clusters.remove(c2.getId());
        /*try {
            Set<Integer> neighbors2=c2.getNeighbors();
            Set<Integer> neighborsToRemove=new HashSet<>();
            for(Integer nId : neighbors2){
                //Remove c2 from c2's neighbors
                Cluster n=clusters.get(nId);
                if(n!=this&&n!=c2) {
                    n.getNeighbors().remove(c2.getId());
                    //neighborsToRemove.add(c2.getId());
                }
            }

            //Remove this neighbors
            if(this.getNeighbors().remove(c2.getId())){
                System.out.println("Not removed from neighbors loop!");
            }
        }catch (Exception e1){
            System.out.println("First failed:"+e1);

            try {
                boolean itSelf=false;
                Set<Integer> neighbors3=c2.getNeighbors();
                for(Integer nId : neighbors3){
                    //Remove c2 from c2's neighbors
                    Cluster n=clusters.get(nId);
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
        }*/

        //neighbors.remove(c2);
    }


    private void addEdges(List<Edge> edges) {
    }

    public Pair<Cluster, Cluster> findBestMajorityMinorityPair(DemographicGroup d, Map<Integer,Cluster> clusters) {
        double bestScore = 0;
        Cluster bestNeighbor = null;
        double candidateScore = 0;
        Set<Integer> neighbors=getNeighbors();
        //System.out.println("MM4:"+neighbors.size());
        Set<Integer> unremovedNeighbors=new HashSet<>();
        for (Integer nId : neighbors) {
            Cluster n=clusters.get(nId);
             if(!n.paired) {
                candidateScore = n.calculateMajorityMinorityScore(n, d);
                //System.out.println(candidateScore);
                if (candidateScore > bestScore) {
                    //System.out.println("MM5:"+candidateScore);
                    bestScore = candidateScore;
                    bestNeighbor = n;
                }
             }

        }
        //neighbors.removeAll(unremovedNeighbors);
        Threshold t = new Threshold();

        double threshold = t.getMajorityMinorityThreshold();
        //System.out.println("Th:"+threshold);
        if (bestScore > threshold && bestNeighbor != null) {
           //System.out.println("CombineScore:"+bestScore);

            this.paired = true;
            bestNeighbor.paired = true;
            return Pair.of(this, bestNeighbor);
        } else {
            return null;
        }
    }

    public Pair<Cluster, Cluster> findBestPairBasedOnFactor(JoinFactor factor) {
       /* double bestScore = 0;
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
        }*/
       return null;
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
    public boolean removeNeighbor(Integer id){
        return this.neighbors.remove(id);
    }
    /*public Set<Edge> getEdges(Set<Cluster> clusters){
        return null;
    }*/
}
