package com.example.fitlifetracker.utils;

public class CalorieCalculator {
    public static int calculateCalories(int steps) {
        return (int) (steps * 0.04); // Rough estimate: 0.04 kcal per step
    }
}
