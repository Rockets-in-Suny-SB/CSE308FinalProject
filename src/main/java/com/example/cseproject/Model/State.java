package com.example.cseproject.Model;

import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;
import com.example.cseproject.Model.CompositeKeys.StateId;

import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(StateId.class)
public class State {
    @Id
    private StateName name;
    @Id
    private State_Status status;

    @OneToMany
    private List<District> districts;


    public StateName getName() {
        return name;
    }

    public void setName(StateName name) {
        this.name = name;
    }

    public State_Status getStatus() {
        return status;
    }

    public void setStatus(State_Status status) {
        this.status = status;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
}


