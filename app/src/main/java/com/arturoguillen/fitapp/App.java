package com.arturoguillen.fitapp;

import android.app.Application;

import com.arturoguillen.fitapp.di.DaggerFitComponent;
import com.arturoguillen.fitapp.di.FitComponent;
import com.arturoguillen.fitapp.di.module.GoogleApiModule;
import com.arturoguillen.fitapp.di.module.NetModule;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class App extends Application {

    private FitComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = createComponent();
    }

    protected FitComponent createComponent() {
        return DaggerFitComponent.builder()
                .googleApiModule(new GoogleApiModule(this))
                .netModule(new NetModule())
                .build();

    }

    public FitComponent getComponent() {
        return component;
    }
}
