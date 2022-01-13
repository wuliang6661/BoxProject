package com.mustlisten.mbm.box;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class InstallApkUtils {
    public static Context mContext = null;

    //判断是否update目录下有文件
    public static boolean isHasFile(String currenttempfilepath) {
        try {
            File f = new File(currenttempfilepath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /*
　　@pararm apkPath 等待安装的app全路径，如：/sdcard/app/app.apk
**/
    private static boolean clientInstall(String apkPath) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 " + apkPath);
            PrintWriter
                    .println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r " + apkPath);
            // PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }


    public static void excuteSuCMD(String currenttempfilepath) {
        if (isHasFile(currenttempfilepath)) {
            Process process = null;
            OutputStream out = null;
            InputStream in = null;
            try {
                //请求root
                process = Runtime.getRuntime().exec("su");
                out = process.getOutputStream();
                //调用安装
                out.write(("pm install -r " + currenttempfilepath + "\n").getBytes());
                in = process.getInputStream();
                int len = 0;
                byte[] bs = new byte[256];
                while (-1 != (len = in.read(bs))) {
                    String state = new String(bs, 0, len);
                    if (state.equals("success\n")) {
                        //安装成功后的操作
                        //静态注册自启动广播
                        Intent intent = new Intent();
                        //与清单文件的receiver的anction对应
                        intent.setAction("android.intent.action.PACKAGE_REPLACED");
                        //发送广播
                        mContext.sendBroadcast(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e("install", "apk is not exist");
        }
    }
}
