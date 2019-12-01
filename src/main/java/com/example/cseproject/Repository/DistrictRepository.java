package com.example.cseproject.Repository;

import com.example.cseproject.Model.District;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends CrudRepository<District, Integer> {
}
