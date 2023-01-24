package com.example.iotapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.iotapp.Manager.DeviceManager;
import com.example.iotapp.Model.DataResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DeviceActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";

    private DeviceManager deviceManager;
    private Handler mHandler;

    SharedPreferences sharedpreferences;
    String token, name;
    Integer deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_device);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(AUTHENTICATION_TOKEN, null);
        deviceManager = new DeviceManager();
        deviceId = getIntent().getIntExtra("deviceId", 0);
        name = getIntent().getStringExtra("deviceName");
        TextView nameText = findViewById(R.id.deviceText);
        nameText.setText(name);

        SetBackButton();
        SetForgotButton();
        SetDeviceData();
        SetRefresh();
    }

    private void SetBackButton()
    {
        Button backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnableCode); // off handler

                Intent i = new Intent(DeviceActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void SetForgotButton()
    {
        Button forgetBtn = findViewById(R.id.forgetButton);
        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnableCode); // off handler

                try {
                    String message = deviceManager.ForgetDevice(token, deviceId);
                    Toast.makeText(DeviceActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(DeviceActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void SetDeviceData()
    {
        try {
            List<DataResponse> data = deviceManager.GetData(token, deviceId);
            TableLayout table = findViewById(R.id.dataLayout);

            int childCount = table.getChildCount();
            if(childCount > 1)
            {
                table.removeViews(1, childCount - 1);
            }

            if(data.size() > 0)
            {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
                params.weight = 1;
                for(int i = 0;i<data.size();i++)
                {
                    // Row
                    TableRow row = new TableRow(this);
                    row.setPadding(20, 20, 20, 20);
                    row.setBackground(ContextCompat.getDrawable(this, R.drawable.back));

                    // Data
                    TextView textDate = new TextView(this);
                    Date expiry = new Date(data.get(i).epochDate * 1000);
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    textDate.setText(df.format(expiry));
                    textDate.setBackground(ContextCompat.getDrawable(this, R.drawable.back));
                    textDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textDate.setLayoutParams(params);
                    textDate.setPadding(20, 20, 20, 20);

                    // Value
                    TextView textValue = new TextView(this);
                    textValue.setText(String.valueOf(data.get(i).dataInt));
                    textValue.setBackground(ContextCompat.getDrawable(this, R.drawable.back));
                    textValue.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textValue.setLayoutParams(params);
                    textValue.setPadding(20, 20, 20, 20);

                    row.addView(textDate);
                    row.addView(textValue);
                    table.addView(row,1);
                }
            }
            else
            {
                // Row
                TableRow row = new TableRow(this);
                row.setPadding(20, 20, 20, 20);
                row.setDividerPadding(5);
                row.setGravity(Gravity.CENTER_HORIZONTAL);
                row.setBackground(ContextCompat.getDrawable(this, R.drawable.back));

                // Name
                TextView textInfo = new TextView(this);
                textInfo.setText("No data");
                textInfo.setTextColor(Color.RED);

                row.addView(textInfo);
                table.addView(row,1);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void SetRefresh()
    {
        mHandler = new Handler();
        mHandler.post(runnableCode);
    }

    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
                SetDeviceData();
                mHandler.postDelayed(runnableCode, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
