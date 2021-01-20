package com.example.watchfaceadvanced;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.TimeZone;

import static com.example.watchfaceadvanced.MyView.updateTime;


public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.linearLayout);
        MyView myView = new MyView(this);
        linearLayout.addView(myView);

        getSupportActionBar().hide(); //hide the title bar
    }

}