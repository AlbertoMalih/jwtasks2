package com.example.jwtasks2.dagger.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.jwtasks2.services.DbManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {
    private Application application;
    private Context context;

    public AppModule(Application application) {
        this.application = application;
        this.context = application;
    }

    @Provides
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    DbManager provideDbManager(Context context) {
        return new DbManager(context);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
