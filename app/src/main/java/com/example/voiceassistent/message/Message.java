package com.example.voiceassistent.message;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String text;
    public Date date;
    public Boolean isSend;

    public Message(String text, Boolean isSend) {
        this.text = text;
        this.isSend = isSend;
        this.date = new Date();
    }

    public Message(String text, Date date, Boolean isSend) {
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }

    public Message(MessageEntity messageEntity) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        this.text = messageEntity.text;
        try {
            this.date = dateFormat.parse(messageEntity.date);
        } catch (ParseException e) {
            System.out.println("Дата ("+ messageEntity.date +") была передана не правильно и не соответствует шаблону: HH:mm:ss dd.MM.yyyy");
            e.printStackTrace();
        }
        if(messageEntity.isSend == 0)
            this.isSend = false;
        else
            this.isSend = true;
    }
}
