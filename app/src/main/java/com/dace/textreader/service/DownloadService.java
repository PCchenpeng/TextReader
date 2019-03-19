package com.dace.textreader.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.listen.DownloadListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DownloadTask;
import com.dace.textreader.util.MyToastUtil;

import java.io.File;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.service
 * Created by Administrator.
 * Created time 2018/9/27 0027 上午 11:38.
 * Version   1.0;
 * Describe :  为了保证下载任务可以一直在后台运行，我们创建一个下载的服务
 * History:
 * ==============================================================================
 */

public class DownloadService extends Service {

    private DownloadTask downloadTask;
    private String downloadUrl;
    private String mVersion;

    private ProgressBar mProgressBar;
    private TextView mTextView;

    //创建 DownloadListener的匿名类实例，并在类中实现了5个方法
    private DownloadListen listen = new DownloadListen() {
        @Override
        public void onProcessChange(int process) {
            //用于显示下载进度的通知
            if (mProgressBar != null) {
                mProgressBar.setProgress(process);
            }
            if (mTextView != null) {
                String s = "下载中" + process + "%";
                mTextView.setText(s);
            }
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载成功时，将前台服务通知关闭，并创建一个下载成功的通知
            //关闭前台服务通知
            stopForeground(true);
            if (mOnDownLoadSuccess != null) {
                mOnDownLoadSuccess.onSuccess();
            }
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            //下载失败时，将前台服务通知关闭，并创建一个下载失败的通知
            //关闭前台服务通知
            stopForeground(true);
        }

        @Override
        public void onPause() {
            downloadTask = null;
        }

        @Override
        public void onCancel() {
            downloadTask = null;
            stopForeground(true);
        }
    };

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public static void install(Context context, String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(name);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.dace.textreader.provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(name)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder {

        public void setProgress(ProgressBar progressBar, TextView textView) {
            mProgressBar = progressBar;
            mTextView = textView;
        }

        public DownloadService getService() {
            return DownloadService.this;
        }

        //开始下载
        public void startDownload(String url, String version) {
            if (downloadTask == null) {
                downloadUrl = url;
                mVersion = version;
                //创建一个downloadTask实例，把DownloadListener作为参数传入
                downloadTask = new DownloadTask(listen);
                //调用execute方法开始下载，方法中传入下载的url
                downloadTask.execute(url, version);

            }
        }

        //暂停下载
        public void pauseDownload() {
            if (downloadTask != null) {
                //暂停下载，直接调用DownloadTask的pauseDownload方法
                downloadTask.pauseDownload();
            }
        }

        //取消下载
        public void cancelDownload() {
            if (downloadTask != null) {
                //取消下载，直接调用DownloadTask的cancelDownload方法
                downloadTask.cancelDownload();
            }
            if (downloadUrl != null) {
                //取消下载时需要将文件删除，并将通知关闭
                //根据URL解析出下载的文件名
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                //将文件下载到Environment.DIRECTORY_DOWNLOADS目录下，也就是SD卡的Download目录
                String directory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath();
                String filePath = directory + fileName;
                File file = new File(filePath);
                //取消下载时需要将文件删除，并将通知关闭
                if (file.exists()) {
                    file.delete();
                }
                stopForeground(true);
            }
        }
    }

    public interface OnDownLoadSuccess {
        void onSuccess();
    }

    private OnDownLoadSuccess mOnDownLoadSuccess;

    public void setOnDownLoadSuccessListen(OnDownLoadSuccess onDownLoadSuccessListen) {
        this.mOnDownLoadSuccess = onDownLoadSuccessListen;
    }

}
