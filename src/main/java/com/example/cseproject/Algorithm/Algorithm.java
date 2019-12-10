package com.example.cseproject.Algorithm;

import com.example.cseproject.DataClasses.*;
import com.example.cseproject.Enum.*;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.State;
import com.example.cseproject.Service.PrecinctService;
import com.example.cseproject.Service.StateService;
import com.example.cseproject.Enum.Measure;
import com.example.cseproject.phase2.algorithm.MyAlgorithm;
import com.example.cseproject.phase2.measures.DefaultMeasures;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.*;

public class Algorithm {
    private Set<Pair<Cluster, Cluster>> resultPairs;
    private JoinFactor joinFactor;
    private Parameter parameter;
    private State targetState;
    @Autowired
    private StateService stateService;
    @Autowired
    private PrecinctService precinctService;
    public void setPhase1(Parameter parameter){
        this.parameter = parameter;
        //System.out.println(stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()), State_Status.NEW));
        //State targetState = stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()), State_Status.NEW).get();
        //this.targetState = targetState;
        this.targetState=new State();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<Integer,Cluster> clusters = mapper.readValue(ResourceUtils.getFile("classpath:cluster2.json"), new TypeReference<>(){});
            this.targetState.setClusters(clusters);
            //System.out.println(clusters);
            System.out.println("Read success");
        }catch (Exception e){
            System.out.println(e);
        }

        //initializeClusters(this.targetState);
    }
    public Result phase1(Parameter parameter) {
        this.resultPairs = new HashSet<>();
        Map<Integer,Cluster> clusters = targetState.getClusters();

        boolean isFinalIteration = false;
        if (parameter.getUpdateDiscrete()) {
            isFinalIteration = combineIteration(clusters);
        } else {
            while (clusters.size() > parameter.getTargetDistricts() && !isFinalIteration) {
                isFinalIteration = combineIteration(clusters);
            }
        }
        Result r = new Result();
        if (isFinalIteration) {
            if(clusters.size() > parameter.getTargetDistricts()){
                finalCombineIteration(clusters);
            }
            r.addResult("isFinal", true);
        }else{
            r.addResult("isFinal", false);
        }
        //Return result
        Map<Integer,Set<Integer>> resultSet=new HashMap<>();
        for(Cluster c:clusters.values()){

            Set<Integer> precinctIdSet = new HashSet<>();
            Set<Precinct> precincts = c.getPrecincts();
            for (Precinct p : precincts) {
                precinctIdSet.add(p.getId());
            }
            resultSet.put(c.getId(),precinctIdSet);

        }
        r.addResult("clusters", resultSet);
        return r;
    }

    public Result phase2(Map<Measure, Double> weights) {
        MyAlgorithm myAlgorithm = new MyAlgorithm(this.targetState, DefaultMeasures.defaultMeasuresWithWeights(weights));
        while (true) {
            if (myAlgorithm.makeMove() == null) {
                break;
            }
        }
        Result result = new Result();
        Set<District> districts = targetState.getDistricts();
        result.addResult("districts", districts);
        return  result;
    }



    public boolean combineIteration(Map<Integer,Cluster> clusters) {
        boolean isFinalIteration = false;
        combineBasedOnMajorityMinority(clusters);
        //combineBasedOnJoinFactor(clusters);
        if (resultPairs.size() > 0) {
            combinePairs(resultPairs,clusters);
            resultPairs.removeAll(resultPairs);
        } else {
            isFinalIteration = true;
        }
        clearPaired(clusters);
        return isFinalIteration;
    }

    public void finalCombineIteration(Map<Integer,Cluster> clusters) {
        //System.out.println("Final iteration");
        PriorityQueue<Cluster> minPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        minPriorityQueue.addAll(clusters.values());
        int targetDistricts = parameter.getTargetDistricts();

        while (!minPriorityQueue.isEmpty()&&clusters.size()>targetDistricts) {
            Cluster c1 = minPriorityQueue.poll();
            Set<Integer> c1Neighbors=c1.getNeighbors();
            int minPopulation=Integer.MAX_VALUE;
            Cluster minCluster=null;

            for (Integer nId : c1Neighbors) {
                Cluster n = clusters.get(nId);

                if (n.getPopulation() < minPopulation) {
                    minCluster = n;
                    minPopulation = n.getPopulation();
                }


            }

            if(minCluster!=null) {
                //System.out.println("Combined");
                //targetState.combine(c1, minCluster, clusters);
                resultPairs.add(Pair.of(c1,minCluster));
                combinePairs(resultPairs,clusters);
                resultPairs=new HashSet<>();
                minPriorityQueue.add(c1);
                if(minPriorityQueue.remove(minCluster)){
                    //System.out.println(minCluster);
                    //System.out.println("Removed min Cluster");
                }
                /*if (clusters.remove(minCluster.getId())!=null){
                    System.out.println("Not removed from clusters set!");
                }*/
            }else {
                //System.out.println("Not combined");
                minPriorityQueue.remove(c1);
                //minPriorityQueue.remove(minCluster);
            }
            //System.out.println("q size"+minPriorityQueue.size());

        }
        int psizeAfter=0;
        for(Cluster c:clusters.values()){

            psizeAfter += c.getPrecincts().size();

        }
        //System.out.println("P size After final:"+psizeAfter);
    }


    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }


    //

    //    public Result phase2(Parameter parameter){}
