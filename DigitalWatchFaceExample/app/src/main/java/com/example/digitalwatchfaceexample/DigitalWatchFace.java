package com.example.digitalwatchfaceexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.icu.lang.UCharacter.toUpperCase;
import static android.support.wearable.watchface.WatchFaceService.PROPERTY_BURN_IN_PROTECTION;
import static android.support.wearable.watchface.WatchFaceService.PROPERTY_LOW_BIT_AMBIENT;

public class DigitalWatchFace {


    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String TIME_FORMAT_HOURS = "%02d";
    private static final String TIME_FORMAT_MINUTES = "%02d";
    private static final String DATE_FORMAT = "%d/%02d/%02d";   //"%02d.%02d.%d"
    private static final String DAY_FORMAT = "E";

    //private final Paint timePaint;
    private final Paint hourPaint, minutePaint, datePaint, backgroundPaint, dayPaint;
    Paint heartImgPaint;

//   private final Paint batteryPaint;
    //private final Paint secondStickPaint;

    static Calendar mCalendar;


    private boolean shouldShowSeconds = true;

    //private String batteryText = "100%";

    private int mWidth;
    private int mHeight;
    private boolean mAmbient;
    private boolean mLowBitAmbient;
    private boolean mBurnInProtection;
    Rect mRedPaddleRect;
    private Bitmap mHeartImg, mFootstepsBitmap, mCaloriesBitmap;


    public static DigitalWatchFace newInstance(Context context){

//        Paint timePaint = new Paint();
//        timePaint.setColor(context.getResources().getColor(R.color.primaryColorBlue));
//        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
//        timePaint.setAntiAlias(true);

        Paint hourPaint = new Paint();
        //hourPaint.setStyle(Paint.Style.STROKE);
        hourPaint.setColor(context.getResources().getColor(R.color.white));
        hourPaint.setTypeface(Typeface.SERIF);
        hourPaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        //hourPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.paint_width));
        //hourPaint.setTypeface(se)
        hourPaint.setAntiAlias(true);

        Paint minutePaint = new Paint();
        minutePaint.setColor(context.getResources().getColor(R.color.white));
        minutePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        minutePaint.setTypeface(Typeface.SERIF);
        minutePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(context.getResources().getColor(R.color.white));
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        //datePaint.setTypeface(Typeface.SANS_SERIF);
        //datePaint.setLetterSpacing(0.1f);
        datePaint.setTypeface(Typeface.SERIF);
        datePaint.setAntiAlias(true);

        Paint dayPaint = new Paint();
        dayPaint.setColor(context.getResources().getColor(R.color.white));
        dayPaint.setTextSize(context.getResources().getDimension(R.dimen.day_size));
        //dayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        dayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        dayPaint.setLetterSpacing(0.3f);
        dayPaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(context.getResources().getColor(R.color.black));

//        Paint heartImgPaint = new Paint();
//        heartImgPaint.setStyle(Paint.Style.FILL);
//        heartImgPaint.setColor(Color.BLUE);
//        heartImgPaint.setBitmap(R.drawable.heart1);

        //float scale = ((float) width) / (float) mBackgroundBitmap.getWidth();
        Paint heartImgPaint = new Paint();
        heartImgPaint.setColor(Color.BLACK);
        heartImgPaint.setAntiAlias(true);
        Bitmap mHeartImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart4);
        Bitmap mFootstepsBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.footsteps1);
        Bitmap mCaloriesBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.calories2);
//        mHeartImg = Bitmap.createScaledBitmap(mHeartImg,
//                (int) (mHeartImg.getWidth() * scale),
//                (int) (mHeartImg.getHeight() * scale), true);
//        //Bitmap mHeartImg = BitmapFactory.decodeResource(context.getResources(),R.drawable.heart1);

//        Paint batteryPaint = new Paint();
//        batteryPaint.setColor(context.getResources().getColor(R.color.primaryColorBlue));
//        batteryPaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
//        batteryPaint.setAntiAlias(true);
//
//        Paint secondStickPaint = new Paint();
//        secondStickPaint.setColor(context.getResources().getColor(R.color.primaryColorBlue));
//        secondStickPaint.setStrokeWidth(3);
//        secondStickPaint.setAntiAlias(true);

        return new DigitalWatchFace(hourPaint, minutePaint, datePaint, dayPaint, backgroundPaint, mHeartImg, mFootstepsBitmap, mCaloriesBitmap);    }

    DigitalWatchFace(Paint hourPaint, Paint minutePaint, Paint datePaint, Paint dayPaint, Paint backgroundPaint,Bitmap mHeartImg, Bitmap mFootstepsBitmap, Bitmap mCaloriesBitmap) {
        //this.timePaint = timePaint;
        this.hourPaint = hourPaint;
        this.minutePaint = minutePaint;
        this.datePaint = datePaint;
        this.dayPaint = dayPaint;
        this.backgroundPaint = backgroundPaint;
        this.mHeartImg = mHeartImg;
        this.mFootstepsBitmap = mFootstepsBitmap;
        this.mCaloriesBitmap = mCaloriesBitmap;
        //this.heartImgPaint = heartImgPaint;

//        this.batteryPaint = batteryPaint;
//        this.secondStickPaint = secondStickPaint;

    }

    public void propertiesChanged(Bundle properties){
        mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
    }



    public void draw(Canvas canvas, Rect bounds){
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);



