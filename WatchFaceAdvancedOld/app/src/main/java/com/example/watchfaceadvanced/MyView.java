package com.example.watchfaceadvanced;
import android.app.Activity;
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.view.View;

import androidx.palette.graphics.Palette;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyView extends View {
    private static final float SECOND_TICK_STROKE_WIDTH = 2f;

    private Paint mTimePaint;
    private Paint mDatePaint;
    private Paint mBatteryPaint;
    private Paint mSecondStickPaint;
    private Paint mTickAndCirclePaint;
    private Calendar mCalendar;
    private Paint mBackgroundPaint;
    private Bitmap mBackgroundBitmap;
    private Bitmap mGrayBackgroundBitmap;

    private int mTimePaintColor;
    private int mSecondStickPaintColor;
    private int mPaintShadowColor;

    private boolean mAmbient;
    private boolean mLowBitAmbient;
    private boolean mBurnInProtection;
    Rect bounds;

    private int mWidth;
    private int mHeight;
    private boolean shouldShowSeconds = true;
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
                   // updateWatchHandStyle();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);

        drawBackground(canvas);
        drawWatchFace(canvas);
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

    private void drawWatchFace(Canvas canvas) {

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();
        //Rect bounds = canvas.();

//        mRight = this.getRight();
//        mLeft = this.getLeft();
//        mBottom = this.getBottom();
//        mTop = this.getTop();
//
//        int availableWidth = mRight - mLeft;
//        int availableHeight = mBottom - mTop;

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

        //String timeText1 = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE),mCalendar.get(Calendar.SECOND));
        //String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));
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

}
