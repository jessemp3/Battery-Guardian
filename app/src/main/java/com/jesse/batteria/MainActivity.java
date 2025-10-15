package com.jesse.batteria;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jesse.batteria.ViewModel.BatteryViewModel;
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
        BatteryViewModel viewModel = new ViewModelProvider(this).get(BatteryViewModel.class);


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

        viewModel.getBatteryIntent().observe(this, this::batteryVerification);

        Intent batteryStatus = Objects.requireNonNull(registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
        viewModel.updateBatteryIntent(batteryStatus);
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryViewModel viewModel = new ViewModelProvider(MainActivity.this).get(BatteryViewModel.class);
            viewModel.updateBatteryIntent(intent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }


    private void batteryVerification(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
        Log.d("BatteryService", String.valueOf(level));
        ImageView bateria = binding.imageViewBatteryHealth;
        TextView bateriaText = binding.textViewbatteryHealthStatus;


        switch (level) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                bateria.setImageResource(R.drawable.battery_status_good_24px);
                bateriaText.setText("Boa");
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                bateria.setImageResource(R.drawable.battery_alert_24px);
                bateriaText.setText("Morta");
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                bateria.setImageResource(R.drawable.battery_change_24px);
                bateriaText.setText("Superaquecida");
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                bateria.setImageResource(R.drawable.battery_alert_24px);
                bateriaText.setText("Alta tensão");
                break;
            case BatteryManager.BATTERY_HEALTH_COLD:
                bateria.setImageResource(R.drawable.battery_0_bar_24px);
                bateriaText.setText("Fria");
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                bateria.setImageResource(R.drawable.battery_unknown_24px);
                bateriaText.setText("Falha");
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                bateria.setImageResource(R.drawable.battery_unknown_24px);
                bateriaText.setText("Desconhecido");
                break;
        }

    }
}
