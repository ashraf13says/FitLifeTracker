package com.example.fitlifetracker; // This is the correct package for SplashActivity

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlifetracker.ui.MainActivity;
import com.example.fitlifetracker.ui.LoginActivity;
import com.example.fitlifetracker.utils.AuthManager; // CORRECT IMPORT: AuthManager is in utils package

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If you have a layout file specifically for the splash screen (e.g., activity_splash.xml), uncomment below:
        // setContentView(R.layout.activity_splash); // Example: R.layout.activity_splash

        // Initialize AuthManager
        AuthManager authManager = new AuthManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent nextActivityIntent;
            if (authManager.isLoggedIn()) {
                // User is logged in, go to MainActivity
                nextActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // User is not logged in, go to LoginActivity
                nextActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(nextActivityIntent);
            finish(); // Close the splash activity so it's not on the back stack
        }, SPLASH_DISPLAY_LENGTH);
    }
}