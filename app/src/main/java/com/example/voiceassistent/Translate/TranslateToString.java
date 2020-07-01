package com.example.voiceassistent.Translate;

import android.util.Log;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslateToString {
    public static void getTranslate(String text, final Consumer<String> callback){
        TranslateApi api = TranslateService.getApi();
        Call<Translate> call = api.getTranslate(text,"en-ru");
        call.enqueue(new Callback<Translate>() {
            @Override
            public void onResponse(Call<Translate> call, Response<Translate> response) {
                Translate result = response.body();
                if(result != null && result.text != null){
                    callback.accept(result.text.get(0));
                }else{
                    callback.accept("Не могу перевести это");
                }
            }

            @Override
            public void onFailure(Call<Translate> call, Throwable t) {
                Log.w("TRANSLATOR",t.getMessage());
                callback.accept("Не могу это перевести");
            }
        });
    }
}
