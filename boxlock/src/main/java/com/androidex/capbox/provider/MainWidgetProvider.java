package com.androidex.capbox.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.activity.LoginActivity;

/**
 * Created by daiyiming on 2016/11/28.
 */

public class MainWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        display(context);
    }

    private void display(Context context) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.provider_appwidget_main);
        // 父控件添加点击事件
        remoteView.setOnClickPendingIntent(R.id.ll_layout, PendingIntent.getActivity(context, 0,
                new Intent(context, LoginActivity.class), 0));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MainWidgetProvider.class));
        appWidgetManager.updateAppWidget(appIds, remoteView);
    }
}
