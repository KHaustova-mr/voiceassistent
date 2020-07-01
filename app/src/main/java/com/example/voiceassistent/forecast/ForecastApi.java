package com.example.voiceassistent.forecast;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=f47634496072eeeaf3ca60ec78ccc827")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
