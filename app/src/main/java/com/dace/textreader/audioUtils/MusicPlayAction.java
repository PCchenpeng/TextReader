package com.dace.textreader.audioUtils;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.audioUtils
 * Created by Administrator.
 * Created time 2018/4/18 0018 上午 9:07.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class MusicPlayAction {

    /**--------------播放类型--------------------------------*/

    /**
     * 点击了上一首按钮
     */
    public static final String TYPE_PRE = "TYPE_PRE";
    /**
     * 点击了下一首按钮
     */
    public static final String TYPE_NEXT = "TYPE_NEXT";
    /**
     * 点击了播放暂停按钮
     */
    public static final String TYPE_START_PAUSE = "TYPE_START_PAUSE";


    /**--------------播放状态--------------------------------*/

    /**
     * 默认状态
     */
    public static final int STATE_IDLE = 100;
    /**
     * 正在准备中
     */
    public static final int STATE_PREPARING = 101;
    /**
     * 正在播放中
     */
    public static final int STATE_PLAYING = 102;
    /**
     * 暂停状态
     */
    public static final int STATE_PAUSE = 103;

}
