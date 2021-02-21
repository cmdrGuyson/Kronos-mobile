package com.guyson.kronos.service;

import com.guyson.kronos.model.Lecturer;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LecturerClient {

    //Get all lecturers
    @GET("lecturers")
    Call<List<Lecturer>> getLecturers(@Header("Authorization") String token);

    //Add lecturer
    @POST("lecturer")
    Call<ResponseBody> addLecturer(@Header("Authorization") String token, @Body Lecturer lecturer);

    //Delete lecturer
    @DELETE("lecturer/{id}")
    Call<ResponseBody> deleteLecturer(@Header("Authorization") String token, @Path("id") int id);
}
