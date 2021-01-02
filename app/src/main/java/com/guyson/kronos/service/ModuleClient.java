package com.guyson.kronos.service;

import com.guyson.kronos.model.Module;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ModuleClient {

    //Get list of modules
    @GET("modules")
    Call<List<Module>> getModules();

}