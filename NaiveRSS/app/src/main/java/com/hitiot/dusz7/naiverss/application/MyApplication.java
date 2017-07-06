package com.hitiot.dusz7.naiverss.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by dusz7 on 2017/7/4.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
