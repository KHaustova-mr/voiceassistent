package com.example.voiceassistent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.voiceassistent.db.DBHelper;
import com.example.voiceassistent.message.MessageEntity;
import com.example.voiceassistent.message.MessageListAdapter;
import com.example.voiceassistent.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    protected boolean ttsEnabled;
    protected MessageListAdapter messageListAdapter;
    SharedPreferences sPref;
    private boolean isLight = true;
    private String THEME = "THEME";
    DBHelper dBHelper;
    SQLiteDatabase database;


    public static final String APP_PREFERENCES = "mysettings";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("messageList", (ArrayList) messageListAdapter.messageList);
        outState.putString("inputText", questionText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        messageListAdapter.messageList = (List<Message>) savedInstanceState.getSerializable("messageList");
        questionText.setText(savedInstanceState.getString("inputText"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        isLight = sPref.getBoolean(THEME, true);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if(!isLight){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

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
                    textToSpeech.setPitch(0.2f);
                    textToSpeech.setSpeechRate(0.8f);
                    ttsEnabled = true;
                } else  if (status == TextToSpeech.ERROR) {
                    System.out.println("Произошла ошибка инициализации воспроизведения ответа");
                    ttsEnabled = false;
                }

            }
        });

        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();

        if(savedInstanceState != null) return;

        Cursor cursor = database.query(dBHelper.TABLE_MESSAGES, null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            int messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE);
            int dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE);
            int sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND);

            do{
                MessageEntity entity = new MessageEntity(cursor.getString(messageIndex), cursor.getString(dateIndex), cursor.getInt(sendIndex));
                Message message = new Message(entity);
                messageListAdapter.messageList.add(message);
            }while (cursor.moveToNext());
        }
        cursor.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                isLight = true;
                break;
            case R.id.night_settings:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                isLight = false;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        editor.apply();

        database.delete(dBHelper.TABLE_MESSAGES, null, null);
        for(Message message : messageListAdapter.messageList){
            MessageEntity entity = new MessageEntity(message);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);
            database.insert(dBHelper.TABLE_MESSAGES,null,contentValues);
        }

        super.onStop();
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
