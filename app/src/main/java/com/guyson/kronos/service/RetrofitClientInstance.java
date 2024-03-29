package com.guyson.kronos.service;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**Singleton class to initialize retrofit instance**/
public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://0f4619287030.ngrok.io/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}