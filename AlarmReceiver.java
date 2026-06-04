package com.example.myapplication.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        if (title != null) {
            NotificationHelper.showNotification(context, "Напоминание: " + title, description);
        }
    }
}
