package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.fragment.ReaderTabAlbumDetailBookFragment;
import com.dace.textreader.fragment.ReaderTabAlbumDetailListFragment;
import com.dace.textreader.fragment.ReaderTabAlbumDetailSentenceFragment;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.MyRefreshHeader;
import com.dace.textreader.view.tab.SmartTabLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_tab_album_detail);
//
        initData();
        initView();
        loadDetailData();

    }



    private void initData() {
        format = getIntent().getIntExtra("format",-1);
        albumId = getIntent().getStringExtra("albumId");
        sentenceNum = getIntent().getStringExtra("sentenceNum");
        if (getIntent().getStringExtra("sentenceNum").contains(".")){
            Log.d("111","format  " + format + " sentenceNum " + sentenceNum + " albumId " + albumId);
            sentenceNum = getIntent().getStringExtra("sentenceNum").split("\\.")[0];
        }

        //format
        if(!sentenceNum.equals("null")&&!sentenceNum.equals("0")){
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
            params.put("sign",sign);
            params.put("albumId",albumId);
            params.put("width","750");
            params.put("height","420");

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

                        GlideApp.with(ReaderTabAlbumDetailActivity.this)
                        .load(readTabAlbumDetailBean.getData().getCover())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_img);
                        tv_title.setText(readTabAlbumDetailBean.getData().getTitle());
                        expTv1.setText(readTabAlbumDetailBean.getData().getIntroduction());
//                        List<AlbumDetailBean.DataBean.ArticleListBean> data = albumDetailBean.getData().getArticleList();
////                        if(isRefresh){
//////                            Toast.makeText(getContext(),"hahhaha",Toast.LENGTH_SHORT).show();
////                            if(mData != null){
////                                mData.clear();
////                                mData.addAll(data);
////
////                            }
////                            mRecycleView.onPullComplete();
////                        } else{
//                            if(mData != null)
//                                mData.addAll(data);
////                        }

//                        homeLevelAdapter.setData(mData);
                        if (readerTabAlbumDetailListFragment != null) {
                            readerTabAlbumDetailListFragment.setmData(readTabAlbumDetailBean.getData().getBook().get(0).getArticleList());
                        }
                        if (readerTabAlbumDetailBookFragment != null) {
                            readerTabAlbumDetailBookFragment.setmData(readTabAlbumDetailBean.getData().getBook());
                        }
                        if (readerTabAlbumDetailSentenceFragment != null) {
                            readerTabAlbumDetailSentenceFragment.setAlbumId(readTabAlbumDetailBean.getData().getAlbumId());
                        }

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
//                        mRecycleView.onPullComplete();
                    }
                });
    }

//    private OnCreateViewListener onCreateViewListener;
//
//    public void setOnCreateViewListener(OnCreateViewListener onCreateViewListener) {
//        this.onCreateViewListener = onCreateViewListener;
//    }
//
//    public interface OnCreateViewListener{
//        void onCreateView();
//    }


    private void initView() {
         expTv1 = findViewById(R.id.expand_text_view);

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
}
