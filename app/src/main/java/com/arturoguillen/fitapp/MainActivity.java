package com.arturoguillen.fitapp;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agl on 11/06/2017.
 */

public class MainActivity extends PermissionsActivity {

    public static final String TAG = "MainActivity";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public List<String> getPermissionsToGrant() {
        List<String> permissionsToRequest = new ArrayList<>();
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionsToRequest;
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
