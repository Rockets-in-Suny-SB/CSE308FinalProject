package com.example.cseproject.Algorithm;

import com.example.cseproject.DataClasses.Cluster;
import com.example.cseproject.DataClasses.Parameter;
import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.DataClasses.Threshold;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.JoinFactor;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.State;
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
    public Result phase0(Threshold threshold){
        State targetState=stateService.getState(StateName.valueOf(this.parameter.getStateName().toUpperCase()),
                State_Status.NEW, this.parameter.getElection()).get();
        Set<Set<Object>> eligibleBlocs = targetState.findEligibleBlocs();
        return null;
    }
    public Result phase1(Parameter parameter){

        this.parameter=parameter;
        State targetState=stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()), State_Status.NEW,parameter.getElection()).get();
        this.targetState=targetState;
        this.resultPairs=new HashSet<>();
        Set<Cluster> clusters=targetState.getClusters();

        boolean isFinalIteration=false;
        if(parameter.getUpdateDiscrete()){
            isFinalIteration=combineIteration(clusters);
        }else{
            while(clusters.size()>parameter.getTargetDistricts()&&!isFinalIteration) {
                isFinalIteration=combineIteration(clusters);
            }
        }
        if(isFinalIteration&&clusters.size()>parameter.getTargetDistricts()){
            finalCombineIteration(clusters);
        }
        //Return result
        return null;
    }
    public boolean combineIteration(Set<Cluster> clusters){
        boolean isFinalIteration=false;
        combineBasedOnMajorityMinority(clusters);
        combineBasedOnJoinFactor(clusters);
        if(resultPairs.size()>0) {
            combinePairs(resultPairs);
            resultPairs.removeAll(resultPairs);
        }else{
            isFinalIteration=true;
        }
        clearPaired(clusters);
        return isFinalIteration;
    }
    public void finalCombineIteration(Set<Cluster> clusters){
        PriorityQueue<Cluster> minPriorityQueue=new PriorityQueue<>((o1, o2) -> -(o1.getPopulation()-o2.getPopulation()));
        int targetDistricts=parameter.getTargetDistricts();
        while(minPriorityQueue.size()>targetDistricts){
            Cluster c1=minPriorityQueue.poll();
            Cluster c2=minPriorityQueue.poll();
            targetState.combine(c1,c2);
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
      public void combineBasedOnJoinFactor(Set<Cluster> clusters){
          for(JoinFactor j:JoinFactor.values()) {
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
      public void combineBasedOnMajorityMinority(Set<Cluster> clusters){
          for(Cluster c:clusters){
              if(!c.paired) {
                  Pair<Cluster, Cluster> p = c.findBestMajorityMinorityPair(parameter.getTargetMinorityPopulation());
                  if (p != null) {
                      resultPairs.add(p);
                  }
              }
          }
      }
      public void combinePairs(Set<Pair<Cluster,Cluster>> pairs){
          for(Pair<Cluster,Cluster> p:pairs){
              targetState.combine(p.getFirst(),p.getSecond());
          }
      }
      public void clearPaired(Set<Cluster> clusters){
        for (Cluster c:clusters){
            c.paired=false;
        }
      }
//    public void move(Cluster c){}
}