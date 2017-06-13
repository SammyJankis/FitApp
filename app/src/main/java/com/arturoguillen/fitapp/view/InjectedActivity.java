package com.arturoguillen.fitapp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.arturoguillen.fitapp.App;
import com.arturoguillen.fitapp.di.FitComponent;

/**
 * Created by agl on 12/06/2017.
 */

public abstract class InjectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectComponent(((App) getApplication())
                .getComponent());
    }

    protected abstract void injectComponent(FitComponent component);
}
