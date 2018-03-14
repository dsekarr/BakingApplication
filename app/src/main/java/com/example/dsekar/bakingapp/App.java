package com.example.dsekar.bakingapp;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class App extends Application {

    static App context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = (App) getApplicationContext();
    }

    public static App getContext() {
        return context;
    }
}


