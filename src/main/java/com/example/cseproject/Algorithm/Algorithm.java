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
@Service
public class Algorithm {
    private Set<Pair<Cluster, Cluster>> resultPairs;
    private JoinFactor joinFactor;
    private Parameter parameter;
    private State targetState;
    private Map<Integer, Cluster> phase1Cluster;
    private Queue<Result> phase2Results;

    @Autowired
    private StateService stateService;
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

        //initializeClusters(this.targetState);

    }
    public Result phase1(Parameter parameter) {
        this.resultPairs = new HashSet<>();
        Map<Integer,Cluster> clusters = targetState.getClusters();

        boolean isFinalIteration = true;
        if (parameter.getUpdateDiscrete()&&!isFinalIteration) {
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
        this.phase1Cluster = clusters;
        r.addResult("clusters", resultSet);
        /*Set<Set<Integer>> resultSet=new HashSet<>();
        for(Cluster c:clusters.values()){

            Set<Integer> precinctIdSet = new HashSet<>();
            Set<Precinct> precincts = c.getPrecincts();
            for (Precinct p : precincts) {
                precinctIdSet.add(p.getId());
            }
            resultSet.add(precinctIdSet);

        }
        r.addResult("clusters", resultSet);*/
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
            district.setMinorityGroupPopulation(cluster.getMinorityGroupPopulation());
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

    public void finalCombineIteration(Map<Integer,Cluster> clusters) {
        //System.out.println("Final iteration");
        PriorityQueue<Cluster> minPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        PriorityQueue<Cluster> removedPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Cluster::getPopulation));
        minPriorityQueue.addAll(clusters.values());
        int targetDistricts = parameter.getTargetDistricts();
        int t=0;
        for(Cluster c:clusters.values()){
            t+=c.getPopulation();
        }
        int originalCS=clusters.size();
        int multiplier=2;
        int div=(clusters.size()/multiplier)==0?1:(clusters.size()/multiplier);
        int sl=t/div;
        while (/*!minPriorityQueue.isEmpty()&&!removedPriorityQueue.isEmpty()&&*/clusters.size()>targetDistricts) {
            Cluster c1 = minPriorityQueue.poll();
            if(c1==null){
                if(removedPriorityQueue==null)
                    break;
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
                /*if (n.getPopulation() +c1.getPopulation()< minPopulation) {
                    minCluster = n;
                    minPopulation = n.getPopulation();
                }*/
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
                if(minPriorityQueue.remove(minCluster)){
                    //System.out.println(minCluster);
                    //System.out.println("Removed min Cluster");
                }
                /*if (clusters.remove(minCluster.getId())!=null){
                    System.out.println("Not removed from clusters set!");
                }*/
                //minPriorityQueue.addAll(removedPriorityQueue);
                //removedPriorityQueue.removeAll(removedPriorityQueue);
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
            //System.out.println("q size"+minPriorityQueue.size());
            System.out.println("Cluster size:"+ clusters.size());
            System.out.println("sl:"+sl);
        }
        /*int psizeAfter=0;
        for(Cluster c:clusters.values()){

            psizeAfter += c.getPrecincts().size();

        }*/

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

    public Result gerrymanderingScore() {
        Set<District> districts = this.targetState.getDistricts();
        Result result = new Result();
        for (District district : districts){
            double efficiencyGap = Measure.EFFICIENCY_GAP.calculateMeasure(district);
            double gerrymanderDemocrat = Measure.GERRYMANDER_DEMOCRAT.calculateMeasure(district);
            double gerrymanderRepublican = Measure.GERRYMANDER_REPUBLICAN.calculateMeasure(district);
            GerrymanerScore gerrymanerScore = new GerrymanerScore();
            gerrymanerScore.setEfficiencyGap(efficiencyGap);
            gerrymanerScore.setGerrymanderDemocrat(gerrymanderDemocrat);
            gerrymanerScore.setGerrymanderRepublican(gerrymanderRepublican);
            result.addResult(district.getId().toString(), gerrymanerScore);
        }
        return result;
    }

    public Result displayNewPopulationDistribution() {
        Result result = new Result();
        if (phase2Results == null) {
            result.addResult("status", "Have not run phase2");
            return result;
        }
        this.parameter = new Parameter();
        Set<DemographicGroup> d = new HashSet<>();
        d.add(DemographicGroup.WHITE);
        parameter.setMinorityPopulations(d);
        parameter.setStateName("OHIO");
        Map<Integer, District> oldDistrictsMap = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("here");
            System.out.println(parameter.getStateName());
            oldDistrictsMap = mapper.readValue(ResourceUtils.getFile(
                    "classpath:"+parameter.getStateName() +"_Districts.json"), new TypeReference<>(){});
            System.out.println("Read success");
        }catch (Exception e){
            System.out.println(e);
        }
        for(Map.Entry<Integer, District> entry : targetState.getDistrictMap().entrySet()) {
            Integer districtId = entry.getKey();
            District oldDistrict;
            Map<DemographicGroup,Integer> oldDistrictData;
            while (true) {
                oldDistrict = (District) this.getRandomObject(oldDistrictsMap.values());
                oldDistrictData= oldDistrict.demographicGroups(this.parameter);
                if (oldDistrictData != null) {
                    break;
                }
            }
            Integer oldDistrictPopulation = oldDistrict.getPopulation();
            District newDistrict = entry.getValue();
            Map<DemographicGroup,Integer> newDistrictData = newDistrict.demographicGroups(this.parameter);
            Integer newDistrictPopulation = newDistrict.getPopulation();
            Map<DemographicGroup, DistrictComparison> selectedDemoComparision = new HashMap<>();
            for (Map.Entry<DemographicGroup, Integer> demEntry : newDistrictData.entrySet()) {
                DemographicGroup demographicGroup = demEntry.getKey();
                Integer newPopulation = demEntry.getValue();
                Integer oldPopulation = oldDistrictData.get(demographicGroup);
                if (oldPopulation == null)
                    oldPopulation = 0;
                Float oldPercentage = (float) oldPopulation / oldDistrictPopulation;
                Float newPercentage = (float) newPopulation / newDistrictPopulation;
                Boolean increaseMoreThanTenPercent = false;
                if ((float) (newPopulation - oldPopulation)/oldDistrictPopulation > 0.1) {
                    increaseMoreThanTenPercent = true;
                }
                DistrictComparison districtComparison = new DistrictComparison(oldPopulation,newPopulation,
                                                    oldPercentage,newPercentage, increaseMoreThanTenPercent);
                selectedDemoComparision.put(demographicGroup,districtComparison);
            }
            DemoDistrictComparision demoDistrictComparision = new DemoDistrictComparision(districtId, selectedDemoComparision);
            result.addResult( districtId.toString(), demoDistrictComparision);
        }
        return result;

    }
    public void initDistrict(State state) {
        Map<Integer, District> districtMap = state.getDistrictMap();
        System.out.println(state.getName());
        try {
            File file = new File(getClass().getClassLoader().getResource(".").getFile() + "/"
                                                    + state.getName().toString()+"_Districts" + ".json");
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, districtMap);
            System.out.println("Write Success.");
        }catch (Exception e){
            System.out.println("Write failed.");
            System.out.println(e);
        }

    }



    public void initPrecincts(State state) {
        Set<Precinct> precincts = state.getPrecincts();
        Map<Integer, Precinct> precinctMap = new HashMap<>();
        for (Precinct precinct : precincts) {
            precinctMap.put(precinct.getId(), precinct);
        }
        try {
            File file = new File(getClass().getClassLoader().getResource(".").getFile() + "/"
                                                                    + state.getName().toString() + "_precincts.json");
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

    private Object getRandomObject(Collection from) {
        Random rnd = new Random();
        int i = rnd.nextInt(from.size());
        return from.toArray()[i];
    }
    //    public void move(Cluster c){}
}
