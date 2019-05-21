package com.dace.textreader;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.WeiBoConstants;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cn.jpush.android.api.JPushInterface;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader
 * Created by Administrator.
 * Created time 2018/9/6 0006 下午 5:06.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class App extends Application {

    public static String tips = "请输入字、词、文章、作者";
    private List<Activity> mList;

    //接入微信的APP_ID
    public static final String APP_ID = "wx33aa4fea0c1da2a3";
    public static final String APP_SECRET = "93745297c300b2a7a3353a2e3665b6e8";
    public static final String WX_MCH_ID = "1495762062";  //微信商户号
    //IWXAPI是第三方app和微信通信的openapi接口
    public static IWXAPI api;

    //将APP注册到微信
    private void regToWx() {
        //通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        //将应用的appid注册到微信
        api.registerApp(APP_ID);
    }

    //接入QQ的应用ID
    private final String QQ_APP_ID = "1106272259";
    // 新建Tencent实例用于调用分享方法
    public static Tencent mTencent;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册到微信
        regToWx();
        //注册到QQ
        mTencent = Tencent.createInstance(QQ_APP_ID, getApplicationContext());
        //初始化讯飞
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a66a5ec");
        //腾讯bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "65cb368781", false);
        //litePal数据库初始化
        LitePal.initialize(this);
        //极光推送初始化
        JPushInterface.init(this);
        //注册到微博
        WbSdk.install(this, new AuthInfo(
                this, WeiBoConstants.APP_KEY,
                WeiBoConstants.REDIRECT_URL, WeiBoConstants.SCOPE));
        //初始化mp3转码工具
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                DataUtil.isMp3Ok = true;
            }

            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                DataUtil.isMp3Ok = false;
            }
        });
        mList = new ArrayList<>();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    public void addActivity(Activity activity) {
        if (!mList.contains(activity)) {
            mList.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (mList.contains(activity)) {
            mList.remove(activity);
        }
    }

    public void removeMainActivity(Activity activity) {
        if (mList.contains(activity)) {
            mList.remove(activity);
        }
    }

    public void backToMainActivity() {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).finish();
        }
    }

    public void removeAllActivity() {
        for (Activity activity : mList) {
            activity.finish();
        }
    }
}
