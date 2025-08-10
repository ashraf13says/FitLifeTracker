package com.example.fitlifetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

// Manages user authentication state using SharedPreferences
public class AuthManager {

    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_IDENTIFIER = "loggedInIdentifier"; // This will remain email
    private static final String KEY_USER_PREFIX = "user_"; // Prefix for user-specific data (for password)

    // >>> NEW KEYS <<<
    private static final String KEY_USER_NAME_PREFIX = "userName_"; // Prefix for storing name by email
    public static final String KEY_CURRENT_LOGGED_IN_USER_NAME = "currentLoggedInUserName"; // Public so HomeFragment can access
    // >>> END NEW KEYS <<<

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public AuthManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Registers a new user by storing their name, email, and password.
     *
     * @param name The user's full name.
     * @param email The user's email.
     * @param password The user's password.
     * @return true if registration is successful, false if user already exists or data is invalid.
     */
    public boolean registerUser(String name, String email, String password) { // Modified signature
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (sharedPreferences.contains(KEY_USER_PREFIX + email)) {
            // User already registered
            return false;
        }

        // Store user credentials (password)
        editor.putString(KEY_USER_PREFIX + email, password);
        // >>> NEW: Store user's name associated with their email <<<
        editor.putString(KEY_USER_NAME_PREFIX + email, name);
        // >>> END NEW <<<
        editor.apply();
        return true;
    }

    /**
     * Logs in a user by verifying credentials.
     * On successful login, also stores the user's name as the current logged-in user's display name.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return true if login is successful, false otherwise.
     */
    public boolean loginUser(String email, String password) {
        String storedPassword = sharedPreferences.getString(KEY_USER_PREFIX + email, null);

        if (storedPassword != null && storedPassword.equals(password)) {
            // Credentials match, mark as logged in
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_LOGGED_IN_IDENTIFIER, email); // Still store email as identifier

            // >>> NEW: Retrieve the user's name and store it for current session display <<<
            String userName = sharedPreferences.getString(KEY_USER_NAME_PREFIX + email, "Guest");
            editor.putString(KEY_CURRENT_LOGGED_IN_USER_NAME, userName);
            // >>> END NEW <<<

            editor.apply();
            return true;
        }
        return false;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Logs out the current user.
     */
    public void logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_LOGGED_IN_IDENTIFIER); // Clear logged-in email identifier
        editor.remove(KEY_CURRENT_LOGGED_IN_USER_NAME); // >>> NEW: Clear current user's display name <<<
        editor.apply();
    }

    /**
     * Gets the identifier of the currently logged-in user (email).
     * @return The identifier (email), or null if not logged in.
     */
    public String getLoggedInIdentifier() {
        return sharedPreferences.getString(KEY_LOGGED_IN_IDENTIFIER, null);
    }

    /**
     * Gets the display name of the currently logged-in user.
     * @return The display name, or "Guest" if not logged in or name not found.
     */
    public String getLoggedInUserName() {
        return sharedPreferences.getString(KEY_CURRENT_LOGGED_IN_USER_NAME, "Guest");
    }
}