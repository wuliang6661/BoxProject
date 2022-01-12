package com.mustlisten.mbm.box;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.webkit.HttpAuthHandler;

import com.mustlisten.mbm.box.api.HttpResultSubscriber;
import com.mustlisten.mbm.box.api.HttpServiceIml;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestHeart();
    }


    /**
     * 轮询获取歌曲接口
     */
    private void requestHeart() {
        Observable.interval(0, 2, TimeUnit.SECONDS)
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
                        getMusic();
                    }
                });
    }


    private void getMusic() {
        HttpServiceIml.getPlayBox().subscribe(new HttpResultSubscriber<TaskBean>() {
            @Override
            public void onSuccess(TaskBean taskBean) {

            }

            @Override
            public void onFiled(String message) {

            }
        });
    }


    private MediaPlayer mediaPlayer;

    public void startVoice() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource("https://cdn.mustlisten.com/qq_music/%E8%90%8C%E8%90%8C%E5%93%92%E5%A4%A9%E5%9B%A2%2F%E6%8B%9C%E5%B9%B4%E6%AD%8C%2F16641083.mp3");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVoice();
    }

    public void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); //切记一定要release
            mediaPlayer = null;
        }
    }
}