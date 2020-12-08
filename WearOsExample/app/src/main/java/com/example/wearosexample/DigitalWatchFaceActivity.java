package com.example.wearosexample;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.Display;

public abstract class DigitalWatchFaceActivity extends WearableActivity implements DisplayManager.DisplayListener  {

    /**
     * Used to detect when the watch is dimming.<br/>
     * Remember to make gray-scale versions of your watch face so they won't burn
     * and drain battery unnecessarily.
     */
    public abstract void onScreenDim();


    /**
     * Used to detect when the watch is not in a dimmed state.<br/>
     * This does not handle when returning "home" from a different activity/application.
     */
    public abstract void onScreenAwake();


    /**
     * When the screen is OFF due to "Always-On" being disabled.
     */
    public abstract void onScreenOff();


    /**
     * Used to detect when a watch face is being removed (switched).<br/>
     * You can either do what you need here, or simply override {@code onDestroy()}.
     */
    public void onWatchFaceRemoved(){
        onDestroy();
    }


    private DisplayManager displayManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(this,null);
    }
    protected void onDestroy()
    {
        super.onDestroy();
        displayManager.unregisterDisplayListener(this);
    }

    @Override
    public void onDisplayAdded(int displayId)
    {

    }

    @Override
    public void onDisplayRemoved(int displayId)
    {
        onWatchFaceRemoved();
    }

    @Override
    public void onDisplayChanged(int displayId)
    {
        switch(displayManager.getDisplay(displayId).getState()){
            case Display.STATE_DOZE:
                onScreenDim();
                break;
            case Display.STATE_OFF:
                onScreenOff();
                break;
            case Display.STATE_ON:
                onScreenAwake();
                break;
            case Display.STATE_ON_SUSPEND:
                onWatchFaceRemoved();
                break;
            default:
                //  Not really sure what to so about Display.STATE_UNKNOWN, so
                //  we'll treat it as if the screen is normal.
                onScreenAwake();
                break;
        }
    }
}
