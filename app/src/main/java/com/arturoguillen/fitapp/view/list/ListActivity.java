package com.arturoguillen.fitapp.view.list;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.component.FitComponent;
import com.arturoguillen.fitapp.entities.Goal;
import com.arturoguillen.fitapp.presenter.GoalsPresenter;
import com.arturoguillen.fitapp.view.InjectedActivity;
import com.arturoguillen.fitapp.view.detail.DetailActivity;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class ListActivity extends InjectedActivity implements GoalListView, ListItemOnClickListener {

    @Inject
    GoalsPresenter presenter;

    @BindView(R.id.recyclerview_list)
    RecyclerView recyclerView;

    @BindView(R.id.progress_list)
    ProgressBar progress;

    @BindView(R.id.text_message_list)
    TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.attachView(this);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);
        setupRecyclerView();
        retrieveData();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    private void retrieveData() {
        presenter.getGoalsList();
    }

    private void setupRecyclerView() {
        GoalAdapter goalAdapter = new GoalAdapter(this);
        recyclerView.setAdapter(goalAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected void injectComponent(FitComponent component) {
        component.inject(this);
    }

    @Override
    public void showProgressFooter() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressFooter() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(@StringRes int stringId) {
        textMessage.setVisibility(View.VISIBLE);
        textMessage.setText(getText(stringId));
    }

    @Override
    public void hideMessage() {
        textMessage.setVisibility(View.GONE);
    }

    @OnClick(R.id.text_message_list)
    public void onClick(View view) {
        retrieveData();
    }

    @Override
    public void showMoreData(List<Goal> goals) {
        GoalAdapter adapter = (GoalAdapter) recyclerView.getAdapter();
        adapter.appendFeedContent(goals);
    }

    @Override
    public void onClickListItem(View v, Goal goal) {
        startActivity(DetailActivity.createIntent(ListActivity.this, goal));
    }
}
