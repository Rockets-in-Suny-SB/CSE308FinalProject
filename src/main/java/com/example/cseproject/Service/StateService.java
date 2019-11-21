package com.example.cseproject.Service;

import com.example.cseproject.DataClasses.DistrictData;
import com.example.cseproject.DataClasses.Result;
import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.State;
import com.example.cseproject.Model.CompositeKeys.StateId;
import com.example.cseproject.Repository.DistrictRepository;
import com.example.cseproject.Repository.StateRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.Optional;

@Service
public class StateService {
    @Autowired
    private StateRepository stateRepository;

    private Iterable<State> getAllState(){ return stateRepository.findAll(); }

    public Optional<State> getState(StateName name, State_Status status, Election election){
        StateId id = new StateId(name, status, election);
        return stateRepository.findById(id);}

    public String addState(StateName name, State_Status status, Set<District> districts){
        State  state = new State();
        state.setDistricts(districts);
        state.setName(name);
        state.setStatus(status);
        stateRepository.save(state);
        return "Saved";
    }

    public String deleteState(State state){
        stateRepository.delete(state);
        return "Deleted";
    }
    public Result getDistrictsData(String state, String year) {
        State targetState=getState(StateName.valueOf(state), State_Status.OLD,Election.valueOf(year)).get();
        Result districtDataList=new Result();
        for (District d : targetState.getDistricts()) {
            districtDataList.addResult(d.getId().toString(),new DistrictData(d));
        }
        return districtDataList;
    }
}
