package com.dace.textreader.util;

import android.os.AsyncTask;

import com.dace.textreader.listen.DownloadListen;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/9/27 0027 上午 10:58.
 * Version   1.0;
 * Describe :  下载任务
 * History:
 * ==============================================================================
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_FAILED = 0;
    public static final int TYPE_PAUSE = 2;
    public static final int TYPE_CANCEL = -1;

    private DownloadListen listen;
    private boolean isCancel = false;
    private boolean isPause = false;
    private int lastProgress;

    public DownloadTask(DownloadListen listen) {
        this.listen = listen;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            long downloadLength = 0;  //记录已下载的文件的长度
            //获取到下载的URL地址
            String downloadUrl = strings[0];
            //根据URL解析出下载的文件名
            String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            file = new File(DataUtil.getDownloadFileName(filename, strings[1]));
            //判断目录中是否已经存在要下载的文件
            if (file.exists()) {
                //如果已经存在，则读取已下载的字节数，这样就可以在后面使用断点续传的功能
                downloadLength = file.length();
            }
            //获取待下载文件的总长度
            long contentLength = getContentLength(downloadUrl);

            if (contentLength == 0) {  //若文件长度=0，则文件有问题，直接返回下载失败
                return TYPE_FAILED;
            } else if (contentLength == downloadLength) {  //若文件长度=已经下载的文件的长度
                //已下载的字节和文件总字节相同,则下载成功
                return TYPE_SUCCESS;
            }

            //建立网络链接
            OkHttpClient client = new OkHttpClient();
            //要发起一个http请求，首先创建一个Request对象
            //断点下载，这里添加了一个header，指定从那个字节开始下载，因为已经下载过的就不要下载了
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadLength + "-")
                    .url(downloadUrl)
                    .build();
            //调用Okhttp的newCall方法返回根据request请求，服务器返回的数据
            Response response = client.newCall(request).execute();

            /**
             * 采用java流的方式，不断从网络上读取数据，不断写入到本地
             * 一直到文件全部下载完为止，在整个过程中，我们还要判断用户有没有
             * 触发暂停、取消等操作，有的话通过TYPE的响应方式返回，
             * 没有的话则实时计算当前的下载进度
             */
            if (response != null) {
                //用inputstream的实例得到服务器返回的数据的详细内容
                is = response.body().byteStream();
                //把文件进行保存到本地
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadLength);
                byte[] bytes = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(bytes)) != -1) {
                    if (isCancel) {  //判断用户是否触发取消事件
                        return TYPE_CANCEL;
                    } else if (isPause) {  //判断用户是否触发暂停事件
                        return TYPE_PAUSE;
                    } else {
                        total = total + len;
                        savedFile.write(bytes, 0, len);
                        //计算下载的百分比
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        //调用 publishProgress进行通知
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCancel && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            //如果下一次的下载进度大于上一次的，则通过progress通知一下下载进度
            listen.onProcessChange(progress);
            //然后把当前的进度当作上一次的进度
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                listen.onSuccess();
                break;
            case TYPE_FAILED:
                listen.onFailed();
                break;
            case TYPE_PAUSE:
                listen.onPause();
                break;
            case TYPE_CANCEL:
                listen.onCancel();
                break;
        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        isPause = true;
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        isCancel = true;
    }

    /**
     * 获取待下载内容的长度
     *
     * @param downloadUrl
     * @return
     * @throws Exception
     */
    private long getContentLength(String downloadUrl) throws Exception {
        //建立网络链接
        OkHttpClient client = new OkHttpClient();
        //要发起一个http请求，首先创建一个Request对象
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        //调用Okhttp的newCall方法返回根据request请求，服务器返回的数据
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            //用contentLength接收服务器返回的详细数据
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}
