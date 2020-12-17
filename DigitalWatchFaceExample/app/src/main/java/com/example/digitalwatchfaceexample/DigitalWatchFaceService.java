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
    private static final long timeMs = System.currentTimeMillis();

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    private class SimpleEngine extends CanvasWatchFaceService.Engine {

        //implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        private static final String ACTION_TIME_ZONE = "time-zone";
        private static final String TAG = "SimpleEngine";

        private DigitalWatchFace watchFace;
        private Handler timeTick;
        //private GoogleApiClient googleApiClient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            timeTick = new Handler(Looper.myLooper());
            startTimerIfNecessary();

            DigitalWatchFace.mCalendar = Calendar.getInstance();

            watchFace = DigitalWatchFace.newInstance(DigitalWatchFaceService.this);
//            googleApiClient = new GoogleApiClient.Builder(DigitalWatchFaceService.this)
//                    .addApi(Wearable.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
        }

        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }
        }

        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();

                if (isVisible() && !isInAmbientMode()) {
                    timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerTimeZoneReceiver();
                DigitalWatchFace.mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
                //googleApiClient.connect();
            } else {
                unregisterTimeZoneReceiver();
                //releaseGoogleApiClient();
            }

            startTimerIfNecessary();
        }

//        private void releaseGoogleApiClient() {
//            if (googleApiClient != null && googleApiClient.isConnected()) {
//                Wearable.DataApi.removeListener(googleApiClient, onDataChangedListener);
//                googleApiClient.disconnect();
//            }
//        }

        private void unregisterTimeZoneReceiver() {
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

        private void registerTimeZoneReceiver() {

            IntentFilter timeZoneFilter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(timeZoneChangedReceiver, timeZoneFilter);

            IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(mBatInfoReceiver, batteryFilter);
        }

        private BroadcastReceiver timeZoneChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DigitalWatchFace.mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
                if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                    watchFace.updateTimeZoneWith(intent.getStringExtra(ACTION_TIME_ZONE));
                }
            }
        };

        private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {
               // watchFace.updateBattery(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%"));
            }
        };

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            if (isInAmbientMode()) {
                watchFace.isInAmbientMode(canvas, bounds);
            } else {
                watchFace.isNotInAmbientMode(canvas,bounds);


                watchFace.draw(canvas, bounds);
            }
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            watchFace.AmbientModeChanged(inAmbientMode);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
            watchFace.propertiesChanged(properties);

        }


//        @Override
//        public void onConnected(Bundle bundle) {
//            Log.d(TAG, "connected GoogleAPI");
//
//            Wearable.DataApi.addListener(googleApiClient, onDataChangedListener);
//            Wearable.DataApi.getDataItems(googleApiClient).setResultCallback(onConnectedResultCallback);
//        }

//        private final DataApi.DataListener onDataChangedListener = new DataApi.DataListener() {
//            @Override
//            public void onDataChanged(DataEventBuffer dataEvents) {
//                dataEvents.release();
//                invalidateIfNecessary();
//            }
//        };
//
//        private final ResultCallback<DataItemBuffer> onConnectedResultCallback = new ResultCallback<DataItemBuffer>() {
//            @Override
//            public void onResult(DataItemBuffer dataItems) {
//                dataItems.release();
//                invalidateIfNecessary();
//            }
//        };

//        @Override
//        public void onConnectionSuspended(int i) {
//            Log.e(TAG, "suspended GoogleAPI");
//        }
//
//        @Override
//        public void onConnectionFailed(ConnectionResult connectionResult) {
//            Log.e(TAG, "connectionFailed GoogleAPI");
//        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            //releaseGoogleApiClient();

            super.onDestroy();
        }
    }
}
