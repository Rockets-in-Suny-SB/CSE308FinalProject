package com.example.cseproject.Service;

import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.Model.District;
import com.example.cseproject.Repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class DistrictService {
    @Autowired
    private DistrictRepository districtRepository;

    public Iterable<District> getAllDistrict() {
        return districtRepository.findAll();
    }

    public Optional<District> getDistrict(Integer id) {
        return districtRepository.findById(id);
    }

    public String addDistrict(String name, HashMap<PartyName, Integer> partyVotes, Integer population) {
        District district = new District();
        district.setName(name);
        district.setPartyVotes(partyVotes);
        district.setPopulation(population);
        districtRepository.save(district);
        return "saved";
//        Map<String, Object> attritubes = new HashMap<>();
//        attritubes.put("name", name);
//        attritubes.put("population", population);
//        attritubes.put("partyVotes", partyVotes);
//        district.setDistrictAttributes(attritubes);
//        try{
//            district.serializeDistrictAttributes();
//            districtRepository.save(district);
//            return "Saved";
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return "Unsuccessfully save";


    }

    public String deleteDistrict(District district) {
        districtRepository.delete(district);
        return "Deleted";
    }


}
