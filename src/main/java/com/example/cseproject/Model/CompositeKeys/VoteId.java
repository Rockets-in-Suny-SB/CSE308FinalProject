package com.example.cseproject.Model.CompositeKeys;

import com.example.cseproject.Enum.Election;

import java.io.Serializable;

public class VoteId implements Serializable {

    private Election election;
    private Integer precinct_id;

    public VoteId(Election election, Integer precinct_id) {
        this.election = election;
        this.precinct_id = precinct_id;
    }

    public Election getElection() {
        return election;
    }

    public Integer getPrecinct_id() {
        return precinct_id;
    }
}
