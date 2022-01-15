package com.mustlisten.mbm.box.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import androidx.core.app.ActivityCompat;

/**
 * Created by caoyujie on 16/12/2.
 * Mac地址工具类
 */
public class MacAddressUtils {

    /**
     * 获取当前连接的wifi的mac地址
     */
    public static String getWifiMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String wifiName = info != null ? info.getBSSID() : "";
        return wifiName;
    }

    /**
     * 获取wifi名称
     */
    public static String getWifiName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : "";
        return wifiId;
    }

    /**
     * 获取手机Mac地址
     *
     * @return
     */
    public static String getMacAddress(Context mContext) {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMgr.getConnectionInfo();
        // wifiInf.getMacAddress().getMacAddress方法在安卓6.0系统上获取到的Mac 都是 02:00:00:00:00:00。
        String invalidMacAddress = "02:00:00:00:00:00";

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (wifiInf.getMacAddress().equals(invalidMacAddress)) {
            String ret = null;
            try {
                ret = getAdressMacByInterface();
                if (ret != null) {
                    return ret;
                } else {
                    ret = getAddressMacByFile(wifiMgr);
                    return ret;
                }
            } catch (IOException e) {
                Log.e("TAG", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                Log.e("TAG", "Erreur lecture propriete Adresse MAC ");
            }
        } else {
            return wifiInf.getMacAddress();
        }
        return invalidMacAddress;
    }

    /**
     * 获取6.0以上系统的mac值
     * @throws Exception
     */
    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String fileAddressMac = "/sys/class/net/wlan0/address";
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    /**
     * 获取6.0以上系统的mac值
     * @throws Exception
     */
    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }

    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("TAG", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }
}
