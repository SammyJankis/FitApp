package com.arturoguillen.fitapp.di.api;

import com.arturoguillen.fitapp.entities.GoalsWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public interface GoalsApi {

    @GET("v2/593fe033100000be0acd115c")
    Observable<GoalsWrapper> getGoals();

}
