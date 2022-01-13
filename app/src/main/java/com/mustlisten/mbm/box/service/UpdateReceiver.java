package com.mustlisten.mbm.box.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mustlisten.mbm.box.MainActivity;


public class UpdateReceiver extends BroadcastReceiver {

    public static final String UPDATE_ACTION = "android.intent.action.PACKAGE_REPLACED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(UPDATE_ACTION)) {
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
//            System.out.println("安装了:" + packageName + "包名的程序");        }
            //接收卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
//                String packageName = intent.getDataString();
//            Log.e("install","卸载了:" + packageName + "包名的程序");
            }
        }
    }
}