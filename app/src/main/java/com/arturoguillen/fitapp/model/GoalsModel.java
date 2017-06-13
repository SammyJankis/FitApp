package com.arturoguillen.fitapp.model;

import com.arturoguillen.fitapp.di.api.GoalsApi;
import com.arturoguillen.fitapp.entities.GoalsWrapper;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class GoalsModel {
    private GoalsApi goalsApi;

    @Inject
    public GoalsModel(GoalsApi goalsApi) {
        this.goalsApi = goalsApi;
    }

    public Disposable getGoalsObservable(DisposableObserver<GoalsWrapper> observer) {

        Observable<GoalsWrapper> observable = goalsApi.getGoals();
        return observable.
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeWith(observer);
    }
}
