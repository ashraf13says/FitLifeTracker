package com.example.fitlifetracker.widget; // Corrected package

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.fitlifetracker.R;
// You might also need imports for your data fetching, e.g.,
// import com.example.fitlifetracker.data.repositories.FitnessRepository;
// import com.example.fitlifetracker.auth.AuthManager;
// import com.example.fitlifetracker.utils.Constants; // If you store data in SharedPreferences

public class StepWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private int stepCount = 0;
    private int calories = 0;
    private int appWidgetId; // To identify which widget instance

    public StepWidgetFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        // Initialize values here. This is called when the factory is created.
        // In a real application, you would load persistent data for this specific widget instance.
        // For now, using mock values.
        stepCount = 1500; // Mock step count
        calories = 80;    // Mock calories

        // Example: To get real data from SharedPreferences (you need AuthManager & Constants)
        // AuthManager authManager = new AuthManager(context);
        // String loggedInUser = authManager.getLoggedInIdentifier(); // Get current user
        // SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_AUTH, Context.MODE_PRIVATE);
        // stepCount = prefs.getInt("todaySteps_" + loggedInUser, 0); // Assuming you save steps per user
        // calories = prefs.getInt("todayCalories_" + loggedInUser, 0); // Assuming you save calories per user
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_fitlife);

        // Corrected IDs to match your widget_fitlife.xml
        views.setTextViewText(R.id.widget_steps_value, String.valueOf(stepCount));
        views.setTextViewText(R.id.widget_calories_value, String.valueOf(calories));

        return views;
    }

    @Override
    public int getCount() {
        return 1; // Displaying a single set of data for the whole widget
    }

    @Override
    public void onDataSetChanged() {
        onCreate(); // Re-fetch mock data for simplicity
    }

    @Override
    public void onDestroy() {
        // Clean up any resources here.
    }

    @Override
    public RemoteViews getLoadingView() {
        return null; // Return null for default loading view
    }

    @Override
    public int getViewTypeCount() {
        return 1; // All items (in this case, the single item) look the same
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}