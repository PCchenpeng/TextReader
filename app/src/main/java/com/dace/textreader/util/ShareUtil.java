package com.dace.textreader.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.dace.textreader.App;
import com.dace.textreader.activity.NewMainActivity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.util.ArrayList;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/12/17 0017 上午 10:25.
 * Version   1.0;
 * Describe :  分享工具类
 * History:
 * ==============================================================================
 */
public class ShareUtil {

    /**
     * 分享链接内容到微信
     *
     * @param context 上下文对象
     * @param url     分享链接
     * @param title   分享标题
     * @param content 分享内容
     * @param bitmap  分享图片字节数组
     * @param friend  是否是分享给好友，true分享给好友，false分享朋友圈
     */
    public static void shareToWx(Context context, String url, String title, String content,
                                 byte[] bitmap, boolean friend) {
        if (!App.api.isWXAppInstalled()) {
            MyToastUtil.showToast(context, "您还未安装微信客户端");
            return;
        }
        //初始化一个WXWebpageObject对象，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题和描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;
        msg.thumbData = bitmap;

        //构造一个Req，
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("share");  //transation字段用于唯一标识一个请求
        req.message = msg;
        if (friend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;  //分享到微信好友
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;  //分享到微信朋友圈
        }
        App.api.sendReq(req);
    }

    /**
     * 用于微信分享的唯一标识一个请求
     *
     * @param type
     * @return
     */
    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) :
                type + System.currentTimeMillis();
    }

    /**
     * 分享链接内容到QQ好友
     *
     * @param activity 需要分享内容的activity
     * @param url      分享链接
     * @param title    分享标题
     * @param content  分享内容
     * @param imageUrl 分享图片链接
     */
    public static void shareToQQ(Activity activity, String url, String title, String content,
                                 String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        App.mTencent.shareToQQ(activity, bundle, new MyQQIUiListener());
    }

    /**
     * 分享链接内容到QQ空间
     *
     * @param activity 需要分享内容的activity
     * @param url      分享链接
     * @param title    分享标题
     * @param content  分享内容
     * @param imageUrl 分享图片链接
     */
    public static void shareToQZone(Activity activity, String url, String title, String content,
                                    String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
        //以下这个必须加上  不然无法调动 qq空间
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add(imageUrl);
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        App.mTencent.shareToQzone(activity, bundle, new MyQQIUiListener());
    }

    /**
     * 分享图片到微信
     *
     * @param context  上下文对象
     * @param imageUrl 本地图片路径
     * @param friend   true为分享到好友，false为分享到朋友圈
     */
    public static void shareImageToWX(Context context, String imageUrl, boolean friend) {
        if (!App.api.isWXAppInstalled()) {
            MyToastUtil.showToast(context, "您还未安装微信客户端");
            return;
        }

        WXImageObject imageObject = new WXImageObject();
        imageObject.setImagePath(imageUrl);

        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = imageObject;

        Bitmap bmp = BitmapFactory.decodeFile(imageUrl);
        Bitmap thumb = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        bmp.recycle();

        message.thumbData = ImageUtils.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("imageShare");
        req.message = message;

        if (friend) {
            req.scene = SendMessageToWX.Req.WXSceneSession;  //分享到微信好友
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;  //分享到微信朋友圈
        }
        App.api.sendReq(req);
    }

    /**
     * 本地图片路径
     *
     * @param activity 需要分享内容的activity
     * @param imageUrl 本地图片路径
     * @param friend   true分享给好友，false分享到QQ空间
     */
    public static void shareImageToQQ(Activity activity, String imageUrl, boolean friend) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "派知语文");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        if (friend) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
        App.mTencent.shareToQQ(activity, params, new MyQQIUiListener());
    }

    /**
     * 分享到微博
     *
     * @param shareHandler
     * @param url
     * @param title
     * @param content
     * @param bitmap
     */
    public static void shareToWeibo(WbShareHandler shareHandler, String url, String title,
                                    String content, Bitmap bitmap) {
        WeiboMultiMessage message = new WeiboMultiMessage();
        message.textObject = getTextObj(url, title, content);
        message.imageObject = getImageObj(bitmap);
        shareHandler.shareMessage(message, false);
    }

    private static TextObject getTextObj(String url, String title, String content) {
        TextObject textObject = new TextObject();
        textObject.title = title;
        textObject.text = getShareContent(url, title, content);
        return textObject;
    }

    private static String getShareContent(String url, String title, String content) {
        return title + "\n" + content + url;
    }

    private static ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 分享图片到微博
     *
     * @param shareHandler
     * @param bitmap
     */
    public static void shareImageToWeibo(WbShareHandler shareHandler, Bitmap bitmap) {
        WeiboMultiMessage message = new WeiboMultiMessage();
        message.imageObject = getImageObj(bitmap);
        shareHandler.shareMessage(message, false);
    }

}
