package com.afx.afx_preface_tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = BootBroadcastReceiver.class.getSimpleName();

    void doAutoAP(Context context, ConfigBean configure) {
        if(context != null &&  configure != null) {
            Logger.getInstance().d("设置AP: " + (configure.auto_ap ?"开":"关"));
            Logger.getInstance().d("AP 名称: " +  configure.ap_name);
            Logger.getInstance().d("AP 密码: " +  configure.ap_password);
            boolean success = APUtil.setAP(context, configure.auto_ap, configure.ap_name, configure.ap_password);
            Toast.makeText(context, "AutoService 设置AP参数" + (success ?"成功":"失败") + "!", Toast.LENGTH_LONG).show();

            {
                FileOutputStream fileOutputStream = null;
                String config_path = context.getFilesDir().toString() + "/log.txt";

                File file = new File(config_path);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(("AutoService 设置AP参数" + (success ?"成功":"失败") + "!").getBytes("utf-8"));
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(constant.ReceiverBootCompleted)){
            Logger.getInstance().initialize(context);
            Logger.getInstance().d("进入开机自启");
            ConfigBean configure = ConfigureUtil.loadConfigure(context);
            if(configure != null) {
                Logger.getInstance().d("加载配置成功");
                Logger.getInstance().d("开始设置AP");
                doAutoAP(context, configure);
                Logger.getInstance().d("开始调用其它APP");
                doAutoStartApp(context, configure);
                Logger.getInstance().d("自启完成");
            } else {
                Logger.getInstance().e("加载配置失败");
            }
            Logger.getInstance().flush();
        }
    }

    void doAutoStartApp(Context context, ConfigBean configure) {
        if(context != null && configure != null && configure.auto_start_apps != null) {
            PackageManager packageManager = context.getPackageManager();
            for(String package_name : configure.auto_start_apps) {
                Intent intent=new Intent();
                intent =packageManager.getLaunchIntentForPackage(package_name);
                if(intent==null){
                    Logger.getInstance().e("开机自启软件失败! package: " + package_name);
                }else{
                    context.startActivity(intent);
                    Logger.getInstance().d("开机自启软件成功! package: " + package_name);
                }
            }
        }
    }
}
