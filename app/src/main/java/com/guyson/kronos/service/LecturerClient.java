package com.guyson.kronos.service;

import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LecturerClient {

    //Get all lecturers
    @GET("lecturers")
    Call<List<Lecturer>> getLecturers();
}
