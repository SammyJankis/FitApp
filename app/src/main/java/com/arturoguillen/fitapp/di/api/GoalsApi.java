package com.arturoguillen.fitapp.di.api;

import com.arturoguillen.fitapp.entities.GoalsWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public interface GoalsApi {

    @GET("/v2/5940586d1000001c14cd1284")
    Observable<GoalsWrapper> getGoals();

}
