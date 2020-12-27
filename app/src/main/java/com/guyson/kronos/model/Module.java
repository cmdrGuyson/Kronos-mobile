package com.guyson.kronos.model;

public class Module {
    private int moduleID, credits;
    private String name, lecturer, description;

    public Module(int moduleID, int credits, String name, String lecturer, String description) {
        this.moduleID = moduleID;
        this.credits = credits;
        this.name = name;
        this.lecturer = lecturer;
        this.description = description;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
