package com.artamonov.appanalyzer.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.artamonov.appanalyzer.R;

/** Implementation of App Widget functionality. */
public class ApplicationsWidgetProvider extends AppWidgetProvider {

    private static void setList(RemoteViews views, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, ApplicationsWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.lvAppWidget, adapter);
    }

    private void updateAppWidget(
            Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views =
                new RemoteViews(context.getPackageName(), R.layout.applications_widget_provider);
        setList(views, context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}
}
