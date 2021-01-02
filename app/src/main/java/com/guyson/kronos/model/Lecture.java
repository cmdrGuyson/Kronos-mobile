package com.guyson.kronos.model;

public class Lecture {

    private int lectureID;
    private Module module;
    private Room room;
    private String date, startTime;
    private float duration;

    public Lecture(int lectureID, Module module, Room room, String date, String startTime, float duration) {
        this.lectureID = lectureID;
        this.module = module;
        this.room = room;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getLectureID() {
        return lectureID;
    }

    public void setLectureID(int lectureID) {
        this.lectureID = lectureID;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}
