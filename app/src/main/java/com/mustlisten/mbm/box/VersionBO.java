package com.mustlisten.mbm.box;

public class VersionBO {


    /**
     * latestVersion : 2
     * downloadUrl : https://shjz.yingjin.pro/api/upload/app/app-debug.apk
     * isForceUpdate : 0
     * content : 新功能上线
     */

    private String latest_version;
    private String download_url;
    private int timer;

    public String getLatest_version() {
        return latest_version;
    }

    public void setLatest_version(String latest_version) {
        this.latest_version = latest_version;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
