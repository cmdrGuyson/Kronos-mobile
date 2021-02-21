package com.guyson.kronos.model;

public class Room {
    private int roomID;
    private String description;
    private String type;

    public Room(int roomID, String description, String type) {
        this.roomID = roomID;
        this.description = description;
        this.type = type;
    }

    public Room() {}

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
