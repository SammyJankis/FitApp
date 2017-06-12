package com.arturoguillen.fitapp;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by agl on 11/06/2017.
 */

public class MainActivity extends LocationActivity {

    public static final String TAG = "MainActivity";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Runnable getCallbackOnPermissionsGranted() {
        return new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Permissions Granted ");
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
