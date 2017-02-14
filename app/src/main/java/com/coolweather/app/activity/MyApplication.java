package com.coolweather.app.activity;


import android.app.Application;
import android.content.Context;

/**
 * Created by User on 2017/2/14.
 */

public class MyApplication extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
