package com.mustlisten.mbm.box.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;

/**
 * 音乐播放功能
 */
public class MusicPlayUtils {


    private static MusicPlayUtils playUtils;

    private MediaPlayer mediaPlayer;

    private Context context;


    public static MusicPlayUtils getInstance(Context context) {
        if (playUtils == null) {
            playUtils = new MusicPlayUtils(context);
        }
        return playUtils;
    }

    private MusicPlayUtils(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }


    public void startPlay(String musicUrl, int volume, OnMusicFinishListener listener) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            new AudioMngHelper(context).setVoice100(volume);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                if (listener != null) {
                    listener.onStart();
                }
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                LogUtils.e("播放完成");
                stopPlay();
                if (listener != null) {
                    listener.onFinish(musicUrl);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            stopPlay();
            if (listener != null) {
                listener.onError();
            }
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); //切记一定要release
            mediaPlayer = null;
        }
    }

    public interface OnMusicFinishListener {

        void onStart();

        void onFinish(String musicBo);

        void onError();
    }
}
