package com.example.keyboardplus;

import android.app.ActionBar;
import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;

import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements Button.OnClickListener, SensorEventListener {

    private SensorManager mSensorManager;

    private Sensor mSensor;

    private ToneGenerator tonegen;

    private TextView display;

    private Switch switchPhone;

    private TextToVoice ttv;


    Toolbar toolbar;
    private Timer speakTimer;
    private TimerTask speaker;
    ImageView clearAll;

   ImageView callB;
    ImageView speak;
    Button pound;
    private final int REQ_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



        ActionBar b = getActionBar();
        if (b != null) b.hide();

        display = (TextView) findViewById(R.id.display);
        switchPhone = (Switch) findViewById(R.id.switchPhone);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        clearAll =(ImageView) findViewById(R.id.buttonClear);

        callB =(ImageView)findViewById(R.id.buttonCall);
        pound =(Button)findViewById(R.id.buttonpound);
        ImageView simpleImageView=(ImageView) findViewById(R.id.buttonCall);
        simpleImageView.setImageResource(R.drawable.callkey);



        findViewById(R.id.about_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(about);
            }
        });
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        display.setText("");
                        //equal.setText("=");
                    }
                });
            }
        });
//        pound.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                display.setText("#");
//            }
//        });


        callB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(Intent.ACTION_DIAL);
                String number = display.getText().toString();
                intent.setData(Uri.parse("tel:"+ Uri.encode(number)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Speech To Text Mode",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Sorry your device not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(),"Text to Speech Mode",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //display.setText(result.get(0));
//                    ArrayList<String> gfg = new ArrayList<String>(
//                            Arrays.asList("1",
//                                    "2",
//                                    "3"));
//                    // For Loop for iterating ArrayList
//                    for (int i = 0; i < gfg.size(); i++){

                    if(result.get(0).equals("0")||result.get(0).equals("1")||result.get(0).equals("2")||result.get(0).equals("3")||result.get(0).equals("4")||result.get(0).equals("5")||result.get(0).equals("6")||result.get(0).equals("7")||result.get(0).equals("8")||result.get(0).equals("9")||result.get(0).equals("*")||result.get(0).equals("#"))
                        display.setText(result.get(0));
                    else if(result.get(0).equals("zero")){
                        display.setText("0");
                    }
                    else if(result.get(0).equals("one")){
                        display.setText("1");
                    }
                    else if(result.get(0).equals("two")){
                        display.setText("2");
                    }
                    else if(result.get(0).equals("tu")){
                        display.setText("2");
                    }
                    else if(result.get(0).equals("three")){
                        display.setText("3");
                    }
                    else if(result.get(0).equals("four")){
                        display.setText("4");
                    }
                    else if(result.get(0).equals("five")){
                        display.setText("5");
                    }
                    else if(result.get(0).equals("six")){
                        display.setText("6");
                    }
                    else if(result.get(0).equals("seven")){
                        display.setText("7");
                    }
                    else if(result.get(0).equals("eight")){
                        display.setText("8");
                    }
                    else if(result.get(0).equals("nine")){
                        display.setText("9");
                    }
                    else if(result.get(0).equals("star")){
                        display.setText("*");
                    }
                    else if(result.get(0).equals("hash")){
                        display.setText("#");
                    }
                    else if(result.get(0).equals("harsh")){
                        display.setText("#");
                    }
                    else if(result.get(0).equals("Criss cross")){
                        display.setText("#");
                    }
                    else{
                        String str = result.get(0);
                        for(int i=0; i < str.length(); i++) {
                            boolean flag = Character.isDigit(str.charAt(i));
                            if(flag) {
                                display.setText(result.get(0));
                                ///display.setText(str.charAt(i));
                                //System.out.println("'"+ str.charAt(i)+"' is a number");
                            }
//                            if(!flag){
//                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Please Enter Only Numbers!!",Toast.LENGTH_SHORT).show();
                                //splitString(str);
                                //System.out.println("'"+ str.charAt(i)+"' is a letter");
                            }
                        }


//                        String inputString = result.get(0);
                        String[] stringArray = str.split(" ");//may change to any other splitter regex
                        List<Integer> numbers = new ArrayList<>();
                        for (String string : stringArray) {
                            try {
                                numbers.add(Integer.parseInt(string)); //or may add to the sum of integers here
                            } catch (NumberFormatException e){
                                //display.setText((CharSequence) numbers);
                                System.out.println(e);
                            }
                        }
//                        //System.out.println(sum);
                    }
                }
                break;
            }
        }
//        private boolean isNumber(String word)
//        {
//            boolean isNumber = false;
//            try
//            {
//                Integer.parseInt(word);
//                isNumber = true;
//            } catch (NumberFormatException e)
//            {
//                isNumber = false;
//            }
//            return isNumber;
//        }

