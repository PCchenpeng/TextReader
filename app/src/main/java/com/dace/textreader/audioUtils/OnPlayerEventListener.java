package com.dace.textreader.audioUtils;

import com.dace.textreader.bean.LessonBean;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.audioUtils
 * Created by Administrator.
 * Created time 2018/4/18 0018 上午 9:08.
 * Version   1.0;
 * Describe :播放进度监听器
 * History:
 * ==============================================================================
 */

public interface OnPlayerEventListener {

    /**
     * 切换歌曲
     * 主要是切换歌曲的时候需要及时刷新界面信息
     */
    void onChange(int position, LessonBean lessonBean);

    /**
     * 继续播放
     * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
     */
    void onPlayerStart();

    /**
     * 暂停播放
     * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
     */
    void onPlayerPause();

    /**
     * 更新进度
     * 主要是播放音乐或者拖动进度条时，需要更新进度
     */
    void onUpdateProgress(int progress, int duration);

    /**
     * 缓冲百分比
     */
    void onBufferingUpdate(int percent);

}
