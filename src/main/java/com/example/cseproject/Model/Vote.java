package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Model.CompositeKeys.VoteId;

import javax.persistence.*;
import java.util.List;

@Entity
public class Vote {
    @Id
    @Column(name = "id")
    private VoteId id;
    private Integer totalVotes;
    private Integer winningPartyId;

    @OneToMany
    @JoinColumn(name = "id", referencedColumnName = "election")
    private List<Party> parties;


    public Election getElection() {
        return id.getElection();
    }

    public VoteId getId() {
        return id;
    }

    public void setId(VoteId id) {
        this.id = id;
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
        return id.getPrecinct_id();
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
}



