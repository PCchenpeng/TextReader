package com.dace.textreader.util;

/**
 * 请求数据的统一前缀
 * Created by 70391 on 2017/8/21.
 */

public class HttpUrlPre {

    //请求数据
    public static final String HTTP_URL_ = "https://teacher.pythe.cn/pythe-temp/rest";
//    public static final String HTTP_URL_ = "https://app.pythe.cn/pythe-rest/rest";

    public static final String HTTP_URL = "https://check.pythe.cn/pythe-rest/rest";
//    public static final String HTTP_URL = "https://app.pythe.cn/pythe-rest/rest";

    //文件上传
    public static final String UPLOAD_URL = "https://check.pythe.cn:444/pythe-rest/rest";
//    public static final String UPLOAD_URL = "https://app.pythe.cn:448/pythe-rest/rest";

    //文件访问
    public static final String FILE_URL = "https://check.pythe.cn:446";
//    public static final String FILE_URL = "https://app.pythe.cn:446";

    //公司地址
    public static final String COMPANY_URL = "https://pythe.cn";

    //应用图标,用于QQ空间的分享
    public static final String SHARE_APP_ICON = "https://app.pythe.cn:446/image/icon_512.PNG";

    //应用在应用宝上的下载地址
    public static final String APP_DOWNLOAD_URL = "http://app.qq.com/#id=detail&appid=1106272259";

    //用户退出登录的广播字段
    public static final String ACTION_BROADCAST_USER_EXIT = "com.dace.textreader.home";

    //在优惠券中前往购买微课的广播字段
    public static final String ACTION_BROADCAST_BUY_LESSON = "com.dace.textreader.lesson";

    //账号在别处登录
    public static final String ACTION_BROADCAST_OTHER_DEVICE = "com.dace.textreader.other.device";

    //系统更新推送广播
    public static final String ACTION_BROADCAST_SYSTEM_UPGRADE = "com.dace.textreader.system.upgrade";

    //极光注册
    public static final String ACTION_BROADCAST_JIGUANG_LOGIN = "com.dace.textreader.jiguang.login";

}
