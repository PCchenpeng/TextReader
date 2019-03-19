package com.dace.textreader.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/8/22 0022 下午 5:36.
 * Version   1.0;
 * Describe :  获取手机版本型号
 * History:
 * ==============================================================================
 */
public class Rom {

    static boolean isIntentAvailable(Intent intent, Context context) {
        return intent != null && context.getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    static String getProp(String name) {
        BufferedReader input = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop" + name);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            String line = input.readLine();
            input.close();
            return line;
        } catch (IOException e) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
