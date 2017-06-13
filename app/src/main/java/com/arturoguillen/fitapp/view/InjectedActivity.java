package com.arturoguillen.fitapp.view;

import android.os.Bundle;

import com.arturoguillen.fitapp.di.DaggerFitComponent;
import com.arturoguillen.fitapp.di.FitComponent;
import com.arturoguillen.fitapp.di.module.GoogleApiModule;

/**
 * Created by agl on 12/06/2017.
 */

public abstract class InjectedActivity extends PermissionsActivity {

    private FitComponent component;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (component == null) {
            component = createComponent();
        }
        injectComponent(component);
    }

    protected abstract void injectComponent(FitComponent component);


    protected FitComponent createComponent() {

        return DaggerFitComponent.builder()
                .googleApiModule(new GoogleApiModule(this))
                .build();
    }
}
