package com.example.iotapp.Manager;

import com.example.iotapp.BuildConfig;
import com.example.iotapp.Model.ModuleDataResponse;
import com.example.iotapp.Utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModuleDataManager {
    public List<ModuleDataResponse> Data() throws Exception {
        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_DATA);
        //url = new URL("https://www.google.pl");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        //Getting the response code
        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        else {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<ModuleDataResponse>>(){}.getType();
            List<ModuleDataResponse> data = gson.fromJson(MyUtils.GetJsonFromUrl(url), listType);

            return data;
        }
    }

    public ModuleDataResponse GetLastData() throws Exception {
        List<ModuleDataResponse> list = Data();
        return list.get(list.size() - 1);
    }
}
