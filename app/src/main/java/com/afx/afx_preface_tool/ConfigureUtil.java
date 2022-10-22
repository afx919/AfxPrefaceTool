package com.afx.afx_preface_tool;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigureUtil {
    public static final String TAG = ConfigureUtil.class.getSimpleName();

    static String getConfigureFilePath(Context context) {
        String config_path = context.getFilesDir().getAbsolutePath() + "/config.json";
        Log.d(TAG, "config_path: " + config_path);
        return config_path;
    }

    static ConfigBean parseConfigure(String strConfigure) {
        try {
            return new ObjectMapper().readValue(strConfigure, ConfigBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConfigBean loadConfigure(Context context) {
        try {
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(new File(getConfigureFilePath(context))), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            StringBuilder builder = new StringBuilder();
            String content;
            while ((content = bufferedReader.readLine()) != null)
                builder.append(content);

            content = builder.toString();
            Log.d(TAG, "load configure, content: " + content);
            return parseConfigure(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean saveConfigure(ConfigBean configure, Context context) {
        try {
            String strJson = new ObjectMapper().writeValueAsString(configure);
            Log.d(TAG, "save configure, strJson: " + strJson);

            FileOutputStream fileOutputStream = null;
            File file = new File(getConfigureFilePath(context));
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(strJson.getBytes("utf-8"));
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
