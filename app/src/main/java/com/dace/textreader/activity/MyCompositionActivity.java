package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.DraftFragment;
import com.dace.textreader.fragment.WritingCorrectionFragment;
import com.dace.textreader.fragment.WritingMatchFragment;
import com.dace.textreader.fragment.WritingPublishFragment;
import com.dace.textreader.fragment.WritingWorkFragment;
import com.dace.textreader.listen.OnShareClickListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.tab.SmartTabLayout;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * “我的”作文
 */
public class MyCompositionActivity extends BaseActivity {

    //分享链接
    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/get/share/writing/";

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private ViewPager viewPager;
    private SmartTabLayout tabLayout;
    private RelativeLayout rl_write;
    private RelativeLayout rl_writing;
    private TextView tv_submit;

    private MyCompositionActivity mContext;

    private ViewPagerAdapter viewPagerAdapter;
    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();

    private int index = 0;  //页面索引
    private DraftFragment draftFragment;
    private WritingPublishFragment writingPublishFragment;
    private WritingCorrectionFragment writingCorrectionFragment;
    private WritingWorkFragment writingWorkFragment;
    private WritingMatchFragment writingMatchFragment;

    private boolean isCorrection = false;  //是否是作文批改，是的话点击item跳转批改
    private String writingId = "";
    private String writingTitle = "";
    private String writingContent = "";
    private String writingTaskId = "";
    private int writingArea = 0;
    private int writingType = 0;
    private int writingWordsNum = 0;
    private int writingFormat = 1;

