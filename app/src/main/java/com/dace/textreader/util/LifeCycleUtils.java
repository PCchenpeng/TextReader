package com.dace.textreader.util;

import android.app.Activity;
import android.content.Context;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/9/4 0004 下午 2:46.
 * Version   1.0;
 * Describe :  生命周期工具类
 * History:
 * ==============================================================================
 */

public class LifeCycleUtils {

    /**
     * 判断Context是否有效
     *
     * @param context
     * @return
     */
    public static boolean isValidContextForGlide(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

}
