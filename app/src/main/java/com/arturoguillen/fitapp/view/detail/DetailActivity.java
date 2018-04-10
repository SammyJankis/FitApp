package com.arturoguillen.fitapp.view.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.component.FitComponent;
import com.arturoguillen.fitapp.entities.Goal;
import com.arturoguillen.fitapp.presenter.DetailFitPresenter;
import com.arturoguillen.fitapp.utils.LogUtils;
import com.arturoguillen.fitapp.view.PermissionsActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.result.DailyTotalResult;
import javax.inject.Inject;

/**
 * Created by agl on 11/06/2017.
 */

public class DetailActivity extends PermissionsActivity implements
        DetailGoalView {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static int REQUEST_CODE_RESOLVE_ERR = 1000;

    private static final String EXTRA_GOAL = "EXTRA_GOAL";

    @BindView(R.id.description_detail)
    TextView descriptionDetail;

    @BindView(R.id.more_info_detail)
    TextView moreInfoDetail;

    @Inject
    DetailFitPresenter presenter;

    @BindView(R.id.progress_detail)
    ProgressBar progressDetail;

    @BindView(R.id.title_detail)
    TextView titleDetail;

    @BindView(R.id.type_detail)
    TextView typeDetail;

    private Goal goal;

    public static Intent createIntent(Context context, Goal goal) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_GOAL, goal);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attachView(this);

        ButterKnife.bind(this);
        goal = getGoalExtra(savedInstanceState);
        initUI();
        requestLocationPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            if (resultCode == RESULT_OK) {
                LogUtils.DEBUG(TAG, "Successfully subscribed!");
            } else {
                presenter.onSubscriptionToGoogleFitFailed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void dispatchGoal() {
        if (goal.isDataTypeDistance()) {
            presenter.queryDistanceData();
        } else if (goal.isDataTypeStep()) {
            presenter.queryStepData();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    public void onLocationPermissionGranted() {
        super.onLocationPermissionGranted();
        LogUtils.DEBUG(TAG, "Location Permission Granted");
        if (checkPlayServices()) {
            LogUtils.DEBUG(TAG, "Google Play Services active");
            presenter.registerGoogleApiClientCallbacks();
        }
    }

    @Override
    public void showData(DailyTotalResult dailyTotalResult, Field field) {
        DataSet totalSet = dailyTotalResult.getTotal();
        progressDetail.setMax(goal.getLimit());
        Value totalValue = totalSet.isEmpty() ? null : totalSet.getDataPoints().get(0).getValue(field);
        int total;
        if (goal.isDataTypeStep()) {
            total = totalValue.asInt();
        } else {
            total = (int) totalValue.asFloat();
        }
        LogUtils.DEBUG(TAG, "Total value = " + total);
        progressDetail.setProgress(total);
        showMoreInfo(total);
    }

    @Override
    public void showErrorWhenSubscriptionFails() {
        showErrorDialog(new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {
                dispatchGoal();
            }
        });
    }

    @Override
    public void startResolutionSubscribingToGoogleFitApi(final Status status) {
        try {
            status.startResolutionForResult(DetailActivity.this, REQUEST_CODE_RESOLVE_ERR);
        } catch (IntentSender.SendIntentException e) {
            LogUtils.DEBUG(TAG, e.getMessage());
        }
    }

    @Override
    protected void injectComponent(FitComponent component) {
        component.inject(this);
    }

    private int getDetailTypeText() {
        if (goal.isDataTypeDistance()) {
            return R.string.meters;
        }
        return R.string.steps;
    }

    private Goal getGoalExtra(Bundle savedInstanceState) {
        Goal goal;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                goal = null;
            } else {
                goal = (Goal) extras.get(EXTRA_GOAL);
            }
        } else {
            goal = (Goal) savedInstanceState.get(EXTRA_GOAL);
        }
        return goal;
    }

    private void initUI() {
        titleDetail.setText(goal.getTitle());
        descriptionDetail.setText(goal.getDescription());
        typeDetail.setText(getDetailTypeText());
    }

    private void showErrorDialog(DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.app_name));
        adb.setMessage(R.string.should_accept_permissions);
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton(R.string.try_again, onClickListener);
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.show();
    }

    private void showMoreInfo(int total) {
        int percent = 100 * total / goal.getLimit();
        String text;
        if (percent < 33) {
            text = getString(R.string.come_on, total);
        } else if (percent > 33 && percent < 66) {
            text = getString(R.string.doing_great, total);
        } else {
            text = getString(R.string.keep_pushing, total);
        }
        moreInfoDetail.setText(text);
    }
}