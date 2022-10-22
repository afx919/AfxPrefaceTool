package com.afx.afx_preface_tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class APBroadcastReceiver extends BroadcastReceiver {
    MainActivity main_activity = null;

    APBroadcastReceiver(MainActivity main_activity) {
        super();
        this.main_activity = main_activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(constant.ReceiverWifiAPStateChanged, intent.getAction())) {
            int state = intent.getIntExtra("wifi_state",  0);
            switch (state) {
                case 13:
                case 12:
                    if(main_activity != null) {
                        main_activity.updateAPState(true);
                    }
                    break;
                case 10:
                case 11:
                    if(main_activity != null) {
                        main_activity.updateAPState(false);
                    }
                    break;
            }
        }
    }
}
