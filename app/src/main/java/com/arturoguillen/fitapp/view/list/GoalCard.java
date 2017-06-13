package com.arturoguillen.fitapp.view.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.entities.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by artu on 3/9/17.
 */

public class GoalCard extends RecyclerView.ViewHolder {

    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.description)
    TextView description;

    private ListItemOnClickListener listItemOnClickListener;

    public GoalCard(View itemView, ListItemOnClickListener listItemOnClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.listItemOnClickListener = listItemOnClickListener;
    }

    void fillGoalCard(final Goal goal) {
        title.setText(goal.getTitle());
        description.setText(goal.getDescription());
        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemOnClickListener.onClickListItem(itemView, goal);
            }
        });

    }

}
