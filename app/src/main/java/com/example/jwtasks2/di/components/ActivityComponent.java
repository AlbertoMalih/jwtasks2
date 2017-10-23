package com.example.jwtasks2.di.components;

import com.example.jwtasks2.ui.activities.CreateChangeNoteActivity;
import com.example.jwtasks2.ui.activities.ItemListActivityMain;
import com.example.jwtasks2.di.PerActivity;
import com.example.jwtasks2.di.modules.ActivityModule;

import dagger.Component;

@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class})
@PerActivity
public interface ActivityComponent {

    void inject(ItemListActivityMain activity);

    void inject(CreateChangeNoteActivity activity);

}
