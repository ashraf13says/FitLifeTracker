package com.example.fitlifetracker.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationUtil {
    public static void vibrate(Context context, int durationMs) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}