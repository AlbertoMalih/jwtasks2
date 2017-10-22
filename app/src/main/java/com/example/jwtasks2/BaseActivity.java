package com.example.jwtasks2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.jwtasks2.dagger.components.ActivityComponent;
import com.example.jwtasks2.dagger.components.DaggerActivityComponent;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        activityComponent = DaggerActivityComponent.builder()
                .appComponent(CurrentApplication.get(this).getAppComponent())
                .build();
        injectDependencies();
    }

    protected abstract int getLayoutId();

    public abstract void injectDependencies();

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
