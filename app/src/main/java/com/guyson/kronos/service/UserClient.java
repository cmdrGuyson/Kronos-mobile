package com.guyson.kronos.service;

import com.guyson.kronos.model.LoginCredentials;
import com.guyson.kronos.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserClient {

    //Login as a user
    @POST("login")
    Call<User> login(@Body LoginCredentials loginCredentials);

    //Get user details of logged in user
    @GET("user")
    Call<User> getUser(@Header("Authorization") String token);


    //Get all students
    @GET("students")
    Call<List<User>> getStudents();
}
