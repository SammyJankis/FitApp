package com.arturoguillen.fitapp.presenter;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.entities.Goal;
import com.arturoguillen.fitapp.model.GoalsModel;
import com.arturoguillen.fitapp.utils.LogUtils;
import com.arturoguillen.fitapp.view.list.GoalListView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class GoalsPresenter implements PresenterInterface<GoalListView> {

    public static final String TAG = GoalsPresenter.class.getSimpleName();

    private GoalListView view;

    private Disposable getGoalsDisposable;

    @Inject
    public GoalsModel goalsModel;

    @Override
    public void attachView(GoalListView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        if (getGoalsDisposable != null)
            getGoalsDisposable.dispose();
    }

    public void getGoalsList() {
        view.showProgressFooter();
        getGoalsDisposable = goalsModel.
                getGoalsObservable().
                subscribeWith(new DisposableObserver<List<Goal>>() {
                    @Override
                    public void onNext(List<Goal> goalList) {
                        view.hideProgressFooter();
                        view.hideMessage();
                        view.showMoreData(goalList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgressFooter();
                        view.showMessage(R.string.try_again);
                        LogUtils.DEBUG(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
