package com.dace.textreader.util;

import android.content.Context;
import android.content.Intent;

import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.activity.HomeAudioDetailActivity;

public class TurnToActivityUtil {
    public static void turnToDetail(Context context,int flag, String id ,int py, String imgUrl){
        Intent intent;
        if (flag == 1) {
            intent = new Intent(context, HomeAudioDetailActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("py", py);
        } else {
            intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("essayId", id);
            intent.putExtra("imgUrl", imgUrl);
        }
        context.startActivity(intent);
    }
}
