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
}
