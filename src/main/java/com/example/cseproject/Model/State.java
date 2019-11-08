package com.example.cseproject.Model;

import com.example.cseproject.Model.CompositeKeys.StateId;

import javax.persistence.*;
import java.util.List;

@Entity
public class State {
    @Id
    private StateId id;

    @OneToMany
    private List<District> districts;


    public StateId getId() {
        return id;
    }

    public void setId(StateId id) {
        this.id = id;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
}


