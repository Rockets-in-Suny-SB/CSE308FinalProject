package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;

import javax.persistence.*;
import java.util.List;

@Entity
public class Votes {
    @Id
    @JoinColumn(name = "election")
    @Enumerated
    private Election election;
    private Integer totalVotes;
    private Integer winningPartyId;
    private Integer precinct_id;

    @OneToMany
    @JoinColumn(name = "election", referencedColumnName = "election")
    private List<Party> parties;


    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Integer getWinningPartyId() {
        return winningPartyId;
    }

    public void setWinningPartyId(Integer winningPartyId) {
        this.winningPartyId = winningPartyId;
    }

    public Integer getPrecinct_id() {
        return precinct_id;
    }

    public void setPrecinct_id(Integer precinct_id) {
        this.precinct_id = precinct_id;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
}



