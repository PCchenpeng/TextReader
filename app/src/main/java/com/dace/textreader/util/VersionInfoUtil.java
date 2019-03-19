package com.dace.textreader.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 获取应用的版本信息工具类
 * Created by 70391 on 2017/8/31.
 */

public class VersionInfoUtil {

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 检查是否有新版本
     *
     * @param newVersion
     */
    public static boolean checkNewVersion(Context context, String newVersion) {

        String version = getVersionName(context);

        int one = Integer.valueOf(version.split("\\.")[0]);
        int two = Integer.valueOf(version.split("\\.")[1]);
        int three = Integer.valueOf(version.split("\\.")[2]);

        int newOne = Integer.valueOf(newVersion.split("\\.")[0]);
        int newTwo = Integer.valueOf(newVersion.split("\\.")[1]);
        int newThree = Integer.valueOf(newVersion.split("\\.")[2]);

        if (newOne > one) {
            return true;
        } else {
            if (newTwo > two) {
                return true;
            } else {
                if (newThree > three) {
                    return true;
                }
            }
        }
        return false;
    }

}
