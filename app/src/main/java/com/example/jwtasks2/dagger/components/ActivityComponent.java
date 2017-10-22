package com.example.jwtasks2.dagger.components;

import com.example.jwtasks2.CreateChangeNoteActivity;
import com.example.jwtasks2.ItemListActivityMain;
import com.example.jwtasks2.dagger.PerActivity;
import com.example.jwtasks2.dagger.modules.ActivityModule;

import dagger.Component;

@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class})
@PerActivity
public interface ActivityComponent {

    void inject(ItemListActivityMain activity);

    void inject(CreateChangeNoteActivity activity);

}
