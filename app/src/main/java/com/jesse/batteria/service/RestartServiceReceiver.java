package com.jesse.batteria.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RestartServiceReceiver", "Reiniciando BatteryService...");
        Intent serviceIntent = new Intent(context, BatteryService.class);
        context.startForegroundService(serviceIntent);
    }
}
