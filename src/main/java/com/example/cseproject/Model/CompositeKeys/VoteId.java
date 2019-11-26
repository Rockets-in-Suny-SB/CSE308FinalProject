package com.example.cseproject.Model.CompositeKeys;

import com.example.cseproject.Enum.Election;

import java.io.Serializable;
import java.util.Objects;

public class VoteId implements Serializable {
    private Election election;
    private Integer id;

    public VoteId(){
    }
    public VoteId(Election election, Integer id) {
        this.election = election;
        this.id = id;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }
        if (!(o instanceof VoteId)) {
            return false;
        }
        VoteId voteId = (VoteId) o;
        return this.election == voteId.election &&
                this.id == voteId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(election, id);
    }
}
