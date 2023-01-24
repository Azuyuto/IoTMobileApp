package com.example.iotapp.Manager;

import android.os.StrictMode;

import com.example.iotapp.BuildConfig;
import com.example.iotapp.Model.AccountResponse;
import com.example.iotapp.Model.DataResponse;
import com.example.iotapp.Utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeviceManager {
    public AccountResponse GetUserData(String token) throws Exception {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_USER_DATA);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) conn;
        http.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");
        http.setRequestMethod("GET");
        http.connect();

        //Getting the response code
        int responseCode = http.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            Gson gson = new Gson();
            return gson.fromJson(MyUtils.GetBody(http), AccountResponse.class);
        }
    }

    public List<DataResponse> GetData(String token, Integer deviceId) throws Exception {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DEVICE_DATA + "?deviceId=" + deviceId + "&numberOfLastDataUpdates=10");
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) conn;
        http.setRequestMethod("GET");
        http.setRequestProperty("Authorization", "Bearer " + token);

        http.connect();
        int responseCode = http.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<DataResponse>>() { }.getType();

            return gson.fromJson(MyUtils.GetBody(http), listType);
        }
    }

    public String AddDevice(String token, String name, String guid) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DEVICE_ADD);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestMethod("POST");
        http.setRequestProperty("Authorization", "Bearer " + token);
        http.setDoOutput(true);

        byte[] out = ("{\"name\":\""+name+"\",\"guid\":\""+guid+"\"}").getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        int responseCode = http.getResponseCode();
        if (responseCode != 200) {
            return "Failure during adding new device!";
        }
        else {
            return "Successfully!";
        }
    }

    public String ForgetDevice(String token, Integer deviceId) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DEVICE_FORGET + "?deviceId=" + deviceId);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestMethod("PUT");
        http.setRequestProperty("Authorization", "Bearer " + token);
        http.connect();

        int responseCode = http.getResponseCode();
        if (responseCode != 200) {
            return "Failure during forget new device!";
        }
        else {
            return "Device has been forgotten!";
        }
    }
}
