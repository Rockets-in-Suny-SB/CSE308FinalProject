package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.PartyName;

public class EligibleBloc {
    private String precinctName;
    private DemographicGroup demographicGroup;
    private Integer population;
    private Integer winningVotes;
    private Integer totalVotes;
    private String winningParty;
    private Float percentage;

    public EligibleBloc() {
    }

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public DemographicGroup getDemographicGroup() {
        return demographicGroup;
    }

    public void setDemographicGroup(DemographicGroup demographicGroup) {
        this.demographicGroup = demographicGroup;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getWinningVotes() {
        return winningVotes;
    }

    public void setWinningVotes(Integer winningVotes) {
        this.winningVotes = winningVotes;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public String getWinningParty() {
        return winningParty;
    }

    public void setWinningParty(String winningParty) {
        this.winningParty = winningParty;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }
}
