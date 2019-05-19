package com.dace.textreader.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.EventsActivity;
import com.dace.textreader.activity.MicroLessonActivity;
import com.dace.textreader.activity.NewDailySentenceActivity;
import com.dace.textreader.activity.StartupPageActivity;
import com.dace.textreader.bean.FollowBean;
import com.dace.textreader.bean.JPushMessageBean;
import com.dace.textreader.util.ActivityUtils;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.receiver
 * Created by Administrator.
 * Created time 2018/10/15 0015 上午 10:20.
 * Version   1.0;
 * Describe :  自定义极光推送接收者
 * History:
 * ==============================================================================
 */

public class MyReceiver extends BroadcastReceiver {

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            receivingCustomNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {

            receivingNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            openNotification(context, bundle);

        } else {

        }
    }

    /**
     * 接收到推送
     *
     * @param context
     * @param bundle
     */
    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
    }

    /**
     * 接收到推送
     *
     * @param context
     * @param bundle
     */
    private void receivingCustomNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        if (message == null) {
            return;
        }
        JPushMessageBean followBean = GsonUtil.GsonToBean(message,JPushMessageBean.class);
        Log.d("111","followBean.getMessageType() " + followBean.getMessageType());
        if (followBean.getMessageType() == 1) {
            broadcastUpdate(context, HttpUrlPre.ACTION_BROADCAST_SYSTEM_UPGRADE);
        } else if (followBean.getMessageType() == 2){
            //应用商城评价弹窗
        }
    }

    /**
     * 点击通知
     *
     * @param context
     * @param bundle
     */
    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        try {
            JSONObject extrasJson = new JSONObject(extras);
            String myValue = extrasJson.getString("params");

            JSONObject jsonObject = new JSONObject(myValue);
            int productType = jsonObject.optInt("productType", 0);
            switch (productType) {
                case 0:
                    String id = jsonObject.getString("productId");
                    int area = jsonObject.optInt("area", 0);
                    Intent intent = new Intent(context, CompositionDetailActivity.class);
                    intent.putExtra("writingId", id);
                    intent.putExtra("orderNum", "");
                    intent.putExtra("area", area);
                    context.startActivity(intent);
                    break;
                case 1:
                    long essayId = jsonObject.optLong("productId", -1L);
                    int essayType = jsonObject.optInt("areaType", -1);
//                    Intent intent_essay = new Intent(context, NewArticleDetailActivity.class);
//                    intent_essay.putExtra("id", essayId);
//                    intent_essay.putExtra("type", essayType);
//                    context.startActivity(intent_essay);
                    break;
                case 2:
                    long lessonId = Long.valueOf(jsonObject.getString("productId"));
                    Intent intent_lesson = new Intent(context, MicroLessonActivity.class);
                    intent_lesson.putExtra("id", lessonId);
                    context.startActivity(intent_lesson);
                    break;
                case 3:
                    String name = jsonObject.getString("productId");
                    Intent intent_events = new Intent(context, EventsActivity.class);
                    intent_events.putExtra("pageName", name);
                    context.startActivity(intent_events);
                    break;
                case 4:
                    long sentenceId = Long.valueOf(jsonObject.getString("productId"));
                    Intent intent_sentence = new Intent(context, NewDailySentenceActivity.class);
                    intent_sentence.putExtra("sentenceId", sentenceId);
                    context.startActivity(intent_sentence);
                    break;
                default:
                    startApp(context);
                    break;
            }
        } catch (Exception e) {
            startApp(context);
        }
    }

    /**
     * 启动App
     *
     * @param context
     */
    private void startApp(Context context) {
        if (ActivityUtils.isAppAlive(context, "com.dace.textreader") == 0) {
            context.startActivity(new Intent(context, StartupPageActivity.class));
        }
    }

    /**
     * 发送广播
     *
     * @param action 广播的Action
     */
    private void broadcastUpdate(Context context, String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
