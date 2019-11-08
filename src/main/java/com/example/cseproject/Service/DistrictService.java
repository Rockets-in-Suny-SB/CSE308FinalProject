package com.example.cseproject.Service;

import com.example.cseproject.Model.District;
import com.example.cseproject.Model.Precinct;
import com.example.cseproject.Repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DistrictService {
    @Autowired
    private DistrictRepository districtRepository;

    public Iterable<District> getAllDistrict(){return districtRepository.findAll();}

    public Optional<District> getDistrict(Integer id){ return districtRepository.findById(id);}

    public String addDistrict(Integer id, String color, String name, List<Precinct> precincts){
        District district = new District();
        district.setId(id);
        district.setColor(color);
        district.setName(name);
        district.setPrecincts(precincts);
        districtRepository.save(district);
        return "Saved";
    }

    public String deleteDistrict(District district){
        districtRepository.delete(district);
        return "Deleted";
    }


}
