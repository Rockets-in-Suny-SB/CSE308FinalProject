package com.example.cseproject.Service;

import com.example.cseproject.Model.Edge;
import com.example.cseproject.Repository.EdgeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EdgeService {
    private EdgeRepository edgeRepository;

    private Iterable<Edge> getAllEdges() {
        return edgeRepository.findAll();
    }

    public Optional<Edge> getEdge(Integer id) {
        return edgeRepository.findById(id);
    }

//    public String addEdge(Integer id, Integer adjacentPrecinct1_id, Integer adjacentPrecinct2_id,
//                          Boolean sameCounty, Float demographicSimilarity, Integer precinct_id){
//        Edge edge = new Edge();
//        edge.setId(id);
//        edge.setDemographicSimilarity(demographicSimilarity);
//        edge.setPrecinct_id(precinct_id);
//        edge.setAdjacentPrecinct1_id(adjacentPrecinct1_id);
//        edge.setAdjacentPrecinct2_id(adjacentPrecinct2_id);
//        edge.setSameCounty(sameCounty);
////
//        edgeRepository.save(edge);
//        return "Saved";
//    }

    public String deleteEdge(Edge edge) {
        edgeRepository.delete(edge);
        return "Deleted";
    }


}
