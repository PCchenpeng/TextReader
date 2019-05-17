package com.dace.textreader.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * APP 工具类
 *
 * @author chenxuxu
 * @date 2018/2/10
 *
 */
public class AppUtils {

    /**
     * 跳转到应用商店评分
     *
     * @param context
     * @param myAppPkg
     * @param shopPkg
     */
    public static void goAppShop(Context context, String myAppPkg, String shopPkg) {
        if (TextUtils.isEmpty(myAppPkg)) {
            return;
        }

        try {
            Uri uri = Uri.parse("market://details?id=" + myAppPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(shopPkg)) {
                intent.setPackage(shopPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            // 如果没有该应用商店，则显示系统弹出的应用商店列表供用户选择
            goAppShop(context, myAppPkg, "");
        }
    }
}
