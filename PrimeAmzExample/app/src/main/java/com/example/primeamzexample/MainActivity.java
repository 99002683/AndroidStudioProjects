package com.example.primeamzexample;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.primeamzexample.ui.main.SectionsPagerAdapter;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    TabLayout tabs;
    Handler handler = new Handler();
    //int apiDelayed = 5*1000; //1 second=1000 milisecond, 5*1000=5seconds
    Runnable runnable;
    private int[] icons = {
            R.mipmap.home2,
            // R.drawable.two,
            //  R.drawable.three
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String TAG = "MainActivity";

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        setupTabIcons();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AsyncTask.execute(new Runnable() {
            public void run() {
                // a potentially time consuming task
                try {
                    Log.d(TAG, "MainThreadOfFirst: ");
                    sleep(1000);
                    // perform setOnTabSelectedListener event on TabLayout
                    tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            // get the current selected tab's position and replace the fragment accordingly
                            Fragment fragment = null;
                            switch (tab.getPosition()) {
                                case 0:
                                    new HomeFragment().runThread();
                                    break;
                                case 1:
                                    new FirstFragment().runThread();
                                    break;
                                case 2:
                                    new SecondFragment().runThread();
                                    break;
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "ExceptionThreadOfFirst: ");
                }
            }
        });//.start();
    }

    private void setupTabIcons()
    {
        tabs.getTabAt(0).setIcon(icons[0]);
        //tabs.getTabAt(1).setIcon(icons[1]);
        // tabs.getTabAt(2).setIcon(icons[2]);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}