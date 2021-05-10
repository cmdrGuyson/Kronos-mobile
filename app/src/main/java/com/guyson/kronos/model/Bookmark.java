package com.guyson.kronos.model;

public class Bookmark {

    private int lectureID;
    private int roomID;
    private String module;
    private String lecturer;
    private String date, startTime;
    private int duration;
    private String owner;
    private String priority;

    public Bookmark(String owner, int lectureID, int roomID, String module, String lecturer, String date, String startTime, int duration, String priority) {
        this.lectureID = lectureID;
        this.roomID = roomID;
        this.module = module;
        this.lecturer = lecturer;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.owner = owner;
        this.priority = priority;
    }

    public Bookmark() {}

    public int getLectureID() {
        return lectureID;
    }

    public void setLectureID(int lectureID) {
        this.lectureID = lectureID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
