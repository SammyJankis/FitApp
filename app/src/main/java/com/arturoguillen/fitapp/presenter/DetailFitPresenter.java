package com.arturoguillen.fitapp.presenter;

import android.support.annotation.NonNull;

import com.arturoguillen.fitapp.view.detail.DetailGoalView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by agl on 13/06/2017.
 */

public class DetailFitPresenter implements PresenterInterface<DetailGoalView> {

    private static final String TAG = DetailFitPresenter.class.getSimpleName();

    private GoogleApiClient googleApiClient;

    private DetailGoalView view;

    @Inject
    public DetailFitPresenter(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void queryData() {
        Fitness.HistoryApi.
                readData(googleApiClient, queryFitnessStepPastWeekData()).setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(@NonNull DataReadResult dataReadResult) {
                        Status status = dataReadResult.getStatus();
                        if (status.isSuccess()) {
                            view.showData(dataReadResult);
                        } else {
                            view.requestPermissions(status);
                        }
                    }
                }
        );
    }

    private static DataReadRequest queryFitnessStepPastWeekData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
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
