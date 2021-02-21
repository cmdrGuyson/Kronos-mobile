package com.guyson.kronos.service;

import com.guyson.kronos.model.Class;
import com.guyson.kronos.model.Room;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RoomClient {

    //Get all rooms
    @GET("rooms")
    Call<List<Room>> getRooms(@Header("Authorization") String token);

    //Add room
    @POST("room")
    Call<ResponseBody> addRoom(@Header("Authorization") String token, @Body Room room);

    //Delete room
    @DELETE("room/{id}")
    Call<ResponseBody> deleteRoom(@Header("Authorization") String token, @Path("id") int id);


}
