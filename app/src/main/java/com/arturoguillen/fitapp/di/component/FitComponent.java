package com.arturoguillen.fitapp.di.component;

import com.arturoguillen.fitapp.di.module.GoalsModule;
import com.arturoguillen.fitapp.di.module.GoogleApiModule;
import com.arturoguillen.fitapp.view.detail.DetailActivity;
import com.arturoguillen.fitapp.view.list.ListActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by agl on 12/06/2017.
 */

@Singleton
@Component(modules = {GoogleApiModule.class, GoalsModule.class})
public interface FitComponent {
    void inject(DetailActivity detailActivity);

    void inject(ListActivity listActivity);
}
