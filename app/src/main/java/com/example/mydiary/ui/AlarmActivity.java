package com.example.mydiary.ui;

import android.app.PendingIntent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;

public class AlarmActivity extends AppCompatActivity {
    //private MediaPlayer mediaPlayer;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        textView=findViewById(R.id.textView);
        String title=(String)getIntent().getExtras().get("title");
        textView.setText(title);
        //mediaPlayer = MediaPlayer.create(this, R.raw.sqbm);//铃声
        //mediaPlayer.start();
    }

    public void stop(View view){
        //mediaPlayer.stop();
        finish();

    }
}
