package com.example.iotapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.Manager.DeviceManager;

public class NewDeviceActivity extends AppCompatActivity {
    public static final String SSID = "\"IOT\"";
    public static final String WEBSITE_URL = "http://192.168.10.1";
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";

    private Handler mHandler;
    private WifiManager wifiManager;

    SharedPreferences sharedpreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_device);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(AUTHENTICATION_TOKEN, null);
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        SetBackButton();
        SetConnectButton();
        SetRefresh();
        SetOpenWebsite();
        SetSubmitButton();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {
                    Toast.makeText(NewDeviceActivity.this, "Access denied!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(NewDeviceActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });

    private void SetSubmitButton() {
        Button submitBtn = findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((TextView)findViewById(R.id.nameText)).getText().toString();
                String guid = ((TextView)findViewById(R.id.guidText)).getText().toString();

                if(name.isEmpty() || guid.isEmpty())
                {
                    Toast.makeText(NewDeviceActivity.this, "Name and Guid are required.", Toast.LENGTH_SHORT).show();
                }
                else if(!internetIsConnected())
                {
                    Toast.makeText(NewDeviceActivity.this, "No connection with internet.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mHandler.removeCallbacks(runnableCode); // off handler
                    try {
                        DeviceManager dm = new DeviceManager();
                        String message = dm.AddDevice(token, name, guid);
                        Toast.makeText(NewDeviceActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                    Intent i = new Intent(NewDeviceActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private void SetBackButton() {
        Button backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnableCode); // off handler

                Intent i = new Intent(NewDeviceActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void SetConnectButton() {
        Button connectBtn = findViewById(R.id.openWifiButton);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void SetOpenWebsite() {
        Button websiteBtn = findViewById(R.id.openWebsiteButton);
        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL));
                startActivity(browserIntent);
            }
        });
    }

    private void SetRefresh()
    {
        mHandler = new Handler();
        mHandler.post(runnableCode);
    }

    private void CheckWifiSSID()
    {
        TextView statusText = findViewById(R.id.step1status);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        statusText.setText("Current SSID: " + ssid);
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
                CheckWifiSSID();
                mHandler.postDelayed(runnableCode, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
