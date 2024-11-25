package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

    }
    public void RedirectToRegistrationPage(View view) {
        Intent intent = new Intent(this, RegistrationPage.class);
        startActivity(intent);

    }
    public void RedirectToAdminLoginPage(View view) {
        Intent intent = new Intent(this, AdminLogin.class);
        startActivity(intent);
    }
    public void RedirectToUserLoginPage(View view){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);

    }
}