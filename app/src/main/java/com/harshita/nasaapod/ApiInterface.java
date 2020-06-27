package com.harshita.nasaapod;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("planetary/{type}")
    Call<Results> pictureOfDay(
            @Path("type") String type,
            @Query("api_key") String apiKey,
            @Query("date") String date
    );

    @GET("planetary/{type}")
    Call<Results> pictureOfDay(
            @Path("type") String type,
            @Query("api_key") String apiKey
    );
}
