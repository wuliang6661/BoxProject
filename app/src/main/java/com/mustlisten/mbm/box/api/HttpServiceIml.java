package com.mustlisten.mbm.box.api;

import rx.Observable;

/**
 * Created by wuliang on 2017/4/19.
 * <p>
 * 所有网络请求方法
 */

public class HttpServiceIml {

    static HttpService service;

    /**
     * 获取代理对象
     *
     * @return
     */
    public static HttpService getService() {
        if (service == null)
            service = ApiManager.getInstance().configRetrofit(HttpService.class, HttpService.URL);
        return service;
    }


    /**
     * 获取播放歌曲
     */
    public static Observable<String> getPlayBox() {
        return getService().getPlaySongBox().compose(RxResultHelper.httpRusult());
    }


    /**
     * 开始播放歌曲
     */
    public static Observable<String> startPlayMusic(String taskId) {
        return getService().playStartNotify(taskId).compose(RxResultHelper.httpRusult());
    }

    /**
     * 结束播放歌曲
     */
    public static Observable<String> stopPlayMusic(String taskId) {
        return getService().playStopNotify(taskId).compose(RxResultHelper.httpRusult());
    }

}