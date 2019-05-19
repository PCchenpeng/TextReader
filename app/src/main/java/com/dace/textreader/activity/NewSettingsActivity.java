package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dace.textreader.R;
import com.dace.textreader.util.DataClearManager;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.VersionInfoUtil;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

/**
 * 设置
 */
public class NewSettingsActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private LinearLayout ll_disclaimer;
    private LinearLayout ll_feedback;
    private LinearLayout ll_clear_cache;
    private TextView tv_cache_size;
    private LinearLayout ll_service_agreement;
    private LinearLayout ll_version_update;
    private LinearLayout ll_recommend_friend;
    private TextView tv_cur_version;
    private Button btn_login_or_exit;

    private NewSettingsActivity mContext;

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;
    private String shareTitle = "派知语文，快乐学语文";
    private String shareContent = "派知语文APP依托前沿AI技术，打造智慧学习平台。帮助学生提高语文素养，快乐学语文尽在派知语文。";

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_settings);

        mContext = this;

        initView();
        initEvents();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            String cacheSize = DataClearManager.getTotalCacheSize(mContext);
            tv_cache_size.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        ll_disclaimer.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_clear_cache.setOnClickListener(this);
        ll_service_agreement.setOnClickListener(this);
        ll_version_update.setOnClickListener(this);
        ll_recommend_friend.setOnClickListener(this);
        btn_login_or_exit.setOnClickListener(this);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        tv_page_title.setText("系统设置");

        ll_disclaimer = findViewById(R.id.ll_new_disclaimer_settings);
        ll_feedback = findViewById(R.id.ll_new_feedback_settings);
        ll_clear_cache = findViewById(R.id.ll_new_clearCache_settings);
        tv_cache_size = findViewById(R.id.tv_new_cache_settings);
        ll_service_agreement = findViewById(R.id.ll_service_agreement_settings);
        ll_version_update = findViewById(R.id.ll_version_update_settings);
        tv_cur_version = findViewById(R.id.tv_cur_version_settings);
        ll_recommend_friend = findViewById(R.id.ll_recommend_friend_settings);
        btn_login_or_exit = findViewById(R.id.btn_login_or_exit_settings);

        String curVersion = "当前版本：" + VersionInfoUtil.getVersionName(mContext);
        if (curVersion.contains("f")) {
            curVersion = curVersion.split("f")[0];
        }
        tv_cur_version.setText(curVersion);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.ll_new_disclaimer_settings:
                turnToDisclaimerView();
                break;
            case R.id.ll_new_feedback_settings:
                turnToFeedBackView();
                break;
            case R.id.ll_new_clearCache_settings:
                clearCache();
                break;
            case R.id.ll_service_agreement_settings:
                turnToServiceAgreement();
                break;
            case R.id.ll_version_update_settings:
                broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_SYSTEM_UPGRADE);
                MyToastUtil.showToast(mContext, "正在检查版本信息...");
                break;
            case R.id.btn_login_or_exit_settings:
                clearUser();
                break;
            case R.id.ll_recommend_friend_settings:
                showShareDialog();
                break;
        }
    }

    /**
     * 显示分享对话框
     */
    private void showShareDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_share_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.share_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_wechat, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                share(HttpUrlPre.COMPANY_URL, TYPE_SHARE_WX_FRIEND);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                share(HttpUrlPre.COMPANY_URL, TYPE_SHARE_WX_FRIENDS);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    share(HttpUrlPre.COMPANY_URL, TYPE_SHARE_Weibo);
                                } else {
                                    MyToastUtil.showToast(mContext, "请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                share(HttpUrlPre.COMPANY_URL, TYPE_SHARE_QQ);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                share(HttpUrlPre.COMPANY_URL, TYPE_SHARE_QZone);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                share(HttpUrlPre.COMPANY_URL, TYPE_SHARE_LINK);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setDimAmount(0.3f)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 分享
     *
     * @param url
     */
    private void share(String url, int type_share) {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true, url);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false, url);
                break;
            case TYPE_SHARE_Weibo:
                shareToWeibo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url);
                break;
            case TYPE_SHARE_LINK:
                DataUtil.copyContent(mContext, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWeibo(shareHandler, url, shareTitle, shareContent, thumb);

    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, shareTitle, shareContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, shareTitle, shareContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, shareTitle, shareContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 前往服务协议界面
     */
    private void turnToServiceAgreement() {
        Intent intent = new Intent(mContext, ServiceAgreementActivity.class);
        intent.putExtra("isRecharge", false);
        startActivity(intent);
    }

    /**
     * 前往免责声明页面
     */
    private void turnToDisclaimerView() {
        startActivity(new Intent(mContext, DisclaimerActivity.class));
    }

    /**
     * 前往意见反馈页面
     */
    private void turnToFeedBackView() {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        intent.putExtra("type","使用反馈");
        intent.putExtra("word","");
        startActivity(intent);
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        DataClearManager.deleteDir(mContext.getCacheDir());
        Glide.get(mContext).clearMemory();
        MyToastUtil.showToast(mContext, "清除缓存成功");
        tv_cache_size.setText("");
    }

    /**
     * 退出登录，清除用户信息
     */
    private void clearUser() {
        if (getPlayService() != null) {
            getPlayService().pause();
            getPlayService().hideFloatView();
        }

        NewMainActivity.TOKEN = "";
        NewMainActivity.STUDENT_ID = -1;
        NewMainActivity.USERNAME = "";
        NewMainActivity.GRADE = -1;
        NewMainActivity.LEVEL = -1;
        NewMainActivity.PY_SCORE = "";
        NewMainActivity.USERIMG = "";
        NewMainActivity.NEWS_COUNT = 0;
        NewMainActivity.PHONENUMBER = "";
        NewMainActivity.DESCRIPTION = "";

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("token", "");
        editor.apply();

        PreferencesUtil.saveData(this,"studentId","-1");
        PreferencesUtil.saveData(this,"gradeId","-1");
        PreferencesUtil.saveData(this,"token","");
        PreferencesUtil.saveData(this,"phoneNum","");

        broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);

        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }

    /**
     * 发送广播
     *
     * @param action 广播的Action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

}
