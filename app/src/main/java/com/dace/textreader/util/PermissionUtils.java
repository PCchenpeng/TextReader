package com.dace.textreader.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/8/22 0022 下午 5:09.
 * Version   1.0;
 * Describe :  悬浮窗权限处理类
 * History:
 * ==============================================================================
 */
public class PermissionUtils {

    /**
     * 是否拥有悬浮窗权限
     */
    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            return hasPermissionBelowMarshmallow(context);
        }
    }

    public static boolean hasPermissionOnActivityResult(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return hasPermissionForO(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            return hasPermissionBelowMarshmallow(context);
        }
    }

    /**
     * 6.0一下判断是否有权限
     * 理论上6.0以上才需处理权限，但有的国内rom在6.0以下就添加了权限
     * 这个方法也可以用于判断6.0以上的版本，但是6.0以上有更简单的canDrawOverlays()方法判断
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean hasPermissionBelowMarshmallow(Context context) {
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(
                    Context.APP_OPS_SERVICE);
            Method method = AppOpsManager.class.getMethod(
                    "checkOp", int.class, int.class, String.class);
            return AppOpsManager.MODE_ALLOWED == (Integer) method.invoke(manager, 24,
                    Binder.getCallingUid(),
                    context.getApplicationContext().getPackageName());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用于判断8.0以上是否有权限，仅用于OnActivityResult
     * 针对8.0的官方bug：在用户授予权限后Settings.canOverlays()或checkOp方法仍然返回false
     */
    private static boolean hasPermissionForO(Context context) {
        try {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager == null) return false;
            View view = new View(context);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(0, 0,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            view.setLayoutParams(layoutParams);
            manager.addView(view, layoutParams);
            manager.removeView(view);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
