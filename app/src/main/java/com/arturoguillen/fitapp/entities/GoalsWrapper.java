package com.arturoguillen.fitapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class GoalsWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("goals")
    private List<Goal> goals;

    public List<Goal> getGoals() {
        return goals;
    }
}
