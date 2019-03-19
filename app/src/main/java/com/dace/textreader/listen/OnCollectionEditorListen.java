package com.dace.textreader.listen;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.listen
 * Created by Administrator.
 * Created time 2018/6/7 0007 下午 2:12.
 * Version   1.0;
 * Describe :  收藏页的监听
 * History:
 * ==============================================================================
 */

public interface OnCollectionEditorListen {

    void OnEditorOpen(String tag);

    void OnEditorCancel(String tag);

    void OnSelectAll(String tag, boolean selectAll);

    void OnDeleteData(String tag);

}
