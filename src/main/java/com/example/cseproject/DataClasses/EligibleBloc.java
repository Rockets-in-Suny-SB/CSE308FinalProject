package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.DemographicGroup;

public class EligibleBloc {
    private String precinctName;
    private String demographicGroup;
    private Integer population;
    private Integer winningVotes;
    private Integer totalVotes;
    private String winningParty;
    private Float percentage;
    private Boolean isEligible;

    public EligibleBloc() {
    }

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public String getDemographicGroup() {
        return demographicGroup;
    }

    public void setDemographicGroup(String demographicGroup) {
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

    public Boolean getEligible() {
        return isEligible;
    }

    public void setEligible(Boolean eligible) {
        isEligible = eligible;
    }

}
