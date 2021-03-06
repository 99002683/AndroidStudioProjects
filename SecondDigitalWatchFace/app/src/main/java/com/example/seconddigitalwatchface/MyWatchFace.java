package com.example.seconddigitalwatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.palette.graphics.Palette;

import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class MyWatchFace extends CanvasWatchFaceService {

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.  (to update our watch face every second)
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine()
    {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();  //to handle updating the time periodically in interactive mode.
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine
            //watch face engine that handles system events, such as the screen turning off or going into ambient mode.
    {
        private static final float SECOND_TICK_STROKE_WIDTH = 2f;
        private static final int SHADOW_RADIUS = 6;
        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;

        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)  // This receiver simply resets to default time zone and display time.
            {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent intent)
            {
                updateBattery(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private int mTimePaintColor;
        private int mSecondStickPaintColor;
        private int mPaintShadowColor;
        private Paint mTimePaint;
        private Paint mDatePaint;
        private Paint mBatteryPaint;
        private Paint mSecondStickPaint;
        private Paint mTickAndCirclePaint;
        private Paint mBackgroundPaint;
        private Bitmap mBackgroundBitmap;
        private Bitmap mGrayBackgroundBitmap;
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;

        private int mWidth;
        private int mHeight;
        private boolean shouldShowSeconds = true;
        private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
        private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
        private static final String DATE_FORMAT = "EEE, d MMM yyyy";  //"%02d.%02d.%d";
        private static final String TIME_FORMAT_24_HOUR = "HH:mm";
        private static final String TIME_FORMAT_12_HOUR = "hh:mm";
        private String batteryText = "100%";


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            initializeBackground();
            initializeWatchFace();
        }

        private void initializeBackground() {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watchface_service_bg2);

            /* Extracts colors from background image to improve watchface style. */
            Palette.from(mBackgroundBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (palette != null) {
                        mSecondStickPaintColor = palette.getVibrantColor(Color.RED);
                        mTimePaintColor = palette.getLightVibrantColor(Color.WHITE);
                        mPaintShadowColor = palette.getDarkMutedColor(getResources().getColor(R.color.DarkCyan));
                        updateWatchHandStyle();
                    }
                }
            });
        }

        private void initializeWatchFace() {
            /* Set defaults for colors */
            mTimePaintColor = Color.WHITE;
            mSecondStickPaintColor = Color.RED;
            mPaintShadowColor = getResources().getColor(R.color.DarkCyan);

            mTimePaint = new Paint();
            mTimePaint.setColor(getResources().getColor(R.color.white));
            mTimePaint.setTextSize(getResources().getDimension(R.dimen.time_size));
            mTimePaint.setTypeface(Typeface.DEFAULT);
            mTimePaint.setAntiAlias(true);

            mDatePaint = new Paint();
            mDatePaint.setColor(getResources().getColor(R.color.white));
            mDatePaint.setTextSize(getResources().getDimension(R.dimen.date_size));
            mDatePaint.setTypeface(Typeface.SERIF);
            mDatePaint.setAntiAlias(true);

            mBatteryPaint = new Paint();
            mBatteryPaint.setColor(getResources().getColor(R.color.white));
            mBatteryPaint.setTextSize(getResources().getDimension(R.dimen.battery_size));
            mBatteryPaint.setAntiAlias(true);

            mSecondStickPaint = new Paint();
            mSecondStickPaint.setColor(getResources().getColor(R.color.Red));
            mSecondStickPaint.setStrokeWidth(3);
            mSecondStickPaint.setAntiAlias(true);

            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setColor(mTimePaintColor);
            mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
            //mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mTimePaintColor);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick()
        {
            super.onTimeTick();
            invalidate();
            //If invalidate gets called it tells the system that the current view has changed and it should be redrawn as soon as possible
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;

            updateWatchHandStyle();

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void updateWatchHandStyle() {
            if (mAmbient) {

                initGrayBackgroundBitmap();
                mTimePaint.setColor(getResources().getColor(R.color.SlateGray));
                mDatePaint.setColor(getResources().getColor(R.color.SlateGray));
                mSecondStickPaint.setColor(getResources().getColor(R.color.LightCoral));

                mTimePaint.setAntiAlias(true);
                mDatePaint.setAntiAlias(true);
                mSecondStickPaint.setAntiAlias(true);

                mTimePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mPaintShadowColor);
                mDatePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mPaintShadowColor);
                mSecondStickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mPaintShadowColor);

            } else {
                mTimePaint.setColor(mTimePaintColor);
                mDatePaint.setColor(mTimePaintColor);
                mSecondStickPaint.setColor(mSecondStickPaintColor);

                mTimePaint.setAntiAlias(true);
                mDatePaint.setAntiAlias(true);
                mSecondStickPaint.setAntiAlias(true);

//                mTimePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mPaintShadowColor);
//                mDatePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mPaintShadowColor);
//                mSecondStickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mPaintShadowColor);

                mTimePaint.clearShadowLayer();
                mDatePaint.clearShadowLayer();
                mSecondStickPaint.clearShadowLayer();
            }
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter)
                //is called when the user manually changes the interruption settings on their wearable.
        {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                mTimePaint.setAlpha(inMuteMode ? 100 : 255);
                mDatePaint.setAlpha(inMuteMode ? 100 : 255);
                mSecondStickPaint.setAlpha(inMuteMode ? 80 : 255);
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            /* Scale loaded background image (more efficient) if surface dimensions change. */
            float scale = ((float) width) / (float) mBackgroundBitmap.getWidth();

            mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                    (int) (mBackgroundBitmap.getWidth() * scale),
                    (int) (mBackgroundBitmap.getHeight() * scale), true);

            /*
             * Create a gray version of the image only if it will look nice on the device in
             * ambient mode. That means we don"t want devices that support burn-in
             * protection (slight movements in pixels, not great for images going all the way to
             * edges) and low ambient mode (degrades image quality).
             *
             * Also, if your watch face will know about all images ahead of time (users aren"t
             * selecting their own photos for the watch face), it will be more
             * efficient to create a black/white version (png, etc.) and load that when you need it.
             */
            if (!mBurnInProtection && !mLowBitAmbient) {
                initGrayBackgroundBitmap();
            }
        }

        private void initGrayBackgroundBitmap() {
            mGrayBackgroundBitmap = Bitmap.createBitmap(
                    mBackgroundBitmap.getWidth(),
                    mBackgroundBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mGrayBackgroundBitmap);
            Paint grayPaint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
            grayPaint.setColorFilter(filter);
            canvas.drawBitmap(mBackgroundBitmap, 0, 0, grayPaint);
        }

        /**
         * Captures tap event (and tap type). The {@link WatchFaceService#TAP_TYPE_TAP} case can be
         * used for implementing specific logic to handle the gesture.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT).show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            drawBackground(canvas);
            drawWatchFace(canvas, bounds);
        }

        private void drawBackground(Canvas canvas) {

            if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
                canvas.drawColor(Color.BLACK);
            } else if (mAmbient) {
                canvas.drawBitmap(mGrayBackgroundBitmap, 0, 0, mBackgroundPaint);
            } else {
                canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
            }
        }

        private void drawWatchFace(Canvas canvas, Rect bounds) {

            mWidth = canvas.getWidth();
            mHeight = canvas.getHeight();

            float mCenterX = mWidth / 2f;
            float mCenterY = mHeight / 2f;
            /*
             * Draw ticks. Usually you will want to bake this directly into the photo, but in
             * cases where you want to allow users to select their own photos, this dynamically
             * creates them on top of the photo.
             */
            float innerTickRadius = mCenterX - 10;
            float outerTickRadius = mCenterX ;

            for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
                float innerX = (float) Math.sin(tickRot) * innerTickRadius;
                float innerY = (float) - Math.cos(tickRot) * innerTickRadius;
                float outerX = (float) Math.sin(tickRot) * outerTickRadius;
                float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
                canvas.drawLine(mCenterX + innerX, mCenterY + innerY, mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint);
            }

            /*
             * Save the canvas state before we can begin to rotate it.
             */
