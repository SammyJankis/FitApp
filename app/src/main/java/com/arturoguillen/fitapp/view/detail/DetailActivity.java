package com.arturoguillen.fitapp.view.detail;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.component.FitComponent;
import com.arturoguillen.fitapp.entities.Goal;
import com.arturoguillen.fitapp.presenter.DetailFitPresenter;
import com.arturoguillen.fitapp.utils.LogUtils;
import com.arturoguillen.fitapp.view.PermissionsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static java.text.DateFormat.getTimeInstance;

/**
 * Created by agl on 11/06/2017.
 */

public class DetailActivity extends PermissionsActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DetailGoalView {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static int REQUEST_CODE_RESOLVE_ERR = 1000;
    private static final String EXTRA_GOAL = "EXTRA_GOAL";

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    DetailFitPresenter presenter;

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
        presenter.attachView(this);

        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
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
        presenter.queryData();
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
                            presenter.queryData();
                        }
                    });
                }
            }
        }
    }

    private void showErrorDialog(final Runnable ok) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.app_name));
        adb.setMessage(R.string.should_accept_permissions);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ok.run();
            }
        });
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.show();
    }

    public static Intent createIntent(Context context, Goal goal) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_GOAL, goal);
        return intent;
    }

    public static void printData(DataReadResult dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            LogUtils.DEBUG(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            LogUtils.DEBUG(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }

    private static void dumpDataSet(DataSet dataSet) {
        LogUtils.DEBUG(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            LogUtils.DEBUG(TAG, "Data point:");
            LogUtils.DEBUG(TAG, "\tType: " + dp.getDataType().getName());
            LogUtils.DEBUG(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            LogUtils.DEBUG(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                LogUtils.DEBUG(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    @Override
    public void showData(DataReadResult dataResult) {
        printData(dataResult);
    }

    @Override
    public void requestPermissions(Status status) {
        LogUtils.DEBUG(TAG, "There was a problem subscribing.");
        if (status.hasResolution()) {
            try {
                status.startResolutionForResult(DetailActivity.this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                LogUtils.DEBUG(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void unRegister() {
        unregisterGoogleApiClientCallbacks();
    }
}