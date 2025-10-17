package com.jesse.batteria.viewModel;

import android.content.Intent;
import android.os.BatteryManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jesse.batteria.enums.BatteryHealthState;

public class BatteryViewModel extends ViewModel {
    private final MutableLiveData<BatteryHealthState> batteryHealthState = new MutableLiveData<>();

    public LiveData<BatteryHealthState> getBatteryHealthState() {
        return batteryHealthState;
    }

    public void updateBatteryIntent(Intent intent) {
        int healthStatus = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);

        BatteryHealthState state = switch (healthStatus) {
            case BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealthState.GOOD;
            case BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealthState.DEAD;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealthState.OVERHEAT;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealthState.OVER_VOLTAGE;
            case BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealthState.COLD;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealthState.FAILURE;
            default -> BatteryHealthState.UNKNOWN;
        };

        batteryHealthState.setValue(state);
    }
}

