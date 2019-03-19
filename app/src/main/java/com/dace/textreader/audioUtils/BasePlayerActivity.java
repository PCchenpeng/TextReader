package com.dace.textreader.audioUtils;

import android.os.Bundle;

import com.dace.textreader.activity.BaseActivity;

public class BasePlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获取到播放音乐的服务
     *
     * @return PlayService对象
     */
    public PlayService getPlayService() {
        PlayService playService = AppHelper.get().getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }
}
