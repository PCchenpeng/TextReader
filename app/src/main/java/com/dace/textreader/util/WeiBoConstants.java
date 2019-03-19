package com.dace.textreader.util;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/12/21 0021 下午 3:53.
 * Version   1.0;
 * Describe :  微博SDK相关内容
 * History:
 * ==============================================================================
 */
public interface WeiBoConstants {

    /**
     * 当前应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY
     */
    public static final String APP_KEY = "206804681";

    /**
     * 当前应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * WeiboSDKDemo 应用对应的权限
     */
    public static final String SCOPE = "";

}
