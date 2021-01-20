package com.example.watchfaceadvanced;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.palette.graphics.Palette;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

public class MyView extends View {
    private static final float SECOND_TICK_STROKE_WIDTH = 5f;
    private static final float SHADOW_RADIUS = 2f;

    private Paint mTimePaint;
    private Paint mDatePaint;
    private Paint mBatteryPaint;
    private Paint mSecondStickPaint;
    private Paint mCirclePaint;
    private Paint mTickAndCirclePaint;
    private static Calendar mCalendar;
    private Paint mBackgroundPaint;
    private Bitmap mBackgroundBitmap;
    private Bitmap mGrayBackgroundBitmap;

    private int mTimePaintColor;
    private int mCircleColor;
    private int mSecondStickPaintColor;
    private int mPaintShadowColor;
    private int w;
    private int h;

    private boolean mAmbient;
    private boolean mLowBitAmbient;
    private boolean mBurnInProtection;
    private static boolean mRegisteredTimeZoneReceiver = false;
    //Rect bounds;

    private Calendar mTime;
    private String mDescFormat;
    private TimeZone mTimeZone;
    private boolean mEnableSeconds = true;

    private int mWidth;
    private int mHeight;
    private static boolean shouldShowSeconds = true;
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "EEE, d MMM yyyy";  //"%02d.%02d.%d";
    private static final String TIME_FORMAT_24_HOUR = "HH:mm";
    private static final String TIME_FORMAT_12_HOUR = "hh:mm";
    private String batteryText = "100%";


    public MyView(Context context) {
        super(context);

        mCalendar = Calendar.getInstance();

        initializeBackground();
        initializeWatchFace();
        //registerReceiver();
//        mBackgroundPaint = new Paint();
//        mBackgroundPaint.setTextAlign(Paint.Align.CENTER);

    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String tz = intent.getStringExtra("time-zone");
                mTime = Calendar.getInstance(TimeZone.getTimeZone(tz));
                updateTime();
                //updateBattery(batteryText);
            }
            onTimeChanged();
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

