package com.example.fitlifetracker.ui; // Ensure this package name is correct

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlifetracker.R;
import com.example.fitlifetracker.utils.ChartUtils; // Make sure ChartUtils is in your utils package
import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {

    private BarChart stepsBarChart;
    private TextView totalStepsSummaryText;
    private TextView avgStepsSummaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        stepsBarChart = findViewById(R.id.steps_bar_chart);
        totalStepsSummaryText = findViewById(R.id.total_steps_summary_text);
        avgStepsSummaryText = findViewById(R.id.avg_steps_summary_text);

        displayStepsChart();
    }

    private void displayStepsChart() {
        // Dummy data for demonstration: Steps for the last 7 days
        // In a real app, you would fetch this from your database
        List<Float> stepsData = new ArrayList<>(Arrays.asList(
                (float) 5000, // Day 1
                (float) 7500, // Day 2
                (float) 6000, // Day 3
                (float) 8200, // Day 4
                (float) 4500, // Day 5
                (float) 9000, // Day 6
                (float) 7000  // Day 7 (Today/Most Recent)
        ));

        // Labels for the x-axis (e.g., Day 1, Day 2, ..., Day 7)
        List<String> xLabels = new ArrayList<>();
        for (int i = 1; i <= stepsData.size(); i++) {
            xLabels.add("Day " + i);
        }

        // --- IMPORTANT FIX: Changed method name from setupStepChart to setupBarChart ---
        ChartUtils.setupBarChart(
                stepsBarChart,
                stepsData,
                xLabels,
                "Daily Steps",
                "Steps"
        );
        // --------------------------------------------------------------------------------

        // Calculate and display summary statistics
        calculateAndDisplaySummary(stepsData);
    }

    private void calculateAndDisplaySummary(List<Float> stepsData) {
        if (stepsData == null || stepsData.isEmpty()) {
            totalStepsSummaryText.setText("Total Steps (Last 7 Days): N/A");
            avgStepsSummaryText.setText("Average Daily Steps: N/A");
            return;
        }

        float totalSteps = 0;
        for (Float steps : stepsData) {
            totalSteps += steps;
        }

        int averageSteps = (int) (totalSteps / stepsData.size());

        totalStepsSummaryText.setText(String.format(Locale.getDefault(), "Total Steps (Last %d Days): %d", stepsData.size(), (int) totalSteps));
        avgStepsSummaryText.setText(String.format(Locale.getDefault(), "Average Daily Steps: %d", averageSteps));
    }
}