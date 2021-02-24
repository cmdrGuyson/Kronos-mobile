package com.guyson.kronos.model;

import java.io.Serializable;

public class Lecturer implements Serializable {

    private String firstName, lastName, type, email;
    private int lecturerID;

    public Lecturer(String firstName, String lastName, String type, String email, int lecturerID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.email = email;
        this.lecturerID = lecturerID;
    }

    public Lecturer() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(int lecturerID) {
        this.lecturerID = lecturerID;
    }
}
