package com.dace.textreader.audioUtils;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.audioUtils
 * Created by Administrator.
 * Created time 2018/4/18 0018 上午 9:09.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public interface EventCallback<T> {
    void onEvent(T t);
}
