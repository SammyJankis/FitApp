package com.arturoguillen.fitapp.view.list;

import android.os.Bundle;

import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.di.FitComponent;
import com.arturoguillen.fitapp.view.InjectedActivity;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class ListActivity extends InjectedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    protected void injectComponent(FitComponent component) {
        component.inject(this);
    }
}
