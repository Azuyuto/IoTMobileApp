package com.example.iotapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotapp.Manager.AccountManager;
import com.example.iotapp.Model.AccountResponse;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String AUTHENTICATION_TOKEN = "authentication_token";

    SharedPreferences sharedpreferences;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(AUTHENTICATION_TOKEN, null);

        SetLoginButton();
        SetSignUpButton();
    }

    private void SetLoginButton()
    {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        EditText usernameEdt = findViewById(R.id.usernameText);
        EditText passwordEdt = findViewById(R.id.passwordText);
        Button loginBtn = findViewById(R.id.signInButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                AccountManager am = new AccountManager();
                try {
                    AccountResponse ar = am.SignIn(usernameEdt.getText().toString(), passwordEdt.getText().toString());
                    if(!TextUtils.isEmpty(ar.token))
                    {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(AUTHENTICATION_TOKEN, ar.token);
                        editor.apply();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, ar.message, Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Problem with connection", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }
            }
        });
    }

    private void SetSignUpButton()
    {
        TextView signUpText = findViewById(R.id.signUpText);
        signUpText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (token != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}
