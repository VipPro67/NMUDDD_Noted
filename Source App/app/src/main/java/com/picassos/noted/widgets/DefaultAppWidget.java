package com.picassos.noted.widgets;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.picassos.noted.R;
import com.picassos.noted.activities.MainActivity;

public class DefaultAppWidget extends AppWidgetProvider {
    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("activity", "add_note");
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.default_app_widget);
            views.setOnClickPendingIntent(R.id.add_note, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
