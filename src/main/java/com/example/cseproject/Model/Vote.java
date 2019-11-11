package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Model.CompositeKeys.VoteId;

import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(VoteId.class)
public class Vote {
    @Id
    private Election election;
    @Id
    private Integer precinct_id;
    private Integer totalVotes;
    private Integer winningPartyId;

    @OneToMany
    private List<Party> parties;

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public void setPrecinct_id(Integer precinct_id) {
        this.precinct_id = precinct_id;
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

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
}



