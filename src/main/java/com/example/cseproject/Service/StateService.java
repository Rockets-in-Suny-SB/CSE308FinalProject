package com.example.cseproject.Service;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.State;
import com.example.cseproject.Model.CompositeKeys.StateId;
import com.example.cseproject.Repository.DistrictRepository;
import com.example.cseproject.Repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StateService {
    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private DistrictRepository districtRepository;

    private Iterable<State> getAllState(){ return stateRepository.findAll(); }

    public Optional<State> getState(StateName name, State_Status status, Election election){
        StateId id = new StateId(name, status, election);
        return stateRepository.findById(id);}

    public String addState(StateName name, State_Status status, List<District> districts){
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

}