    private final Runnable mClockTick = new Runnable() {
        @Override
        public void run() {
            onTimeChanged();

            if (mEnableSeconds) {
                final long now = System.currentTimeMillis();
                final long delay = SECOND_IN_MILLIS - now % SECOND_IN_MILLIS;
                postDelayed(this, delay);
            }
        }
    };
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter);

        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(mBatteryInfoReceiver, batteryFilter);
        mTime = Calendar.getInstance(mTimeZone != null ? mTimeZone : TimeZone.getDefault());
        onTimeChanged();
        if (mEnableSeconds) {
            mClockTick.run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getContext().unregisterReceiver(mIntentReceiver);
        getContext().unregisterReceiver(mBatteryInfoReceiver);
        removeCallbacks(mClockTick);
    }

    private void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        //setContentDescription(DateFormat.format(mDescFormat, mTime));
        invalidate();
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
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
                    mCircleColor = palette.getLightVibrantColor(Color.BLACK);
                    mPaintShadowColor = palette.getDarkMutedColor(getResources().getColor(R.color.DarkCyan));
                   // updateWatchHandStyle();
                }
            }
        });
    }



    private void initializeWatchFace() {

        /* Set defaults for colors */
        mCircleColor = Color.BLACK;
        mTimePaintColor = Color.WHITE;
        mSecondStickPaintColor = Color.RED;
        mPaintShadowColor = getResources().getColor(R.color.DarkCyan);



//        mBackgroundPaint = new Paint();
//        mBackgroundPaint.setColor(Color.BLACK);
//        mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watchface_service_bg2);

        mTimePaint = new Paint();
        mTimePaint.setColor(getResources().getColor(R.color.White));
        mTimePaint.setTextSize(getResources().getDimension(R.dimen.time_size));
        mTimePaint.setTypeface(Typeface.DEFAULT);
        mTimePaint.setAntiAlias(true);

        mDatePaint = new Paint();
        mDatePaint.setColor(getResources().getColor(R.color.White));
        mDatePaint.setTextSize(getResources().getDimension(R.dimen.date_size));
        mDatePaint.setTypeface(Typeface.SERIF);
        mDatePaint.setAntiAlias(true);

        mBatteryPaint = new Paint();
        mBatteryPaint.setColor(getResources().getColor(R.color.White));
        mBatteryPaint.setTextSize(getResources().getDimension(R.dimen.battery_size));
        mBatteryPaint.setAntiAlias(true);

        mSecondStickPaint = new Paint();
        mSecondStickPaint.setColor(mSecondStickPaintColor);
        mSecondStickPaint.setStrokeWidth(3);
        mSecondStickPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTickAndCirclePaint = new Paint();
        mTickAndCirclePaint.setColor(mTimePaintColor);
        mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
        mTickAndCirclePaint.setAntiAlias(true);
        mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
        mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mTimePaintColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();
        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;
        //drawBackground(canvas);
        drawCirle(canvas, mCenterX, mCenterY);
        drawWatchFace(canvas,mCenterX, mCenterY);
        updateTime();
        //updateBattery(batteryText);
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

    private void drawWatchFace(Canvas canvas, float mCenterX, float mCenterY) {

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        //Rect bounds = canvas.getClipBounds();

//        mRight = this.getRight();
//        mLeft = this.getLeft();
//        mBottom = this.getBottom();
//        mTop = this.getTop();
//
//        int availableWidth = mRight - mLeft;
//        int availableHeight = mBottom - mTop;


        /*
         * Draw ticks. Usually you will want to bake this directly into the photo, but in
         * cases where you want to allow users to select their own photos, this dynamically
         * creates them on top of the photo.
         */
        float innerTickRadius = mCenterX - 40;
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

        String timeText3 = updateTime();
        float timeXOffset = computeXOffset(timeText3, mTimePaint,mCenterX);
        float timeYOffset = mCenterY;
        canvas.drawText(timeText3, timeXOffset, timeYOffset, mTimePaint);

        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT);
        sdf2.setTimeZone(tz);
        String dateText = String.format(sdf2.format(date), sdf2);
        //String dateText = String.format(DATE_FORMAT, mCalendar.get(Calendar.DAY_OF_MONTH), (mCalendar.get(Calendar.MONTH)+1), mCalendar.get(Calendar.YEAR));
        float dateXOffset = computeXOffset(dateText, mDatePaint, mCenterX);
        float dateYOffset = computeYOffset(dateText, mDatePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset , mDatePaint);
//
        float batteryXOffset = computeXOffset(batteryText, mBatteryPaint, mCenterX);
        float batteryYOffset = computeYOffset(batteryText, mBatteryPaint);
        canvas.drawText(batteryText, batteryXOffset, timeYOffset + batteryYOffset + batteryYOffset + dateYOffset, mBatteryPaint);

        float mSecondHandLength = mCenterX-20;
        float secondsRotation = mCalendar.get(Calendar.SECOND) * 6f;       //These calculations reflect the rotation in degrees per unit of time, e.g., * 360 / 60 = 6
        canvas.rotate(secondsRotation, mCenterX, mCenterY);
        canvas.drawLine(mCenterX, mCenterY - 350, mCenterX, mCenterY - mSecondHandLength, mSecondStickPaint);

        /* Restore the canvas" original orientation. */
        //canvas.restore();
    }

    public static String updateTime(){
        Date date = new Date();
        TimeZone tz = TimeZone.getDefault();

        String timeText1 = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE),mCalendar.get(Calendar.SECOND));
        String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));
        SimpleDateFormat sdf1 = new SimpleDateFormat(TIME_FORMAT_12_HOUR);
        sdf1.setTimeZone(tz);
        String timeText3 = String.format(sdf1.format(date), sdf1);
        return timeText3;
    }

    public void updateBattery(String batteryText) {

        this.batteryText = batteryText;
    }

    private void drawCirle(Canvas canvas , float mCenterX, float mCenterY) {
        // TODO Auto-generated method stub
        int radius = ((w * w) / (8 * h) + h / 2);
        canvas.drawCircle(mCenterX, mCenterY, radius/2, mCirclePaint);
        //paint.setStyle(Paint.Style.STROKE);
        //canvas.drawCircle(200, 60, 50, paint);
    }
    private static float computeXOffset(String text, Paint paint, float mCenterX) {
        float centerX = mCenterX;
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeYOffset(String dateText, Paint mDatePaint) {
        Rect textBounds = new Rect();
        mDatePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 30.0f;
    }

    public void updateTimeZoneWith(String timeZone) {
        mCalendar.setTimeZone(TimeZone.getDefault());
        mCalendar = Calendar.getInstance();
    }
}
