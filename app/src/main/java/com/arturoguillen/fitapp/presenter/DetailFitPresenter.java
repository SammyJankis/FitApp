package com.arturoguillen.fitapp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.arturoguillen.fitapp.utils.LogUtils;
import com.arturoguillen.fitapp.view.detail.DetailGoalView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import javax.inject.Inject;

/**
 * Created by agl on 13/06/2017.
 */

public class DetailFitPresenter implements PresenterInterface<DetailGoalView>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = DetailFitPresenter.class.getSimpleName();

    @Inject
    GoogleApiClient googleApiClient;

    private DetailGoalView view;

    @Inject
    public DetailFitPresenter(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    public void attachView(DetailGoalView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        unregisterGoogleApiClientCallbacks();
        this.view = null;
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        LogUtils.DEBUG(TAG, "Connected");
        view.dispatchGoal();
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        LogUtils.DEBUG(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnectionSuspended(final int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            LogUtils.DEBUG(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            LogUtils.DEBUG(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    public void onSubscriptionToGoogleFitFailed() {
        if (!googleApiClient.isConnecting() && googleApiClient.isConnected()) {
            view.showErrorWhenSubscriptionFails();
        }
    }

    public void queryDistanceData() {
        LogUtils.DEBUG(TAG, "Querying distance data");
        queryFitnessDataForToday(DataType.TYPE_DISTANCE_DELTA, Field.FIELD_DISTANCE);
    }

    public void queryStepData() {
        LogUtils.DEBUG(TAG, "Querying step data");
        queryFitnessDataForToday(DataType.TYPE_STEP_COUNT_DELTA, Field.FIELD_STEPS);
    }

    public void registerGoogleApiClientCallbacks() {
        if (googleApiClient != null &&
                !googleApiClient.isConnectionCallbacksRegistered(this) &&
                !googleApiClient.isConnectionFailedListenerRegistered(this)) {
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);
            googleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
        }
    }

    private void queryFitnessDataForToday(final DataType dataType, final Field field) {
        Fitness.HistoryApi.readDailyTotal(googleApiClient, dataType).setResultCallback(
                new ResultCallback<DailyTotalResult>() {
                    @Override
                    public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                        Status status = dailyTotalResult.getStatus();
                        if (status.isSuccess()) {
                            view.showData(dailyTotalResult, field);
                            subscribeToFitnessData(dataType);
                        } else {
                            requestPermissions(status);
                        }
                    }
                }
        );
    }

    private void requestPermissions(Status status) {
        LogUtils.DEBUG(TAG, "There was a problem subscribing");
        if (status.hasResolution()) {
            view.startResolutionSubscribingToGoogleFitApi(status);
        }
    }

    private void subscribeToFitnessData(DataType dataType) {
        Fitness.RecordingApi.subscribe(googleApiClient, dataType)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (!status.isSuccess()) {
                            requestPermissions(status);
                        }
                    }
                });
    }

    private void unregisterGoogleApiClientCallbacks() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

        if (googleApiClient.isConnectionCallbacksRegistered(this)) {
            googleApiClient.unregisterConnectionCallbacks(this);
        }

        if (googleApiClient.isConnectionFailedListenerRegistered(this)) {
            googleApiClient.unregisterConnectionFailedListener(this);
        }
    }
}
