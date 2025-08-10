package com.example.fitlifetracker.ui.activities;

import android.content.Context; // Added for SharedPreferences
import android.content.SharedPreferences; // Added for data persistence
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Added for user feedback
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlifetracker.R;

import java.util.Locale;

public class ActivityTrackingActivity extends AppCompatActivity {

    private TextView timerTextView;
    private TextView stepsTextView;
    private TextView distanceTextView;
    private TextView caloriesTextView;
    private Button startPauseButton;
    private Button stopButton;
    private Button resetButton;
    private Button tapToBurnButton; // NEW: Declare the Tap to Burn button

    private long startTime = 0L;
    private long pauseOffset = 0L;
    private Handler timerHandler = new Handler();
    private boolean isRunning = false;
    private boolean isPaused = false;

    // Tracking variables
    private int currentSteps = 0;
    private double currentDistance = 0.0; // in km
    private int currentCalories = 0;

    // SharedPreferences for data persistence
    private SharedPreferences sharedPreferences;
    private static final String PREFS_FILE = "FitLifeTrackerPrefs"; // Name of your preference file
    private static final String KEY_STEPS = "saved_steps";
    private static final String KEY_DISTANCE = "saved_distance";
    private static final String KEY_CALORIES = "saved_calories";
    private static final String KEY_START_TIME = "saved_start_time"; // For timer persistence
    private static final String KEY_PAUSE_OFFSET = "saved_pause_offset"; // For timer persistence
    private static final String KEY_IS_RUNNING = "saved_is_running"; // For timer persistence
    private static final String KEY_IS_PAUSED = "saved_is_paused"; // For timer persistence


