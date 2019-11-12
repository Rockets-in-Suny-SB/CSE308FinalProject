package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
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
    private PartyName winningPartyName;
    private Integer winningVotes;

    @OneToMany(targetEntity = Party.class)
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

    public Integer getPrecinct_id() {
        return precinct_id;
    }

    public PartyName getWinningPartyName() {
        return winningPartyName;
    }

    public void setWinningPartyName(PartyName winningPartyName) {
        this.winningPartyName = winningPartyName;
    }

    public Integer getWinningVotes() {
        return winningVotes;
    }

    public void setWinningVotes(Integer winningVotes) {
        this.winningVotes = winningVotes;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
}



