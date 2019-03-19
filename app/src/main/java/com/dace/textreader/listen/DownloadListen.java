package com.dace.textreader.listen;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.listen
 * Created by Administrator.
 * Created time 2018/9/27 0027 上午 10:56.
 * Version   1.0;
 * Describe :  下载监听
 * History:
 * ==============================================================================
 */

public interface DownloadListen {

    void onProcessChange(int process);

    void onSuccess();

    void onFailed();

    void onPause();

    void onCancel();

}
