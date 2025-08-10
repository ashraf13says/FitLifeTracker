package com.example.fitlifetracker.ui;

import android.Manifest; // Import for Permissions
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager; // Import for Permissions
import android.os.Build; // Import for Build version check
import android.os.Bundle;
import android.speech.RecognizerIntent; // Import for Speech Recognition
import android.speech.tts.TextToSpeech; // Optional: For voice feedback
import android.util.Log; // Import for Log.d debugging

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat; // Import for Permissions
import androidx.core.content.ContextCompat; // Import for Permissions
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitlifetracker.R;
import com.example.fitlifetracker.ui.activities.ActivityTrackingActivity; // Corrected path if needed
import com.example.fitlifetracker.ui.LoginActivity; // Corrected path if needed
import com.example.fitlifetracker.ui.SummaryActivity;
import com.example.fitlifetracker.utils.AuthManager;

import java.util.ArrayList;
import java.util.Locale; // For TextToSpeech locale

public class HomeFragment extends Fragment {

    private Button startActivityButton;
    private Button viewSummaryButton;
    private Button logoutButton;
    private Button buttonVoiceCommand; // Declare the voice command button

    private TextView welcomeTextView;
    private TextView textSteps, textCalories, textDistance;
    private TextView recognizedVoiceCommandText; // Declare the TextView for recognized command

    private static final int REQUEST_CODE_SPEECH_INPUT = 1001; // Request code for speech intent
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1; // Request code for audio permission

    private TextToSpeech textToSpeech; // Optional: For voice feedback

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize buttons
        startActivityButton = view.findViewById(R.id.buttonStartTracking);
        viewSummaryButton = view.findViewById(R.id.buttonSummary);
        logoutButton = view.findViewById(R.id.button_logout);
        buttonVoiceCommand = view.findViewById(R.id.buttonVoiceCommand); // Initialize voice command button

        // Initialize TextViews
        welcomeTextView = view.findViewById(R.id.welcomeTextView);
        textSteps = view.findViewById(R.id.textSteps);
        textCalories = view.findViewById(R.id.textCalories);
        textDistance = view.findViewById(R.id.textDistance);
        recognizedVoiceCommandText = view.findViewById(R.id.recognized_voice_command_text); // Initialize recognized text TextView

        setWelcomeMessage();

        // Set OnClickListeners
        startActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityTrackingActivity.class);
            startActivity(intent);
        });

        viewSummaryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SummaryActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            performLogout();
        });

        // >>> SET ONCLICK LISTENER FOR VOICE COMMAND BUTTON <<<
        buttonVoiceCommand.setOnClickListener(v -> {
            checkAndRequestAudioPermission(); // Check and request permission before starting speech recognition
        });

        // Optional: Initialize TextToSpeech for voice feedback
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US); // Set your desired locale
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported or missing data");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    // This method will be called when the fragment is no longer in use
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    // --- Speech Recognition Logic ---

    private void checkAndRequestAudioPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        } else {
            // Permission has already been granted
            startSpeechRecognition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                startSpeechRecognition();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Record Audio permission denied. Voice command cannot be used.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()); // Use default locale
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'Start Tracking' or 'View Summary'");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Speech recognition not supported on your device.", Toast.LENGTH_SHORT).show();
            Log.e("HomeFragment", "Speech recognition not supported: " + e.getMessage());
        }
    }

    // Handles the result from the speech recognition intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == getActivity().RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String recognizedText = result.get(0).toLowerCase(Locale.getDefault()); // Get the first result and convert to lowercase
                    recognizedVoiceCommandText.setText("Recognized: " + recognizedText); // Display recognized text

                    Log.d("VoiceCommand", "Recognized: " + recognizedText);

                    // --- Process the recognized command ---
                    if (recognizedText.contains("start tracking")) {
                        speak("Starting tracking."); // Voice feedback
                        startActivityButton.performClick(); // Simulate button click
                    } else if (recognizedText.contains("view summary")) {
                        speak("Opening summary."); // Voice feedback
                        viewSummaryButton.performClick(); // Simulate button click
                    } else {
                        speak("Command not recognized. Please say 'Start Tracking' or 'View Summary'."); // Voice feedback
                        Toast.makeText(getContext(), "Command not recognized: " + recognizedText, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Speech recognition cancelled or failed
                Toast.makeText(getContext(), "Speech recognition cancelled or failed.", Toast.LENGTH_SHORT).show();
                recognizedVoiceCommandText.setText("Say 'Start Tracking' or 'View Summary'");
            }
        }
    }

    // Optional: Method for Text-to-Speech voice feedback
    private void speak(String text) {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop(); // Stop previous speech
        }
        if (textToSpeech != null && text.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }


    // --- Existing methods (setWelcomeMessage, performLogout) ---

    private void setWelcomeMessage() {
        AuthManager authManager = new AuthManager(getContext());
        String username = authManager.getLoggedInUserName();
        welcomeTextView.setText(getString(R.string.welcome_message, username));
    }

    private void performLogout() {
        AuthManager authManager = new AuthManager(getContext());
        authManager.logoutUser();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
    }
}