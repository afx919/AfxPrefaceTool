package com.afx.afx_preface_tool;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class APUtil {
    public static final String TAG = APUtil.class.getSimpleName();

    public static boolean setAP(Context context, boolean on, String name, String password) {
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            WifiConfiguration ap_configure = new WifiConfiguration();
            ap_configure.SSID = name;
            ap_configure.preSharedKey = password;
            ap_configure.allowedKeyManagement.set(4);//设置加密类型，这里4是wpa加密

            if (wifiManager != null) {
                if (on) {
                    wifiManager.setWifiEnabled(false);
                    try {
                        Method setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                        return (boolean) setWifiApEnabled.invoke(wifiManager, ap_configure, true);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    Logger.getInstance().e(TAG, "turn on ap failed! can't find method: setWifiApEnabled");
                } else {
                    try {
                        Method setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
                        return (boolean) setWifiApEnabled.invoke(wifiManager, ap_configure, false);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    Logger.getInstance().e(TAG, "turn off ap failed! can't find method: setWifiApEnabled");
                }
            } else {
                Logger.getInstance().e(TAG, "set AP get WifiManager failed!");
            }
            return false;
        }
        return false;
    }

    public static boolean getAPState(Context context) {
        if (context != null) {
            try {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
//                method.setAccessible(true);
                return (Boolean) method.invoke(wifiManager);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
