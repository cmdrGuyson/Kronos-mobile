package com.guyson.kronos.model;

public class Class {

    private String type, description;
    private int classID;

    public Class(int classID, String type, String description) {
        this.classID = classID;
        this.type = type;
        this.description = description;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
