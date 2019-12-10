package com.example.cseproject.Untilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> entityInfo) {
       String entityInfoJson = null;
       try {
           ObjectMapper objectMapper = new ObjectMapper();
           entityInfoJson = objectMapper.writeValueAsString(entityInfo);
       } catch (JsonProcessingException e) {
           e.printStackTrace();
       }
        return entityInfoJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String entityInfoJson) {
        Map<String, Object> entityInfo = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            entityInfo = objectMapper.readValue(entityInfoJson, Map.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return entityInfo;
    }
}
