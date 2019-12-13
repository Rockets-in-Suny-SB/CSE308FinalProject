package com.example.cseproject.DataClasses;



import java.util.Set;

public class PopulationDistribution {
   
    private String stateName;
    private String status ;
    private Float maximumPercentage;
    private Float minimumPercentage;
    private Set<String> minorityPopulations;
    private Boolean isCombined;
    private Set<Set<String>> combinedGroup;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getMaximumPercentage() {
        return maximumPercentage;
    }

    public void setMaximumPercentage(Float maximumPercentage) {
        this.maximumPercentage = maximumPercentage;
    }

    public Float getMinimumPercentage() {
        return minimumPercentage;
    }

    public void setMinimumPercentage(Float minimumPercentage) {
        this.minimumPercentage = minimumPercentage;
    }

    public Set<String> getMinorityPopulations() {
        return minorityPopulations;
    }

    public void setMinorityPopulations(Set<String> minorityPopulations) {
        this.minorityPopulations = minorityPopulations;
    }

    public Boolean getIsCombined() {
        return isCombined;
    }

    public void setIsCombined(Boolean combined) {
        isCombined = combined;
    }

    public Set<Set<String>> getCombinedGroup() {
        return combinedGroup;
    }

    public void setCombinedGroup(Set<Set<String>> combinedGroup) {
        this.combinedGroup = combinedGroup;
    }

    @Override
    public String toString() {
        return "PopulationDistribution{" +
                "stateName='" + stateName + '\'' +
                ", status='" + status + '\'' +
                ", maximumPercentage=" + maximumPercentage +
                ", minimumPercentage=" + minimumPercentage +
                ", minorityPopulations=" + minorityPopulations +
                ", isCombined=" + isCombined +
                ", combinedGroup=" + combinedGroup +
                '}';
    }
}
