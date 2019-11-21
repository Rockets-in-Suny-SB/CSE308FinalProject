package com.example.cseproject.Model;

import com.example.cseproject.Enum.DemographicGroup;
import com.example.cseproject.Enum.PartyName;

import javax.persistence.*;
import java.util.Map;

@Entity
public class District {

    private Integer id;
    private String color;
    private String name;
    private Integer population;
    private Map<PartyName, Integer> partyVotes;
    private Map<DemographicGroup, Integer> minorityGroupPopulation;
    private String geoJson;

    /*
    private String districtAttributeJSON;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> districtAttributes;

    public void serializeDistrictAttributes() throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        this.districtAttributeJSON = objectMapper.writeValueAsString(this.districtAttributes);
    }

    public void deserializeCustomerAttributes() throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        this.districtAttributes = objectMapper.readValue(this.districtAttributeJSON, HashMap.class);
    }
    */

    @Id
    @GeneratedValue
    @Column(name = "district_id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Transient
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

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    @ElementCollection
    @CollectionTable(name = "district_partyVotes",
            joinColumns = @JoinColumn(name = "district_id"))
    public Map<PartyName, Integer> getPartyVotes() {
        return partyVotes;
    }

    public void setPartyVotes(Map<PartyName, Integer> partyVotes) {
        this.partyVotes = partyVotes;
    }

    @ElementCollection
    @CollectionTable(name = "district_minorityGroupPopulation",
            joinColumns = @JoinColumn(name = "district_id"))
    public Map<DemographicGroup, Integer> getMinorityGroupPopulation() {
        return minorityGroupPopulation;
    }

    public void setMinorityGroupPopulation(Map<DemographicGroup, Integer> minorityGroupPopulation) {
        this.minorityGroupPopulation = minorityGroupPopulation;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }
}
