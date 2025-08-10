package com.example.fitlifetracker.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechRecognitionHelper {
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private SpeechRecognitionListener listener;
    private Context context;

    public interface SpeechRecognitionListener {
        void onSpeechResult(String result);
        void onSpeechError(String errorMessage);
        void onSpeechReady();
        void onSpeechStarted();
        void onSpeechFinished();
    }

    public SpeechRecognitionHelper(Context context, SpeechRecognitionListener listener) {
        this.context = context;
        this.listener = listener;
        initializeSpeechRecognizer();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new MyRecognitionListener());

            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        } else {
            Toast.makeText(context, "Speech recognition not available on this device.", Toast.LENGTH_LONG).show();
        }
    }

    public void startListening() {
        if (speechRecognizer != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
        } else {
            Toast.makeText(context, "Speech recognizer not initialized.", Toast.LENGTH_SHORT).show();
            initializeSpeechRecognizer();
            if (speechRecognizer != null) {
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        }
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            listener.onSpeechReady();
        }

        @Override
        public void onBeginningOfSpeech() {
            listener.onSpeechStarted();
        }

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {
            listener.onSpeechFinished();
        }

        @Override
        public void onError(int error) {
            String errorMessage;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMessage = "Audio recording error.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMessage = "Client side error. Check permissions or internet.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMessage = "Insufficient permissions.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMessage = "Network error.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMessage = "Network timeout.";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMessage = "No speech recognized.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMessage = "Recognition service busy.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMessage = "Server error.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMessage = "No speech input.";
                    break;
                default:
                    errorMessage = "Unknown speech recognition error.";
                    break;
            }
            listener.onSpeechError(errorMessage);
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                listener.onSpeechResult(matches.get(0));
            } else {
                listener.onSpeechError("No results found.");
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {}
        }

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }
}