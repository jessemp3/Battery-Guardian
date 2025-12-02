package com.jesse.batteria.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jesse.batteria.service.BatteryService;

public class UnlockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, BatteryService.class);
            serviceIntent.putExtra("promote_fg", true);
            context.startService(serviceIntent);
        }
    }
}

