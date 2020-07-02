package com.example.voiceassistent;

import android.os.AsyncTask;

import com.example.voiceassistent.parse.ParsingHtmlService;
import com.example.voiceassistent.translate.TranslateToString;
import com.example.voiceassistent.forecast.ForecastToString;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
            put("какой день", 0);
            put("который час", 1);
            put("какой день недели", 2);
            put("сколько дней до нового года", 3);
            put("погода в городе", 4);
            put("переведи", 5);
            put("праздники", 6);
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
                DateFormat dateFormat = new SimpleDateFormat("EEEE");
                String weekday = dateFormat.format(dateNow);
                callback.accept(Character.toString(weekday.charAt(0)).toUpperCase()+weekday.substring(1));
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
                        public void accept(String answer) {
                            TranslateToString.getTranslate(answer, new Consumer<String>() {
                                @Override
                                public void accept(String text) {
                                    callback.accept(text);
                                }
                            });
                        }
                    });
                }else {
                    callback.accept("Вы не ввели город");
                }
            }break;
            case 5:{
                Pattern translPattern = Pattern.compile("переведи (.+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = translPattern.matcher(question);
                if (matcher.find()) {
                    final String[] text = {matcher.group(1)};
                    TranslateToString.getTranslate(text[0], new Consumer<String>() {
                        @Override
                        public void accept(String text) {
                            callback.accept(text);
                        }
                    });
                }
                else callback.accept("Я не понял");
            }break;
            case 6:{
                List<String> inputStringDateList = new ArrayList<>();
                DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
                Pattern translPattern = Pattern.compile("праздники (.+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = translPattern.matcher(question);
                if (matcher.find()) {
                    final String[] text = {matcher.group(1)};
                    String [] inputStringArray = text[0].split(",");
                    for (int i = 0; i < inputStringArray.length; i++){
                        inputStringArray[i] = inputStringArray[i].trim();
                        Date inputDate = getDate(inputStringArray[i]);
                        if(inputDate != null)
                            inputStringDateList.add(dateFormat.format(inputDate));
                    }
                }
                else{
                    inputStringDateList.add(dateFormat.format(new Date()));
                }
                if(!inputStringDateList.isEmpty()) {
                    new AsyncTask<String, Integer, Void>() {
                        String answer;

                        @Override
                        protected Void doInBackground(String... strings) {
                            answer = "";
                            for(int i = 0; i < strings.length; i++){
                                if(i != 0) answer += "\n\n";
                                answer += "Праздники на " + strings[i] + "\n";
                                answer += ParsingHtmlService.getHoliday(strings[i]);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            callback.accept(answer);
                            super.onPostExecute(aVoid);
                        }
                    }.execute(inputStringDateList.toArray(new String[0]));
                } else callback.accept("Не понял, какой вы день или дни имели ввиду");
            }break;
        }
    }

    private static Date getDate(String inputDate){
        System.out.println(inputDate);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            System.out.println(dateFormat.parse(inputDate));
            return dateFormat.parse(inputDate);
        } catch (ParseException e) {}

        dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        try {
            return dateFormat.parse(inputDate);
        } catch (ParseException e) {}

        inputDate = inputDate.toLowerCase();

        if(inputDate.equals("сегодня"))
            return new Date();

        if(inputDate.equals("завтра"))
            return calculateDateFromNow(1);

        if(inputDate.equals("вчера"))
            return calculateDateFromNow(-1);
        return null;
    }

    private static Date calculateDateFromNow(int num) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, num);
        return cal.getTime();
    }


    private static String toNormalForm(String inputString){
        return inputString.replaceAll("[.,/?!]","").toLowerCase();
    }

    public static void getAnswer(String question, final Consumer<String> callback){
        String questionNormal = toNormalForm(question);

        for (Map.Entry<String, String> answer : answers.entrySet()){
            if(questionNormal.contains(answer.getKey())){
                callback.accept(answer.getValue());
                return;
            }
        }

        int idCommand = -1;
        for (Map.Entry<String, Integer> command : commands.entrySet()){
            if(questionNormal.contains(command.getKey())){
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
            callback.accept("Не знаю ответ на ваш вопрос");
        }
    }
}
