package com.guyson.kronos.model;

public class User {

    private String username, firstName, lastName, password, role, token;
    private int classID;

    public User() {}

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(String username, String firstName, String lastName, String password, String role, int classID, String token) {
        this.username = username;
        this.firstName = firstName;
        this.password = password;
        this.role = role;
        this.classID = classID;
        this.token = token;
        this.lastName = lastName;
    }
}
