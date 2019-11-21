package com.example.cseproject.Model;

import com.example.cseproject.Enum.DemograpicGroup;
import com.example.cseproject.Enum.PartyName;
import com.example.cseproject.untilities.HashMapConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity
public class District {
    @Id
    @GeneratedValue
    @Column(name = "district_id")
    private Integer id;
    @Transient
    private String color;
    private String name;
    private Integer population;

    @ElementCollection
    @CollectionTable(name = "district_partyVotes",
                    joinColumns = @JoinColumn(name = "district_id"))
    private Map<PartyName, Integer> partyVotes;

   /* @ElementCollection
    @CollectionTable(name = "district_minority_group_population",
            joinColumns = @JoinColumn(name = "district_id"))*/
   @Transient
    private Map<DemograpicGroup, Integer> minorityGroupPopulation;

    private String geoJson;

    @Transient
    private Set<Precinct> precincts;

//    private String districtAttributeJSON;
//
//    @Convert(converter = HashMapConverter.class)
//    private Map<String, Object> districtAttributes;

//    public void serializeDistrictAttributes() throws JsonProcessingException{
//        ObjectMapper objectMapper = new ObjectMapper();
//        this.districtAttributeJSON = objectMapper.writeValueAsString(this.districtAttributes);
//    }
//
//    public void deserializeCustomerAttributes() throws IOException{
//        ObjectMapper objectMapper = new ObjectMapper();
//        this.districtAttributes = objectMapper.readValue(this.districtAttributeJSON, HashMap.class);
//    }

    public Map<PartyName, Integer> getPartyVotes() {
        return partyVotes;
    }

    public void setPartyVotes(Map<PartyName, Integer> partyVotes) {
        this.partyVotes = partyVotes;
    }

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

    public Set<Precinct> getPrecincts() {
        return precincts;
    }

    public void setPrecincts(Set<Precinct> precincts) {
        this.precincts = precincts;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Map<DemograpicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemograpicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }



}
