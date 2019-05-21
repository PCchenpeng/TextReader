package com.dace.textreader.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.fragment.ReaderTabAlbumDetailBookFragment;
import com.dace.textreader.fragment.ReaderTabAlbumDetailListFragment;
import com.dace.textreader.fragment.ReaderTabAlbumDetailSentenceFragment;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.MyRefreshHeader;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.tab.SmartTabLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailActivity extends BaseActivity implements View.OnClickListener{

    private String url = HttpUrlPre.HTTP_URL_ + "/select/album/detail";

    private SmartRefreshLayout smartRefreshLayout;
    private ExpandableTextView expTv1;
    private ImageView iv_img;
    private TextView tv_title;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private int currentIndex ;
    private List<Fragment> mList_fragment = new ArrayList<>();
    private String[] titles;
    private ViewPagerAdapter viewPagerAdapter;
    private int format;
    private String sentenceNum;
    private String albumId;
    private ReadTabAlbumDetailBean readTabAlbumDetailBean = null;
    private ReaderTabAlbumDetailBookFragment readerTabAlbumDetailBookFragment;
    private ReaderTabAlbumDetailSentenceFragment readerTabAlbumDetailSentenceFragment;
    private ReaderTabAlbumDetailListFragment readerTabAlbumDetailListFragment;
    private RelativeLayout rl_back,rl_share;

    private String title;
    private Bitmap shareBitmap;
    private String shareImgUrl;
    private WbShareHandler shareHandler;
    private String shareContent = "";
    private String shareQQUrl;
    private String shareWXUrl;
    private String shareWBUrl;
    private boolean isDataComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_tab_album_detail);
