package com.example.wearosexample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends DigitalWatchFaceActivity {
    private static final IntentFilter intentFilter;
    private static final String TIME_FORMAT_24 = "H:mm";
    private static final String TIME_FORMAT_12 = "h:mm";
    private static final String DATE_FORMAT = "EEE, d MMM yyyy";
    private static final String TIME_FORMAT_AMPM = "a";
    private static final String TIME_FORMAT_TIMEZONE = "Z";
    private static final String TIME_FORMAT_24_seconds = "H:mm:ss";
    private static final String TIME_FORMAT_12_allTime = "h:mm:ss";
    //private static final String intent = Intent.ACTION_BATTERY_CHANGED;

    private static final String TIME_FORMAT_12_hours = "h";
    private static final String TIME_FORMAT_12_minutes = "mm";
    private static final String TIME_FORMAT_12_seconds = "ss";

    TextView cDate,cAMPM, cHours, cMinutes, cSeconds, cTimeZone, batteryTxt;
<<<<<<< Updated upstream
    private TimeZone tz;
    private Date date;
    private  Calendar cal;
=======
    androidx.wear.widget.BoxInsetLayout currentLayout;
    LinearLayout LinLayout;
    int deviceStatus;
    String currentBatteryStatus="Battery Info";
    ImageView batteryPercentage;
>>>>>>> Stashed changes


    @Override
    public void onScreenDim() { //The display is dozing in a low power state; it is still on but is optimized for showing system-provided content while the device is non-interactive.
//        cHours.setTextColor(Color.CYAN);
//        batteryTxt.setTextColor(Color.CYAN);


        setActivityBackgroundColor(getColor(R.color.DarkGray1));

        cHours.setTextColor(getResources().getColor(R.color.DarkGray));
        cMinutes.setTextColor(getResources().getColor(R.color.DarkGray));
        cSeconds.setTextColor(getResources().getColor(R.color.DarkGray));
        cSeconds.setVisibility(View.INVISIBLE);

        batteryTxt.setTextColor(getResources().getColor(R.color.LightSlateGray));
        cDate.setTextColor(getResources().getColor(R.color.LightSlateGray));
        cAMPM.setTextColor(getResources().getColor(R.color.LightSlateGray));
        cTimeZone.setTextColor(getResources().getColor(R.color.LightSlateGray));
        cAMPM.setVisibility(View.INVISIBLE);
<<<<<<< Updated upstream
        cTimeZone.setVisibility(View.INVISIBLE);


        //setActivityBackgroundColor(View.VISIBLE);
=======
        //cAMPM.setBackground(getDrawable(R.color.Black));

        cTimeZone.setVisibility(View.INVISIBLE);
        //cTimeZone.setBackground(getDrawable(R.color.Black));
>>>>>>> Stashed changes
    }

    @Override
    public void onScreenAwake() {
        batteryTxt.setVisibility(View.VISIBLE);
<<<<<<< Updated upstream
=======
        //setActivityBackgroundColor(R.color.black);
        //currentLayout.setBackgroundColor(getResources().getColor(R.color.Black));    //if color
        currentLayout.setBackground(getDrawable(R.color.black));               //if image

        LinLayout.setBackground(getDrawable(R.color.FullTransparent));
>>>>>>> Stashed changes

//        cHours.setTypeface(cHours.getTypeface(), Typeface.NORMAL);
//        cMinutes.setTypeface(cMinutes.getTypeface(), Typeface.NORMAL);
//        cSeconds.setTypeface(cSeconds.getTypeface(), Typeface.NORMAL);

        cDate.setVisibility(View.VISIBLE);
        cAMPM.setVisibility(View.VISIBLE);
        cTimeZone.setVisibility(View.VISIBLE);

        cHours.setVisibility(View.VISIBLE);
        cMinutes.setVisibility(View.VISIBLE);
        cSeconds.setVisibility(View.VISIBLE);

        setActivityBackgroundColor(View.INVISIBLE);


        batteryTxt.setTextColor(Color.WHITE);
        cHours.setTextColor(Color.WHITE);
        cMinutes.setTextColor(Color.WHITE);
        cSeconds.setTextColor(Color.WHITE);
<<<<<<< Updated upstream
        cDate.setTextColor(Color.WHITE);
=======
        cDate.setTextColor(getResources().getColor(R.color.DarkRed));
>>>>>>> Stashed changes
        cAMPM.setTextColor(Color.WHITE);
        cTimeZone.setTextColor(Color.WHITE);

    }

    @Override
    public void onScreenOff() { //The display is off.
        batteryTxt.setVisibility(View.GONE);
        cHours.setVisibility(View.GONE);
        cDate.setVisibility(View.GONE);
        cAMPM.setVisibility(View.GONE);
        cTimeZone.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cDate = (TextView)findViewById(R.id.currentDate);
        cAMPM = (TextView)findViewById(R.id.textViewAMPM);

        cHours = (TextView) findViewById(R.id.textViewTimeHour);
        cMinutes = (TextView) findViewById(R.id.textViewTimeMinute);
        cSeconds = (TextView) findViewById(R.id.textViewTimeSecond);

        cTimeZone = (TextView) findViewById(R.id.textViewTimeZone);
        batteryTxt = (TextView) findViewById(R.id.watchBattery);

        //batteryTxt.setTextColor(Color.WHITE);
        registerReceiver(timeReceiver,intentFilter);
        registerReceiver(batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        ImageAdapter adapterView = new ImageAdapter(this);
        mViewPager.setAdapter(adapterView);

        handler.post(runnable);

        // Enables Always-on
       setAmbientEnabled();

    }

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
       // intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           updateTime();
            updateHours();
            updateMinutes();
            updateSeconds();
        }
    };


    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            batteryTxt.setText(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%"));
    }
    };


    private  Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            date = new Date();
