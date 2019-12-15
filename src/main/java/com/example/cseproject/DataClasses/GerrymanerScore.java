package com.example.cseproject.DataClasses;

import com.example.cseproject.Enum.Measure;

public class GerrymanerScore {
    private double efficiencyGap;
    private double gerrymanderDemocrat;
    private double gerrymanderRepublican;

    public GerrymanerScore() {
    }

    public double getEfficiencyGap() {
        return efficiencyGap;
    }

    public void setEfficiencyGap(double efficiencyGap) {
        this.efficiencyGap = efficiencyGap;
    }

    public double getGerrymanderDemocrat() {
        return gerrymanderDemocrat;
    }

    public void setGerrymanderDemocrat(double gerrymanderDemocrat) {
        this.gerrymanderDemocrat = gerrymanderDemocrat;
    }

    public double getGerrymanderRepublican() {
        return gerrymanderRepublican;
    }

    public void setGerrymanderRepublican(double gerrymanderRepublican) {
        this.gerrymanderRepublican = gerrymanderRepublican;
    }
}
