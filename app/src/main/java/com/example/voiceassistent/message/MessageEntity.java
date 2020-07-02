package com.example.voiceassistent.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MessageEntity {
    public String text;
    public String  date;
    public int isSend;


    public MessageEntity(String text, String date, int isSend) {
        this.text = text;
        this.date = date;
        this.isSend = isSend;
    }

    public MessageEntity(Message message){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        this.text = message.text;
        this.date = dateFormat.format(message.date);
        if(message.isSend)
            this.isSend = 1;
        else
            this.isSend = 0;

    }
}
