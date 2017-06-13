package com.arturoguillen.fitapp.view.detail;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.result.DataReadResult;

/**
 * Created by agl on 13/06/2017.
 */

public interface DetailGoalView {
    void showData(DataReadResult dataResult);

    void requestPermissions(Status status);

    void unRegister();

}
