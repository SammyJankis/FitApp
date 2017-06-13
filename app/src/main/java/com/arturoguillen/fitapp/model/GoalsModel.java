package com.arturoguillen.fitapp.model;

import com.arturoguillen.fitapp.di.api.GoalsApi;
import com.arturoguillen.fitapp.entities.GoalsWrapper;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class GoalsModel {

    @Inject
    public GoalsApi goalsApi;

    public Observable<GoalsWrapper> getGoalsObservable() {

        return goalsApi.getGoals().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
    }
}
