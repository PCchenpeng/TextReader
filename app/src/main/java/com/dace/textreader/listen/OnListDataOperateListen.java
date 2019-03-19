package com.dace.textreader.listen;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.listen
 * Created by Administrator.
 * Created time 2018/7/13 0013 上午 10:54.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public interface OnListDataOperateListen {

    //刷新,true为正在刷新，false结束刷新
    void onRefresh(boolean refresh);

    //加载结果，true为加载成功，false为加载失败
    void onLoadResult(boolean success);

    //编辑，true为开启编辑，false为取消编辑
    void onEditor(boolean editor);

}
