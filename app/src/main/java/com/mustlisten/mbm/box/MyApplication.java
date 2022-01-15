package com.mustlisten.mbm.box;

import android.app.Application;

import com.blankj.utilcode.util.SPUtils;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class MyApplication extends Application {

    public static SPUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        spUtils = SPUtils.getInstance("box_project");
        CustomActivityOnCrash.install(this);
    }
}
