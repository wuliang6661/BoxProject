package com.mustlisten.mbm.box;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.mustlisten.mbm.box.api.HttpService;
import com.mustlisten.mbm.box.bean.TaskBean;
import com.mustlisten.mbm.box.utils.MacAddressUtils;
import com.mustlisten.mbm.box.utils.MusicPlayUtils;
import com.mustlisten.mbm.box.utils.RootUtils;

import org.json.JSONException;
import org.json.JSONObject;

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

public class MainActivity extends Activity {

    private TaskBean taskBean;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.spUtils.clear();
        RootUtils.upgradeRootPermission(getPackageCodePath());
        requestHeart();
        handler.sendEmptyMessage(0x11);
    }

    /**
     * 心跳
     */
    private void requestHeart() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                newHeart();
            }
        }, 0, 2 * 60 * 1000);
    }


    Handler handler = new Handler() {

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String taskId = MyApplication.spUtils.getString("taskId", "");
            //没有正在播放的音乐
            if (StringUtils.isEmpty(taskId)) {
                newGetMusic();
            }
        }
    };


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
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }


    private void newGetMusic() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor).build();
        Request request = new Request.Builder()
                .url(HttpService.URL + "/on_demand_songs/api/v1/box/get_play_song")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessageDelayed(0x11, 10000);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    LogUtils.e(result);
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getInt("errcode") == 0) {
                            String json = object.getString("data");
                            TaskBean bean = new Gson().fromJson(json, TaskBean.class);
                            if (bean != null && !StringUtils.isEmpty(bean.task_id)) {
                                startLead(bean);
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessageDelayed(0x11, 10000);
            }
        });
    }


    /**
     * 播放引导语
     */
    private void startLead(TaskBean taskBean) {
        this.taskBean = taskBean;
        MyApplication.spUtils.put("taskId", taskBean.task_id);
        MusicPlayUtils.getInstance(this).startPlay(taskBean.lead_play_url, taskBean.volume, new MusicPlayUtils.OnMusicFinishListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(String musicBo) {
                new Handler().postDelayed(() -> startMusic(), taskBean.interval * 1000L);
            }

            @Override
            public void onError() {
                new Handler().postDelayed(() -> startMusic(), taskBean.interval * 1000L);
            }
        });
    }

    /**
     * 播放音乐
     */
    private void startMusic() {
        MusicPlayUtils.getInstance(this).startPlay(taskBean.play_url, taskBean.volume, new MusicPlayUtils.OnMusicFinishListener() {
            @Override
            public void onStart() {
                syncStart();
            }

            @Override
            public void onFinish(String musicBo) {
                syncStop(1);
            }

            @Override
            public void onError() {
                syncStop(0);
            }
        });
    }


    /**
     * 上报开始播放
     */
    private void syncStart() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor).build();
        FormBody body = new FormBody.Builder()
                .add("task_id", taskBean.task_id)
                .build();
        Request request = new Request.Builder()
                .url(HttpService.URL + "/on_demand_songs/api/v1/box/play_start_notify")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //...
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    LogUtils.e(result);
                }
            }
        });
    }


    /**
     * 上报结束播放
     */
    private void syncStop(int status) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor).build();
        FormBody body = new FormBody.Builder()
                .add("task_id", taskBean.task_id)
                .add("status", String.valueOf(status))
                .build();
        Request request = new Request.Builder()
                .url(HttpService.URL + "/on_demand_songs/api/v1/box/play_stop_notify")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                taskBean = null;
                MyApplication.spUtils.clear();
                handler.sendEmptyMessageDelayed(0x11, 10000);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                taskBean = null;
                MyApplication.spUtils.clear();
                handler.sendEmptyMessageDelayed(0x11, 10000);
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