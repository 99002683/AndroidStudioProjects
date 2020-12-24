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
    private static final String TIME_FORMAT_24_HOURS = "HH";       //Use for 24 Hour Time Format
    private static final String TIME_FORMAT_12_HOURS = "hh";     //Use for 12 Hour Time Format  //%02d
    private static final String TIME_FORMAT_MINUTES = "%02d";
    private static final String DATE_FORMAT = "%d/%02d/%02d";   //"%02d.%02d.%d"
    private static final String DAY_FORMAT = "E";

    private final Paint hourPaint, minutePaint, datePaint, backgroundPaint, dayPaint;
//  private final Paint batteryPaint;
//  private String batteryText = "100%";
    static Calendar mCalendar;

    private int mWidth;
    private int mHeight;
    private boolean mAmbient;
    private boolean mLowBitAmbient;
    private boolean mBurnInProtection;
    private Bitmap mBpmBitmap, mFootstepsBitmap, mCaloriesBitmap;

    public static DigitalWatchFace newInstance(Context context)     //newInstance() method of Constructor class can invoke any number of arguments.
    {

        Paint hourPaint = new Paint();      //The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
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

        //float scale = ((float) width) / (float) mBackgroundBitmap.getWidth();
        Paint heartImgPaint = new Paint();
        heartImgPaint.setColor(Color.BLACK);
        heartImgPaint.setAntiAlias(true);
        Bitmap mBpmBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart4);
        Bitmap mFootstepsBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.footsteps1);
        Bitmap mCaloriesBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.calories2);
//        mBpmBitmap = Bitmap.createScaledBitmap(mBpmBitmap,
//                (int) (mBpmBitmap.getWidth() * scale),
//                (int) (mBpmBitmap.getHeight() * scale), true);
//        //Bitmap mBpmBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.heart1);

//        Paint batteryPaint = new Paint();
//        batteryPaint.setColor(context.getResources().getColor(R.color.primaryColorBlue));
//        batteryPaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
//        batteryPaint.setAntiAlias(true);

        return new DigitalWatchFace(hourPaint, minutePaint, datePaint, dayPaint, backgroundPaint, mBpmBitmap, mFootstepsBitmap, mCaloriesBitmap);
    }

    DigitalWatchFace(Paint hourPaint, Paint minutePaint, Paint datePaint, Paint dayPaint, Paint backgroundPaint,Bitmap mBpmBitmap, Bitmap mFootstepsBitmap, Bitmap mCaloriesBitmap)
    {
        this.hourPaint = hourPaint;
        this.minutePaint = minutePaint;
        this.datePaint = datePaint;
        this.dayPaint = dayPaint;
        this.backgroundPaint = backgroundPaint;
        this.mBpmBitmap = mBpmBitmap;
        this.mFootstepsBitmap = mFootstepsBitmap;
        this.mCaloriesBitmap = mCaloriesBitmap;
//      this.batteryPaint = batteryPaint;
    }

    public void propertiesChanged(Bundle properties)    //Called when the properties of the device are determined.
    {
        mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
    }

    public void draw(Canvas canvas, Rect bounds)    //onDraw() method draws strings on the watch face.
                                                    //When Android calls OnDraw, it passes in a Canvas instance and the bounds in which the face can be drawn.
    {
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);

        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        Date date = new Date();
        TimeZone tz = TimeZone.getDefault();

//      SimpleDateFormat allows to start by choosing any user-defined patterns for date-time formatting.
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_12_HOURS);
        sdf.setTimeZone(tz);
        String hourText = String.format(sdf.format(date), sdf);
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


        canvas.drawBitmap(mBpmBitmap, null, new RectF(hourXOffset + 140, 90 , 230, 110), null);
        canvas.drawBitmap(mFootstepsBitmap, null, new RectF(minuteXOffset + 140, 140 , 230, 160), null);
        canvas.drawBitmap(mCaloriesBitmap, null, new RectF(minuteXOffset + 140, 190 , 230, 220), null);
        //canvas.drawRect(0, 0, bounds.width(), bounds.height(), null);

//        float batteryXOffset = computeXOffset(batteryText, batteryPaint, bounds);
//        float batteryYOffset = computeYOffset(batteryText, batteryPaint);
//        canvas.drawText(batteryText, batteryXOffset, timeYOffset + dateYOffset + batteryYOffset, batteryPaint);

    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds)
    {
        float hourXOffset = (float) (mWidth * 0.2);
        return hourXOffset;
    }

//    private float computeXOffset(String text, Paint paint, Rect watchBounds)
//    {
//        float centerX = watchBounds.exactCenterX();
//        float timeLength = paint.measureText(text);
//        return centerX - (timeLength / 2.0f);
//    }

    private float computeYOffset(String dateText, Paint datePaint)
    {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 15.0f;
    }

    public void updateTimeZoneWith(String timeZone)
    {
        mCalendar.setTimeZone(TimeZone.getDefault());
    }
//    public void updateBattery(String batteryText)
//    {
//        this.batteryText = batteryText;
//    }

    public void AmbientModeChanged(boolean inAmbientMode)
    {
          mAmbient = inAmbientMode;

          if(mLowBitAmbient){    //if the watch is in low bit ambient mode:
              hourPaint.setAntiAlias(!inAmbientMode);
              minutePaint.setAntiAlias(!inAmbientMode);
              datePaint.setAntiAlias(!inAmbientMode);
              backgroundPaint.setAntiAlias(!inAmbientMode);
              dayPaint.setAntiAlias(!inAmbientMode);
          }
    }

    public void isInAmbientMode(Canvas canvas, Rect bounds)
    {
        //canvas.drawColor(Color.DKGRAY);
        backgroundPaint.setColor(Color.GRAY);
        //canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);
        hourPaint.setColor(Color.BLACK);
        minutePaint.setColor(Color.BLACK);
        datePaint.setColor(Color.BLACK);
        dayPaint.setColor(Color.BLACK);
    }

    public void isNotInAmbientMode(Canvas canvas, Rect bounds)
    {
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);
        backgroundPaint.setColor(Color.BLACK);
        hourPaint.setColor(Color.WHITE);
        minutePaint.setColor(Color.WHITE);
        datePaint.setColor(Color.WHITE);
        dayPaint.setColor(Color.WHITE);
    }
}