package com.example.cseproject.DataClasses;

import java.util.HashMap;
import java.util.Map;

public class Result {
    private Map<String,Object> result;
    public Result(){
        result=new HashMap<>();
    }
    public void addResult(String name, Object obj){
        result.put(name,obj);
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
