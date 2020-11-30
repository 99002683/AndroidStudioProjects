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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends WearableActivity implements DisplayManager.DisplayListener {
    private static final IntentFilter intentFilter;
    private static final String DATE_FORMAT = "aaaaa, DD MMM";

    TextView cdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cdate = (TextView)findViewById(R.id.currentDate);

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
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.d("Time zone","="+tz.getDisplayName());
       // TimeZone.getDefault().getDisplayName();

        //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
//        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
//        //System.out.println(dateFormat.format(cal.getTime()));
//        Log.d("Calender zone","="+dateFormat.format(cal.getTime()));
//        cdate.setText(dateFormat.format(date));

    }

}