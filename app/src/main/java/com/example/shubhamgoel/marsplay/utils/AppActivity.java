package com.example.shubhamgoel.marsplay.utils;

import android.app.Application;
import android.os.StrictMode;


/**
 * Created by shubham on 20/4/18.
 */

public class AppActivity extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
