package com.example.seconddigitalwatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    private Paint mDayPaint;
    private Paint mMonthPaint;
    private Paint mSecondStickPaint;
    private Paint mTickPaint;
    private Paint mCirclePaint;
    private int centerX, centerY;
    private int mWidth;
    private int mHeight;
    private Calendar mCalendar;

    private String text = "ABCDEFGHIJKLMN";
    private Paint mnpaint;
    private Paint mncirclePaint;


    private static boolean shouldShowSeconds = true;
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d-%02d-%d";  //"%02d.%02d.%d";
    private static final String DATE_FORMAT_DAY = "%02d";
    private static final String DATE_FORMAT_MONTH = "MMM";
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
        mDatePaint.setTextAlign(Paint.Align.CENTER);

        mTickPaint = new Paint();
        mTickPaint.setColor(getResources().getColor(R.color.white));
        mTickPaint.setStrokeWidth(7);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        //mTickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mTimePaintColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(getResources().getColor(R.color.white));
        mCirclePaint.setStrokeWidth(7);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setTextAlign(Paint.Align.CENTER);

        mDayPaint = new Paint();
        mDayPaint.setColor(getResources().getColor(R.color.white));
        mDayPaint.setTextSize(getResources().getDimension(R.dimen.day_size));
        mDayPaint.setTypeface(Typeface.SERIF);
        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextAlign(Paint.Align.CENTER);

        mMonthPaint = new Paint();
        mMonthPaint.setColor(getResources().getColor(R.color.white));
        mMonthPaint.setTextSize(getResources().getDimension(R.dimen.day_size));
        mMonthPaint.setTypeface(Typeface.SERIF);
        mMonthPaint.setAntiAlias(true);
        mMonthPaint.setTextAlign(Paint.Align.CENTER);

        mnpaint = new Paint();
        mnpaint.setColor(Color.WHITE);
        mnpaint.setTextSize(18f);
        mnpaint.setAntiAlias(true);
        mnpaint.setTextAlign(Paint.Align.CENTER);


    }
    private void drawWatchFace(Canvas canvas) {

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        mCalendar = Calendar.getInstance();
        Date date = new Date();
        TimeZone tz = TimeZone.getDefault();


        //String timeText1 = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE),mCalendar.get(Calendar.SECOND));
        String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));

//        SimpleDateFormat sdf1 = new SimpleDateFormat(TIME_FORMAT_12_HOUR);
//        sdf1.setTimeZone(tz);
//        String timeText3 = String.format(sdf1.format(date), sdf1);

        float timeXOffset = computeXOffset(timeText2, mTimePaint, mCenterX);
        float timeYOffset = mCenterY;
        canvas.drawText(timeText2, timeXOffset, timeYOffset, mTimePaint);

//        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT);
//        sdf2.setTimeZone(tz);
//        String dateText = String.format(sdf2.format(date), sdf2);

//        float dateXOffset = computeXOffset(dateText, mDatePaint, mCenterX);
        //float dateYOffset = computeYOffset(dateText, mDatePaint);
//        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, mDatePaint);

        canvas.drawCircle( mCenterX - timeXOffset + padding , mCenterY, (radius/4) , mCirclePaint);




        String dateText = String.format(DATE_FORMAT_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
        Rect bounds1 = new Rect();
        mDayPaint.getTextBounds(dateText, 0, dateText.length(), bounds1);
        canvas.drawCircle(mCenterX + timeXOffset - padding , mCenterY, (radius/4) , mCirclePaint);
        canvas.drawText(dateText, mCenterX + timeXOffset - padding , mCenterY, mDayPaint);

        String monthText = String.format((new SimpleDateFormat(DATE_FORMAT_MONTH).format(Calendar.MONTH)));
        float dateYOffset = computeYOffset(monthText, mDatePaint);
        Rect bounds2 = new Rect();
        mDayPaint.getTextBounds(monthText, 0, monthText.length(), bounds2);
        //canvas.drawCircle(mCenterX + timeXOffset - padding , mCenterY, (radius/4) , mCirclePaint);
        canvas.drawText(monthText, mCenterX + timeXOffset - padding , mCenterY + dateYOffset, mMonthPaint);

        //canvas.drawCircle(mCenterX , mCenterY - 450, 100 + padding - 10, mCirclePaint);

       // canvas.drawText("Circle", canvas.getWidth() / 2, canvas.getHeight() / 2, mCirclePaint);

//        Rect bounds = new Rect();
//        mnpaint.getTextBounds(text, 0, text.length(), bounds);
//        canvas.drawCircle(100, 15 - (bounds.height() / 2), bounds.width() + 5, mncirclePaint);
//
//        canvas.drawText(text, 100, 15, mnpaint);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }


}