//        switch (requestCode) {
//            case REQ_CODE: {
//                if (resultCode == RESULT_OK && null != data) {
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    ArrayList<String> gfg = new ArrayList<String>(
//                            Arrays.asList("zero",
//                                    "one",
//                                    "two"));
//                    // For Loop for iterating ArrayList
//                    for (int i = 0; i < gfg.size(); i++){
//                    if(result.get(i)==gfg.get(i))
//                    display.setText(result.get(0));
//                }}
//                break;
//            }
//        }
    }

//    void splitString(String str)
//    {
//        String alpha, num, special;
//        for (int i=0; i<str.length(); i++)
//        {
//            if (isDigit(str[i]))
//                num.push_back(str[i]);
//            else if((str[i] >= 'A' && str[i] <= 'Z') ||
//                    (str[i] >= 'a' && str[i] <= 'z'))
//                alpha.push_back(str[i]);
//            else
//                special.push_back(str[i]);
//        }

//        cout << alpha << endl;
//        cout << num << endl;
//        cout << special << endl;

    @Override
    protected void onResume() {
        super.onResume();
        ttv = new TextToVoice(this);
        tonegen = new ToneGenerator(AudioManager.STREAM_MUSIC, 99);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        speakTimer = new Timer();
    }

    @Override
    protected void onPause() {
        tonegen.release();
        ttv.shutDown();
        if (mSensor != null) {
            mSensorManager.unregisterListener(this);
        }
        speakTimer.cancel();
        super.onPause();
    }


    private String history = "";

//    @Override
//    public void onClick(View v) {
//        String key = (String)v.getTag();
//        int tone = -1;
//        int time = 400;
//        if (key!=null) {
//            //Log.d("Button", key);
//            String speak = "";
//            if (Character.isDigit(key.codePointAt(0))) {
//                tone = Integer.parseInt(key);
//                speak = key;
//                history += key;
//
//                if (history.length()>10) {
//                    history = history.substring(history.length()-10,history.length());
//                }
//
//
//            } else if (key.equals("#")) {
//                //tone = ToneGenerator.TONE_DTMF_P;
//                speak = getString(R.string.hash);
//
//            } else if (key.equals("*")) {
//                //tone = ToneGenerator.TONE_DTMF_S;
//                speak = getString(R.string.zero);
//
//            } else if (key.equals("ring")) {
//                tone = ToneGenerator.TONE_CDMA_LOW_L;
//                time = 1000;
//                key = "";
//
//            }
//
//            if (tone > -1) {
//                if (switchPhone.isChecked() && !speak.isEmpty()) {
//                    if (ttv.isReady()) {
//                        ttv.speak(speak);
//                    } else {
//                        tonegen.startTone(ToneGenerator.TONE_DTMF_P, 400);
//                    }
//                } else {
//                    tonegen.startTone(tone, time);
//                }
//
//                display.append(key);
//                if (display.length()>30) {
//                    display.setText(display.getText().subSequence(display.length() - 30, display.length() ));
//                }
//
//
//
//            }
//        }
//    }
    @Override
    public void onClick(View v) {
        String key = (String) v.getTag();
        int tone = -1;
        int time = 400;
        if (key != null) {
            //Log.d("Button", key);
            String speak = "";
            if (Character.isDigit(key.codePointAt(0))) {
                tone = Integer.parseInt(key);
                speak = key;
                history += key;

                if (history.length() > 10) {
                    history = history.substring(history.length() - 10, history.length());
                }

            }
            else if (key.equals("#")) {
                //tone = ToneGenerator.TONE_DTMF_P;
                speak = getString(R.string.hash);

            }
            else if (key.equals("0")) {
                tone = ToneGenerator.TONE_DTMF_P;
                speak = getString(R.string.zero);
                //sayConvo();
            }
//            else if (history.endsWith("#")) {
//                tone = ToneGenerator.TONE_DTMF_P;}
//            else if (key.equals("#")) {
//                //tone = ToneGenerator.TONE_DTMF_P;
//                //speak = getString(R.string.hash);
//                //speak = getString(R.string.hash);
//                //sayConvo();
//            }
            else if (key.equals("*")) {
                tone = ToneGenerator.TONE_DTMF_S;
                speak = getString(R.string.star);
                //sayConvo();
            }

            if (tone > -1) {
                if (switchPhone.isChecked() && !speak.isEmpty())
                {
                    if (ttv.isReady()) {
                        ttv.speak(speak);
                    } else {
                        tonegen.startTone(ToneGenerator.TONE_DTMF_P, 400);
                    }
                } else
                    {
                    tonegen.startTone(tone, time);
                }

                display.append(key);
                if (display.length() > 30) {
                    display.setText(display.getText().subSequence(display.length() - 30, display.length()));
                }

            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
