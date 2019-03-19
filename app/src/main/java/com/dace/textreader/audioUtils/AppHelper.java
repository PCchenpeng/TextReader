package com.dace.textreader.audioUtils;

import android.annotation.SuppressLint;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.audioUtils
 * Created by Administrator.
 * Created time 2018/4/18 0018 上午 9:13.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class AppHelper {
    /**
     * 播放音乐service
     */
    private PlayService mPlayService;

    public AppHelper() {
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private final static AppHelper INSTANCE = new AppHelper();
    }

    public static AppHelper get() {
        return SingletonHolder.INSTANCE;
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

    public void setPlayService(PlayService mPlayService) {
        this.mPlayService = mPlayService;
    }
}
