package com.example.fitlifetracker.utils;

public class Constants {

    // Authentication related constants
    public static final String PREFS_AUTH = "auth_prefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_PASSWORD = "user_password";
    public static final String KEY_IDENTIFIER = "user_identifier"; // e.g., for display name

    // Notification Channel related constants
    public static final String NOTIFICATION_CHANNEL_ID = "fit_life_tracker_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "FitLife Tracker Notifications";
    public static final int STEPS_NOTIFICATION_ID = 101; // Unique ID for steps notification

    // Widget related constants
    public static final String WIDGET_ACTION_REFRESH = "com.example.fitlifetracker.WIDGET_REFRESH"; // <-- ADD THIS LINE

    // Other utility constants (e.g., for permissions, request codes)
    public static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1;
    public static final int PERMISSION_REQUEST_RECORD_AUDIO = 2;
    public static final int SPEECH_RECOGNITION_REQUEST_CODE = 3;

    // Database constants (if you're using a local database)
    public static final String DATABASE_NAME = "fitlife_db";
    public static final int DATABASE_VERSION = 1;

    // Fragment/UI tags
    public static final String FRAGMENT_TAG_HOME = "home_fragment";
    // Add more as needed
}