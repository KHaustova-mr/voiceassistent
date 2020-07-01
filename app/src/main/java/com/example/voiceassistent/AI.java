package com.example.voiceassistent;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.voiceassistent.forecast.Forecast;
import com.example.voiceassistent.forecast.ForecastToString;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AI {
    private static final Map<String, String> answers = new HashMap<String, String>(){
        {
            put("привет", "Привет");
            put("как дела", "Неплохо");
            put("чем занимаешься", "Отвечаю на вопросы");
        }
    };

    private static final Map<String, Integer> commands = new HashMap<String, Integer>(){
        {
            put("какой сегодня день", 0);
            put("который час сейчас", 1);
            put("какой день недели сейчас", 2);
            put("сколько дней до нового года", 3);
            put("погода в городе", 4);
        }
    };

    private static void getAnswerToCommand(int idCommand, String question, final Consumer<String> callback){
        switch (idCommand){
            case 0:{
                Date dateNow = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                callback.accept(dateFormat.format(dateNow));
            }break;
            case 1:{
                Date dateNow = new Date();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                callback.accept(dateFormat.format(dateNow));
            }break;
            case 2:{
                Date dateNow = new Date();
                DateFormat dateFormat = new SimpleDateFormat("E");
                callback.accept(dateFormat.format(dateNow));
            }break;
            case 3:{
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                DateFormat dateYear = new SimpleDateFormat("yyyy");
                Date dateNow = new Date();
                try {
                    Date dateNewYear = dateFormat.parse("01.01."+(Integer.parseInt(dateYear.format(dateNow))+1));
                    long difference = dateNewYear.getTime() - dateNow.getTime();
                    int days =  (int)(difference / (24 * 60 * 60 * 1000));
                    callback.accept(String.valueOf(days));
                    return;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                callback.accept("0");
            }break;
            case 4:{
                Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = cityPattern.matcher(question);
                if (matcher.find()){
                    String cityName = matcher.group(1);
                    ForecastToString.getForecast(cityName, new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            callback.accept(s);
                        }
                    });
                }else {
                    callback.accept("Вы не ввели город");
                }
            }break;
        }
    }


    private static String toNormalForm(String inputString){
        return inputString.replaceAll("[.,/?!]","").toLowerCase();
    }

    public static void getAnswer(String question, final Consumer<String> callback){
        question = toNormalForm(question);

        for (Map.Entry<String, String> answer : answers.entrySet()){
            if(question.contains(answer.getKey())){
                callback.accept(answer.getValue());
                return;
            }
        }

        int idCommand = -1;
        for (Map.Entry<String, Integer> command : commands.entrySet()){
            if(question.contains(command.getKey())){
                idCommand = command.getValue();
            }
        }

        if(idCommand != -1) {

            getAnswerToCommand(idCommand, question, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    callback.accept(s);
                }
            });
        }else {
            callback.accept("Вопрос понял. Думаю...");
        }
    }
}
