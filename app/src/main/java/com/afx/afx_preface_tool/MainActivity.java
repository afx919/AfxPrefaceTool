package com.afx.afx_preface_tool;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    ConfigBean configure = new ConfigBean();


    Switch auto_ap_switch = null;
    Switch set_ap_state_switch = null;
    EditText ap_name_edit = null;
    EditText ap_password_edit = null;
    Button ap_save_config = null;
    Button run_android_setting = null;
    Button quite_application = null;
    Button clean_auto_start_app = null;
    Button add_auto_start_app = null;
    ListView app_list_view = null;
    TextView current_ip_address_view = null;


    APBroadcastReceiver ap_broadcast_receiver = null;
    WifiBroadcastReceiver wifi_broadcast_receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.getInstance().initialize(getApplicationContext());

        ConfigBean bean = ConfigureUtil.loadConfigure(getApplicationContext());
        if (bean != null) {
            configure = bean;
        }

        initializeViews();

        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(constant.ReceiverWifiAPStateChanged);
            ap_broadcast_receiver = new APBroadcastReceiver(this);
            registerReceiver(ap_broadcast_receiver, intentFilter);
        }

        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            wifi_broadcast_receiver = new WifiBroadcastReceiver(this);
            registerReceiver(wifi_broadcast_receiver, intentFilter);
        }

        if (false) {
            try {
                {
                    File file = new File(getApplicationContext().getCacheDir().getAbsolutePath() + "/preface_tool_tmp");
                    file.delete();
                }
                new HttpService(getApplicationContext().getCacheDir().getAbsolutePath(), 17246).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void showAboutMessage() {
        String message = "";
        if (false) {
            message += "HTTP端口:17246 \n";
        }

        message += "项目地址:  https://github.com/afx919/AfxPrefaceTool/tree/master \n";
        message += "V1.0.0";
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("关于")
                .setMessage(message)
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (ap_broadcast_receiver != null) {
            unregisterReceiver(ap_broadcast_receiver);
            ap_broadcast_receiver = null;
        }

        if (wifi_broadcast_receiver != null) {
            unregisterReceiver(wifi_broadcast_receiver);
            wifi_broadcast_receiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_action_about);
        assert (item != null);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAboutMessage();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void initializeViews() {
        initializeAPViews();
        initializeAutoStartAppViews();
        initializeOtherViews();
    }

    void initializeAPViews() {
        auto_ap_switch = (Switch) findViewById(R.id.auto_ap_switch);
        assert (auto_ap_switch != null);
        set_ap_state_switch = (Switch) findViewById(R.id.set_ap_state_switch);
        assert (set_ap_state_switch != null);
        ap_name_edit = (EditText) findViewById(R.id.ap_name);
        assert (ap_name_edit != null);
        ap_password_edit = (EditText) findViewById(R.id.ap_password);
        assert (ap_password_edit != null);
        ap_save_config = (Button) findViewById(R.id.ap_save_config);
        assert (ap_save_config != null);

        auto_ap_switch.setChecked(configure.auto_ap);
        ap_name_edit.setText(configure.ap_name);
        ap_password_edit.setText(configure.ap_password);

        {
            boolean state = APUtil.getAPState(getApplicationContext());
            Log.d(TAG, "wifi ap: " + state);
            set_ap_state_switch.setChecked(state);
        }

        auto_ap_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveAPConfigure();
            }
        });
        ap_save_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ap_password_edit.getText().toString();
                if (password != null && password.length() >= 8) {
                    saveAPConfigure();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("保存热点参数")
                            .setMessage("保存失败! 热点密码长度必须大于等于8且不能有特殊字符,否则会导致热点开启失败!")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", null).create();
                    dialog.show();
                }
            }
        });

        set_ap_state_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (configure != null) {
                    boolean success = APUtil.setAP(getApplicationContext(), isChecked, configure.ap_name, configure.ap_password);
                    Toast.makeText(getApplicationContext(), (isChecked ? "打开" : "关闭") + "热点" + (success ? "成功" : "失败") + "!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    void initializeOtherViews() {
        run_android_setting = (Button) findViewById(R.id.run_android_setting);
        assert (run_android_setting != null);

        quite_application = (Button) findViewById(R.id.quite_application);
        assert (quite_application != null);

        current_ip_address_view = (TextView) findViewById(R.id.current_ip_address_view);
        assert (current_ip_address_view != null);

        updateWifiState(WifiUtil.getWifiIpAddress(getApplicationContext()));

        run_android_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
                intent.setComponent(cm);
                intent.setAction("android.intent.action.VIEW");
                startActivity(intent);
            }
        });

        quite_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
    }

    void saveAPConfigure() {
        configure.auto_ap = auto_ap_switch.isChecked();
        configure.ap_name = ap_name_edit.getText().toString();
        configure.ap_password = ap_password_edit.getText().toString();
        ConfigureUtil.saveConfigure(configure, getApplicationContext());
    }

    void initializeAutoStartAppViews() {
        add_auto_start_app = (Button) findViewById(R.id.add_auto_start_app);
        assert (add_auto_start_app != null);

        clean_auto_start_app = (Button) findViewById(R.id.clean_auto_start_app);
        assert (clean_auto_start_app != null);

        app_list_view = (ListView) findViewById(R.id.app_list_view);
        assert (app_list_view != null);


        add_auto_start_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> simpleAppList = getSimpleAppList();
                final CharSequence[] names = simpleAppList.keySet().toArray(new CharSequence[simpleAppList.size()]);
                final AtomicInteger selected_index = new AtomicInteger(-1);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("添加应用")
                        .setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected_index.set(which);
                            }
                        })
                        .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int index = selected_index.get();
                                    if (index >= 0 && index < names.length) {
                                        if (simpleAppList.containsKey(names[index])) {
                                            String package_name = simpleAppList.get(names[index]);
                                            configure.auto_start_apps.add(package_name);
                                            ConfigureUtil.saveConfigure(configure, getApplicationContext());
                                            refreshAppListView();
                                            Toast.makeText(getApplicationContext(), "添加应用成功!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(), "添加应用失败!", Toast.LENGTH_LONG).show();
                            }
                        }).create();
                dialog.show();
            }
        });

        clean_auto_start_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("清空应用列表")
                        .setMessage("确定清空应用列表吗？")
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                configure.auto_start_apps = new HashSet<>();
                                ConfigureUtil.saveConfigure(configure, getApplicationContext());
                                refreshAppListView();
                                Toast.makeText(getApplicationContext(), "清空应用列表成功!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });

        refreshAppListView();
    }


    Map<String, String> getSimpleAppList() {
        Map<String, String> result = new HashMap<>();

        PackageManager packageManager = getPackageManager();
        String self_package_name = getApplicationContext().getPackageName();
        if (packageManager != null) {
            List<PackageInfo> package_infos = packageManager.getInstalledPackages(0);
            for (PackageInfo package_info : package_infos) {
                if (Objects.equals(package_info.applicationInfo.packageName, self_package_name)) {
                    continue;
                }
                if ((package_info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    ApplicationInfo application_info = package_info.applicationInfo;

                    String name = packageManager.getApplicationLabel(application_info).toString().trim();
                    if (name != null && !name.isEmpty() && !Objects.equals(name, package_info.packageName) && !configure.auto_start_apps.contains(application_info.packageName)) {
                        result.put(name, application_info.packageName);
                    }
                }
            }
        }
        return result;
    }

    void refreshAppListView() {
        PackageManager packageManager = getPackageManager();
        HashSet<String> unknow_packages = new HashSet<>();
        List<AppListItemBean> items = new ArrayList<>();
        for (String package_name : configure.auto_start_apps) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(package_name, 0);
                if (applicationInfo != null) {
                    AppListItemBean bean = new AppListItemBean();
                    bean.icon = applicationInfo.loadIcon(packageManager);
                    bean.name = packageManager.getApplicationLabel(applicationInfo).toString().trim();
                    bean.package_name = package_name;
                    items.add(bean);
                } else {
                    unknow_packages.add(package_name);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                unknow_packages.add(package_name);
            }
        }

        if (unknow_packages.size() != 0) {
            configure.auto_start_apps.removeAll(unknow_packages);
            ConfigureUtil.saveConfigure(configure, getApplicationContext());
        }

        AppListViewAdapter adapter = new AppListViewAdapter(this, R.layout.app_list_item, R.id.item_app_name, items, this);
        app_list_view.setAdapter(adapter);
    }

    void removeAutoStartApp(String package_name) {
        if (package_name != null && !package_name.trim().isEmpty()) {
            if (configure.auto_start_apps.remove(package_name)) {
                ConfigureUtil.saveConfigure(configure, getApplicationContext());
                refreshAppListView();
            }
        }
    }

    void updateAPState(final boolean is_on) {
        if (set_ap_state_switch != null) {
            set_ap_state_switch.post(new Runnable() {
                @Override
                public void run() {
                    set_ap_state_switch.setChecked(is_on);
                }
            });
        }
    }

    void updateWifiState(String ip_address) {
        String text = null;
        if (ip_address == null || ip_address.trim().isEmpty()) {
            text = "WIFI 未连接";
        } else {
            text = ip_address.trim();
        }

        if (current_ip_address_view != null && text != null) {
            current_ip_address_view.setText("WIFI IP: " + text);
        }
    }
}
