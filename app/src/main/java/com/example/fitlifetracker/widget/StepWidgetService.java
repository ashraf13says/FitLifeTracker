package com.example.fitlifetracker.widget; // Corrected package

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * This is the service that provides the factory for the widget's data.
 */
public class StepWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StepWidgetFactory(this.getApplicationContext(), intent);
    }
}