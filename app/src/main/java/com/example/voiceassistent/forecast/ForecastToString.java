package com.example.voiceassistent.forecast;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(String city, final Consumer<String> callback){
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast result = response.body();
                if (result!=null && result.current != null) {
                    String answer = "Сейчас где-то " + result.current.temperature + " " + getEndingOfNumber(result.current.temperature, "градус", "градуса", "градусов") + " и " + result.current.weather_descriptions.get(0).toLowerCase();
                    callback.accept(answer);
                }
                else
                    callback.accept("Не могу узнать погоду");
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.w("WEATHER",t.getMessage());
                callback.accept("Не могу узнать погоду");
            }
        });
    }

    private static String getEndingOfNumber(int number, String oneObject, String twoObjects, String manyObjects){
        if(number < 0)
            number *= (-1);
        number %= 100;
        if(number > 19)
            number %= 10;
        switch (number){
            case 1: return oneObject;
            case 2: case 3: case 4: return twoObjects;
            default: return manyObjects;
        }
    }
}
