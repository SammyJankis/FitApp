package com.arturoguillen.fitapp.view.list;

import android.support.annotation.StringRes;

import com.arturoguillen.fitapp.entities.Goal;

import java.util.List;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public interface GoalListView {

    void showProgressFooter();

    void hideProgressFooter();

    void showMessage(@StringRes int stringId);

    void hideMessage();

    void showMoreData(List<Goal> comics);

}