//        int hour = mCalendar.get(Calendar.HOUR);
//        hour = hour == 0 ? 12 : hour;
//
//        String month = new SimpleDateFormat("MMM dd").format(mCalendar.getTime());
//        String text = String.format("%d:%02d", hour, mCalendar.get(Calendar.MINUTE));
//        String date = month;
//        String seconds_text = String.format("%02d", mCalendar.get(Calendar.SECOND));
//        int seconds_now = mCalendar.get(Calendar.SECOND);
//        int millis_now = mCalendar.get(Calendar.MILLISECOND);

        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

//        String timeText = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute, time.second);
//        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
//        float timeYOffset = bounds.centerY();
//        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);


        String hourText = String.format(TIME_FORMAT_HOURS,mCalendar.get(Calendar.HOUR));
        float hourXOffset = computeXOffset(hourText, hourPaint, bounds);
        float hourYOffset = (float) (mHeight * 0.4);
        canvas.drawText(hourText, hourXOffset , hourYOffset, hourPaint);

        String minuteText = String.format(TIME_FORMAT_MINUTES,mCalendar.get(Calendar.MINUTE));
        float minuteXOffset =  computeXOffset(minuteText, minutePaint, bounds);
        float minuteYOffset = (float) (mHeight * 0.6);
        canvas.drawText(minuteText, minuteXOffset, minuteYOffset, minutePaint);

        String dateText = String.format(DATE_FORMAT, mCalendar.get(Calendar.YEAR), (mCalendar.get(Calendar.MONTH)+1), mCalendar.get(Calendar.DATE));
        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
        float dateYOffset = computeYOffset(dateText, datePaint);
        canvas.drawText(dateText, dateXOffset + 10f, minuteYOffset + 35.0f, datePaint);

        String month = toUpperCase(new SimpleDateFormat(DAY_FORMAT).format(mCalendar.getTime()));
        canvas.drawText(month, minuteXOffset + 10f, minuteYOffset + 62.f , dayPaint);



        //canvas.drawBitmap(bitmap, null, mRedPaddleRect, heartImgPaint);
        //canvas.drawBitmap(mHeartImg, 0, 0, heartImgPaint);
        canvas.drawBitmap(mHeartImg, null, new RectF(hourXOffset + 140, 90 , 230, 110), null);
        canvas.drawBitmap(mFootstepsBitmap, null, new RectF(minuteXOffset + 140, 140 , 230, 160), null);
        canvas.drawBitmap(mCaloriesBitmap, null, new RectF(minuteXOffset + 140, 190 , 230, 220), null);
        //canvas.drawRect(0, 0, bounds.width(), bounds.height(), null);

//        float batteryXOffset = computeXOffset(batteryText, batteryPaint, bounds);
//        float batteryYOffset = computeYOffset(batteryText, batteryPaint);
//        canvas.drawText(batteryText, batteryXOffset, timeYOffset + dateYOffset + batteryYOffset, batteryPaint);

//        float mSecondHandLength = mCenterX;
//        float secondsRotation = time.second * 6f;
//        canvas.rotate(secondsRotation, mCenterX, mCenterY);
//        canvas.drawLine(mCenterX, mCenterY - 120, mCenterX, mCenterY - mSecondHandLength, secondStickPaint);
    }


    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float hourXOffset = (float) (mWidth * 0.2);
        return hourXOffset;
    }

//    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
//        float centerX = watchBounds.exactCenterX();
//        float timeLength = paint.measureText(text);
//        return centerX - (timeLength / 2.0f);
//    }

    private float computeYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 15.0f;
    }

    public void updateTimeZoneWith(String timeZone) {
        mCalendar.setTimeZone(TimeZone.getDefault());
    }

//    public void updateBattery(String batteryText) {
//        this.batteryText = batteryText;
//    }


    public void AmbientModeChanged(boolean inAmbientMode){
          mAmbient = inAmbientMode;

          if(mLowBitAmbient){
              hourPaint.setAntiAlias(!inAmbientMode);
              minutePaint.setAntiAlias(!inAmbientMode);
              datePaint.setAntiAlias(!inAmbientMode);
              backgroundPaint.setAntiAlias(!inAmbientMode);
              dayPaint.setAntiAlias(!inAmbientMode);
          }

    }

    public void isInAmbientMode(Canvas canvas, Rect bounds){
        canvas.drawColor(Color.BLACK);
    }
    public void isNotInAmbientMode(Canvas canvas, Rect bounds){
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);
    }

}