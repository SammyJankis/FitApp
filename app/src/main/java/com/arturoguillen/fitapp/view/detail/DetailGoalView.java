package com.arturoguillen.fitapp.view.detail;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

/**
 * Created by agl on 13/06/2017.
 */

public interface DetailGoalView {

    void dispatchGoal();

    void showData(DailyTotalResult dailyTotalResult, Field field);

    void showErrorWhenSubscriptionFails();

    void startResolutionSubscribingToGoogleFitApi(Status status);

}
