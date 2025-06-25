package com.easylinker.proxy.server.app.utils;


import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Date;

@Component
public class HttpTool {
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    /**
     * emq.api.host=http://localhost:8080/api/v2/
     * emq.api.user=admin
     * emq.api.password=public
     */

    @Value("${emq.api.user}")
    String username;
    @Value("${emq.api.password}")
    String password;

    /**
     * 发送http post请求
     *
     * @param url
     * @param data 提交的参数为key=value&key1=value1的形式
     * @return
     */
    public JSONObject postWithAuthorization(String url, JSONObject data) throws Exception {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toJSONString());
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .addHeader("Content-Length", "22")
                .addHeader("Content-Type", "application/json")
                .addHeader("Server", "MochiWeb/1.0 (Any of you quaids got a smint?)")
                .url(url)
                .post(body)
                .build();
        return JSONObject.parseObject(client.newCall(request).execute().body().string());

    }

    /**
     * Connection →keep-alive
     * Content-Encoding →gzip
     * Content-Type →text/html;charset=UTF-8
     * Date →Sat, 21 Apr 2018 10:49:39 GMT
     * Transfer-Encoding →chunked
     * Vary →Accept-Encoding
     */

    public String get(String url) throws Exception {

        Request request = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Encoding", "gzip")
                .addHeader("Content-Type", "text/html;charset=UTF-8")
                .addHeader("Date", new Date().toString())
                .addHeader("Transfer-Encoding", "chunked")
                .addHeader("Vary", "Accept-Encoding")
                .addHeader("Server", "MochiWeb/1.0 (Any of you quaids got a smint?)")
                .url(url)
                .get()
                .build();
        return client.newCall(request).execute().body().string();
    }

    public static void main(String[] args) throws Exception {
        //http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=
        //System.out.println(new HttpTool().get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=211.162.236.68"));
        System.out.println(new HttpTool().get("http://ip.taobao.com/service/getIpInfo.php?ip=211.162.236.68"));
    }

}
