package com.arturoguillen.fitapp.view.list;

import android.view.View;

import com.arturoguillen.fitapp.entities.Goal;

/**
 * Created by artu on 3/11/17.
 */

public interface ListItemOnClickListener {

    void onClickListItem(View v, Goal goal);
}
