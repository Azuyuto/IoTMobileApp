package com.example.iotapp;

import android.content.Intent;
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

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_registration);

        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        SetRegistrationButton();
        SetLoginButton();
    }

    private void SetRegistrationButton()
    {
        EditText emailEdt = findViewById(R.id.emailText);
        EditText usernameEdt = findViewById(R.id.usernameText);
        EditText passwordEdt = findViewById(R.id.passwordText);
        Button registrationBtn = findViewById(R.id.signUpButton);

        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                AccountManager am = new AccountManager();
                try {
                    AccountResponse ar = am.SignUp(emailEdt.getText().toString(), usernameEdt.getText().toString(), passwordEdt.getText().toString());
                    if(TextUtils.isEmpty(ar.message))
                    {
                        Toast.makeText(RegistrationActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this, ar.message, Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, "Problem with connection", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }
            }
        });
    }

    private void SetLoginButton()
    {
        TextView signUpText = findViewById(R.id.signInText);
        signUpText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
