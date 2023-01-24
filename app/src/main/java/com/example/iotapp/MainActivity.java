package com.example.iotapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.iotapp.Manager.DeviceManager;
import com.example.iotapp.Model.AccountResponse;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";

    SharedPreferences sharedpreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(AUTHENTICATION_TOKEN, null);

        SetLogoutButton();
        SetRefreshButton();
        SetUserData();
        SetNewDevice();
    }

    private void SetUserData()
    {
        DeviceManager dm = new DeviceManager();
        try {
            AccountResponse user = dm.GetUserData(token);
            TableLayout table = findViewById(R.id.tableLayout);

            TextView helloText = findViewById(R.id.hello);
            helloText.setText("Hello " + user.username + "!");

            if(user.userDevices.size() > 0)
            for(int i = 0;i<user.userDevices.size();i++)
            {
                // Row
                TableRow row = new TableRow(this);
                row.setPadding(20, 20, 20, 20);
                row.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_VERTICAL);
                row.setBackground(ContextCompat.getDrawable(this, R.drawable.back));
                Integer id = user.userDevices.get(i).id;
                String name = user.userDevices.get(i).name;
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(MainActivity.this, DeviceActivity.class);
                        in.putExtra("deviceId", id);
                        in.putExtra("deviceName", name);
                        startActivity(in);
                        finish();
                    }
                });

                // Name
                TextView textName = new TextView(this);
                textName.setText(user.userDevices.get(i).name);

                // Image
                ImageView img = new ImageView(this);
                img.setImageResource(R.mipmap.device);

                row.addView(img);
                row.addView(textName);
                table.addView(row,1);
            }
            else
            {
                // Row
                TableRow row = new TableRow(this);
                row.setDividerPadding(5);
                row.setGravity(Gravity.CENTER_HORIZONTAL);
                row.setBackground(ContextCompat.getDrawable(this, R.drawable.back));

                // Name
                TextView textInfo = new TextView(this);
                textInfo.setText("No connected devices");
                textInfo.setTextColor(Color.RED);

                row.addView(textInfo);
                table.addView(row,1);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void SetRefreshButton()
    {
        TextView refreshText = findViewById(R.id.refresh);
        refreshText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void SetLogoutButton()
    {
        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void SetNewDevice()
    {
        Button logoutBtn = findViewById(R.id.addDeviceButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewDeviceActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}