//
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
        initData();
        initView();
        loadDetailData();

    }



    private void initData() {
        format = getIntent().getIntExtra("format",-1);
        albumId = getIntent().getStringExtra("albumId");
        sentenceNum = getIntent().getStringExtra("sentenceNum");
        if (sentenceNum != null && sentenceNum.contains(".")){
            Log.d("111","format  " + format + " sentenceNum " + sentenceNum + " albumId " + albumId);
            sentenceNum = getIntent().getStringExtra("sentenceNum").split("\\.")[0];
        }

        //format
        if(sentenceNum != null && !sentenceNum.equals("null")&&!sentenceNum.equals("0")){
            if(format == 0){
                titles = new String[]{"章节","名句"};
                readerTabAlbumDetailListFragment = new  ReaderTabAlbumDetailListFragment();
                readerTabAlbumDetailSentenceFragment = new ReaderTabAlbumDetailSentenceFragment();
                mList_fragment.add(readerTabAlbumDetailListFragment);
                mList_fragment.add(readerTabAlbumDetailSentenceFragment);

            }else if(format == 1){//书本
                readerTabAlbumDetailBookFragment = new  ReaderTabAlbumDetailBookFragment();
                readerTabAlbumDetailSentenceFragment = new ReaderTabAlbumDetailSentenceFragment();
                mList_fragment.add(readerTabAlbumDetailBookFragment);
                mList_fragment.add(readerTabAlbumDetailSentenceFragment);
                titles = new String[]{"书本","名句"};
            }
        }else {
            if(format == 0){
                titles = new String[]{"章节"};
                readerTabAlbumDetailListFragment = new  ReaderTabAlbumDetailListFragment();
                mList_fragment.add(readerTabAlbumDetailListFragment);
            }else if(format == 1){//书本
                titles = new String[]{"书本"};
                readerTabAlbumDetailBookFragment = new  ReaderTabAlbumDetailBookFragment();
                mList_fragment.add(readerTabAlbumDetailBookFragment);
            }
        }

    }

    private void loadDetailData() {
        JSONObject params = new JSONObject();
        try {
            String sign = DataEncryption.encode(System.currentTimeMillis() + "","Z25pYW5l");
            params.put("studentId",PreferencesUtil.getData(this,"studentId","-1"));
            params.put("gradeId",PreferencesUtil.getData(this,"gradeId","-1"));
            params.put("isShare","0");
//            params.put("sign",sign);
            params.put("albumId",DataEncryption.encode(albumId));
            params.put("width",DensityUtil.getScreenWidth(this));
            params.put("height",DensityUtil.getScreenWidth(this)*2/3);

            Log.d("111","sign " + sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        Log.d("111","result.toString() " + result.toString());
                        readTabAlbumDetailBean = GsonUtil.GsonToBean(result.toString(),ReadTabAlbumDetailBean.class);
                        if (readTabAlbumDetailBean.getStatus() == 200) {
                            ReadTabAlbumDetailBean.DataBean.ShareListBean shareListBean = readTabAlbumDetailBean.getData().getShareList();
                            if (shareListBean != null) {
                                shareQQUrl = shareListBean.getQq().getLink();
                                shareWXUrl = shareListBean.getWx().getLink();
                                shareWBUrl = shareListBean.getWeibo().getLink();
                                shareImgUrl = shareListBean.getWx().getImage();
                                title = readTabAlbumDetailBean.getData().getTitle();
                                shareContent = readTabAlbumDetailBean.getData().getIntroduction();
                                prepareBitmap(shareImgUrl);
                                isDataComplete = true;
                            }
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_img.getLayoutParams();
                            params.width = DensityUtil.getScreenWidth(ReaderTabAlbumDetailActivity.this);
                            params.height = DensityUtil.getScreenWidth(ReaderTabAlbumDetailActivity.this)*2/3;
                            iv_img.setLayoutParams(params);
                            GlideApp.with(ReaderTabAlbumDetailActivity.this)
                                    .load(readTabAlbumDetailBean.getData().getCover())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(iv_img);
                            tv_title.setText(readTabAlbumDetailBean.getData().getTitle());
                            expTv1.setText(readTabAlbumDetailBean.getData().getIntroduction());

                            if (readerTabAlbumDetailListFragment != null) {
                                readerTabAlbumDetailListFragment.setmData(readTabAlbumDetailBean.getData().getBook().get(0).getArticleList());
                            }
                            if (readerTabAlbumDetailBookFragment != null) {
                                readerTabAlbumDetailBookFragment.setmData(readTabAlbumDetailBean.getData().getBook());
                                readerTabAlbumDetailBookFragment.setImgUrl(readTabAlbumDetailBean.getData().getCover());
                            }
                            if (readerTabAlbumDetailSentenceFragment != null) {
                                readerTabAlbumDetailSentenceFragment.setAlbumId(readTabAlbumDetailBean.getData().getAlbumId());
                                readerTabAlbumDetailSentenceFragment.setImgUrl(readTabAlbumDetailBean.getData().getCover());
                            }
                        }
                        smartRefreshLayout.finishRefresh();
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        smartRefreshLayout.finishRefresh();
                    }
                });
    }



    private void initView() {
        expTv1 = findViewById(R.id.expand_text_view);
        rl_back = findViewById(R.id.rl_back);
        rl_share = findViewById(R.id.rl_share);
        rl_back.setOnClickListener(this);
        rl_share.setOnClickListener(this);

        expTv1.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
            }
        });

        expTv1.setText("");

        Html.fromHtml("");
        smartRefreshLayout = findViewById(R.id.smart_refresh);
        iv_img = findViewById(R.id.iv_img);
        tv_title = findViewById(R.id.tv_title);


        smartRefreshLayout.setRefreshHeader(new MyRefreshHeader(this));
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableLoadMore(false);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                if (isLoading) {
//                    refreshLayout.finishRefresh();
//                } else {
//                    initData();
//                }
//                if(currentIndex == 0 && onSelectPullListener != null){
//                    onSelectPullListener.onRefresh();
//                    refreshLayout.finishRefresh();
//                }
//
//                if(currentIndex == 1 && onAlbumPullListener != null){
//                    onAlbumPullListener.onRefresh();
//                    refreshLayout.finishRefresh();
//                }

            }
        });

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setChangeTextSize(false);
        tabLayout.setViewPager(viewPager);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_share:
                if(isDataComplete){
                    share();
                }else {
                    MyToastUtil.showToast(this,"请等待数据加载完成之后再试");
                }
                break;
        }
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
            return titles[position];
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

    private void share() {
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
                                shareArticleToWX(true,shareWXUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareArticleToWX(false,shareWXUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(ReaderTabAlbumDetailActivity.this)) {
                                    shareToWeibo(shareWBUrl);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQQ(shareQQUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQZone(shareQQUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataUtil.copyContent(ReaderTabAlbumDetailActivity.this, shareQQUrl);
                                MyToastUtil.showToast(ReaderTabAlbumDetailActivity.this,"复制成功");
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
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, title, shareContent, shareImgUrl);
    }

    /**
     * 分享笔记到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {

        Bitmap thumb = shareBitmap;
        if(thumb == null)
            thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(this, url, title, shareContent,
                ImageUtils.bmpToByteArrayCopy(thumb, false), friend);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, title, shareContent, shareImgUrl);
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        if (shareBitmap == null) {
            shareBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
        }

        ShareUtil.shareToWeibo(shareHandler, url, title,
                shareContent, shareBitmap);

    }

    /**
     * 准备Bitmap
     */
    private void prepareBitmap(final String shareImgUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                shareBitmap = ImageUtils.GetNetworkBitmap(shareImgUrl);
                if (shareBitmap == null) {
                    shareBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }
            }
        }.start();
    }

}
