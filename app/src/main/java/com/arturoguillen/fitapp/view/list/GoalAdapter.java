package com.arturoguillen.fitapp.view.list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.arturoguillen.fitapp.entities.Goal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class GoalAdapter extends RecyclerView.Adapter {

    private ListItemOnClickListener listItemOnClickListener;
    private List<Goal> goals;

    public GoalAdapter(ListItemOnClickListener listItemOnClickListener) {
        this.goals = new ArrayList<>();
        this.listItemOnClickListener = listItemOnClickListener;
    }

    public void appendFeedContent(List<Goal> goals) {
        this.goals.clear();
        this.goals.addAll(goals);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }
}
