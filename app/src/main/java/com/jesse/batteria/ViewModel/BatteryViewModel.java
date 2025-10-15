package com.jesse.batteria.ViewModel;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BatteryViewModel extends ViewModel {
    private final MutableLiveData<Intent> batteryIntent = new MutableLiveData<>();

    public LiveData<Intent> getBatteryIntent() {
        return batteryIntent;
    }

    public void updateBatteryIntent(Intent intent) {
        batteryIntent.setValue(intent);
    }
}
