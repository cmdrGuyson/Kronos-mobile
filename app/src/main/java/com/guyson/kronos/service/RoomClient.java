package com.guyson.kronos.service;

import com.guyson.kronos.model.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RoomClient {

    //Get all rooms
    @GET("rooms")
    Call<List<Room>> getRooms();
}
