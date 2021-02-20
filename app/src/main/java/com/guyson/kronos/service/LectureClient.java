package com.guyson.kronos.service;

import com.guyson.kronos.model.Lecture;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface LectureClient {

    //Get list of all lectures in the system
    @GET("lectures")
    Call<List<Lecture>> getAllLectures(@Header("Authorization") String token);

    @GET("lectures/{date}")
    Call<List<Lecture>> getAllLecturesByDate(@Header("Authorization") String token, @Path(value = "date", encoded = true) String date);
}
