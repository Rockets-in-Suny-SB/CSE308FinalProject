package com.example.cseproject.Model.CompositeKeys;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;

import java.io.Serializable;

public class StateId implements Serializable {

    private StateName name;
    private State_Status status;
    private Election election;

    public StateId(StateName name, State_Status status, Election election) {
        this.name = name;
        this.status = status;
        this.election = election;
    }
}
