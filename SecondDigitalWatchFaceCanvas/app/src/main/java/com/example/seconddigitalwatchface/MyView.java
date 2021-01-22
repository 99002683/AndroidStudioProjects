package com.example.seconddigitalwatchface;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
    int percent;
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
    private Paint mHandsPaint;
    private int centerX, centerY;
    private int mWidth;
    private int mHeight;
    private Calendar mCalendar;
    private String mDescFormat;
    private TimeZone mTimeZone;

    private String text = "ABC";
    private Paint mPaint;
    private Paint mncirclePaint;
    RectF mRectF;


    private static boolean shouldShowSeconds = true;
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d-%02d-%d";  //"%02d.%02d.%d";
    private static final String DATE_FORMAT_DATE = "%02d";
    private static final String DATE_FORMAT_MONTH = "MMM";
    private static final String TIME_FORMAT_24_HOUR = "HH:mm";
    private static final String TIME_FORMAT_12_HOUR = "hh:mm";
    private static final String DATE_FORMAT_DAY = "EEE";
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

        mCalendar = Calendar.getInstance();
        //mDescFormat = ((SimpleDateFormat) DateFormat.getTimeFormat(mContext)).toLocalizedPattern();
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics());
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        handTruncation = (min / 20)*8;   //8
        hourHandTruncation = (min / 7)*3;    //5
        paint = new Paint();
        isInit = true;
        percent = 100;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initClock();
        }

        canvas.drawColor(getResources().getColor(R.color.grey));
        drawCircle(canvas);
        //drawCenter(canvas);
        initializeWatchFace();
        drawWatchFace(canvas);
        drawHands(canvas);
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
        paint.setColor(getResources().getColor(android.R.color.darker_gray));
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
        mTickPaint.setStrokeWidth(getResources().getDimension(R.dimen.default_stroke_width));
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        //mTickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mTimePaintColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(getResources().getColor(R.color.black));
        mCirclePaint.setStrokeWidth(getResources().getDimension(R.dimen.default_stroke_width));
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setTextAlign(Paint.Align.CENTER);

        mHandsPaint = new Paint();
        mHandsPaint.setColor(getResources().getColor(R.color.white));
        mHandsPaint.setStrokeWidth(getResources().getDimension(R.dimen.default_stroke_width));
        mHandsPaint.setAntiAlias(true);
        mHandsPaint.setStyle(Paint.Style.STROKE);
        mHandsPaint.setTextAlign(Paint.Align.CENTER);

        mDayPaint = new Paint();
        mDayPaint.setColor(getResources().getColor(R.color.white));
        mDayPaint.setTextSize(getResources().getDimension(R.dimen.day_size));
        //mDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        mDayPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDayPaint.setLetterSpacing(0.3f);
        }
        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextAlign(Paint.Align.CENTER);

        mMonthPaint = new Paint();
        mMonthPaint.setColor(getResources().getColor(R.color.white));
        mMonthPaint.setTextSize(getResources().getDimension(R.dimen.day_size));
        mMonthPaint.setTypeface(Typeface.SERIF);
        mMonthPaint.setAntiAlias(true);
        mMonthPaint.setTextAlign(Paint.Align.CENTER);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(200, 200, conf);

        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG |
                Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.parseColor("#33b5e5"));
        mPaint.setStrokeWidth(15);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCalendar = Calendar.getInstance(mTimeZone != null ? mTimeZone : TimeZone.getDefault());
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void drawWatchFace(Canvas canvas) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//
//        ((Activity) getContext()).getWindowManager()
//                .getDefaultDisplay()
//                .getMetrics(displayMetrics);
//
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

//        mWidth = screenWidth;
//        mHeight = screenHeight;

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        mCalendar = Calendar.getInstance();
        Date date = new Date();
        mTimeZone = TimeZone.getDefault();


        //String timeText1 = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE),mCalendar.get(Calendar.SECOND));
        //String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));
        SimpleDateFormat sdf1 = new SimpleDateFormat(TIME_FORMAT_12_HOUR);
        sdf1.setTimeZone(mTimeZone);
        String timeText3 = String.format(sdf1.format(date), sdf1);

        float timeXOffset = computeXOffset(timeText3, mTimePaint, mCenterX);
        float timeYOffset = mCenterY;
        canvas.drawText(timeText3, timeXOffset, timeYOffset, mTimePaint);

//        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT);
//        sdf2.setTimeZone(tz);
//        String dateText = String.format(sdf2.format(date), sdf2);

