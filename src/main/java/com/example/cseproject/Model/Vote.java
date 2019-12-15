package com.example.cseproject.Model;

import com.example.cseproject.Enum.Election;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.Model.CompositeKeys.VoteId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.Map;

@Entity
@IdClass(VoteId.class)
public class Vote {

    @Id
    private Integer id;
    @Id
    private Election election;
    private Integer totalVotes;
    private PartyName winningPartyName;
    private Integer winningVotes;
    private Float winningPartyPercentage;

    @ElementCollection (fetch = FetchType.EAGER)
    @CollectionTable(name = "vote_partyVotes",
            joinColumns = {@JoinColumn(name = "election"),
                    @JoinColumn(name = "vote_id")})
    @MapKeyColumn(name = "partyName")
    @Column(name = "partyVotes")
//    @JsonIgnore
    private Map<PartyName, Integer> partyVotes;

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


    public Map<PartyName, Integer> getPartyVotes() {
        return partyVotes;
    }

    public void setPartyVotes(Map<PartyName, Integer> partyVotes) {
        this.partyVotes = partyVotes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getWinningPartyPercentage() {
        return winningPartyPercentage;
    }

    public void setWinningPartyPercentage(Float winningPartyPercentage) {
        this.winningPartyPercentage = winningPartyPercentage;
    }
}



