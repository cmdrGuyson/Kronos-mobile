package com.guyson.kronos.service;

import com.guyson.kronos.model.Class;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClassClient {

    //Get all classes
    @GET("classes")
    Call<List<Class>> getClasses(@Header("Authorization") String token);

    //Add class
    @POST("class")
    Call<ResponseBody> addClass(@Header("Authorization") String token, @Body Class _class);

    //Delete Class
    @DELETE("class/{id}")
    Call<ResponseBody> deleteClass(@Header("Authorization") String token, @Path("id") int id);

}
