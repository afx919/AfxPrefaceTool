package com.afx.afx_preface_tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    MainActivity main_activity = null;

    WifiBroadcastReceiver(MainActivity main_activity) {
        super();
        this.main_activity = main_activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                main_activity.updateWifiState(null);
            }

            if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                main_activity.updateWifiState(WifiUtil.getWifiIpAddress(context));
            }
        }
    }
}
