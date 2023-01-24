package com.example.iotapp.Manager;

import android.os.StrictMode;

import com.example.iotapp.BuildConfig;
import com.example.iotapp.Model.AccountResponse;
import com.example.iotapp.Model.DataResponse;
import com.example.iotapp.Utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.OutputStream;
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

    public List<DataResponse> GetData(String token, Integer deviceId) throws Exception {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DEVICE_DATA);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestMethod("POST");
        http.setRequestProperty("Authorization","Bearer " + token);

        String json = "{\"deviceId\":"+deviceId+",\"numberOfLastDataUpdates\":10}";
        OutputStream os = http.getOutputStream();
        os.write(json.getBytes());
        os.flush();

        http.connect();
        int responseCode = http.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        else {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<DataResponse>>(){}.getType();
            List<DataResponse> data = gson.fromJson(MyUtils.GetBody(http), listType);

            return data;
        }
    }

    public DataResponse GetLastData(String token, Integer deviceId) throws Exception {
        List<DataResponse> list = GetData(token, deviceId);
        return list.get(list.size() - 1);
    }
}
