package com.guyson.kronos.service;

import com.guyson.kronos.model.Module;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ModuleClient {

    //Get list of modules
    @GET("modules")
    Call<List<Module>> getModules(@Header("Authorization") String token);

    //Get list of logged in students modules
    @GET("my-modules")
    Call<List<Module>> getMyModules(@Header("Authorization") String token);

    //Get a list of all student modules
    @GET("student-modules")
    Call<List<Module>> getStudentModules(@Header("Authorization") String token);

    //Add a module
    @POST("module")
    Call<ResponseBody> addModule(@Header("Authorization") String token, @Body Module module);

    //Delete a module
    @DELETE("module/{id}")
    Call<ResponseBody> deleteModule(@Header("Authorization") String token, @Path("id") int id);

    //Enroll in module
    @POST("enroll/{id}")
    Call<ResponseBody> enroll(@Header("Authorization") String token, @Path("id") int id);

    //Unroll from module
    @POST("unroll/{id}")
    Call<ResponseBody> unroll(@Header("Authorization") String token, @Path("id") int id);
}