//            canvas.save();

            mCalendar = Calendar.getInstance();
            Date date = new Date();
            TimeZone tz = TimeZone.getDefault();

            //canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

            String timeText1 = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE),mCalendar.get(Calendar.SECOND));
            String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));
            SimpleDateFormat sdf1 = new SimpleDateFormat(TIME_FORMAT_12_HOUR);
            sdf1.setTimeZone(tz);
            String timeText3 = String.format(sdf1.format(date), sdf1);
            float timeXOffset = computeXOffset(timeText3, mTimePaint, bounds);
            float timeYOffset = bounds.centerY();
            canvas.drawText(timeText3, timeXOffset, timeYOffset, mTimePaint);

            SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT);
            sdf2.setTimeZone(tz);
            String dateText = String.format(sdf2.format(date), sdf2);
            //String dateText = String.format(DATE_FORMAT, mCalendar.get(Calendar.DAY_OF_MONTH), (mCalendar.get(Calendar.MONTH)+1), mCalendar.get(Calendar.YEAR));
            float dateXOffset = computeXOffset(dateText, mDatePaint, bounds);
            float dateYOffset = computeYOffset(dateText, mDatePaint);
            canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, mDatePaint);

            float batteryXOffset = computeXOffset(batteryText, mBatteryPaint, bounds);
            float batteryYOffset = computeYOffset(batteryText, mBatteryPaint);
            canvas.drawText(batteryText, batteryXOffset, batteryYOffset + dateYOffset, mBatteryPaint);

            float mSecondHandLength = mCenterX;
            float secondsRotation = mCalendar.get(Calendar.SECOND) * 6f;       //These calculations reflect the rotation in degrees per unit of time, e.g., * 360 / 60 = 6
            canvas.rotate(secondsRotation, mCenterX, mCenterY);
            canvas.drawLine(mCenterX, mCenterY - 120, mCenterX, mCenterY - mSecondHandLength, mSecondStickPaint);

            /* Restore the canvas" original orientation. */
            //canvas.restore();
        }

        private float computeXOffset(String text, Paint paint, Rect watchBounds) {
            float centerX = watchBounds.exactCenterX();
            float timeLength = paint.measureText(text);
            return centerX - (timeLength / 2.0f);
        }

        private float computeYOffset(String dateText, Paint mDatePaint) {
            Rect textBounds = new Rect();
            mDatePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
            return textBounds.height() + 10.0f;
        }

        public void updateTimeZoneWith(String timeZone) {
            mCalendar.setTimeZone(TimeZone.getDefault());
            mCalendar = Calendar.getInstance();
        }

        public void updateBattery(String batteryText) {
            this.batteryText = batteryText;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);

            IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            MyWatchFace.this.registerReceiver(mBatteryInfoReceiver, batteryFilter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
            MyWatchFace.this.unregisterReceiver(mBatteryInfoReceiver);
        }

        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}