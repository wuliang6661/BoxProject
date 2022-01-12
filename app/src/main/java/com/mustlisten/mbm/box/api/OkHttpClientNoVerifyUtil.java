package com.mustlisten.mbm.box.api;

import androidx.annotation.Nullable;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * @author wuliang
 * @since 2018-09-17 10:44
 */
public class OkHttpClientNoVerifyUtil {


    /**
     * 解决ssl证书失效，信任所有证书
     */
    public static OkHttpClient.Builder createClientBuilder_noVerify(@Nullable OkHttpClient.Builder builder) {
        try {
            if (builder == null) {
                builder = new OkHttpClient.Builder();
            }
            SSLContext sc = SSLContext.getInstance("SSL");
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };
            sc.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            builder
                    .sslSocketFactory(sc.getSocketFactory(), x509TrustManager)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
            return builder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }



}
