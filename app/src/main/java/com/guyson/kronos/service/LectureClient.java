package com.guyson.kronos.service;

import com.guyson.kronos.model.Lecture;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LectureClient {

    //Get list of all lectures in the system
    @GET("lectures")
    Call<List<Lecture>> getAllLectures();

    @GET("my-lectures/{date}")
    Call<List<Lecture>> getMyLectures(@Path(value = "date", encoded = true) String date);
}
