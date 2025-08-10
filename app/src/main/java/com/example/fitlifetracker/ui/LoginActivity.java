package com.example.fitlifetracker.ui; // Assuming your activities are in .ui.activities
// Corrected package based on previous context. If still .ui, please adjust.

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitlifetracker.R;
import com.example.fitlifetracker.utils.AuthManager;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginBtn, signupRedirect;
    private AuthManager authManager;

    // Define constants for validation
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final String EMAIL_DOMAIN = "@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);

        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.buttonLogin);
        signupRedirect = findViewById(R.id.buttonSignupRedirect);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim(); // Trim whitespace
            String password = passwordInput.getText().toString().trim(); // Trim whitespace

            // --- Comprehensive Input Validation ---
            if (email.isEmpty()) {
                emailInput.setError("Email cannot be empty");
                return;
            }
            if (!email.endsWith(EMAIL_DOMAIN)) {
                emailInput.setError("Email must end with " + EMAIL_DOMAIN);
                return;
            }
            if (password.isEmpty()) {
                passwordInput.setError("Password cannot be empty");
                return;
            }
            if (password.length() < MIN_PASSWORD_LENGTH) {
                passwordInput.setError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
                return;
            }
            // --- End Input Validation ---

            // --- Authentication Logic using AuthManager ---
            // NOW, we check the actual return value of authManager.loginUser()
            if (authManager.loginUser(email, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Close LoginActivity
            } else {
                Toast.makeText(this, "Invalid email or password. Please check your credentials.", Toast.LENGTH_LONG).show();
            }
        });

        signupRedirect.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );
    }
}