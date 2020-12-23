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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private static final String TIME_FORMAT_TIMEZONE = "+05:30";
    private static final String TIME_FORMAT_24_seconds = "H:mm:ss";
    private static final String TIME_FORMAT_12_allTime = "h:mm:ss";
    //private static final String intent = Intent.ACTION_BATTERY_CHANGED;

    private static final String TIME_FORMAT_12_hours = "h:";
    private static final String TIME_FORMAT_12_minutes = "mm";
    private static final String TIME_FORMAT_12_seconds = "ss";

    TextView cDate,cAMPM, cHours, cMinutes, cSeconds, cTimeZone, batteryTxt;
    private TimeZone tz;
    private Date date;
    private  Calendar cal;
    androidx.wear.widget.BoxInsetLayout currentLayout;
    LinearLayout LinLayout;
    int deviceStatus;
    String currentBatteryStatus="Battery Info";
    int batteryLevel;
    ImageView batteryPercentage;


    @Override
    public void onScreenDim() { //The display is dozing in a low power state; it is still on but is optimized for showing system-provided content while the device is non-interactive.
//        cHours.setTextColor(Color.CYAN);
//        batteryTxt.setTextColor(Color.CYAN);

        //setActivityBackgroundColor(View.VISIBLE);
        //setActivityBackgroundColor(getResources().getColor(R.color.Cyan));
        //setActivityBackgroundColor(R.color.Cyan);
        currentLayout.setBackgroundColor(getResources().getColor(R.color.DarkGray1));
        //LinLayout.setBackground(getDrawable(R.drawable.circlewithimage));
        LinLayout.setBackground(getDrawable(R.drawable.circlewithimage));

        cHours.setTextColor(getResources().getColor(R.color.DarkGray));
        cMinutes.setTextColor(getResources().getColor(R.color.DarkGray));
        cSeconds.setTextColor(getResources().getColor(R.color.DarkGray));
        //cSeconds.setVisibility(View.INVISIBLE);

        batteryTxt.setTextColor(getResources().getColor(R.color.white));
        cDate.setTextColor(getResources().getColor(R.color.white));
        cAMPM.setTextColor(getResources().getColor(R.color.LightSlateGray));
//        cTimeZone.setTextColor(getResources().getColor(R.color.LightSlateGray));
        cAMPM.setVisibility(View.INVISIBLE);
        cAMPM.setBackground(getDrawable(R.color.Black));

        cTimeZone.setVisibility(View.INVISIBLE);
        cTimeZone.setBackground(getDrawable(R.color.Black));
    }

    @Override
    public void onScreenAwake() {
        batteryTxt.setVisibility(View.VISIBLE);
        //setActivityBackgroundColor(R.color.black);
        currentLayout.setBackgroundColor(getResources().getColor(R.color.Black));

        LinLayout.setBackground(getDrawable(R.color.black));

//        cHours.setTypeface(cHours.getTypeface(), Typeface.NORMAL);
//        cMinutes.setTypeface(cMinutes.getTypeface(), Typeface.NORMAL);
//        cSeconds.setTypeface(cSeconds.getTypeface(), Typeface.NORMAL);

        cDate.setVisibility(View.VISIBLE);
        cAMPM.setVisibility(View.VISIBLE);
        cAMPM.setBackground(getDrawable(R.drawable.circle));

        cTimeZone.setVisibility(View.VISIBLE);
        cTimeZone.setBackground(getDrawable(R.drawable.circle));
//        cTimeZone.setVisibility(View.VISIBLE);

        cHours.setVisibility(View.VISIBLE);
        cMinutes.setVisibility(View.VISIBLE);
        cSeconds.setVisibility(View.VISIBLE);

//        setActivityBackgroundColor(View.INVISIBLE);
//        setActivityBackgroundColor(getColor(R.color.DarkGray1));


        batteryTxt.setTextColor(Color.WHITE);
        cHours.setTextColor(Color.WHITE);
        cMinutes.setTextColor(Color.WHITE);
        cSeconds.setTextColor(Color.WHITE);
        cDate.setTextColor(getResources().getColor(R.color.LightSeaGreen));
        cAMPM.setTextColor(Color.WHITE);

//        cTimeZone.setTextColor(Color.WHITE);
// android:background="@drawable/circlewithimage"
    }

    @Override
    public void onScreenOff() { //The display is off.
        batteryTxt.setVisibility(View.GONE);
        cHours.setVisibility(View.GONE);
        cDate.setVisibility(View.GONE);
        cAMPM.setVisibility(View.GONE);
//        cTimeZone.setVisibility(View.GONE);
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
        cTimeZone.setSingleLine(false);
        //cTimeZone.setText("first line\n"+"second line\n"+"third line");

        batteryTxt = (TextView) findViewById(R.id.watchBattery);
        batteryPercentage = (ImageView) findViewById(R.id.batteryStatus);

        currentLayout = (androidx.wear.widget.BoxInsetLayout) findViewById(R.id.myLayout);
        LinLayout = (LinearLayout) findViewById(R.id.linear1);

        //batteryTxt.setTextColor(Color.WHITE);
        registerReceiver(timeReceiver,intentFilter);
        registerReceiver(batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

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

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            //int batteryLevel=(int)(((float)level / (float)scale) * 100.0f);

            if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){

                //textview.setText(currentBatteryStatus+" = Charging at "+batteryLevel+" %");
                batteryPercentage.setImageDrawable(getDrawable(R.drawable.rounded_charging));
                Log.d("Battery Status","= BATTERY_STATUS_CHARGING");
            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){

                //textview.setText(currentBatteryStatus+" = Discharging at "+batteryLevel+" %");
                batteryPercentage.setImageDrawable(getDrawable(R.drawable.rounded_critical));
                Log.d("Battery Status","= BATTERY_STATUS_DISCHARGING");
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL){

                //textview.setText(currentBatteryStatus+"= Battery Full at "+batteryLevel+" %");
                batteryPercentage.setImageDrawable(getDrawable(R.drawable.charging));
                Log.d("Battery Status","= BATTERY_STATUS_FULL");
            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN){

                //textview.setText(currentBatteryStatus+" = Unknown at "+batteryLevel+" %");
                batteryPercentage.setImageDrawable(getDrawable(R.drawable.rounded_unknown));
                Log.d("Battery Status","= BATTERY_STATUS_UNKNOWN");
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING){

                //textview.setText(currentBatteryStatus+" = Not Charging at "+batteryLevel+" %");
                batteryPercentage.setImageDrawable(getDrawable(R.drawable.rounded_default));
                Log.d("Battery Status","= BATTERY_STATUS_NOT_CHARGING");
            }

            if(level > 90)
                batteryPercentage.setImageResource(R.drawable.battery100);
            else if(level > 80)
                batteryPercentage.setImageResource(R.drawable.battery80);
            else if(level > 70)
                batteryPercentage.setImageResource(R.drawable.battery80);
            else if(level > 60)
                batteryPercentage.setImageResource(R.drawable.rounded_default);
            else if(level > 50)
                batteryPercentage.setImageResource(R.drawable.rounded_default);
            else if(level > 30)
                batteryPercentage.setImageResource(R.drawable.battery35);
            else if(level > 20)
                batteryPercentage.setImageResource(R.drawable.battery20);
            else if(level > 15)
                batteryPercentage.setImageResource(R.drawable.battery20);
            else if(level > 10)
                batteryPercentage.setImageResource(R.drawable.rounded_critical);
            else if(level > 2)
                batteryPercentage.setImageResource(R.drawable.rounded_critical);
            else
                batteryPercentage.setImageResource(R.drawable.battery00);
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
//        Log.d("Time zone","="+tz.getDisplayName());


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


//        SimpleDateFormat timezoneSdf = new SimpleDateFormat(TIME_FORMAT_TIMEZONE);
//        timezoneSdf.setTimeZone(tz);
//        cTimeZone.setText(tz.getDisplayName(tz.inDaylightTime(date), TimeZone.SHORT,
//                Locale.getDefault()));
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
//    public void setActivityBackgroundColor(int color)
//    {
////        View view = this.getWindow().getDecorView();
////        view.setBackgroundColor(color);
////        LinearLayout li=(LinearLayout)findViewById(R.id.myLayout);
////        li.setBackgroundColor(color);
//    }
}