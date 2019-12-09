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
import com.fasterxml.jackson.core.type.TypeReference;
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

                Set<Integer> precinctIdSet = new HashSet<>();
                Set<Precinct> precincts = c.getPrecincts();
                for (Precinct p : precincts) {
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
        int psizeBefore=0;
        for(Cluster c:clusters.values()){
            psizeBefore+=c.getPrecincts().size();
        }
        System.out.println("R size:"+resultPairs.size());
        System.out.println(("C size:"+clusters.size()));
        System.out.println("P size:"+psizeBefore);
        if (resultPairs.size() > 0) {
            //System.out.println("Has Combine:"+resultPairs);
            combinePairs(resultPairs,clusters);
            resultPairs=new HashSet<>();
        } else {
            System.out.println("Final!");
            isFinalIteration = true;
        }
        System.out.println("R size After:"+resultPairs.size());
        System.out.println(("C size After:"+clusters.size()));
        int psizeAfter=0;
        for(Cluster c:clusters.values()){
            psizeAfter+=c.getPrecincts().size();
        }
        System.out.println("P size After:"+psizeAfter);
        clearPaired(clusters);
        return isFinalIteration;
    }

    public void finalCombineIteration(Map<Integer, Cluster> clusters) {
        //System.out.println("Final iteration");
        PriorityQueue<Cluster> minPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        minPriorityQueue.addAll(clusters.values());
        int targetDistricts = parameter.getTargetDistricts();
        int clusterSize=0;

        while (!minPriorityQueue.isEmpty()&&clusterSize>targetDistricts) {
            clusterSize=0;
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
                System.out.println("Combined");
                targetState.combine(c1, minCluster, clusters);
                minPriorityQueue.add(c1);
                if(minPriorityQueue.remove(minCluster)){
                    //System.out.println(minCluster);
                    System.out.println("Removed min Cluster");
                }
                /*if (clusters.remove(minCluster.getId())!=null){
                    System.out.println("Not removed from clusters set!");
                }*/
            }else {
                System.out.println("Not combined");
                minPriorityQueue.remove(c1);
                //minPriorityQueue.remove(minCluster);
            }
            System.out.println("q size"+minPriorityQueue.size());

        }
        int psizeAfter=0;
        for(Cluster c:clusters.values()){

                psizeAfter += c.getPrecincts().size();

        }
        System.out.println("P size After final:"+psizeAfter);

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
                Pair<Cluster, Cluster> p = c.findBestMajorityMinorityPair(parameter.getTargetMinorityPopulation(),clusters);
                if (p != null) {
                    //System.out.println(parameter.getTargetMinorityPopulation());
                    resultPairs.add(p);
                }
            }
        }
    }

    public void combinePairs(Set<Pair<Cluster, Cluster>> pairs, Map<Integer,Cluster> clusters) {

        for (Pair<Cluster, Cluster> p : pairs) {
            Cluster c1=p.getFirst();
            Cluster c2=p.getSecond();
            //if(c1!=null&&c2!=null) {
                targetState.combine(c1, c2, clusters);
            //}
            //clusters.remove(p.getSecond());
            //clusters.remove(p.getSecond().getId());
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
