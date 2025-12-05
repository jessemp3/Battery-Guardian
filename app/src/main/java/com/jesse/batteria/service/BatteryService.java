package com.jesse.batteria.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.jesse.batteria.ui.MainActivity;

public class BatteryService extends Service {
    private static final String CHANNEL_ID = "monitor_bateria_channel";
    private static final int NOTIF_ID_FOREGROUND = 100;


    private BroadcastReceiver batteryReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BatteryService", "Serviço iniciado");

        Notification notification = buildForegroundNotification();
        startForeground(NOTIF_ID_FOREGROUND, notification);

        createNotificationChannel();

        // Cria e registra o receiver que monitora a bateria
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                Log.d("BatteryService", "Bateria: " + level + "%, carregando: " + isCharging);

                SharedPreferences prefs = getSharedPreferences("battery_prefs", MODE_PRIVATE);
                int userLimit = prefs.getInt("battery_limit", 20);

                if (level <= userLimit && !isCharging) {
                    enviarNotificacao(context, level);
                }
            }
        };

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) unregisterReceiver(batteryReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BatteryService", "onStartCommand chamado");
        return START_STICKY;
    }

    private Notification buildForegroundNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Monitorando bateria 🔋")
                .setContentText("O serviço está ativo em segundo plano.")
                .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)// não pode ser dispensada
                .setAutoCancel(false)
                .build();
    }

    private void enviarNotificacao(Context context, int level) {
        String message = "Coloque o celular para carregar";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Bateria em " + level + "% 🔋")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)// não pode ser dispensada
                .setAutoCancel(false);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            int NOTIF_ID_ALERT = 101;
            nm.notify(NOTIF_ID_ALERT, builder.build());
            /*
            a cada notificação
            o Id é unico , isso permite que mesmo que o usuario apague a notificação
            seja possivel enviar outra sem que o android interprete como spam e não mostre novamente
             */
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Monitoramento de Bateria",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notificações do serviço de monitoramento de bateria");
        channel.enableVibration(true); // vibração
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }


}
