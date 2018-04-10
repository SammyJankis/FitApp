package com.arturoguillen.fitapp.view.detail;

import com.google.android.gms.common.api.Status;

/**
 * Created by agl on 13/06/2017.
 */

public interface DetailGoalView {

    void dispatchGoal();

    void showData(int total);

    void showErrorWhenSubscriptionFails();

    void showMoreInfo(int messageId, int total);

    void startResolutionSubscribingToGoogleFitApi(Status status);

}
