package com.example.cseproject.Model.CompositeKeys;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.StateName;
import com.example.cseproject.Enum.State_Status;

import java.io.Serializable;

public class VoteId implements Serializable {

    private Integer id;
    private Election election;
    public VoteId(){}
    public VoteId(Integer id, Election election) {
        this.id = id;
        this.election = election;
    }
}
