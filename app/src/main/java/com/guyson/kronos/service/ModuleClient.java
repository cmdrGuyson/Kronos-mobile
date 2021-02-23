package com.guyson.kronos.service;

import com.guyson.kronos.model.Module;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ModuleClient {

    //Get list of modules
    @GET("modules")
    Call<List<Module>> getModules(@Header("Authorization") String token);

    //Get list of logged in students modules
    @GET("my-modules")
    Call<List<Module>> getMyModules();
}