package com.projegrid.mobile;

import android.app.Application;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by yuki on 8/15/16.
 */
public class MobileApplication extends Application {
    private String TAG = "MobileApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        Log.d(TAG, "in application");
    }
}
