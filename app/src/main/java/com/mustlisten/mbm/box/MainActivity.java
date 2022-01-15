package com.mustlisten.mbm.box;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.blankj.utilcode.util.StringUtils;
import com.mustlisten.mbm.box.api.HttpResultSubscriber;
import com.mustlisten.mbm.box.api.HttpServiceIml;
import com.mustlisten.mbm.box.bean.TaskBean;
import com.mustlisten.mbm.box.bean.VersionBO;
import com.mustlisten.mbm.box.utils.MusicPlayUtils;
import com.mustlisten.mbm.box.utils.RootUtils;
import com.mustlisten.mbm.box.utils.UpdateUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private TaskBean taskBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RootUtils.upgradeRootPermission(getPackageCodePath());
        requestHeart();
        getMusicByLunXun();
    }

    /**
     * 心跳
     */
    private void requestHeart() {
        Observable.interval(0, 2, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        heartBox();
                    }
                });
    }


    private void heartBox() {
        HttpServiceIml.heartBeanBox().subscribe(new HttpResultSubscriber<VersionBO>() {
            @Override
            public void onSuccess(VersionBO versionBO) {
                new UpdateUtils().checkUpdate(MainActivity.this, versionBO);
            }

            @Override
            public void onFiled(String message) {

            }
        });
    }


    private void getMusicByLunXun() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        String taskId = MyApplication.spUtils.getString("taskId", "");
                        //没有正在播放的音乐
                        if (StringUtils.isEmpty(taskId)) {
                            getMusic();
                        }
                    }
                });
    }

    private void getMusic() {
        HttpServiceIml.getPlayBox().subscribe(new HttpResultSubscriber<TaskBean>() {
            @Override
            public void onSuccess(TaskBean taskBean) {
                if (taskBean != null && !StringUtils.isEmpty(taskBean.task_id)) {
                    startLead(taskBean);
                }
            }

            @Override
            public void onFiled(String message) {

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
        HttpServiceIml.startPlayMusic(taskBean.task_id).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFiled(String message) {

            }
        });
    }


    /**
     * 上报结束播放
     */
    private void syncStop(int status) {
        HttpServiceIml.stopPlayMusic(taskBean.task_id, status).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                taskBean = null;
                MyApplication.spUtils.clear();
            }

            @Override
            public void onFiled(String message) {
                taskBean = null;
                MyApplication.spUtils.clear();
            }
        });
    }
}