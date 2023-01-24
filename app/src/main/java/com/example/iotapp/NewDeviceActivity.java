package com.example.iotapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.Manager.DeviceManager;

public class NewDeviceActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";

    private DeviceManager deviceManager;
    private Handler mHandler;

    SharedPreferences sharedpreferences;
    String token;
    Integer deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_device);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(AUTHENTICATION_TOKEN, null);
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        findViewById(R.id.infoText).setVisibility(View.INVISIBLE);

        SetBackButton();
        SetConnectButton();
    }

    private void SetBackButton()
    {
        Button backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewDeviceActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void SetConnectButton()
    {
        Button connectBtn = findViewById(R.id.connectButton);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                // TODO: below

                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", "IOT");
                wifiConfig.preSharedKey = String.format("\"%s\"", "");

                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();

                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                findViewById(R.id.infoText).setVisibility(View.VISIBLE);
                TextView infoText = (TextView)findViewById(R.id.infoText);
                infoText.setText(wifiManager.getConnectionInfo().toString());
            }
        });
    }
}
