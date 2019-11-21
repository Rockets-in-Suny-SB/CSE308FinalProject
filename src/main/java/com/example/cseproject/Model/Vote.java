package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.Model.CompositeKeys.VoteId;

import javax.persistence.*;
import java.util.Set;

@Entity
@IdClass(VoteId.class)
public class Vote {

    private Integer id;

    private Election election;

    private Integer totalVotes;

    private PartyName winningPartyName;

    private Integer winningVotes;

    private Set<Party> parties;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
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

    @OneToMany(targetEntity = Party.class)
    public Set<Party> getParties() {
        return parties;
    }

    public void setParties(Set<Party> parties) {
        this.parties = parties;
    }
}



