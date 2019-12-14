package com.example.cseproject.Algorithm;

import com.example.cseproject.DataClasses.*;
import com.example.cseproject.Enum.*;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.State;
import com.example.cseproject.Service.PrecinctService;
import com.example.cseproject.Service.StateService;
import com.example.cseproject.Enum.Measure;
import com.example.cseproject.phase2.Move;
import com.example.cseproject.phase2.algorithm.MyAlgorithm;
import com.example.cseproject.phase2.measures.DefaultMeasures;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Algorithm {
    private Set<Pair<Cluster, Cluster>> resultPairs;
    private JoinFactor joinFactor;
    private Parameter parameter;
    private State targetState;
    private Map<Integer, Cluster> phase1Cluster;
    private Queue<Result> phase2Results;
    private Map<Integer,Set<Integer>> changeMap;
    private boolean isFinalIteration;
    //@Autowired
    //private StateService stateService;
    @Autowired
    private PrecinctService precinctService;
    public void setPhase1(Parameter parameter, StateService stateService){
        this.parameter = parameter;
        //State targetState = stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()), State_Status.NEW).get();
       //this.targetState = targetState;
        this.targetState=new State();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<Integer,Cluster> clusters = mapper.readValue(ResourceUtils.getFile("classpath:cluster5.json"), new TypeReference<>(){});
            this.targetState.setClusters(clusters);
            //System.out.println(clusters);
            System.out.println("Read success");
        }catch (Exception e){
            System.out.println(e);
        }
        isFinalIteration=false;
        //initializeClusters(this.targetState);

    }
    public Result phase1(Parameter parameter) {
        this.resultPairs = new HashSet<>();
        Map<Integer,Cluster> clusters = targetState.getClusters();
        int i=0;
        //boolean isFinalIteration = false;
        if (parameter.getUpdateDiscrete()&&!isFinalIteration) {
            while(i<100) {
                isFinalIteration = combineIteration(clusters);
                i++;
            }
        } else {
            while (clusters.size() > parameter.getTargetDistricts() && !isFinalIteration) {
                isFinalIteration = combineIteration(clusters);
            }
        }
        Result r = new Result();
        r.addResult("isFinal",false);
        if (isFinalIteration&& !parameter.getUpdateDiscrete()) {
            if(clusters.size() > parameter.getTargetDistricts()){
                if(!finalIterationSet){
                    setFinalCombineIteration(clusters);
                }
                while (!((Boolean) r.getResult().get("isFinal")))
                    finalCombineIteration(clusters,r);
            }else{
                r.addResult("isFinal", true);
            }

        }else if(isFinalIteration ){
            if(clusters.size() > parameter.getTargetDistricts()){
                if(!finalIterationSet){
                    setFinalCombineIteration(clusters);
                }
                while (i<100) {
                    finalCombineIteration(clusters, r);
                    i++;
                }
            }else{
                r.addResult("isFinal", true);
            }
        } else{
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
        this.phase1Cluster = clusters;
        r.addResult("clusters", resultSet);
        r.addResult("changeMap",changeMap);
        return r;
    }

    public Queue<Result> phase2(Map<Measure, Double> weights) {
        if (this.phase1Cluster == null) {
            System.out.println("Run phase 1 first");
            return null;
        }
        Map<Integer, Precinct> totalPrecincts = new HashMap<>();
        for (Map.Entry<Integer, Cluster> entry : this.phase1Cluster.entrySet()){
            Cluster cluster = entry.getValue();
            for (Precinct precinct : cluster.getPrecincts()) {
                precinct.setOriginalDistrictID(cluster.getId());
                totalPrecincts.put(precinct.getId(), precinct);

            }
        }
        Map<Integer, District> districts = new HashMap<>();
        for (Map.Entry<Integer, Cluster> entry : this.phase1Cluster.entrySet()) {
            District district = new District();
            Cluster cluster = entry.getValue();
            district.setPopulation(cluster.getPopulation());
            district.setId(cluster.getId());
            Map<Integer, Precinct> precinctMap = new HashMap<>();
            Set<Precinct> precincts = cluster.getPrecincts();
            Set<Precinct> borderPrecincts = new HashSet<>();
            for (Precinct precinct : precincts) {
                //set precinct borderPrecincts
                precinct.calculateNeighborId();
                Set<Integer> neighborIds = precinct.getNeighborIds();
                for (Integer id : neighborIds) {
                    Precinct neighborPrecinct = totalPrecincts.get(id);
                    if(precinct.getParentCluster() != neighborPrecinct.getParentCluster()) {
                        borderPrecincts.add(neighborPrecinct);
                        break;
                    }
                }

                precinctMap.put(precinct.getId(), precinct);
            }
            district.setBorderPrecincts(borderPrecincts);
            district.setPrecincts(precinctMap);
            district.setElection(parameter.getElection());
            district.setState(targetState);
            districts.put(district.getId(), district);
        }
        this.targetState.setPrecinctsJson(totalPrecincts);
        this.targetState.setDistricts(districts);

        MyAlgorithm myAlgorithm = new MyAlgorithm(this.targetState, DefaultMeasures.defaultMeasuresWithWeights(weights));
        Queue<Result> results = new LinkedList<>();
        Move previousMove = null;
        while (true) {
//            if (myAlgorithm.makeMove() == null) {
//                break;
//            }
//            System.out.println("make a move");
            Move move = myAlgorithm.makeMove();
            if (move == null || move.equal(previousMove)) {
                break;
            }
            previousMove = move;
//            System.out.println(move.toString());
            Result result = new Result();
            Set<District> resultDistricts = targetState.getDistricts();
            Map<Integer, Set<Integer>> districtPrecinctMap = new HashMap<>();
            for (District district : resultDistricts) {
                Set<Integer> precinctIDs = new HashSet<>();
                for (Precinct p : district.getPrecincts()) {
                    precinctIDs.add(p.getId());
                }
                districtPrecinctMap.put(district.getId(),precinctIDs);
            }
            result.addResult("districts", districtPrecinctMap);
            results.add(result);
        }
        this.phase2Results = results;
        return results;
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
    int t;
    int originalCS;
    int multiplier;
    int div;
    int sl;
    PriorityQueue<Cluster> minPriorityQueue ;
    PriorityQueue<Cluster> removedPriorityQueue;
    boolean finalIterationSet=false;
    public void setFinalCombineIteration(Map<Integer,Cluster> clusters){
        t=0;
        for(Cluster c:clusters.values()){
            t+=c.getPopulation();
        }
        originalCS=clusters.size();
        multiplier=2;
        div=(clusters.size()/multiplier)==0?1:(clusters.size()/multiplier);
        sl=t/div;
        minPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        removedPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        minPriorityQueue.addAll(clusters.values());
        finalIterationSet=true;
    }
    public void finalCombineIteration(Map<Integer,Cluster> clusters, Result r) {
        int targetDistricts = parameter.getTargetDistricts();
        if (clusters.size()>targetDistricts) {
            Cluster c1 = minPriorityQueue.poll();
            if(c1==null){
                if(removedPriorityQueue==null) {
                    r.addResult("isFinal",true);
                    finalIterationSet=false;
                    return;
                }
                minPriorityQueue.addAll(removedPriorityQueue);
                c1=minPriorityQueue.poll();
                multiplier*=2;
                sl=t/(clusters.size()/multiplier);
                originalCS/=2;
            }
            Set<Integer> c1Neighbors=c1.getNeighbors();
            int minPopulation=Integer.MAX_VALUE;
            Cluster minCluster=null;

            for (Integer nId : c1Neighbors) {

                Cluster n = clusters.get(nId);
                if(n.getPopulation()+c1.getPopulation()<sl) {
                    minCluster = n;
                    break;
                }
            }

            if(minCluster!=null) {
                System.out.println("Combined");
                //targetState.combine(c1, minCluster, clusters);
                resultPairs=new HashSet<>();
                resultPairs.add(Pair.of(c1,minCluster));
                combinePairs(resultPairs,clusters);
                minPriorityQueue.add(c1);
                minPriorityQueue.remove(minCluster);
            }else {

                //minPriorityQueue.add(c1);
                System.out.println("Not combined");
                minPriorityQueue.remove(c1);
                removedPriorityQueue.add(c1);
                //minPriorityQueue.remove(minCluster);
            }
            if(clusters.size()<originalCS/2){
                multiplier*=2;
                int divider=(clusters.size()/multiplier)==0?1:(clusters.size()/multiplier);
                sl=t/divider;
                originalCS/=2;
            }
            r.addResult("isFinal",false);
        }else{
            r.addResult("isFinal",true);
            finalIterationSet=false;
            return;
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
        changeMap=new HashMap<>();
        for (Pair<Cluster, Cluster> p : pairs) {
            Cluster c1=p.getFirst();
            Cluster c2=p.getSecond();
            targetState.combine(c1, c2, clusters);
            removed.add(c2.getId());
            //Add all c2's neighbors to c1
            c1.getNeighbors().addAll(c2.getNeighbors());
            //Remove c1 from neighbors
            c1.getNeighbors().remove(c1.getId());
            //Remove c2 from neighbors and c1
            Set<Integer> c1Neighbors=c1.getNeighbors();
            for(Integer n:c1Neighbors){
                clusters.get(n).getNeighbors().remove(c2.getId());
            }
            clusters.get(c1.getId()).getNeighbors().remove(c2.getId());
            //Add connection between c1 and neighbors
            for(Integer n:c1Neighbors){
                clusters.get(n).getNeighbors().add(c1.getId());
                c1.getNeighbors().add(n);
            }
            //Add to changed map
            if(changeMap.get(c1.getId())==null){
                changeMap.put(c1.getId(),new HashSet<>());
                changeMap.get(c1.getId()).addAll(c2.getPrecincts().stream().map(Precinct::getId).collect(Collectors.toSet()));
            }else{
                changeMap.get(c1.getId()).addAll(c2.getPrecincts().stream().map(Precinct::getId).collect(Collectors.toSet()));
            }

        }
        for(Integer i:removed){
            clusters.remove(i);
        }
        /*for (Pair<Cluster, Cluster> p : pairs) {
            Set<Integer> n=p.getSecond().getNeighbors();
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
        }*/
    }

    public void clearPaired(Map<Integer,Cluster> clusters) {
        for (Cluster c : clusters.values()) {
            c.paired = false;
        }
    }

    public void initPrecincts(State state) {
        Set<Precinct> precincts = state.getPrecincts();
        Map<Integer, Precinct> precinctMap = new HashMap<>();
        for (Precinct precinct : precincts) {
            precinctMap.put(precinct.getId(), precinct);
        }
        try {
            File file = new File(getClass().getClassLoader().getResource(".").getFile() + "/precincts.json");
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, precinctMap);
            System.out.println("Write Success.");
        }catch (Exception e){
            System.out.println("Write failed.");
            System.out.println(e);
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
            File file = new File(getClass().getClassLoader().getResource(".").getFile() + "/cluster5.json");
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

    public State getTargetState() {
        return targetState;
    }

    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    public Queue<Result> getPhase2Results() {
        return phase2Results;
    }

    public void setPhase2Results(Queue<Result> phase2Results) {
        this.phase2Results = phase2Results;
    }

    //    public void move(Cluster c){}
}
