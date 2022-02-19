package com.mustlisten.mbm.box.utils;

import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.mustlisten.mbm.box.api.HttpService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HeartTimerUtils {


    private final Timer timer;

    private HeartTimerUtils(){
        timer = new Timer();
        requestHeart();
    }

    private final static HeartTimerUtils heartTimerUtils = new HeartTimerUtils();


    public static HeartTimerUtils getInstance(){
        return heartTimerUtils;
    }


    /**
     * 心跳
     */
    private synchronized void requestHeart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                newHeart();
            }
        }, 0, 2 * 60 * 1000);
    }


    private void newHeart() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor).build();
        FormBody body = new FormBody.Builder()
                .add("data", "online")
                .add("version", String.valueOf(AppUtils.getAppVersionCode()))
                .build();
        Request request = new Request.Builder()
                .url(HttpService.URL + "/on_demand_songs/api/v1/box/heartbeat")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtils.e("心跳失败！！！");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                LogUtils.i("心跳成功！！！");
            }
        });
    }


    /**
     * 请求头增加公共参数
     */
    Interceptor headerInterceptor = chain -> {
        Request request;
//        LogUtils.e(DeviceUtils.getMacAddress() + chain.request().url().toString());
        String mac = MacAddressUtils.getMacAddress();
        if (StringUtils.isEmpty(mac)) {
            mac = "00:00:00:00:00";
        }
        // 以拦截到的请求为基础创建一个新的请求对象，然后插入Header
        request = chain.request().newBuilder()
                .addHeader("DEVICE-ID", mac)
                .build();
        return chain.proceed(request);
    };

}
