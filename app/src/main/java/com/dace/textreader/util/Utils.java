package com.dace.textreader.util;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取手机屏幕相关的工具类
 * Created by 70391 on 2017/9/26.
 */

public class Utils {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取终端IP
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取系统亮度
     *
     * @param context 上下文对象
     * @return 0~255之间
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 设置当前屏幕亮度值  0--255
     */
    public static void saveScreenBrightness(Activity activity, int paramInt) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = paramInt / 255f;
        activity.getWindow().setAttributes(params);
    }

    static int getMeasuredWidth(View v) {
        return (v == null) ? 0 : v.getMeasuredWidth();
    }

    static int getWidth(View v) {
        return (v == null) ? 0 : v.getWidth();
    }

    static int getWidthWithMargin(View v) {
        return getWidth(v) + getMarginHorizontally(v);
    }

    static int getStart(View v) {
        return getStart(v, false);
    }

    static int getStart(View v, boolean withoutPadding) {
        if (v == null) {
            return 0;
        }
        if (isLayoutRtl(v)) {
            return (withoutPadding) ? v.getRight() - getPaddingStart(v) : v.getRight();
        } else {
            return (withoutPadding) ? v.getLeft() + getPaddingStart(v) : v.getLeft();
        }
    }

    static int getEnd(View v) {
        return getEnd(v, false);
    }

    static int getEnd(View v, boolean withoutPadding) {
        if (v == null) {
            return 0;
        }
        if (isLayoutRtl(v)) {
            return (withoutPadding) ? v.getLeft() + getPaddingEnd(v) : v.getLeft();
        } else {
            return (withoutPadding) ? v.getRight() - getPaddingEnd(v) : v.getRight();
        }
    }

    static int getPaddingStart(View v) {
        if (v == null) {
            return 0;
        }
        return ViewCompat.getPaddingStart(v);
    }

    static int getPaddingEnd(View v) {
        if (v == null) {
            return 0;
        }
        return ViewCompat.getPaddingEnd(v);
    }

    static int getPaddingHorizontally(View v) {
        if (v == null) {
            return 0;
        }
        return v.getPaddingLeft() + v.getPaddingRight();
    }

    static int getMarginStart(View v) {
        if (v == null) {
            return 0;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(lp);
    }

    static int getMarginEnd(View v) {
        if (v == null) {
            return 0;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginEnd(lp);
    }

    static int getMarginHorizontally(View v) {
        if (v == null) {
            return 0;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(lp) + MarginLayoutParamsCompat.getMarginEnd(lp);
    }

    static boolean isLayoutRtl(View v) {
        return ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    private Utils() {
    }

}
