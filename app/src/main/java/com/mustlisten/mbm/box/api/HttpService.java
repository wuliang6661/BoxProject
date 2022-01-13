package com.mustlisten.mbm.box.api;

import com.mustlisten.mbm.box.bean.BaseResult;
import com.mustlisten.mbm.box.bean.TaskBean;
import com.mustlisten.mbm.box.bean.VersionBO;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wuliang on 2017/3/9.
 * <p>
 * 此处存放后台服务器的所有接口数据
 */

public interface HttpService {

    String URL = "https://dsongs.xuju.club/";   //测试服
//    String URL = "http://mapi.open.yinghezhong.com/";  //正式环境

    /**
     * 心跳接口
     */
    @FormUrlEncoded
    @POST("/on_demand_songs/api/v1/heartbeat_box")
    Observable<BaseResult<VersionBO>> heartBeanBox(@Field("data") String data, @Field("time") long time);


    /**
     * 获取要播放的歌曲
     */
    @GET("/on_demand_songs/api/v1/get_play_song_box")
    Observable<BaseResult<TaskBean>> getPlaySongBox();


    /**
     * 歌曲开始播放
     */
    @FormUrlEncoded
    @POST("/on_demand_songs/api/v1/play_start_notify")
    Observable<BaseResult<String>> playStartNotify(@Field("task_id") String task_id);


    /**
     * 歌曲结束播放
     */
    @FormUrlEncoded
    @POST("/on_demand_songs/api/v1/play_stop_notify")
    Observable<BaseResult<String>> playStopNotify(@Field("task_id") String task_id);

}
