package com.example.cseproject.phase2.algorithm;



import com.example.cseproject.Enum.Measure;
import com.example.cseproject.Model.District;
import com.example.cseproject.Model.State;
import com.example.cseproject.phase2.measures.DefaultMeasures;

import java.util.Map;
import java.util.Set;

public class MyAlgorithm extends AlgorithmPhase2 {

    public MyAlgorithm(State state, DefaultMeasures measures) {
        super(state, measures);
    }

    public String describeDistrict(District d) {
        String to_return = "{ \"ID\": \"" + d.getId() + "\", \"MEASURES\": [";
        boolean first = true;
        Set<Measure> measures = (Set) getDistrictScoreFunction().subMeasures();
        for (Measure m : measures) {
            try {
                if (first) {
                    first = false;
                } else {
                    to_return += ", ";
                }
                double rating = m.calculateMeasure(d);
                to_return += "{ \"MEASURE\": \"" + m.name() + "\", ";
                to_return +=  "\"SCORE\": " + (rating * 100) + " }";
            } catch (Exception e) {
                System.out.println(m.name() + " - " + e.getClass().getCanonicalName() + " - Message:");
                System.out.println(e.getMessage());
                return "ERROR";
            }
        }
        to_return += "] }";
        return to_return;
    }

    public void setWeights(Map<Measure, Double> weights) {
        // TODO -- this should NOT be typecasted !! ! !. We know people will only use this with default measures, but its still a meh casting
        System.out.println(weights);
        ((DefaultMeasures) getDistrictScoreFunction()).updateConstantWeights(weights);
        updateScores();
    }
}
