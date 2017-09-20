package com.example.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by D22396 on 2017/9/20.
 */

public class HttpUtil {

    /**
     * 发送网络请求
     * @param address   URL
     * @param callback  处理服务器响应
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
