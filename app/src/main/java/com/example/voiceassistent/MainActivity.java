package com.example.voiceassistent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voiceassistent.model.Message;

import java.io.Console;
import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    protected boolean ttsEnabled;
    protected MessageListAdapter messageListAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString("chatText", chatMessageList.getText().toString());
        outState.putString("inputText", questionText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        chatMessageList.setText(savedInstanceState.getString("chatText"));
        questionText.setText(savedInstanceState.getString("inputText"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);

        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onSend();
            }
        });
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    if (textToSpeech.isLanguageAvailable(new Locale(Locale.getDefault().getLanguage()))
                            == textToSpeech.LANG_AVAILABLE) {
                        textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));
                    } else {
                        textToSpeech.setLanguage(Locale.US);
                    }
                    textToSpeech.setPitch(1.3f);
                    textToSpeech.setSpeechRate(0.7f);
                    ttsEnabled = true;
                } else  if (status == TextToSpeech.ERROR) {
                    System.out.println("Произошла ошибка инициализации воспроизведения ответа");
                    ttsEnabled = false;
                }

            }
        });


    }


    protected void onSend() {
        String text = questionText.getText().toString();
        messageListAdapter.messageList.add(new Message(text, true));
        messageListAdapter.notifyDataSetChanged();
        chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);
        questionText.setText("");
        AI.getAnswer(text, new Consumer<String>() {
            @Override
            public void accept(String answer) {
                messageListAdapter.messageList.add(new Message(answer, false));
                messageListAdapter.notifyDataSetChanged();
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size()-1);
                if(ttsEnabled)
                    textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH,null, null );
            }
        });
    }
}
