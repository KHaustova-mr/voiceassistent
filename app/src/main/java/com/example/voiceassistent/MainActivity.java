package com.example.voiceassistent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Console;

public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected TextView chatWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatWindow = findViewById(R.id.chatWindow);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Произошло нажатие на кнопку");
                onSend();
                System.out.println("Вызвался метод onSend()");
            }
        });
    }
    protected void onSend() {
        System.out.println("метод onSend()");
        String text = questionText.getText().toString();
        String answer = "Вопрос понял Думаю…";
        chatWindow.setText("Какой-то текст");//проблема здесь
        //почему-то chatWindow не появляется текст
        //сам onClick работает видно в консоли эту строку писала я, надо гуглить тогда как присвоить значение в текст вью др способом
    }
}
