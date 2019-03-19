package com.dace.textreader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/9/25 0025 下午 6:17.
 * Version   1.0;
 * Describe :   网络状态
 * History:
 * ==============================================================================
 */

public class NetWorkUtils {

    /**
     * 判断是否有网络连接     *    * @param context  * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //判断NetworkInfo对象是否为空 并且类型是否为WIFI
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return networkInfo.isAvailable();
        }
        return false;
    }

}
