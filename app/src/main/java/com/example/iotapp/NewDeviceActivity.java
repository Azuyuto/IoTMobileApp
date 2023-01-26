package com.example.iotapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.iotapp.Manager.DeviceManager;
import com.example.iotapp.Utils.WifiReceiver;

import java.util.Objects;

public class NewDeviceActivity extends AppCompatActivity {
    public static final String IOT_WIFI_PASSWORD = "1qazxsW@";
    public static final String WEBSITE_URL = "http://192.168.10.1";
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    private ListView wifiList;
    private WifiManager wifiManager;
    private WifiReceiver receiverWifi;
    private String wifiName;

    private Handler mHandler;
    private Handler mHandlerScanning;

    SharedPreferences sharedpreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_new_device);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(AUTHENTICATION_TOKEN, null);
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        SetWifiConnect();
        SetBackButton();
        SetConnectButton();
        SetRefresh();
        SetOpenWebsite();
        SetSubmitButton();
    }

    private void SetWifiConnect()
    {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiList = findViewById(R.id.wifiList);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        wifiList.setOnItemClickListener((adapterView, view, i, l) -> wifiName = wifiList.getAdapter().getItem(i).toString());
        Button connectBtn = findViewById(R.id.selectWifiButton);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectToWiFi();
            }
        });
    }

    private void Scanning() {
        if (ActivityCompat.checkSelfPermission(NewDeviceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    NewDeviceActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            wifiManager.startScan();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
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
                String name = ((TextView) findViewById(R.id.nameText)).getText().toString();
                String guid = ((TextView) findViewById(R.id.guidText)).getText().toString();

                if (name.isEmpty() || guid.isEmpty()) {
                    Toast.makeText(NewDeviceActivity.this, "Name and Guid are required.", Toast.LENGTH_SHORT).show();
                } else if (!internetIsConnected()) {
                    Toast.makeText(NewDeviceActivity.this, "No connection with internet.", Toast.LENGTH_SHORT).show();
                } else {
                    mHandler.removeCallbacks(runnableCode); // off handler
                    mHandlerScanning.removeCallbacks(runnableCodeScanning); // off handler
                    try {
                        DeviceManager dm = new DeviceManager();
                        String message = dm.AddDevice(token, name, guid);
                        Toast.makeText(NewDeviceActivity.this, message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
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
                mHandlerScanning.removeCallbacks(runnableCodeScanning); // off handler

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(NewDeviceActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            } else {
                Toast.makeText(NewDeviceActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if we have permission to access coarse location
            if (ContextCompat.checkSelfPermission(NewDeviceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(NewDeviceActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(NewDeviceActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(NewDeviceActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(NewDeviceActivity.this, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
    }


    private void ConnectToWiFi() {
        WifiConfiguration conf = BuildWifiConfig();
        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private WifiConfiguration BuildWifiConfig() {
        String networkSSID = wifiName.split(" - ")[0];
        String networkPass = IOT_WIFI_PASSWORD;
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey = String.format("\"%s\"", networkPass);
        return conf;
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

        mHandlerScanning = new Handler();
        mHandlerScanning.post(runnableCodeScanning);
    }

    @SuppressLint("SetTextI18n")
    private void CheckWifiSSID()
    {
        TextView statusText = findViewById(R.id.step1status);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        statusText.setText("Current SSID: " + ssid);
    }

    private final Runnable runnableCode = new Runnable() {
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

    private final Runnable runnableCodeScanning = new Runnable() {
        @Override
        public void run() {
            try {
                Scanning();
                mHandler.postDelayed(runnableCode, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
