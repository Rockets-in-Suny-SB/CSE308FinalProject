package com.example.cseproject.Service;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Model.Edge;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Model.Vote;
import com.example.cseproject.Repository.PrecinctRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.Map;
import java.util.Optional;
@Service
public class PrecinctService {
    private PrecinctRepository precinctRepository;
    private Iterable<Precinct> getAllPrecinct(){ return precinctRepository.findAll(); }

    public Optional<Precinct> getPrecinct(Integer id){return precinctRepository.findById(id);}


    public String deletePrecinct(Precinct precinct){
        precinctRepository.delete(precinct);
        return "Deleted";
    }
}
