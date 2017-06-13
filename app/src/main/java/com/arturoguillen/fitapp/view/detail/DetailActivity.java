package com.arturoguillen.fitapp.view.detail;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.component.FitComponent;
import com.arturoguillen.fitapp.utils.LogUtils;
import com.arturoguillen.fitapp.view.PermissionsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by agl on 11/06/2017.
 */

public class DetailActivity extends PermissionsActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = DetailActivity.class.getSimpleName();
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
                LogUtils.DEBUG(TAG, "Permissions Granted");
                registerGoogleApiClientCallbacks();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void injectComponent(FitComponent component) {
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerGoogleApiClientCallbacks();
    }

    private void registerGoogleApiClientCallbacks() {
        if (googleApiClient != null &&
                !googleApiClient.isConnectionCallbacksRegistered(this) &&
                !googleApiClient.isConnectionFailedListenerRegistered(this)) {
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);
            googleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterGoogleApiClientCallbacks();
    }

    private void unregisterGoogleApiClientCallbacks() {
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();

        if (googleApiClient.isConnectionCallbacksRegistered(this))
            googleApiClient.unregisterConnectionCallbacks(this);

        if (googleApiClient.isConnectionFailedListenerRegistered(this))
            googleApiClient.unregisterConnectionFailedListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.DEBUG(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            LogUtils.DEBUG(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            LogUtils.DEBUG(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.DEBUG(TAG, "onConnectionFailed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            if (resultCode == RESULT_OK) {
                LogUtils.DEBUG(TAG, "Successfully subscribed!");
            } else if (resultCode != RESULT_OK) {
                if (!googleApiClient.isConnecting() && googleApiClient.isConnected()) {
                    showErrorDialog(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        }
    }

    private void showErrorDialog(final Runnable ok) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("FitApp");
        adb.setMessage("You should accept all the pemissions to use the app");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ok.run();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.show();
    }
}
