package com.example.cseproject.Service;

import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Repository.PrecinctRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrecinctService {
    @Autowired
    private PrecinctRepository precinctRepository;

    private Iterable<Precinct> getAllPrecinct() {
        return precinctRepository.findAll();
    }

    public Optional<Precinct> getPrecinct(Integer id) {
        return precinctRepository.findById(id);
    }


    public String deletePrecinct(Precinct precinct) {
        precinctRepository.delete(precinct);
        return "Deleted";
    }
}
