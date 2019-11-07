package com.example.cseproject.Model;

import com.example.cseproject.Enum.StateName;

import javax.persistence.*;
import java.util.List;

@Entity
public class State {
    @Id
    @Enumerated(EnumType.STRING)
    private StateName name;

    @OneToMany
    private List<District> districts;



    public StateName getName() {
        return name;
    }

    public void setName(StateName name) {
        this.name = name;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }
}
