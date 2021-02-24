package com.guyson.kronos.model;

import java.io.Serializable;

public class Module implements Serializable {
    private int moduleID;
    private int credits;

    public int getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(int lecturerID) {
        this.lecturerID = lecturerID;
    }

    private int lecturerID;
    private String name, description;
    private Lecturer lecturer;
    private boolean enrolled;

    public Module(int moduleID, int credits, String name, String description, Lecturer lecturer, boolean enrolled) {
        this.moduleID = moduleID;
        this.credits = credits;
        this.name = name;
        this.description = description;
        this.lecturer = lecturer;
        this.enrolled = enrolled;
    }

    public Module() {}

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

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }
}
