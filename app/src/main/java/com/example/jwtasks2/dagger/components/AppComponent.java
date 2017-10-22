package com.example.jwtasks2.dagger.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jwtasks2.CurrentApplication;
import com.example.jwtasks2.dagger.modules.AppModule;
import com.example.jwtasks2.services.DbManager;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {
    void inject(CurrentApplication currentApplication);

    SharedPreferences sharedPreferences();

    DbManager dbManager();

    Context getApplicationContext();
}
