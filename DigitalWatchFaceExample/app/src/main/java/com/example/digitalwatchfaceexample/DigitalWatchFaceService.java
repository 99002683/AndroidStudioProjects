package com.example.digitalwatchfaceexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DigitalWatchFaceService extends CanvasWatchFaceService {

    private static final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine()       //Provided the actual implementation of a watch face that draws on a Canvas. Implemented onCreateEngine() to return a concrete Engine implementation.
    {
        return new SimpleEngine();
    }

    private class SimpleEngine extends CanvasWatchFaceService.Engine    //implemented service callback methods here.
    {
        private static final String ACTION_TIME_ZONE = "time-zone";
        private static final String TAG = "SimpleEngine";

        private DigitalWatchFace watchFace;
        private Handler timeTick;

        @Override
        public void onCreate(SurfaceHolder holder)     //initialized watch face here.
        {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this).build());  //Watch face style is set here. This affects how UI elements such as the battery indicator are drawn on top of the watch face.
            timeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();

            DigitalWatchFace.mCalendar = Calendar.getInstance();

            watchFace = DigitalWatchFace.newInstance(DigitalWatchFaceService.this);
        }

        @Override
        public void onPropertiesChanged(Bundle properties)     // Added device features (burn-in, low-bit ambient) here
        {
            super.onPropertiesChanged(properties);
            watchFace.propertiesChanged(properties);
        }

        @Override
        public void onTimeTick()     //Called periodically in ambient mode to update the time shown by the watch face. This method is called at least once per minute.
        {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode)   // Called when the device enters or exits ambient mode. The watch face should switch to a black and white display in ambient mode. If the watch face displays seconds, it should hide them in ambient mode.
        {
            super.onAmbientModeChanged(inAmbientMode);
            watchFace.AmbientModeChanged(inAmbientMode);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds)     //Added watch face Draw Method here. Draws the watch face.
        {
            super.onDraw(canvas, bounds);

            if (isInAmbientMode())          //isInAmbientMode() Returns whether the watch face is in ambient mode. When true, the watch face should display in white on black.
            {
                watchFace.isInAmbientMode(canvas, bounds);
            } else {
                watchFace.isNotInAmbientMode(canvas, bounds);
            }
            watchFace.draw(canvas, bounds);
        }

        @Override
        public void onVisibilityChanged(boolean visible)     //Called to inform that watch face becoming visible or hidden.
        {
            super.onVisibilityChanged(visible);
            if (visible)
            {
                registerTimeZoneReceiver();
                DigitalWatchFace.mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else
            {
                unregisterTimeZoneReceiver();
            }
            startTimerIfNecessary();
        }

        private void startTimerIfNecessary()
        {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode())     //If Watch face is visible and not in Ambient Mode
            {
                timeTick.post(timeRunnable);
            }
        }

        private final Runnable timeRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                onSecondTick();

                if (isVisible() && !isInAmbientMode())
                {
                    timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick()
        {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary()
        {
            if (isVisible() && !isInAmbientMode())
            {
                invalidate();
            }
        }

        private void unregisterTimeZoneReceiver()
        {
            unregisterReceiver(timeZoneChangedReceiver);
            unregisterReceiver(mBatInfoReceiver);
        }

//        static {
//            intentFilter = new IntentFilter();
//            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
//            intentFilter.addAction(Intent.ACTION_TIME_TICK);
//            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
//            // intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        }

        private void registerTimeZoneReceiver()
        {

            IntentFilter timeZoneFilter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(timeZoneChangedReceiver, timeZoneFilter);

            IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(mBatInfoReceiver, batteryFilter);
        }

        private BroadcastReceiver timeZoneChangedReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                DigitalWatchFace.mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
                if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()))
                {
                    watchFace.updateTimeZoneWith(intent.getStringExtra(ACTION_TIME_ZONE));
                }
            }
        };

        private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent intent)
            {
               // watchFace.updateBattery(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%"));
            }
        };

        @Override
        public void onDestroy()
        {
            timeTick.removeCallbacks(timeRunnable);
            super.onDestroy();
        }
    }
}
