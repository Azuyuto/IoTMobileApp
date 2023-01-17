package com.example.iotapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.Manager.ModuleDataManager;

public class MainActivity extends AppCompatActivity {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    private ModuleDataManager moduleDataManager;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String email;

    boolean isSwitchOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Button ledButton = (Button) findViewById(R.id.button1);
        ImageView ledImage = (ImageView) findViewById(R.id.led);
        isSwitchOn = false;
        ledButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (!isSwitchOn) {
                    ledButton.setText("Turn On");
                    ledButton.setTextColor(Color.parseColor("white"));
                    ledButton.setBackgroundColor(Color.parseColor("#36BD31"));
                    ledImage.setImageResource(R.mipmap.ledoff);
                    isSwitchOn=true;
                }else{
                    ledButton.setText("Turn Off");
                    ledButton.setTextColor(Color.parseColor("white"));
                    ledButton.setBackgroundColor(Color.parseColor("red"));
                    ledImage.setImageResource(R.mipmap.ledon);
                    isSwitchOn=false;
                }
            }
        });

        Button wifiButton = (Button) findViewById(R.id.button2);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        Button nextButton = (Button) findViewById(R.id.button3);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkRequest.Builder builder = new NetworkRequest.Builder();

                connectivityManager.registerNetworkCallback(builder.build(),
                        new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                //Do your work here or restart your activity

                            }
                            @Override
                            public void onLost(Network network) {
                                //internet lost
                            }
                        });
            }
        });

        // initializing our shared preferences.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // getting data from shared prefs and
        // storing it in our string variable.
        email = sharedpreferences.getString(EMAIL_KEY, null);

        // initializing our textview and button.
        TextView welcomeTV = findViewById(R.id.hello);
        welcomeTV.setText("Welcome " + email + "!");
        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // calling method to edit values in shared prefs.
                SharedPreferences.Editor editor = sharedpreferences.edit();

                // below line will clear
                // the data in shared prefs.
                editor.clear();

                // off handlers
                mHandler.removeCallbacks(runnableCode);

                // below line will apply empty
                // data to shared prefs.
                editor.apply();

                // starting mainactivity after
                // clearing values in shared preferences.
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
        moduleDataManager = new ModuleDataManager();
        TextView light = findViewById(R.id.light);
        mHandler = new Handler();
        mHandler.post(runnableCode);
    }
    private Handler mHandler;

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
                Integer i = moduleDataManager.GetLastData().dataInt;
                TextView light = findViewById(R.id.light);
                light.setText("Resistor val: " + i.toString());
                mHandler.postDelayed(runnableCode, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}