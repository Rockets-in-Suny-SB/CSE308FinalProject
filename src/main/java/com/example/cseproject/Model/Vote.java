package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;

import javax.persistence.*;
import java.util.List;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer totalVotes;
    private PartyName winningPartyName;
    private Integer winningVotes;

    @OneToMany(targetEntity = Party.class)
    private List<Party> parties;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }
}



