package com.jesse.batteria.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jesse.batteria.databinding.ActivityMainBinding;
import com.jesse.batteria.service.BatteryService;
import com.jesse.batteria.viewModel.BatteryViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_POST_NOTIF = 101;
    private static final String PREFS_NAME = "battery_prefs";
    private static final String KEY_BATTERY_LIMIT = "battery_limit";
    private ActivityMainBinding binding;
    private int ultimoProgresso = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        BatteryViewModel viewModel = new ViewModelProvider(this).get(BatteryViewModel.class);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedLimit = prefs.getInt(KEY_BATTERY_LIMIT, 20);


        ViewCompat.setOnApplyWindowInsetsListener(binding.materialToolbar, (view, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            view.setPadding(0, statusBarHeight, 0, 0);

            //dessa forma consigo verificar que os botoes de navegaçaõe estáo aparecendo
            //se sim a margin é maior pro valor não ficar escondido
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.linearLayoutControls.getLayoutParams();
            params.bottomMargin = dpToPx(this, navBarHeight > 0 ? 34 : 16);
            binding.linearLayoutControls.setLayoutParams(params);

            return insets;
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTIF);
        }

        Intent serviceIntent = new Intent(this, BatteryService.class);
        ContextCompat.startForegroundService(this, serviceIntent);


        Intent batteryStatus = Objects.requireNonNull(registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
        viewModel.updateBatteryIntent(batteryStatus);

        viewModel.getBatteryHealthState().observe(this, state -> {
            binding.imageViewBatteryHealth.setImageResource(state.iconResId);
            binding.textViewbatteryHealthStatus.setText(getString(state.statusText));
        });

        binding.seekBarLimit.setMin(10);
        binding.seekBarLimit.setMax(30);
        binding.seekBarLimit.setProgress(savedLimit);

        binding.textViewLimitValue.setText(savedLimit + "%");


        binding.seekBarLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.textViewLimitValue.setText(progress + "%");
                prefs.edit().putInt(KEY_BATTERY_LIMIT, progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryViewModel viewModel = new ViewModelProvider(MainActivity.this).get(BatteryViewModel.class);
            viewModel.updateBatteryIntent(intent);
            dashboard(intent);
        }
    };


    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }

    private void dashboard(Intent serviceIntent) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, filter);
        if (batteryStatus == null) return;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level * 100f) / scale;

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

        binding.txtPercent.setText(String.format("%.0f%%", batteryPct));
        binding.txtTemp.setText(String.format("Temperatura: %.1f°C", temperature / 10f));
        binding.txtVolt.setText(String.format("Voltagem: %.2fV", voltage / 1000f));
        binding.txtStatus.setText(isCharging ? "Status: Carregando ⚡" : "Status: Descarregando");


        int novoProgresso = (int) batteryPct;

        if (Math.abs(ultimoProgresso - novoProgresso) >= 1) {
            animarGauge(ultimoProgresso, novoProgresso);
        } else {
            binding.batteryGauge.setProgress(novoProgresso);
        }

        ultimoProgresso = novoProgresso;

    }

    private void animarGauge(int de, int para) {
        ObjectAnimator animator = ObjectAnimator.ofInt(binding.batteryGauge, "progress", de, para);
        animator.setDuration(700); // animação mais fluida
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}
