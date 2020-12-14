package com.guyson.kronos.service;

import com.guyson.kronos.model.Class;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ClassClient {

    //Get all classes
    @GET("classes")
    Call<List<Class>> getClasses();

}
