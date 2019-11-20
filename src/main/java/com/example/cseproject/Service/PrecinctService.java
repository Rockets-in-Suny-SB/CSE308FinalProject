package com.example.cseproject.Service;

import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import com.example.cseproject.Repository.PrecinctRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class PrecinctService {
    private PrecinctRepository precinctRepository;
    private Iterable<Precinct> getAllPrecinct(){ return precinctRepository.findAll(); }

    public Optional<Precinct> getPrecinct(Integer id){return precinctRepository.findById(id);}

    public String addPrecinct(Integer id, String name, Integer population, String party,
                              Integer districtId, Integer countyId,Vote vote,
                              List<Edge> edges, Map<DemograpicGroup,Integer> demographicGroups,
                              Map<DemograpicGroup, Integer> minorityGroupPopulation, Map<Integer, Float> countyAreas){
        Precinct precinct = new Precinct();
        precinct.setId(id);
        precinct.setName(name);
        precinct.setPopulation(population);
        precinct.setParty(party);
        precinct.setDistrictId(districtId);
        precinct.setCountyId(countyId);
        precinct.setVote(vote);
        precinct.setEdges(edges);
        precinct.setDemographicGroups(demographicGroups);
        precinct.setMinorityGroupPopulation(minorityGroupPopulation);
        precinct.setCountyAreas(countyAreas);
        precinctRepository.save(precinct);
        return "Saved";
    }

    public String deletePrecinct(Precinct precinct){
        precinctRepository.delete(precinct);
        return "Deleted";
    }
}
