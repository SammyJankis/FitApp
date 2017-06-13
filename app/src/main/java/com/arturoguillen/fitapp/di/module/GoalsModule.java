package com.arturoguillen.fitapp.di.module;

import com.arturoguillen.fitapp.di.api.GoalsApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by agl on 13/06/2017.
 */

@Module
public class GoalsModule extends NetModule {
    @Provides
    @Singleton
    GoalsApi provideGoalsApi(Retrofit retrofit) {
        return retrofit.create(GoalsApi.class);
    }
}
