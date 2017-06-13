package com.arturoguillen.fitapp.di.module;

import android.content.Context;

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

    private Context context;

    public GoogleApiModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    GoogleApiClient providesGoogleGoalsApiClient(GoogleApiClient.Builder builder) {
        return builder
                .addApi(Fitness.HISTORY_API)
                .build();
    }

    @Provides
    @Singleton
    GoogleApiClient.Builder providesGoogleApiClientBuilder(Context context, GoogleSignInOptions gso) {
        return new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso);
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

    @Provides
    public Context providesContext() {
        return context;
    }

}
