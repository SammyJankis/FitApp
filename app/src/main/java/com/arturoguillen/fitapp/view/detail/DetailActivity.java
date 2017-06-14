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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.component.FitComponent;
import com.arturoguillen.fitapp.entities.Goal;
import com.arturoguillen.fitapp.presenter.DetailFitPresenter;
import com.arturoguillen.fitapp.utils.LogUtils;
import com.arturoguillen.fitapp.view.PermissionsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by agl on 11/06/2017.
 */

public class DetailActivity extends PermissionsActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DetailGoalView {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static int REQUEST_CODE_RESOLVE_ERR = 1000;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String EXTRA_GOAL = "EXTRA_GOAL";

    private Goal goal;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    DetailFitPresenter presenter;

    @BindView(R.id.progress_detail)
    ProgressBar progressDetail;

    @BindView(R.id.description_detail)
    TextView descriptionDetail;

    @BindView(R.id.title_detail)
    TextView titleDetail;

    @BindView(R.id.more_info_detail)
    TextView moreInfoDetail;

    @BindView(R.id.type_detail)
    TextView typeDetail;

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
        ButterKnife.bind(this);
        goal = getGoalExtra(savedInstanceState);

        initUI();
    }

    private void initUI() {
        titleDetail.setText(goal.getTitle());
        descriptionDetail.setText(goal.getDescription());
        typeDetail.setText(getTypeString());
    }

    private int getTypeString() {
        if (goal.isDataTypeDistance())
            return R.string.meters;
        return R.string.steps;
    }

    private Goal getGoalExtra(Bundle savedInstanceState) {
        Goal goal;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                goal = null;
            } else {
                goal = (Goal) extras.get(EXTRA_GOAL);
            }
        } else {
            goal = (Goal) savedInstanceState.get(EXTRA_GOAL);
        }
        return goal;
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

        if (checkPlayServices() && googleApiClient != null &&
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
        dispatchGoal();
    }

    private void dispatchGoal() {
        if (goal.isDataTypeDistance()) {
            presenter.queryDistanceData();
        } else if (goal.isDataTypeStep()) {
            presenter.queryStepData();
        }
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

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
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
                            dispatchGoal();
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

    @Override
    public void showData(DailyTotalResult dailyTotalResult, Field field) {
        DataSet totalSet = dailyTotalResult.getTotal();
        progressDetail.setMax(goal.getLimit());
        Value totalValue = totalSet.isEmpty() ? null : totalSet.getDataPoints().get(0).getValue(field);
        int total;
        if (goal.isDataTypeStep()) {
            total = totalValue.asInt();
        } else {
            total = (int) totalValue.asFloat();
        }
        LogUtils.DEBUG(TAG, "Total value = " + total);
        progressDetail.setProgress(total);
        showMoreInfo(total);
    }

    private void showMoreInfo(int total) {
        int percent = 100 * total / goal.getLimit();
        String text;
        if (percent < 33) {
            text = getString(R.string.come_on, total);
        } else if (percent > 33 && percent < 66) {
            text = getString(R.string.doing_great, total);
        } else {
            text = getString(R.string.keep_pushing, total);
        }
        moreInfoDetail.setText(text);
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