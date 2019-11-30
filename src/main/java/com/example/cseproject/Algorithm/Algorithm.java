package com.example.cseproject.Algorithm;

import com.example.cseproject.DataClasses.*;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.JoinFactor;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.State;
import com.example.cseproject.Service.PrecinctService;
import com.example.cseproject.Service.StateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.*;
@Service
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
        State targetState = stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()), State_Status.NEW).get();
        this.targetState = targetState;
        initializeClusters(this.targetState);
    }
    public Result phase1() {
        this.resultPairs = new HashSet<>();
        Map<Integer, Cluster> clusters = targetState.getClusters();

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
        Set<Set<Integer>> resultSet=new HashSet<>();
        for(Cluster c:clusters.values()){
            Set<Integer> precinctIdSet=new HashSet<>();
            Set<Precinct> precincts=c.getPrecincts();
            for(Precinct p:precincts){
                precinctIdSet.add(p.getId());
            }
            resultSet.add(precinctIdSet);
        }
        r.addResult("clusters", resultSet);
        return r;
    }

    public boolean combineIteration(Map<Integer, Cluster> clusters) {
        boolean isFinalIteration = false;
        combineBasedOnMajorityMinority(clusters);
        //combineBasedOnJoinFactor(clusters);
        System.out.println(resultPairs.size());
        if (resultPairs.size() > 0) {
            System.out.println("Has Combine:"+resultPairs);
            combinePairs(resultPairs);
            resultPairs=new HashSet<>();
        } else {

            isFinalIteration = true;
        }
        clearPaired(clusters);
        return isFinalIteration;
    }

    public void finalCombineIteration(Map<Integer, Cluster> clusters) {
        //System.out.println("Final iteration");
        PriorityQueue<Cluster> minPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        minPriorityQueue.addAll(clusters.values());
        int targetDistricts = parameter.getTargetDistricts();
        while (minPriorityQueue.size() > targetDistricts) {
            Cluster c1 = minPriorityQueue.poll();
            Set<Cluster> c1Neighbors=c1.getNeighbors();
            int minPopulation=Integer.MAX_VALUE;
            Cluster minCluster=null;
            for(Cluster n:c1Neighbors){
                if(n.getPopulation()<minPopulation){
                    minCluster=n;
                    minPopulation=n.getPopulation();
                }
            }
            targetState.combine(c1, minCluster);
            minPriorityQueue.add(c1);
            minPriorityQueue.remove(minCluster);
        }
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
    public void combineBasedOnJoinFactor(Set<Cluster> clusters) {
        System.out.println("JF");
        for (JoinFactor j : JoinFactor.values()) {
            for (Cluster c : clusters) {
                if (!c.paired) {
                    Pair<Cluster, Cluster> p = c.findBestPairBasedOnFactor(j);
                    if (p != null) {
                        resultPairs.add(p);
                    }
                }
            }
        }
    }

    public void combineBasedOnMajorityMinority(Map<Integer,Cluster> clusters) {
        //System.out.println("MM");
        for (Cluster c : clusters.values()) {
            //System.out.println("MM2");
            if (!c.paired) {
                //System.out.println("MM3");
                Pair<Cluster, Cluster> p = c.findBestMajorityMinorityPair(parameter.getTargetMinorityPopulation());
                if (p != null) {
                    //System.out.println(parameter.getTargetMinorityPopulation());
                    resultPairs.add(p);
                }
            }
        }
    }

    public void combinePairs(Set<Pair<Cluster, Cluster>> pairs) {
        for (Pair<Cluster, Cluster> p : pairs) {
            targetState.combine(p.getFirst(), p.getSecond());
        }
    }

    public void clearPaired(Map<Integer,Cluster> clusters) {
        for (Cluster c : clusters.values()) {
            c.paired = false;
        }
    }
    public void initializeClusters(State state){
        Map<Integer,Cluster> clusters=state.getClusters();
        Set<Precinct> precincts=state.getPrecincts();
        //System.out.println(precincts);
        for(Precinct p:precincts){
            //System.out.println("hi0");
            Cluster c = new Cluster(p);
            p.setParentCluster(c.getId());
            clusters.put(c.getId(), c);
        }
        for(Cluster c:clusters.values()){
           // System.out.println("hi1");
            c.getPrecincts().forEach((p)->{
               // System.out.println("hi2");
                Set<Cluster> cNeighbor = c.getNeighbors();
                p.getPrecinctEdges().forEach(e->{
                   // System.out.println("hi3");
                    //Add Cluster to neighbor
                    Cluster parent=clusters.get( precinctService.getPrecinct(e.getAdjacentPrecinctId()).get().getParentCluster());
                    if(parent!=null){
                        cNeighbor.add(parent);
                    }else{
                        System.out.println("Error: Parent Cluster is null!");
                    }
                });
            });
        }
        /*try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(ResourceUtils.getFile("classpath:clusters.json"), clusters);
        }catch (Exception e){
            System.out.println(e);
        }*/

    }
//    public void move(Cluster c){}
}
