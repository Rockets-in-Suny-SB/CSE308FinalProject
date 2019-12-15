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
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import static com.example.cseproject.Enum.StateName.ILLINOIS;

@Service
public class Algorithm {
    private ArrayList<Pair<Cluster, Cluster>> resultPairs;
    private JoinFactor joinFactor;
    private Parameter parameter;
    private State targetState;
    private Map<Integer, Cluster> phase1Cluster;
    private Queue<Result> phase2Results;

    //private Map<Integer,Set<Integer>> changeMap;
    private boolean isFinalIteration;
    private int realTargetSize;
    private Result majorityMinorityResult;
    private Result r;
    public  double phase1Time;
    public double phase2Time;
    private final int OHTotalPopulation=11306259;
    private final int ORTotalPopulation=3837339;
    private final int ILtotalPopulation=12768497;
    //@Autowired
    //private StateService stateService;


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
            Map<Integer,Cluster> clusters;
            switch (StateName.valueOf(parameter.getStateName().toUpperCase())){
                case ILLINOIS:
                    clusters= mapper.readValue(ResourceUtils.getFile("classpath:ILLINOIS_clusters.json"), new TypeReference<>(){});
                    this.targetState.setPopulation(ILtotalPopulation);
                    break;
                case OHIO:
                    clusters= mapper.readValue(ResourceUtils.getFile("classpath:OHIO_clusters.json"), new TypeReference<>(){});
                    this.targetState.setPopulation(OHTotalPopulation);
                    break;
                case OREGON:
                    clusters= mapper.readValue(ResourceUtils.getFile("classpath:OREGON_clusters.json"), new TypeReference<>(){});
                    this.targetState.setPopulation(ORTotalPopulation);
                    break;
                default:
                    System.out.println("State Not Specified!");
                    clusters= mapper.readValue(ResourceUtils.getFile("classpath:OREGON_clusters.json"), new TypeReference<>(){});
            }
            this.targetState.setClusters(clusters);
            //System.out.println(clusters);
            System.out.println("Read success");
        }catch (Exception e){
            System.out.println(e);
        }

        //Other attr.
        isFinalIteration=false;
        realTargetSize=(int)((targetState.getClusters().size()/parameter.getTargetDistricts())*0.75);
        r=new Result();
        r.addResult("isFinal",false);
        //initializeClusters(this.targetState);

    }
    public Result phase1(Parameter parameter) {
        //Timer start
        long startTime = System.nanoTime();
        this.resultPairs = new ArrayList<>();
        Map<Integer,Cluster> clusters = targetState.getClusters();
        int i=0;
        //boolean isFinalIteration = false;
        if (parameter.getUpdateDiscrete()&&!isFinalIteration&&!((Boolean) r.getResult().get("isFinal"))) {
            while(i<50) {
                isFinalIteration = combineIteration(clusters);
                i++;
            }
        } else if (!((Boolean) r.getResult().get("isFinal"))){
            while (clusters.size() > parameter.getTargetDistricts() && !isFinalIteration) {
                isFinalIteration = combineIteration(clusters);
            }
        }

        if (isFinalIteration&& !parameter.getUpdateDiscrete()&&!((Boolean) r.getResult().get("isFinal"))) {
            if(clusters.size() > parameter.getTargetDistricts()){
                if(!finalIterationSet){
                    setFinalCombineIteration(clusters);
                }
                while (!((Boolean) r.getResult().get("isFinal")))
                    finalCombineIteration(clusters,r);
            }else{
                r.addResult("isFinal", true);
            }

        }else if(isFinalIteration&&!((Boolean) r.getResult().get("isFinal")) ){
            if(clusters.size() > parameter.getTargetDistricts()){
                if(!finalIterationSet){
                    setFinalCombineIteration(clusters);
                }
                while (i<50) {
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
        /*int count=0;
        for(Cluster c:clusters.values()){
            if(c.getPrecincts().size()>realTargetSize){
                count++;
            }
        }
        if(count>=parameter.getTargetDistricts()){
            r.addResult("isFinal",true);
        }*/
        //Timer End
        long endTime = System.nanoTime();
        long elapsedTime = endTime-startTime;
        double seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        phase1Time=seconds;
        //Log time
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler((getClass().getClassLoader().getResource(".").getFile() + "/log/MyLogFile.log").replaceFirst("/",""));
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("Phase 1 time:"+seconds);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    public Queue<Result> phase2(Map<Measure, Double> weights) {
        //Timer start
        long startTime = System.nanoTime();
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
        //Timer End
        long endTime = System.nanoTime();
        long elapsedTime = endTime-startTime;
        double seconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        phase2Time=seconds;
        //Log time
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler((getClass().getClassLoader().getResource(".").getFile() + "/log/MyLogFile.log").replaceFirst("/",""));
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("Phase 2 time:"+seconds);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
    //Must call phase 2 before calling this method;
    public Result calculateMajorityMinorityDistrictData(StateService stateService){
        Set<District> newDistrict=targetState.getDistricts();
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
        Set<District> oldDistrict=oldDistrictsMap.values().stream().collect(Collectors.toSet());
        ArrayList<DistrictData> oldDistrictData=new ArrayList<>();
        ArrayList<DistrictData> newDistrictData=new ArrayList<>();
        for(District d:oldDistrict){
            oldDistrictData.add(new DistrictData(d));
        }
        for(District d:newDistrict){
            newDistrictData.add(new DistrictData(d));
        }
        oldDistrictData.sort(((o1, o2) -> {
            Random random=new Random();
            double o1mmp=random.nextDouble(),o2mmp=random.nextDouble();
            if(o1.getMinorityGroupPopulation()!=null)
                o1mmp=o1.getMinorityGroupPopulation().get(parameter.getTargetMinorityPopulation())*1.0/o1.getPopulation();
            if(o2.getMinorityGroupPopulation()!=null)
                o2mmp=o2.getMinorityGroupPopulation().get(parameter.getTargetMinorityPopulation())*1.0/o2.getPopulation();
            if(o1mmp<o2mmp){
                return 1;
            }else {
                return -1;
            }
        }));
        newDistrictData.sort(((o1, o2) -> {
            Random random=new Random();
            double o1mmp=random.nextDouble(),o2mmp=random.nextDouble();
            //if(o1.getMinorityGroupPopulation()!=null)
                o1mmp=o1.getMinorityGroupPopulation().get(parameter.getTargetMinorityPopulation())*1.0/o1.getPopulation();
            //if(o2.getMinorityGroupPopulation()!=null)
                o2mmp=o2.getMinorityGroupPopulation().get(parameter.getTargetMinorityPopulation())*1.0/o2.getPopulation();
            if(o1mmp<o2mmp){
                return 1;
            }else {
                return -1;
            }
        }));
        Result r=new Result();
        r.addResult("oldDistrictData",oldDistrictData);
        r.addResult("newDistrictData",newDistrictData);
        return r;
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
                resultPairs=new ArrayList<>();
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
                Pair<Cluster, Cluster> p = c.findBestMajorityMinorityPair(parameter.getTargetMinorityPopulation(),clusters,this.targetState.getPopulation());
                if (p != null) {
                    resultPairs.add(p);
                }
            }
        }
    }

    public void combinePairs(ArrayList<Pair<Cluster, Cluster>> pairs, Map<Integer,Cluster> clusters) {
        Set<Integer> removed=new HashSet<>();
        //changeMap=new HashMap<>();
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
            File file = new File(getClass().getClassLoader().getResource(".").getFile() + "/OREGON_clusters.json");
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
