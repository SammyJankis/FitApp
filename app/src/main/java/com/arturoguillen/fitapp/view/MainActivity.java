package com.arturoguillen.fitapp.view;

import android.Manifest;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.FitComponent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by agl on 11/06/2017.
 */

public class MainActivity extends InjectedActivity implements GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = "MainActivity";
    private static int REQUEST_CODE_RESOLVE_ERR = 1000;

    @Inject
    GoogleApiClient googleApiClient;

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
                buildFitnessClient();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void injectComponent(FitComponent component) {
        component.inject(this);
    }

    private void buildFitnessClient() {
        if (googleApiClient != null && !googleApiClient.isConnectionCallbacksRegistered(this)) {
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiClient.unregisterConnectionCallbacks(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected!!!");
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildFitnessClient();
    }

    public void subscribe() {
        Fitness.RecordingApi
                .subscribe(googleApiClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                            readData();
                        } else {
                            Log.w(TAG, "There was a problem subscribing.");
                            if (status.hasResolution()) {
                                try {
                                    status.startResolutionForResult(MainActivity.this, REQUEST_CODE_RESOLVE_ERR);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

    private void readData() {

    }
}
