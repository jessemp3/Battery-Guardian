package com.jesse.batteria;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jesse.batteria.databinding.ActivityMainBinding;
import com.jesse.batteria.service.BatteryService;


import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_POST_NOTIF = 101;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

     ViewCompat.setOnApplyWindowInsetsListener(binding.materialToolbar, (view, insets) -> {
         int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
         view.setPadding(0, statusBarHeight, 0, 0);
         return insets;
     });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTIF);
        }

        Intent serviceIntent = new Intent(this, BatteryService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        batteryVerification(Objects.requireNonNull(registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED))));
    }

    private void batteryVerification(Intent intent){
//        BATTERY_HEALTH_GOOD (2): bateria em bom estado.
//        BATTERY_HEALTH_DEAD (4): bateria está "morta".
//                BATTERY_HEALTH_OVERHEAT (3): bateria superaquecida.
//        BATTERY_HEALTH_OVER_VOLTAGE (5): tensão acima do normal.
//                BATTERY_HEALTH_COLD (7): bateria muito fria.
//                BATTERY_HEALTH_UNSPECIFIED_FAILURE (6): falha não especificada.
//                BATTERY_HEALTH_UNKNOWN (1): estado desconhecido.


        int level = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        Log.d("BatteryService", String.valueOf(level));
        ImageView bateria = binding.imageViewBatteryHealth;


        if(level == BatteryManager.BATTERY_HEALTH_GOOD){
            bateria.setImageResource(R.drawable.ic_battery_good);
        }

    }
}
