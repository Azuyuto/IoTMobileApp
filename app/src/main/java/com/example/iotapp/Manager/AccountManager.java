package com.example.iotapp.Manager;

import com.example.iotapp.BuildConfig;
import com.example.iotapp.Model.AccountResponse;
import com.example.iotapp.Utils.MyUtils;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;

public class AccountManager {

    public AccountResponse SignIn(String username, String password) throws Exception {
        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_SIGN_IN + "?username=" + username + "&password=" + password);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.connect();

        //Getting the response code
        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }
        else {
            Gson gson = new Gson();
            AccountResponse data = gson.fromJson(MyUtils.GetJsonFromUrl(url), AccountResponse.class);
            return data;
        }
    }

    public AccountResponse SignUp(String username, String password, String repeatPassword)
    {
        return new AccountResponse();
    }
}
