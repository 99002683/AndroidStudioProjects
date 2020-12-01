package com.example.wearosexample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends WearableActivity implements DisplayManager.DisplayListener {
    private static final IntentFilter intentFilter;
    private static final String TIME_FORMAT_24 = "H:mm";
    private static final String TIME_FORMAT_12 = "h:mm";
    private static final String DATE_FORMAT = "EEE, d MMM yyyy";
    private static final String TIME_FORMAT_AMPM = "a";
    private static final String TIME_FORMAT_TIMEZONE = "zzz";


    TextView cDate,cAMPM, cTime,cTimeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cDate = (TextView)findViewById(R.id.currentDate);
        cAMPM = (TextView)findViewById(R.id.textViewAMPM);
        cTime = (TextView) findViewById(R.id.textViewTime);
        cTimeZone = (TextView) findViewById(R.id.textViewTimeZone);

        registerReceiver(timeReceiver,intentFilter);

        // Enables Always-on
        setAmbientEnabled();
    }

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           updateTime();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeReceiver);
    }

    @Override
    public void onDisplayAdded(int displayId) {

    }

    @Override
    public void onDisplayRemoved(int displayId) {

    }

    @Override
    public void onDisplayChanged(int displayId) {

    }

    private void updateTime() {
        SimpleDateFormat sdf, sdfAMPM,sdfCurrentDate, sdfTimeZone;

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        //TimeZone tz = cal.getTimeZone();
        TimeZone tz = TimeZone.getDefault();

        Log.d("Time zone","="+tz.getDisplayName());


        if(android.text.format.DateFormat.is24HourFormat(this)){
            sdf = new SimpleDateFormat(TIME_FORMAT_24);
            cAMPM.setVisibility(View.INVISIBLE);
        }
        else {
            sdf = new SimpleDateFormat(TIME_FORMAT_12);
            cAMPM.setVisibility(View.VISIBLE);
        }

        sdf.setTimeZone(tz);
        cTime.setText(sdf.format(date));

        sdfAMPM = new SimpleDateFormat(TIME_FORMAT_AMPM);
        sdfAMPM.setTimeZone(tz);
        cAMPM.setText(sdfAMPM.format(date));

        sdfCurrentDate = new SimpleDateFormat(DATE_FORMAT);
        sdfCurrentDate.setTimeZone(tz);
        cDate.setText(sdfCurrentDate.format(date));


        sdfTimeZone = new SimpleDateFormat(TIME_FORMAT_TIMEZONE);
        sdfTimeZone.setTimeZone(tz);
        cTimeZone.setText(sdfTimeZone.format(date));

//        sdfTimeZone = new SimpleDateFormat(DATE_FORMAT);
//        sdfTimeZone.setTimeZone(tz);
//        cAMPM.setText(sdfTimeZone.format(date));

       // TimeZone.getDefault().getDisplayName();

        //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
//        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
//        //System.out.println(dateFormat.format(cal.getTime()));
//        Log.d("Calender zone","="+dateFormat.format(cal.getTime()));
//        cdate.setText(dateFormat.format(date));

    }
}