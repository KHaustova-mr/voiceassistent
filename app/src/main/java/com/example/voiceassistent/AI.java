package com.example.voiceassistent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
            put("который час", 1);
            put("какой день недели сейчас", 2);
            put("сколько дней до нового года", 3);
        }
    };

    private static String getAnswerToCommand(int idCommand){
        switch (idCommand){
            case 0:{
                Date dateNow = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                return dateFormat.format(dateNow);

            }
            case 1:{
                Date dateNow = new Date();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                return dateFormat.format(dateNow);
            }
            case 2:{
                Date dateNow = new Date();
                DateFormat dateFormat = new SimpleDateFormat("E");
                return dateFormat.format(dateNow);
            }
            case 3:{
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                DateFormat dateYear = new SimpleDateFormat("yyyy");
                Date dateNow = new Date();
                try {
                    Date dateNewYear = dateFormat.parse("01.01."+(Integer.parseInt(dateYear.format(dateNow))+1));
                    long difference = dateNewYear.getTime() - dateNow.getTime();
                    int days =  (int)(difference / (24 * 60 * 60 * 1000));
                    return String.valueOf(days);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return "0";
            }
            default: return null;
        }
    }


    private static String toNormalForm(String inputString){
        return inputString.replaceAll("[.,/?!]","").toLowerCase();
    }

    public static String getAnswer(String question){
        question = toNormalForm(question);

        int idCommand = -1;
        for (Map.Entry<String, Integer> command : commands.entrySet()){
            if(question.contains(command.getKey())){
                idCommand = command.getValue();
            }
        }
        if(idCommand != -1)
            return getAnswerToCommand(idCommand);
        for (Map.Entry<String, String> answer : answers.entrySet()){
            if(question.contains(answer.getKey())){
                return answer.getValue();
            }
        }
        return "Вопрос понял. Думаю...";
    }
}
