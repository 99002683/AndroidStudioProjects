package com.example.primeamzexample;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.lang.Thread.sleep;

public class FirstFragment extends Fragment {
    final String TAG = "FirstActivity";

    public FirstFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first, container, false);
        return root;
    }

    public void runThread() {
        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                try {
                    sleep(2000);
                    new FirstFragment();
                    Log.d(TAG, "FirstThread: ");

                } catch (Exception e) {
                    Log.d(TAG, "ExceptionThreadOfFirst: ");
                }
            }
        }).start();
    }
}

