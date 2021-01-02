package com.guyson.kronos.model;

public class Module {
    private int moduleID, credits;
    private String name, description;
    private User lecturer;

    public Module(int moduleID, int credits, String name, String description, User lecturer) {
        this.moduleID = moduleID;
        this.credits = credits;
        this.name = name;
        this.description = description;
        this.lecturer = lecturer;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getLecturer() {
        return lecturer;
    }

    public void setLecturer(User lecturer) {
        this.lecturer = lecturer;
    }
}
