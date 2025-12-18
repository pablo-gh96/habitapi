package com.apphabit.backend.models;

public class PlateRequest {
    private String name;
    private String type;

    public PlateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
