package com.arturoguillen.fitapp.di.module;

import android.app.Application;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by agl on 12/06/2017.
 */

@Module
public class GoogleApiModule {

    private Application application;

    public GoogleApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleApiClient(GoogleSignInOptions gso) {
        return new GoogleApiClient.Builder(application)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .build();
    }

    @Provides
    @Singleton
    GoogleSignInOptions providesGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(
                        new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE),
                        new Scope(Scopes.FITNESS_LOCATION_READ))
                .build();
    }

}