//        float dateXOffset = computeXOffset(dateText, mDatePaint, mCenterX);
        //float dateYOffset = computeYOffset(dateText, mDatePaint);
//        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, mDatePaint);




        //Second Circle with Date and Month Text
        String dateText = String.format(DATE_FORMAT_DATE, mCalendar.get(Calendar.DAY_OF_MONTH));
        canvas.drawCircle(mCenterX + timeXOffset - padding , mCenterY, (radius/4) , mCirclePaint);
        canvas.drawText(dateText, mCenterX + timeXOffset - padding , mCenterY - (padding * 0.3f), mDatePaint);

        String monthText = String.format((new SimpleDateFormat(DATE_FORMAT_MONTH).format(Calendar.MONTH)));
        float dateYOffset = computeYOffset(monthText, mDatePaint);
        //canvas.drawCircle(mCenterX + timeXOffset - padding , mCenterY, (radius/4) , mCirclePaint);
        canvas.drawText(monthText, mCenterX + timeXOffset - padding, mCenterY + dateYOffset - (padding * 0.3f), mMonthPaint);

        //Lower Rectangle
        mRectF = new RectF(mCenterX - timeXOffset + padding , mCenterY + (dateYOffset * 2.5f) + padding , mCenterX + timeXOffset -padding ,mCenterY +  timeXOffset );
       // mRectF = new RectF(mCenterX -200 , mCenterY + 100, mCenterX + 200 ,mCenterY + 200);
        canvas.drawRoundRect(mRectF, 600, 600, mCirclePaint);
        canvas.drawText("No Upcoming Events", mCenterX , mCenterY +  timeXOffset -(padding *1.7f), mDatePaint);


        //Upper Circle
        String dayText = String.format(new SimpleDateFormat(DATE_FORMAT_DAY).format(Calendar.DAY_OF_MONTH));
        canvas.drawCircle(mCenterX , mCenterY - timeXOffset + padding, radius/4, mCirclePaint);
        //canvas.drawCircle(mCenterX , mCenterY - 450, 100 + padding - 10, mCirclePaint);
        canvas.drawText(dayText.toUpperCase(), mCenterX, mCenterY - timeXOffset + padding, mDayPaint);


        //Upper Circle Arc
        RectF box1 = new RectF(mCenterX - (padding *3)   ,mCenterY - timeXOffset -(padding*2f) ,mCenterX + (padding *3),mCenterY - timeXOffset + (padding*4f));
        //RectF box = new RectF(mCenterX - (dateYOffset * 1.5f)   ,mCenterY - timeXOffset -(padding*2) ,mCenterX +(dateYOffset*1.5f),mCenterY - (dateYOffset *2.6f)-padding);
        float sweep = 360 * percent * 0.01f;
        canvas.drawArc(box1, 0, sweep, false, mPaint);

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

    private void drawHand(Canvas canvas, double loc, boolean isHour) {
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        float mCenterX = mWidth / 2f;
        float mCenterY = mHeight / 2f;

        //First Circle with Analog Clock
        String timeText2 = String.format(TIME_FORMAT_WITHOUT_SECONDS, mCalendar.get(Calendar.HOUR),mCalendar.get(Calendar.MINUTE));
        float timeXOffset = computeXOffset(timeText2, mTimePaint, mCenterX);
        float timeYOffset = mCenterY;

        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = isHour ? radius - hourHandTruncation : radius - handTruncation;
        canvas.drawCircle(mCenterX - timeXOffset + padding , mCenterY, (radius/4) , mCirclePaint);
        canvas.drawLine(mCenterX - timeXOffset + padding , mCenterY,
                (float) (mCenterX - timeXOffset+ padding + Math.cos(angle) * handRadius),
                (float) (mCenterY + Math.sin(angle) * handRadius),
                mHandsPaint);
    }

    private void drawHands(Canvas canvas) {
        Calendar c = Calendar.getInstance();
        float hour = c.get(Calendar.HOUR_OF_DAY);
        hour = hour > 12 ? hour - 12 : hour;
        drawHand(canvas, (hour + c.get(Calendar.MINUTE) / 60) * 5f, true);
        drawHand(canvas, c.get(Calendar.MINUTE), false);
        drawHand(canvas, c.get(Calendar.SECOND), false);
    }

    private void onTimeChanged() {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        //setContentDescription(DateFormat.format(mDescFormat, mCalendar));
        invalidate();
    }
    
}