//            cal = Calendar.getInstance();
//            //TimeZone tz = cal.getTimeZone();
//            tz = TimeZone.getDefault();
            handler.postDelayed(this, 1000);
            updateTime();
            updateHours();
            updateMinutes();
            updateSeconds();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeReceiver);
        unregisterReceiver(batteryReceiver);
    }


//    @Override
//    public void onEnterAmbient(Bundle ambientDetails) {
//        super.onEnterAmbient(ambientDetails);
////        View view = this.getWindow().getDecorView();
////        view.setBackgroundColor(#009688);
//
//        //cHours.getPaint().setAntiAlias(false);
//        batteryTxt.setTextColor(Color.GRAY);
//        cHours.setTextColor(Color.GRAY);
//        cDate.setTextColor(Color.GRAY);
//        cAMPM.setTextColor(Color.GRAY);
//        cTimeZone.setTextColor(Color.GRAY);
//    }
//    @Override
//    public void onExitAmbient(){
//        //cHours.getPaint().setAntiAlias(true);
//        batteryTxt.setTextColor(Color.WHITE);
//        cHours.setTextColor(Color.WHITE);
//        cDate.setTextColor(Color.WHITE);
//        cAMPM.setTextColor(Color.WHITE);
//        cTimeZone.setTextColor(Color.WHITE);
//        super.onExitAmbient();
//    }
//
//    @Override
//    public void onUpdateAmbient() {
//        super.onUpdateAmbient();
////        cAMPM.setText("");
////        cTimeZone.setText("");
//    }

    private void updateTime() {
        SimpleDateFormat sdf, sdfAMPM, sdfCurrentDate, sdfTimeZone;

        Date date = new Date();
        //Calendar cal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        //TimeZone tz = cal.getTimeZone();
        TimeZone tz = TimeZone.getDefault();
        Log.d("Time zone","="+tz.getDisplayName());


//        if(android.text.format.DateFormat.is24HourFormat(this)){
//            sdf = new SimpleDateFormat(TIME_FORMAT_24_seconds);
//            cHours.setVisibility(View.INVISIBLE);
//        }
//        else {
//            sdf = new SimpleDateFormat(TIME_FORMAT_12_allTime);
//            cHours.setVisibility(View.VISIBLE);
//        }

//        sdf = new SimpleDateFormat(TIME_FORMAT_12_allTime);
//        sdf.setTimeZone(tz);
//        cHours.setText(sdf.format(date));

        sdfAMPM = new SimpleDateFormat(TIME_FORMAT_AMPM);
        sdfAMPM.setTimeZone(tz);
        cAMPM.setText(sdfAMPM.format(date));

        sdfCurrentDate = new SimpleDateFormat(DATE_FORMAT);
        sdfCurrentDate.setTimeZone(tz);
        cDate.setText(sdfCurrentDate.format(date));


//        sdfTimeZone = new SimpleDateFormat(TIME_FORMAT_TIMEZONE);
//        sdfTimeZone.setTimeZone(tz);


        Date currentLocalTime = calendar.getTime();
        DateFormat date1 = new SimpleDateFormat(TIME_FORMAT_TIMEZONE);
        String localTime = date1.format(currentLocalTime);
        cTimeZone.setText(localTime);

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
    private void updateHours(){
        SimpleDateFormat sdf;

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        //TimeZone tz = cal.getTimeZone();
        TimeZone tz = TimeZone.getDefault();

        sdf = new SimpleDateFormat(TIME_FORMAT_12_hours);
        sdf.setTimeZone(tz);
        cHours.setText(sdf.format(date));
    }
    private void updateMinutes(){
        SimpleDateFormat sdf;

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        //TimeZone tz = cal.getTimeZone();
        TimeZone tz = TimeZone.getDefault();

        sdf = new SimpleDateFormat(TIME_FORMAT_12_minutes);
        sdf.setTimeZone(tz);
        cMinutes.setText(sdf.format(date));
    }
    private void updateSeconds(){
        SimpleDateFormat sdf;

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        //TimeZone tz = cal.getTimeZone();
        TimeZone tz = TimeZone.getDefault();

        sdf = new SimpleDateFormat(TIME_FORMAT_12_seconds);
        sdf.setTimeZone(tz);
        cSeconds.setText(sdf.format(date));
    }
    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
}