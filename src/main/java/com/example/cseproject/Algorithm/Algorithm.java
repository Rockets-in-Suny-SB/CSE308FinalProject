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

import java.util.List;

public class Algorithm {
    private List<Pair<Cluster, Cluster>> resultPairs;
    private JoinFactor joinFactor;
    private Parameter parameter;
    @Autowired
    private StateService stateService;
    public Result phase0(Threshold threshold){
        State targetState=stateService.getState(StateName.valueOf(this.parameter.getStateName().toUpperCase()),
                State_Status.NEW, this.parameter.getElection()).get();
        List<List<Object>> eligibleBlocs = targetState.findEligibleBlocs();
        return null;
    }
    public Result phase1(Parameter parameter){
        State targetState=stateService.getState(StateName.valueOf(parameter.getStateName().toUpperCase()),
                State_Status.NEW, parameter.getElection()).get();
        targetState.initializeClusters();
        List<Cluster> clusters=targetState.getClusters();
        //compare cluster with it's neighbor and determine if it should be combine
        //combine pair of clusters
        return null;
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
//
//    public Cluster combine(Cluster c){}
//
//    public void move(Cluster c){}
}
