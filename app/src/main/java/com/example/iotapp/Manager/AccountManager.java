package com.example.iotapp.Manager;

import android.os.StrictMode;

import com.example.iotapp.BuildConfig;
import com.example.iotapp.Model.AccountResponse;
import com.example.iotapp.Utils.MyUtils;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class AccountManager {
    public AccountResponse SignIn(String username, String password) throws Exception {
        if(username.isEmpty() || password.isEmpty())
        {
            AccountResponse data = new AccountResponse();
            data.message = "Username and Password not be empty";
            return data;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_SIGN_IN);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] out = ("{\"username\":\""+username+"\",\"password\":\""+password+"\"}").getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        int responseCode = http.getResponseCode();
        if (responseCode != 200) {
            if(400 <= responseCode && responseCode < 500)
            {
                Gson gson = new Gson();
                return gson.fromJson(MyUtils.GetBody(http), AccountResponse.class);
            }
            else
            {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        }
        else {
            Gson gson = new Gson();
            return gson.fromJson(MyUtils.GetBody(http), AccountResponse.class);
        }
    }

    public AccountResponse SignUp(String email, String username, String password, String repeatPassword) throws Exception {
        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty())
        {
            AccountResponse data = new AccountResponse();
            data.message = "All fields are required";
            return data;
        }

        if(!password.equals(repeatPassword))
        {
            AccountResponse data = new AccountResponse();
            data.message = "Passwords must be the same";
            return data;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(BuildConfig.API_URL + BuildConfig.API_SIGN_UP);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        byte[] out = ("{\"email\":\""+email+"\",\"username\":\""+username+"\",\"password\":\""+password+"\"}").getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        int responseCode = http.getResponseCode();
        if (responseCode != 200) {
            if(400 <= responseCode && responseCode < 500)
            {
                Gson gson = new Gson();
                return gson.fromJson(MyUtils.GetBody(http), AccountResponse.class);
            }
            else
            {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        }
        else {
            Gson gson = new Gson();
            return gson.fromJson(MyUtils.GetBody(http), AccountResponse.class);
        }
    }
}
