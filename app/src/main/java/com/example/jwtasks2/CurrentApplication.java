package com.example.jwtasks2;

import android.app.Application;
import android.content.Context;

import com.example.jwtasks2.di.components.AppComponent;
import com.example.jwtasks2.di.components.DaggerAppComponent;
import com.example.jwtasks2.di.modules.AppModule;


public class CurrentApplication extends Application {
    public AppComponent appComponent;

    public static CurrentApplication get(Context context) {
        return (CurrentApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
