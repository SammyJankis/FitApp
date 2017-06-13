package com.arturoguillen.fitapp.di;

import com.arturoguillen.fitapp.di.module.GoogleApiModule;
import com.arturoguillen.fitapp.view.DetailActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by agl on 12/06/2017.
 */

@Singleton
@Component(modules = {GoogleApiModule.class})
public interface FitComponent {
    void inject(DetailActivity detailActivity);
}
