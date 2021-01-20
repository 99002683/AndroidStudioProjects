package com.example.firstdigitalwatchcanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.chrono.HijrahEra;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyView extends View {
    Context mContext;
    private int height, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int numeralSpacing = 0;
    private int handTruncation, hourHandTruncation = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private int[] numbers = {1,2,3,4,5,6,7,8,9,10,11,12};
    private Rect rect = new Rect();
    private Paint mTimePaint;
    private Paint mDatePaint;
    private Paint mSecondStickPaint;
    private Paint mTickPaint;
    private int mWidth;
    private int mHeight;
    private Calendar mCalendar;

    private static boolean shouldShowSeconds = true;
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d-%02d-%d";  //"%02d.%02d.%d";
    private static final String DATE_FORMAT1 = "EEE, d MMM yyyy";
    private static final String TIME_FORMAT_24_HOUR = "HH:mm";
    private static final String TIME_FORMAT_12_HOUR = "hh:mm";
    private String batteryText = "100%";

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics());
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        handTruncation = min / 20;
        hourHandTruncation = min / 7;
        paint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initClock();
        }

        canvas.drawColor(Color.BLACK);
        drawCircle(canvas);
        //drawCenter(canvas);
        initializeWatchFace();
        drawWatchFace(canvas);
        //drawNumeral(canvas);
        //drawTicks(canvas);
        //drawHands(canvas);

        postInvalidateDelayed(500);
        invalidate();
    }

    private void drawCenter(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, 12, paint);
    }
    private void drawCircle(Canvas canvas) {
        paint.reset();
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, height / 2, radius + padding - 10, paint);
    }

    private void drawNumeral(Canvas canvas) {
        paint.setTextSize(fontSize);

        for (int number : numbers) {
            String tmp = String.valueOf(number);
            paint.getTextBounds(tmp, 0, tmp.length(), rect);
            double angle = Math.PI / 6 * (number - 3);
            int x = (int) (width / 2 + Math.cos(angle) * radius - rect.width() / 2);
            int y = (int) (height / 2 + Math.sin(angle) * radius + rect.height() / 2);
            canvas.drawText(tmp, x, y, paint);
        }
    }

    private void Ticks(Canvas canvas) {
        paint.setTextSize(fontSize);

        for (int number : numbers) {
            String tmp = String.valueOf(number);
            paint.getTextBounds(tmp, 0, tmp.length(), rect);
            double angle = Math.PI / 6 * (number - 3);
            int x = (int) (width / 2 + Math.cos(angle) * radius - rect.width() / 2);
            int y = (int) (height / 2 + Math.sin(angle) * radius + rect.height() / 2);
            canvas.drawText(tmp, x, y, mTickPaint);
        }
    }

    private void drawTicks(Canvas canvas){
        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;
        /*
         * Draw ticks. Usually you will want to bake this directly into the photo, but in
         * cases where you want to allow users to select their own photos, this dynamically
         * creates them on top of the photo.
         */
        float innerTickRadius = mCenterX - 30;
        float outerTickRadius = mCenterX - 10;

        for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
            float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
            float innerX = (float) Math.sin(tickRot) * innerTickRadius;
            float innerY = (float) - Math.cos(tickRot) * innerTickRadius;
            float outerX = (float) Math.sin(tickRot) * outerTickRadius;
            float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
            canvas.drawLine(mCenterX + innerX, mCenterY + innerY, mCenterX + outerX, mCenterY + outerY, mTickPaint);
        }
    }
    private void initializeWatchFace() {
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

        mSecondStickPaint = new Paint();
        mSecondStickPaint.setColor(getResources().getColor(R.color.red));
        mSecondStickPaint.setStrokeWidth(5);
        mSecondStickPaint.setAntiAlias(true);

        mTickPaint = new Paint();
        mTickPaint.setColor(getResources().getColor(R.color.white));
        mTickPaint.setStrokeWidth(7);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        //mTickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mTimePaintColor);
    }
    private void drawWatchFace(Canvas canvas) {

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        mCalendar = Calendar.getInstance();
        Date date = new Date();
        TimeZone tz = TimeZone.getDefault();

        //Time
        String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));

//        SimpleDateFormat sdf1 = new SimpleDateFormat(TIME_FORMAT_12_HOUR);
//        sdf1.setTimeZone(tz);
//        String timeText3 = String.format(sdf1.format(date), sdf1);
        float timeXOffset = computeXOffset(timeText2, mTimePaint, mCenterX);
        float timeYOffset = mCenterY;
        canvas.drawText(timeText2, timeXOffset, timeYOffset, mTimePaint);

        //Date
        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT1);
        sdf2.setTimeZone(tz);
        String dateText = String.format(sdf2.format(date), sdf2);
        //String monthText = String.format((new SimpleDateFormat(DATE_FORMAT_MONTH).format(Calendar.MONTH)));
        //String dateText = new SimpleDateFormat(DATE_FORMAT1).format(mCalendar.get(Calendar.DAY_OF_MONTH) + mCalendar.get(Calendar.DATE) + mCalendar.get(Calendar.MONTH) + mCalendar.get(Calendar.YEAR));
        float dateXOffset = computeXOffset(dateText, mDatePaint, mCenterX);
        float dateYOffset = computeYOffset(dateText, mDatePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, mDatePaint);

        //Seconds
        float mSecondHandLength = mCenterX - 10;
        float secondsRotation = mCalendar.get(Calendar.SECOND)*6f;       //These calculations reflect the rotation in degrees per unit of time, e.g., * 360 / 60 = 6
        canvas.rotate(secondsRotation, mCenterX, mCenterY);
        //canvas.drawLine(mCenterX, mCenterY - 350, mCenterX, mCenterY - mSecondHandLength, mSecondStickPaint);
        canvas.drawLine(mCenterX, mCenterY - 350, mCenterX, mCenterY - mSecondHandLength, mSecondStickPaint);
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

    private void drawHand(Canvas canvas, double loc, boolean isHour) {
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = isHour ? radius - handTruncation - hourHandTruncation : radius - handTruncation;
        float mSecondHandLength = (mCenterX-10);
        //canvas.drawLine(width / 2, height/2, (float) (mCenterX + Math.cos(angle) * mSecondHandLength), (float) (height / 2 + Math.sin(angle)* mSecondHandLength), paint);
        //canvas.drawLine(mCenterX , mCenterY , (float) (mCenterX + Math.cos(angle) * handRadius), (float) (height/2  + Math.sin(angle)* handRadius), paint);
        canvas.drawLine(mCenterX, mCenterY, (float)(mCenterX + Math.cos(angle) * handRadius), (float)(mCenterY + Math.sin(angle)* handRadius), mSecondStickPaint);
    }

    private void drawHands(Canvas canvas) {
        Calendar c = Calendar.getInstance();
        float hour = c.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;
        //drawHand(canvas, (hour + c.get(Calendar.MINUTE) / 60) * 5f, true);
        //
        // drawHand(canvas, c.get(Calendar.MINUTE), false);
        drawHand(canvas, c.get(Calendar.SECOND), false);
    }
}
