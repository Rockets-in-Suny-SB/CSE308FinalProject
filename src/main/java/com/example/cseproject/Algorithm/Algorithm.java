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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

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
        State targetState = stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()), State_Status.NEW).get();
        this.targetState = targetState;
        initializeClusters(this.targetState);
    }
    public Result phase1(Parameter parameter) {
        this.resultPairs = new HashSet<>();
        Set<Cluster> clusters = targetState.getClusters();

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
        r.addResult("clusters", clusters);
        return r;
    }

    public boolean combineIteration(Set<Cluster> clusters) {
        boolean isFinalIteration = false;
        combineBasedOnMajorityMinority(clusters);
        combineBasedOnJoinFactor(clusters);
        if (resultPairs.size() > 0) {
            combinePairs(resultPairs);
            resultPairs.removeAll(resultPairs);
        } else {
            isFinalIteration = true;
        }
        clearPaired(clusters);
        return isFinalIteration;
    }

    public void finalCombineIteration(Set<Cluster> clusters) {
        PriorityQueue<Cluster> minPriorityQueue = new PriorityQueue<>((o1, o2) -> -(o1.getPopulation() - o2.getPopulation()));
        minPriorityQueue.addAll(clusters);
        int targetDistricts = parameter.getTargetDistricts();
        while (minPriorityQueue.size() > targetDistricts) {
            Cluster c1 = minPriorityQueue.poll();
            Cluster c2 = minPriorityQueue.poll();
            targetState.combine(c1, c2);
            minPriorityQueue.add(c1);
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

    public void combineBasedOnMajorityMinority(Set<Cluster> clusters) {
        for (Cluster c : clusters) {
            if (!c.paired) {
                Pair<Cluster, Cluster> p = c.findBestMajorityMinorityPair(parameter.getTargetMinorityPopulation());
                if (p != null) {
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

    public void clearPaired(Set<Cluster> clusters) {
        for (Cluster c : clusters) {
            c.paired = false;
        }
    }
    public void initializeClusters(State state){
        Set<Cluster> clusters=state.getClusters();
        for(Precinct p:state.getPrecincts()){
            Cluster c = new Cluster(p);
            p.setParentCluster(c);
            clusters.add(c);
        }
        for(Cluster c:clusters){
            c.getPrecincts().forEach((p)->{
                Set<Cluster> cNeighbor = c.getNeighbors();
                p.getPrecinctEdges().forEach(e->{
                    //Add Cluster to neighbor
                    Cluster parent=precinctService.getPrecinct(e.getAdjacentPrecinctId()).get().getParentCluster();
                    if(parent!=null){
                        cNeighbor.add(parent);
                    }else{
                        System.out.println("Error: Parent Cluster is null!");
                    }
                });
            });
        }
    }
//    public void move(Cluster c){}
}
