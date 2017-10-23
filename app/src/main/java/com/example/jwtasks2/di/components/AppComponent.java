package com.example.jwtasks2.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jwtasks2.CurrentApplication;
import com.example.jwtasks2.di.modules.AppModule;
import com.example.jwtasks2.services.ContainerNotes;
import com.example.jwtasks2.services.DbManager;
import com.example.jwtasks2.services.ResourcesAndSettings;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {
    void inject(CurrentApplication currentApplication);

    SharedPreferences sharedPreferences();

    DbManager dbManager();

    ContainerNotes containerNotes();

    ResourcesAndSettings resourcesAndSettings();

    Context getApplicationContext();
}
