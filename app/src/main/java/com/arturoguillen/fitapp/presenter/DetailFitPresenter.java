package com.arturoguillen.fitapp.presenter;

import android.support.annotation.NonNull;

import com.arturoguillen.fitapp.view.detail.DetailGoalView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DailyTotalResult;

import javax.inject.Inject;

/**
 * Created by agl on 13/06/2017.
 */

public class DetailFitPresenter implements PresenterInterface<DetailGoalView> {

    private GoogleApiClient googleApiClient;

    private DetailGoalView view;

    @Inject
    public DetailFitPresenter(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void queryStepData() {
        queryFitnessDataForToday(DataType.TYPE_STEP_COUNT_DELTA);
    }

    public void queryDistanceData() {
        queryFitnessDataForToday(DataType.TYPE_DISTANCE_DELTA);
    }

    private void queryFitnessDataForToday(DataType dataType) {
        Fitness.HistoryApi.readDailyTotal(googleApiClient, dataType).setResultCallback(
                new ResultCallback<DailyTotalResult>() {
                    @Override
                    public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                        Status status = dailyTotalResult.getStatus();
                        if (status.isSuccess()) {
                            view.showData(dailyTotalResult);
                        } else {
                            view.requestPermissions(status);
                        }
                    }
                }
        );
    }

    @Override
    public void attachView(DetailGoalView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view.unRegister();
        this.view = null;
    }
}
