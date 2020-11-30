package com.example.clockexample;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // inititate the digital and analog clock
        DigitalClock simpleDigitalClock = (DigitalClock) findViewById(R.id.simpleDigitalClock);
//        AnalogClock simpleAnalogClock = (AnalogClock) findViewById(R.id.simpleAnalogClock);
        // perform click event on analog clock
//        simpleAnalogClock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Analog Clock", Toast.LENGTH_SHORT).show(); // display a toast for analog clock
//            }
//        });
        // perform click event on digital clock
        simpleDigitalClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Digital Clock", Toast.LENGTH_SHORT).show(); //display a toast for digital clock
            }
        });
    }
}