    // Runnable for the timer
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));

            // Simulate progress (for demonstration only, replace with real sensor data)
            if (isRunning && !isPaused) {
                // Simulate steps increase every 5 seconds (50 steps per 5 seconds)
                // This condition makes sure it only adds steps if the timer is moving
                if (seconds % 5 == 0 && seconds > 0 && timerTextView.getTag() == null) {
                    currentSteps += 50;
                    currentDistance += 0.04; // Roughly 50 steps is 40 meters (0.04 km)
                    currentCalories += 2; // Roughly 2 calories per 50 steps
                    updateStatsUI();
                    saveTrackingData(); // Save data after automatic increment
                    timerTextView.setTag("updated"); // Set a tag to prevent multiple updates in the same second
                } else if (seconds % 5 != 0) {
                    timerTextView.setTag(null); // Clear tag when not on a 5-second mark
                }
            }

            timerHandler.postDelayed(this, 1000); // Run every 1 second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        // Initialize UI elements
        timerTextView = findViewById(R.id.text_timer);
        stepsTextView = findViewById(R.id.text_steps_tracked);
        distanceTextView = findViewById(R.id.text_distance_tracked);
        caloriesTextView = findViewById(R.id.text_calories_tracked);
        startPauseButton = findViewById(R.id.button_start_pause);
        stopButton = findViewById(R.id.button_stop);
        resetButton = findViewById(R.id.button_reset);
        tapToBurnButton = findViewById(R.id.button_tap_to_burn); // NEW: Initialize the Tap to Burn button

        // Load saved data and state
        loadTrackingData(); // Load steps, distance, calories
        loadTimerState(); // Load timer state

        // Set initial state for buttons (based on loaded timer state)
        updateButtonStates();

        // Set Click Listeners
        startPauseButton.setOnClickListener(v -> toggleStartPause());
        stopButton.setOnClickListener(v -> stopTracking());
        resetButton.setOnClickListener(v -> resetTracking());
        tapToBurnButton.setOnClickListener(v -> addManualBurn()); // NEW: Set listener for Tap to Burn

        // Initial UI update with loaded data
        updateStatsUI();
    }

    // NEW: Method for "Tap to Burn" functionality
    private void addManualBurn() {
        final int stepsAdd = 50;
        final double distanceAdd = 0.04; // km
        final int caloriesAdd = 2;

        currentSteps += stepsAdd;
        currentDistance += distanceAdd;
        currentCalories += caloriesAdd;

        updateStatsUI(); // Update UI immediately
        saveTrackingData(); // Save data immediately

        Toast.makeText(this, String.format(Locale.getDefault(), "+%d Steps, +%.2f km, +%d Cal Burned!", stepsAdd, distanceAdd, caloriesAdd), Toast.LENGTH_SHORT).show();
    }

    private void toggleStartPause() {
        if (!isRunning) { // If not running, start
            startTime = System.currentTimeMillis() - pauseOffset; // Adjust start time based on pause offset
            timerHandler.postDelayed(timerRunnable, 0); // Start the timer immediately
            isRunning = true;
            isPaused = false;
            saveTimerState(); // Save timer state
        } else if (isPaused) { // If paused, resume
            startTime = System.currentTimeMillis() - pauseOffset; // Resume from where it left off
            timerHandler.postDelayed(timerRunnable, 0);
            isPaused = false;
            saveTimerState(); // Save timer state
        } else { // If running, pause
            timerHandler.removeCallbacks(timerRunnable);
            pauseOffset = System.currentTimeMillis() - startTime; // Store elapsed time as offset
            isPaused = true;
            saveTimerState(); // Save timer state
        }
        updateButtonStates();
    }

    private void stopTracking() {
        timerHandler.removeCallbacks(timerRunnable); // Stop the timer
        isRunning = false;
        isPaused = false;
        startTime = 0L; // Reset start time
        pauseOffset = 0L; // Reset pause offset as well
        timerTextView.setText("00:00:00"); // Reset timer display
        saveTimerState(); // Save timer state

        // Reset stats for new tracking session if desired, AND THEN SAVE
        currentSteps = 0;
        currentDistance = 0.0;
        currentCalories = 0;
        updateStatsUI(); // Update UI with reset stats
        saveTrackingData(); // Save reset stats to SharedPreferences
        updateButtonStates();
    }

    private void resetTracking() {
        // This stops and resets everything, essentially like hitting Stop and then it's ready for Start again
        stopTracking(); // Reuses the stop logic to clear everything
        // Note: stopTracking already handles saving reset data and updating button states
    }

    private void updateStatsUI() {
        stepsTextView.setText(String.format(Locale.getDefault(), "%d", currentSteps));
        distanceTextView.setText(String.format(Locale.getDefault(), "%.2f km", currentDistance));
        caloriesTextView.setText(String.format(Locale.getDefault(), "%d", currentCalories));
    }

    private void updateButtonStates() {
        startPauseButton.setText(isRunning && !isPaused ? "Pause" : (isPaused ? "Resume" : "Start"));
        stopButton.setEnabled(isRunning || isPaused); // Enable stop if running or paused
        resetButton.setEnabled(isRunning || isPaused); // Enable reset if running or paused
        // Tap to Burn button can be enabled/disabled based on preference.
        // I'll keep it always enabled for manual logging.
        // tapToBurnButton.setEnabled(true);
    }

    // NEW: Save all tracking data to SharedPreferences
    private void saveTrackingData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_STEPS, currentSteps);
        editor.putFloat(KEY_DISTANCE, (float) currentDistance); // SharedPreferences stores float, not double. Cast it.
        editor.putInt(KEY_CALORIES, currentCalories);
        editor.apply(); // Apply asynchronously
        // For immediate save, use editor.commit(); (but apply() is generally preferred)
    }

    // NEW: Load all tracking data from SharedPreferences
    private void loadTrackingData() {
        currentSteps = sharedPreferences.getInt(KEY_STEPS, 0);
        currentDistance = sharedPreferences.getFloat(KEY_DISTANCE, 0.0f); // Load as float, then assign to double
        currentCalories = sharedPreferences.getInt(KEY_CALORIES, 0);
    }

    // NEW: Save timer state
    private void saveTimerState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_START_TIME, startTime);
        editor.putLong(KEY_PAUSE_OFFSET, pauseOffset);
        editor.putBoolean(KEY_IS_RUNNING, isRunning);
        editor.putBoolean(KEY_IS_PAUSED, isPaused);
        editor.apply();
    }

    // NEW: Load timer state
    private void loadTimerState() {
        startTime = sharedPreferences.getLong(KEY_START_TIME, 0L);
        pauseOffset = sharedPreferences.getLong(KEY_PAUSE_OFFSET, 0L);
        isRunning = sharedPreferences.getBoolean(KEY_IS_RUNNING, false);
        isPaused = sharedPreferences.getBoolean(KEY_IS_PAUSED, false);

        // If the timer was running when the app was closed, resume it
        if (isRunning && !isPaused) {
            timerHandler.postDelayed(timerRunnable, 0);
        } else if (isPaused) {
            // If it was paused, just display the timer at the paused offset
            long millis = pauseOffset;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Pause timer and save state/data when the activity goes to background
        if (isRunning && !isPaused) { // Only pause if actively running, not if user already paused
            timerHandler.removeCallbacks(timerRunnable);
            pauseOffset = System.currentTimeMillis() - startTime; // Store elapsed time if paused by system
            isPaused = true; // Mark as paused by system
            saveTimerState(); // Save the paused state
        }
        saveTrackingData(); // Always save data when leaving foreground
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume timer if it was running and went to background (not user-paused)
        loadTrackingData(); // Ensure data is up-to-date
        if (isRunning && isPaused && pauseOffset > 0) { // If it was running and paused by system
            startTime = System.currentTimeMillis() - pauseOffset;
            isPaused = false; // Unpause
            timerHandler.postDelayed(timerRunnable, 0);
            saveTimerState(); // Save resumed state
        }
        updateStatsUI(); // Ensure UI reflects current data on resume
        updateButtonStates(); // Ensure buttons reflect current state on resume
    }

    @Override
    protected void onStop() {
        super.onStop();
        // No additional handler removal needed here, onPause should have handled it if running
        // Data is saved in onPause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure all pending callbacks are removed when the activity is destroyed
        timerHandler.removeCallbacks(timerRunnable);
        // Data should have been saved in onPause/onStop.
    }
}