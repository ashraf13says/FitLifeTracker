package com.example.fitlifetracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Placeholder for AuthService.
 * If this service is intended for background authentication tasks (e.g., refreshing tokens),
 * it should extend android.app.Service.
 * If it's not actually a Service, remove it from AndroidManifest.xml.
 */
public class AuthService extends Service {

    private static final String TAG = "AuthService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AuthService created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AuthService started.");
        // TODO: Implement your authentication logic here (e.g., token refresh)
        // This service might run for a short period to perform a task and then stop itself.
        // If it needs to run in the foreground, you'll need to call startForeground().
        stopSelf(); // Stop the service when its work is done (for simple cases)
        return START_NOT_STICKY; // Not sticky, as it's a one-off task
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AuthService destroyed.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This service does not support binding, so return null.
        return null;
    }
}