    private boolean isShare = false;  //是否正在分享
    private int type_share = -1;
    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_composition);

        mContext = this;

        index = getIntent().getIntExtra("index", 0);
        isCorrection = getIntent().getBooleanExtra("isCorrection", false);

        initData();
        initView();
        initEvents();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));

        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_write.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initData() {
        mList_title.add("草稿");
        mList_title.add("发布");
        mList_title.add("批改");
        mList_title.add("作业");
        mList_title.add("活动");

        draftFragment = new DraftFragment();
        draftFragment.setCorrection(isCorrection);
        draftFragment.setOnShareClickListen(new OnShareClickListen() {
            @Override
            public void onShare(String writingId, String writingTitle, String writingContent, int writingArea, int writingFormat) {
                showShareDialog(writingId, writingTitle, writingContent, writingArea, writingFormat);
            }
        });
        mList_fragment.add(draftFragment);

        writingPublishFragment = new WritingPublishFragment();
        writingPublishFragment.setCorrection(isCorrection);
        writingPublishFragment.setOnShareClickListen(new OnShareClickListen() {
            @Override
            public void onShare(String writingId, String writingTitle, String writingContent, int writingArea, int writingFormat) {
                showShareDialog(writingId, writingTitle, writingContent, writingArea, writingFormat);
            }
        });
        mList_fragment.add(writingPublishFragment);

        writingCorrectionFragment = new WritingCorrectionFragment();
        writingCorrectionFragment.setCorrection(isCorrection);
        writingCorrectionFragment.setOnShareClickListen(new OnShareClickListen() {
            @Override
            public void onShare(String writingId, String writingTitle, String writingContent, int writingArea, int writingFormat) {
                showShareDialog(writingId, writingTitle, writingContent, writingArea, writingFormat);
            }
        });
        mList_fragment.add(writingCorrectionFragment);

        writingWorkFragment = new WritingWorkFragment();
        writingWorkFragment.setCorrection(isCorrection);
        writingWorkFragment.setOnShareClickListen(new OnShareClickListen() {
            @Override
            public void onShare(String writingId, String writingTitle, String writingContent, int writingArea, int writingFormat) {
                showShareDialog(writingId, writingTitle, writingContent, writingArea, writingFormat);
            }
        });
        mList_fragment.add(writingWorkFragment);

        writingMatchFragment = new WritingMatchFragment();
        writingMatchFragment.setCorrection(isCorrection);
        writingMatchFragment.setOnShareClickListen(new OnShareClickListen() {
            @Override
            public void onShare(String writingId, String writingTitle, String writingContent, int writingArea, int writingFormat) {
                showShareDialog(writingId, writingTitle, writingContent, writingArea, writingFormat);
            }
        });
        mList_fragment.add(writingMatchFragment);

    }

    @Override
    protected void onStop() {
        DataUtil.isDraftNeedRefresh = true;
        super.onStop();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToWriting();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writingId.equals("")) {
                    MyToastUtil.showToast(mContext, "请先选择作文~");
                } else {
                    submitReview();
                }
            }
        });

        draftFragment.setOnItemCorrectionClick(new DraftFragment.OnItemCorrectionClick() {
            @Override
            public void onClick(String id, String title, String taskId, int area, int type, int count) {
                itemSelected(id, title, taskId, area, type, count);
            }
        });
        writingPublishFragment.setOnItemCorrectionClick(new WritingPublishFragment.OnItemCorrectionClick() {
            @Override
            public void onClick(String id, String title, String taskId, int area, int type, int count) {
                itemSelected(id, title, taskId, area, type, count);
            }
        });
        writingCorrectionFragment.setOnItemCorrectionClick(new WritingCorrectionFragment.OnItemCorrectionClick() {
            @Override
            public void onClick(String id, String title, String taskId, int area, int type, int count) {
                itemSelected(id, title, taskId, area, type, count);
            }
        });
        writingWorkFragment.setOnItemCorrectionClick(new WritingWorkFragment.OnItemCorrectionClick() {
            @Override
            public void onClick(String id, String title, String taskId, int area, int type, int count) {
                itemSelected(id, title, taskId, area, type, count);
            }
        });
        writingMatchFragment.setOnItemCorrectionClick(new WritingMatchFragment.OnItemCorrectionClick() {
            @Override
            public void onClick(String id, String title, String taskId, int area, int type, int count) {
                itemSelected(id, title, taskId, area, type, count);
            }
        });

    }

    /**
     * 前往写作
     */
    private void turnToWriting() {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", "");
        intent.putExtra("taskId", "");
        intent.putExtra("area", 5);
        intent.putExtra("type", 5);
        startActivity(intent);
    }

    /**
     * 作文被选中
     *
     * @param id
     * @param title
     * @param taskId
     * @param area
     * @param type
     * @param count
     */
    private void itemSelected(String id, String title, String taskId, int area, int type, int count) {
        clearSelectedItem();
        writingId = id;
        writingTitle = title;
        writingTaskId = taskId;
        writingArea = area;
        writingType = type;
        writingWordsNum = count;
        tv_submit.setTextColor(Color.parseColor("#FF9933"));
    }

    /**
     * 清除选中item
     */
    private void clearSelectedItem() {
        switch (writingArea) {
            case 5:
                draftFragment.clearSelectedItem();
                break;
            case 0:
                writingPublishFragment.clearSelectedItem();
                break;
            case 1:
                writingCorrectionFragment.clearSelectedItem();
                break;
            case 6:
                writingWorkFragment.clearSelectedItem();
                break;
            case 2:
                writingMatchFragment.clearSelectedItem();
                break;
        }
    }

    /**
     * 提交批改
     */
    private void submitReview() {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("isCorrection", true);
        intent.putExtra("id", writingId);
        intent.putExtra("area", writingArea);
        intent.putExtra("type", writingType);
        intent.putExtra("taskId", writingTaskId);
        startActivity(intent);
        finish();
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        tv_page_title.setText("我的作文");

        rl_write = findViewById(R.id.rl_write_my_composition);
        rl_writing = findViewById(R.id.rl_writing_my_composition);
        tv_submit = findViewById(R.id.tv_submit_my_composition);

        tabLayout = findViewById(R.id.tab_layout_my_composition);

        if (isCorrection) {
            rl_writing.setVisibility(View.GONE);
            tv_submit.setVisibility(View.VISIBLE);
            tabLayout.setInternalTabClickable(false);
        } else {
            rl_writing.setVisibility(View.VISIBLE);
            tv_submit.setVisibility(View.GONE);
            tabLayout.setInternalTabClickable(true);
        }

        viewPager = findViewById(R.id.view_pager_my_composition);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setViewPager(viewPager);
        if (index > -1 && index < mList_fragment.size()) {
            viewPager.setCurrentItem(index);
        }

    }

    /**
     * 显示分享对话框
     */
    private void showShareDialog(String id, String title, String content, int area, int format) {
        if (isShare) {
            MyToastUtil.showToast(mContext, "另一项操作正在进行中，请稍候...");
            return;
        }
        type_share = -1;
        writingId = id;
        writingTitle = title;
        writingContent = content;
        writingArea = area;
        writingFormat = format;
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
                                getShareHtml(TYPE_SHARE_WX_FRIEND);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    getShareHtml(TYPE_SHARE_Weibo);
                                } else {
                                    MyToastUtil.showToast(mContext, "请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setDimAmount(0.3f)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void getShareHtml(int type) {
        isShare = true;
        type_share = type;
        MyToastUtil.showToast(mContext, "正在准备分享内容...");
        new GetShareHtml(this).execute(shareUrl, writingId, String.valueOf(writingFormat)
                , String.valueOf(writingArea));
    }

    /**
     * 分享
     */
    private void share(String url) {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true, url, writingTitle, writingContent);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false, url, writingTitle, writingContent);
                break;
            case TYPE_SHARE_Weibo:
                shareToWeibo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url, writingTitle, writingContent);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url, writingTitle, writingContent);
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

        ShareUtil.shareToWeibo(shareHandler, url, writingTitle, writingContent, thumb);

    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url, String title, String content) {
        ShareUtil.shareToQQ(this, url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url, String title, String content) {
        ShareUtil.shareToQZone(this, url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url, String title, String content) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, title, content,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, MyCompositionActivity> {

        protected GetShareHtml(MyCompositionActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(MyCompositionActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("format", strings[2]);
                object.put("area", strings[3]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MyCompositionActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
            activity.isShare = false;
        }
    }

    /**
     * 分析分享链接数据
     *
     * @param s
     */
    private void analyzeShareData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                String url = jsonObject.getString("data");
                share(url);
            } else {
                errorShare();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorShare();
        }
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        MyToastUtil.showToast(mContext, "分享失败，请稍后重试");
    }

    /**
     * 适配器
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mList_title.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mList_fragment.get(position);
        }

        @Override
        public int getCount() {
            return mList_fragment.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }

}
