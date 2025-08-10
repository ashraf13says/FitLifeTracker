package com.example.fitlifetracker.ui; // Ensure this package matches your actual structure

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitlifetracker.R;
import com.example.fitlifetracker.utils.AuthManager;

public class SignupActivity extends AppCompatActivity {
    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput; // Declare confirmPasswordInput
    private Button signupBtn;
    private AuthManager authManager;

    // Define constants for validation
    private static final int MIN_PASSWORD_LENGTH = 8; // Changed to 8 for better security
    private static final String EMAIL_DOMAIN = "@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        authManager = new AuthManager(this);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput); // Initialize confirmPasswordInput
        signupBtn = findViewById(R.id.signupButton);

        signupBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim(); // Get confirm password

            // --- Comprehensive Input Validation ---
            if (name.isEmpty()) {
                nameInput.setError("Full Name cannot be empty");
                return;
            }
            if (email.isEmpty()) {
                emailInput.setError("Email cannot be empty");
                return;
            }
            if (!email.endsWith(EMAIL_DOMAIN)) {
                emailInput.setError("Email must end with " + EMAIL_DOMAIN);
                return;
            }

            // Password Validation
            if (password.isEmpty()) {
                passwordInput.setError("Password cannot be empty");
                return;
            }
            if (password.length() < MIN_PASSWORD_LENGTH) {
                passwordInput.setError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
                return;
            }
            if (!password.matches(".*[a-z].*")) { // At least one lowercase letter
                passwordInput.setError("Password must contain at least one lowercase letter");
                return;
            }
            if (!password.matches(".*[A-Z].*")) { // At least one uppercase letter
                passwordInput.setError("Password must contain at least one uppercase letter");
                return;
            }
            if (!password.matches(".*\\d.*")) { // At least one digit
                passwordInput.setError("Password must contain at least one digit");
                return;
            }
            // Basic special character check (you can expand this regex if needed for more special chars)
            if (!password.matches(".*[!@#$%^&*()_+=\\[\\]{}|;:'\",.<>/?`~-].*")) {
                passwordInput.setError("Password must contain at least one special character (!@#$%...)");
                return;
            }

            // Confirm Password Check
            if (confirmPassword.isEmpty()) {
                confirmPasswordInput.setError("Please confirm your password");
                return;
            }
            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }
            // --- End Input Validation ---

            // If all validations pass, proceed with registration
            if (authManager.registerUser(name,email, password)) {
                Toast.makeText(this, "Signup successful! Please log in.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Signup failed. User with this email might already exist.", Toast.LENGTH_LONG).show();
            }
        });
    }
}