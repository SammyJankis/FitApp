package com.arturoguillen.fitapp.di;

import com.arturoguillen.fitapp.di.module.GoogleApiModule;
import com.arturoguillen.fitapp.di.module.NetModule;
import com.arturoguillen.fitapp.view.detail.DetailActivity;
import com.arturoguillen.fitapp.view.list.ListActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by agl on 12/06/2017.
 */

@Singleton
@Component(modules = {GoogleApiModule.class, NetModule.class})
public interface FitComponent {
    void inject(DetailActivity detailActivity);

    void inject(ListActivity listActivity);
}
