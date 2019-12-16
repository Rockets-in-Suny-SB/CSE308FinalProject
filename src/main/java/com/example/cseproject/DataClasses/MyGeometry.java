package com.example.cseproject.DataClasses;

import java.io.Serializable;
import java.util.List;

public class MyGeometry implements Serializable {
    private String type;
    private List<List<List<Object>>> coordinates;
    public MyGeometry() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<List<Object>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<Object>>> coordinates) {
        this.coordinates = coordinates;
    }
}
