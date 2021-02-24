package com.guyson.kronos.model;

import java.io.Serializable;

public class Lecture implements Serializable {

    private int lectureID;
    private int roomID;
    private int moduleID;
    private Module module;
    private Room room;
    private String date, startTime;
    private int duration;

    public Lecture(int lectureID, Module module, Room room, String date, String startTime, int duration) {
        this.lectureID = lectureID;
        this.module = module;
        this.room = room;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Lecture() {}

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
