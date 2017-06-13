package com.arturoguillen.fitapp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class Goal implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("limit")
    private int limit;

    private static final String TYPE_STEP_COUNT_DELTA = "TYPE_STEP_COUNT_DELTA";
    private static final String TYPE_DISTANCE_DELTA = "TYPE_DISTANCE_DELTA";

    @SerializedName("type")
    private String dataType;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public int getLimit() {
        return limit;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isDataTypeStep() {
        return getDataType().equals(TYPE_STEP_COUNT_DELTA);
    }

    public boolean isDataTypeDistance() {
        return getDataType().equals(TYPE_DISTANCE_DELTA);
    }
}
