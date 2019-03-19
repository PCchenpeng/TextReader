package com.dace.textreader.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by 70391 on 2017/8/29.
 */

public class MyToastUtil {

    private static Toast toast = null;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
