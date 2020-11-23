package com.example.primeamzexample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
//    int b[] = {R.drawable.dark, R.drawable.strangerth, R.drawable.moneyh, R.drawable.narcos, R.drawable.bodyguard, R.drawable.blacklist, R.drawable.blackm, R.drawable.ozark, R.drawable.crown,
//    R.drawable.alavaikuntha,R.drawable.alienist,R.drawable.andhadhun};
    public ImageView j;
    int z = 0;
    final String TAG = "HomeActivity";

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    public void runThread() {
        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                try {
                    sleep(1000);
                    new HomeFragment();
                    Log.d(TAG, "HomeThread: ");

                } catch (Exception e) {
                    Log.d(TAG, "ExceptionThreadOfHome: ");
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
