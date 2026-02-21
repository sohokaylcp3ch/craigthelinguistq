package com.easylinker.proxy.server.app.utils;

import okhttp3.*;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {

        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:8080/api/v2/management/nodes/emq@127.0.0.1")
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
