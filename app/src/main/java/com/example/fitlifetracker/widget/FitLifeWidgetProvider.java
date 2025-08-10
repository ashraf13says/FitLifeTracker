package com.example.fitlifetracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.fitlifetracker.R; // Make sure R is resolved
import com.example.fitlifetracker.ui.MainActivity; // Assuming MainActivity is in ui package
import com.example.fitlifetracker.utils.Constants; // Assuming Constants is in utils package

/**
 * Implementation of App Widget functionality for FitLife Tracker.
 * This class is responsible for updating the widget's display.
 */
public class FitLifeWidgetProvider extends AppWidgetProvider {

    /**
     * Updates a single instance of the widget.
     * @param context The context.
     * @param appWidgetManager The AppWidgetManager instance.
     * @param appWidgetId The ID of the widget instance to update.
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object for the widget layout
        // It references R.layout.widget_fitlife, so this XML must exist and be correct.
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_fitlife);

        // Set up a click listener for the entire widget to open MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        // FLAG_IMMUTABLE is required for PendingIntents targeting Android 12 (API 31) and above.
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        // R.id.widget_layout must be the ID of the root layout in widget_fitlife.xml
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        // --- How the widget gets its data (using the factory for dynamic content) ---
        // This intent will be passed to the RemoteViewsService (StepWidgetService)
        // to create the StepWidgetFactory.
        Intent serviceIntent = new Intent(context, StepWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // It's good practice to make the Intent data unique for each widget instance
        // so the factory can distinguish them, especially if you have multiple widgets.
        serviceIntent.setData(android.net.Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // Manually create the factory and get the view from it.
        // This pattern is used when you don't have a ListView/GridView inside your widget
        // but still want to use a RemoteViewsFactory for data management.
        StepWidgetFactory factory = new StepWidgetFactory(context, serviceIntent);
        factory.onCreate(); // Initialize data in the factory
        RemoteViews updatedViews = factory.getViewAt(0); // Get the single view from the factory
        factory.onDestroy(); // Clean up factory resources after getting the view

        // Instruct the widget manager to update the widget with the views from the factory
        appWidgetManager.updateAppWidget(appWidgetId, updatedViews);

        // This line is important if the data in your factory can change.
        // It tells the AppWidgetManager that the data in the widget's views has changed,
        // prompting a refresh.
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_layout);
    }

    /**
     * Called when the widget is updated (e.g., periodically, or on user request).
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Called when the first instance of this widget is placed on the home screen.
     */
    @Override
    public void onEnabled(Context context) {
        // Perform any one-time setup here, e.g., starting a background service if needed.
    }

    /**
     * Called when the last instance of this widget is removed from the home screen.
     */
    @Override
    public void onDisabled(Context context) {
        // Perform any cleanup here, e.g., stopping background services that are only for the widget.
    }

    /**
     * Handles custom broadcast intents sent to the widget.
     * This is used for actions like a "Refresh" button on the widget.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // Check if the intent is for a custom refresh action (defined in Constants)
        if (Constants.WIDGET_ACTION_REFRESH.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, FitLifeWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            // Request data set changed for all instances of this widget
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_layout);
            // Also trigger a full update for all instances
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}