//
//    public Cluster findPair(Cluster c){}
    public void combineBasedOnJoinFactor(Map<Integer,Cluster> clusters) {
        System.out.println("JF");
        for (JoinFactor j : JoinFactor.values()) {
            for (Cluster c : clusters.values()) {
                if (!c.paired) {
                    Pair<Cluster, Cluster> p = c.findBestPairBasedOnFactor(j,clusters);
                    if (p != null) {
                        resultPairs.add(p);
                    }
                }
            }
        }
    }

    public void combineBasedOnMajorityMinority(Map<Integer,Cluster> clusters) {
        for (Cluster c : clusters.values()) {
            if (!c.paired) {
                Pair<Cluster, Cluster> p = c.findBestMajorityMinorityPair(parameter.getTargetMinorityPopulation(),clusters);
                if (p != null) {
                    resultPairs.add(p);
                }
            }
        }
    }

    public void combinePairs(Set<Pair<Cluster, Cluster>> pairs, Map<Integer,Cluster> clusters) {
        Set<Integer> removed=new HashSet<>();
        for (Pair<Cluster, Cluster> p : pairs) {
            Cluster c1=p.getFirst();
            Cluster c2=p.getSecond();
            targetState.combine(c1, c2, clusters);
            removed.add(c2.getId());
        }
        for(Integer i:removed){
            clusters.remove(i);
        }
        for (Pair<Cluster, Cluster> p : pairs) {
            Set<Integer> n=p.getSecond().getNeighbors();
            if(clusters.get(p.getFirst().getId())==null){
                System.out.println("First is null!");
            }
            p.getFirst().getNeighbors().addAll(n);
            Set<Integer> removedI=new HashSet<>();
            for(Integer i:p.getFirst().getNeighbors()){
                if(clusters.get(i)==null){
                    removedI.add(i);
                }
            }
            p.getFirst().getNeighbors().removeAll(removedI);
            for(Integer i:p.getFirst().getNeighbors()){
                clusters.get(i).getNeighbors().add(p.getFirst().getId());
                p.getFirst().getNeighbors().add(i);
                clusters.get(i).getNeighbors().remove(p.getSecond().getId());
            }
        }
        for(Cluster c:clusters.values()){
            c.getNeighbors().remove(c.getId());
        }
    }

    public void clearPaired(Map<Integer,Cluster> clusters) {
        for (Cluster c : clusters.values()) {
            c.paired = false;
        }
    }
    public void initializeClusters(State state){
        state.setClusters(new HashMap<>());
        Map<Integer,Cluster> clusters=state.getClusters();
        Set<Precinct> precincts=state.getPrecincts();
        //System.out.println(precincts);
        for(Precinct p:precincts){
            //System.out.println("hi0");
            Cluster c = new Cluster(p);
            clusters.put(c.getId(), c);
        }
        /*for(Cluster c:clusters.values()){
           // System.out.println("hi1");
            c.getPrecincts().forEach((p)->{
               // System.out.println("hi2");
                Set<Integer> cNeighbor = c.getNeighbors();
                p.getPrecinctEdges().forEach(e->{
                   // System.out.println("hi3");
                    //Add Cluster to neighbor
                    Cluster parent=clusters.get( precinctService.getPrecinct(e.getAdjacentPrecinctId()).get().getParentCluster());
                    if(p.getId()==e.getAdjacentPrecinctId()){
                        System.out.println("Data Problem");
                    }
                    if(parent!=null){
                        cNeighbor.add(parent.getId());
                    }else{
                        System.out.println("Error: Parent Cluster is null!");
                    }
                });
            });
        }*/
        try {
            File file = new File(getClass().getClassLoader().getResource(".").getFile() + "/cluster2.json");
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, clusters);
            System.out.println("Write Success.");
        }catch (Exception e){
            System.out.println("Write failed.");
            System.out.println(e);
        }

    }
//    public void move(Cluster c){}
}
