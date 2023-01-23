package com.example.iotapp.Manager;

import android.os.StrictMode;

import com.example.iotapp.BuildConfig;
import com.example.iotapp.Model.AccountResponse;
import com.example.iotapp.Model.DeviceResponse;
import com.example.iotapp.Model.ModuleDataResponse;
import com.example.iotapp.Utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DeviceManager {
    public AccountResponse GetUserData(String token) throws Exception {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_USER_DATA);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestProperty("Authorization","Bearer " + token);
        conn.setRequestProperty("Content-Type","application/json");
        http.setRequestMethod("GET");
        http.connect();

        //Getting the response code
        int responseCode = http.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        else {
            Gson gson = new Gson();
            AccountResponse data = gson.fromJson(MyUtils.GetBody(http), AccountResponse.class);
            return data;
        }
    }

    public List<DeviceResponse> GetUserDevices(String token) throws Exception {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DEVICES);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestProperty("Authorization","Bearer " + token);
        conn.setRequestProperty("Content-Type","application/json");
        http.setRequestMethod("GET");
        http.connect();

        //Getting the response code
        int responseCode = http.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        else {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ModuleDataResponse>>(){}.getType();
            List<DeviceResponse> data = gson.fromJson(MyUtils.GetBody(http), listType);

            return data;
        }
    }

    public List<ModuleDataResponse> GetData(Integer deviceId) throws Exception {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DATA);
        //url = new URL("https://www.google.pl");
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.connect();

        //Getting the response code
        int responseCode = http.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        else {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ModuleDataResponse>>(){}.getType();
            List<ModuleDataResponse> data = gson.fromJson(MyUtils.GetBody(http), listType);

            return data;
        }
    }

    public ModuleDataResponse GetLastData(Integer deviceId) throws Exception {
        List<ModuleDataResponse> list = GetData(deviceId);
        return list.get(list.size() - 1);
    }
}
