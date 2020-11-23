package com.example.keyboardplus;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;


import java.util.Locale;

public class TextToVoice implements TextToSpeech.OnInitListener {
    private TextToSpeech tTts = null;
    private Context tContext;
    private boolean isInit = false;

    private float tPitch = .8f;
    private float tSpeed = .6f;
    private int stateID = 0;
    private VoiceReadyListener tFil;
    private boolean fullyInited;

    private boolean IsSpeaking = false;

    public TextToVoice(Context context) {
        tContext = context;
        try {
            Log.d("TextToVoice", "started " + System.currentTimeMillis());
            tTts = new TextToSpeech(tContext, this);

            setPitch(tPitch);
            setSpeed(tSpeed);
            tTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    IsSpeaking = false;
                    //Log.d("TextToSpeech", "Done!" +  System.currentTimeMillis());
                    if (!fullyInited) {
                        fullyInited = true;
                        if (tFil != null) tFil.onVoiceReady(TextToVoice.this);
                    } else {
                        if (tFil != null) tFil.onSpeakComplete(TextToVoice.this);
                    }
                }

                @Override
                public void onError(String s) {
                    IsSpeaking = false;
                    Log.e("TextToSpeech", "Error with " + s);
                    if (tFil != null) tFil.onError(TextToVoice.this);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tTts.setLanguage(Locale.getDefault());
            isInit = true;

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Log.e("error", "This Language is not supported");
            }
            Log.d("TextToSpeech", "Initialization Suceeded! " + System.currentTimeMillis());
            speak(tContext.getString(R.string.hello1) + ",");

        } else {
            Log.e("error", "Initialization Failed! " + status);
        }
    }

    public void shutDown() {
        isInit = false;
        fullyInited = false;
        if (tTts != null) {
            tTts.shutdown();
            tTts = null;
        }
    }

    public void speak(String text) {
        // Log.d("TextToSpeech", text + " " +  System.currentTimeMillis());
        if (isInit) {
            if (Build.VERSION.SDK_INT >= 21) {
                tTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utt" + stateID);
            } else {
                tTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
            stateID++;
            IsSpeaking = true;
        } else {
            Log.e("error", "TTS Not Initialized");
        }
    }

    public void stop() {
        if (fullyInited) {
            tTts.stop();
        }
    }

    public boolean speakingFunction() {
        return IsSpeaking;
    }

    public float getPitch() {
        return tPitch;
    }

    public void setPitch(float pitch) {
        tPitch = pitch;
        tTts.setPitch(pitch);
    }

    public float getSpeed() {
        return tSpeed;
    }

    public void setSpeed(float speed) {
        tSpeed = speed;
        tTts.setSpeechRate(speed);
    }

    public boolean isReady() {
        return fullyInited;
    }

    public void setVoiceReadyListener(VoiceReadyListener fil) {
        tFil = fil;
    }


    public interface VoiceReadyListener {
        void onVoiceReady(TextToVoice ttv);

        void onSpeakComplete(TextToVoice ttv);

        void onError(TextToVoice ttv);
    }
}


