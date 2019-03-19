package com.dace.textreader.listen;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.listen
 * Created by Administrator.
 * Created time 2018/11/23 0023 上午 10:03.
 * Version   1.0;
 * Describe :  分享接口
 * History:
 * ==============================================================================
 */
public interface OnShareClickListen {
    void onShare(String writingId, String writingTitle, String writingContent, int writingArea, int writingFormat);
}
