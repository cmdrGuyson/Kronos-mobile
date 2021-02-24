package com.guyson.kronos.service;

import com.guyson.kronos.model.Lecture;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LectureClient {

    //Get list of all lectures in the system
    @GET("lectures")
    Call<List<Lecture>> getAllLectures(@Header("Authorization") String token);

    @GET("lectures/{date}")
    Call<List<Lecture>> getAllLecturesByDate(@Header("Authorization") String token, @Path(value = "date", encoded = true) String date);

    @POST("lecture")
    Call<ResponseBody> addLecture(@Header("Authorization") String token, @Body Lecture lecture);

    @DELETE("lecture/{id}")
    Call<ResponseBody> deleteLecture(@Header("Authorization") String token, @Path(value = "id", encoded = true) int id);

    @PUT("lecture/{id}")
    Call<ResponseBody> updateLecture(@Header("Authorization") String token, @Path(value = "id", encoded = true) int id, @Body Lecture lecture);
}
