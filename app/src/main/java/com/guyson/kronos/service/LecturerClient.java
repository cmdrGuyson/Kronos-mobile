package com.guyson.kronos.service;

import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.LoginCredentials;
import com.guyson.kronos.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LecturerClient {

    //Get all lecturers
    @GET("lecturers")
    Call<List<Lecturer>> getLecturers(@Header("Authorization") String token);

    //Add lecturer
    @POST("lecturer")
    Call<ResponseBody> addLecturer(@Header("Authorization") String token, @Body Lecturer lecturer);
}
