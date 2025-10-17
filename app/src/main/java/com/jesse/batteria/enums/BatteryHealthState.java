package com.jesse.batteria.enums;

import com.jesse.batteria.R;

public enum BatteryHealthState {
    GOOD(R.drawable.battery_status_good_24px, R.string.battery_health_good),
    DEAD(R.drawable.battery_alert_24px, R.string.battery_health_dead),
    OVERHEAT(R.drawable.battery_change_24px, R.string.battery_health_overheat),
    OVER_VOLTAGE(R.drawable.battery_alert_24px, R.string.battery_health_over_voltage),
    COLD(R.drawable.battery_0_bar_24px, R.string.battery_health_cold),
    FAILURE(R.drawable.battery_unknown_24px, R.string.battery_health_unspecified_failure),
    UNKNOWN(R.drawable.battery_unknown_24px, R.string.battery_health_unknown);

    public final int iconResId;
    public final int statusText;

    BatteryHealthState(int iconResId, int statusText) {
        this.iconResId = iconResId;
        this.statusText = statusText;
    }
}

