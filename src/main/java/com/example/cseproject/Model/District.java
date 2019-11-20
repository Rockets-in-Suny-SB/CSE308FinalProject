package com.example.cseproject.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class District {
    @Id
    @GeneratedValue
    private Integer id;
    private String color;
    private String name;
    private Integer population;
    private String geoJson;
    @OneToMany
    private List<Precinct> precincts;


    public String getGeoJson(){return geoJson;}
    public void setGeoJson(String geoJson){this.geoJson=geoJson;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Precinct> getPrecincts() {
        return precincts;
    }

    public void setPrecincts(List<Precinct> precincts) {
        this.precincts = precincts;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }
}
