package com.example.sensolab;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SensorService {

    public interface SensorServiceInterface {
        void onSuccess(String data);
        void onError(Exception e);
    }

    private static final int PORT = 8080;
    private static final String SCHEMA = "http";
    private final OkHttpClient client = new OkHttpClient();

    public void requestData(String host, String path, SensorServiceInterface callback) {
        if (path.equals("/humidity") || path.equals("/temperature")) {
            path = "/hum-temp";
        }
        String url = SCHEMA + "://" + host + ":" + PORT + path;

        Log.d("URL", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError(new IOException("Respuesta no exitosa: " + response.code()));
                }
                response.close();
            }
        });
    }
}
