package com.example.jwtasks2.dagger.modules;


import android.content.Context;

import com.example.jwtasks2.services.ResourcesAndSettings;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    @Provides
    @Inject
    ResourcesAndSettings provideResourcesAndSettings(Context context) {
        return new ResourcesAndSettings(context);
    }
}
