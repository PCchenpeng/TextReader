package com.dace.textreader.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.bean.ReaderTabBean;
import com.dace.textreader.fragment.ReaderTabAlbumFragment;
import com.dace.textreader.fragment.ReaderTabSelectFragment;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.MyRefreshHeader;
import com.dace.textreader.view.tab.SmartTabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabActivity extends BaseActivity implements View.OnClickListener,AppBarLayout.OnOffsetChangedListener{

    private ViewPager viewPager;
    private SmartTabLayout tabLayout;
    private String[] titles = new String[]{"精选","专辑"};
    private List<Fragment> mList_fragment = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;
    private SmartRefreshLayout smartRefreshLayout;

    private String type;
    private String typeName;
    private String imgUrl;
    private int currentIndex;
    private ImageView iv_img;
    private TextView tv_title;
    private ReaderTabBean readerTabBean;
    private RelativeLayout rl_back;
    private RelativeLayout rl_back_1;

    private Toolbar toolbar;
    private Toolbar toolbar1;
    private AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_tab);
//
        loadData();
        initView();
        initEvents();

    }



    private void initView() {
        smartRefreshLayout = findViewById(R.id.smart_refresh);
        rl_back = findViewById(R.id.rl_back);
        iv_img = findViewById(R.id.iv_img);
        tv_title = findViewById(R.id.tv_title);
        GlideApp.with(this)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_img);

        smartRefreshLayout.setRefreshHeader(new MyRefreshHeader(this));
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        smartRefreshLayout.setEnableLoadMore(false);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        appBarLayout = findViewById(R.id.appbar);
        rl_back_1 = findViewById(R.id.rl_back_1);

        toolbar = findViewById(R.id.toolbar);
        toolbar1 = findViewById(R.id.toolbar1);

        tv_title.setText(typeName);

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
                if(currentIndex == 0 && onSelectPullListener != null){
                    onSelectPullListener.onRefresh();
                    refreshLayout.finishRefresh();
                }

                if(currentIndex == 1 && onAlbumPullListener != null){
                    onAlbumPullListener.onRefresh();
                    refreshLayout.finishRefresh();
                }

            }
        });

        ReaderTabSelectFragment readTextBookFragment = ReaderTabSelectFragment.newInstance(type);
        ReaderTabAlbumFragment readerTabAlbumFragment = ReaderTabAlbumFragment.newInstance(type);
        mList_fragment.add(readTextBookFragment);
        mList_fragment.add(readerTabAlbumFragment);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setChangeTextSize(false);
        tabLayout.setViewPager(viewPager);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        rl_back_1.setOnClickListener(this);

        appBarLayout.addOnOffsetChangedListener(this);

        //增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)  titleBar
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setPaddingSmart(this, toolbar1);
    }

    private void loadData(){
        type = getIntent().getStringExtra("type");
        typeName = getIntent().getStringExtra("typename");
        imgUrl = getIntent().getStringExtra("imgurl");
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_back_1:
            case R.id.rl_back:
                finish();
                break;
        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        int scrollRangle = appBarLayout.getTotalScrollRange();

        if(i < -DensityUtil.dip2px(this,123)){
            toolbar1.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }else {
            toolbar1.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }


        Log.e("scrollRangle" ,String.valueOf(scrollRangle));

        Log.e("scrollRangle" ,"i="+String.valueOf(i));
        /**
         * 如果是verticalOffset改成负数   有不一样的效果，可以模拟试试
         */
//        mIvHeader.setTranslationY(verticalOffset);
//
//        /**
//         * 这个数值可以自己定义
//         */
//        if (verticalOffset < -10) {
//            mIvBack.setImageResource(R.drawable.back_black);
//            mIvMenu.setImageResource(R.drawable.icon_menu_black);
//        } else {
//            mIvBack.setImageResource(R.drawable.back_white);
//            mIvMenu.setImageResource(R.drawable.icon_menu_white);
//        }
//
//        int mAlpha = (int) Math.abs(255f / scrollRangle * verticalOffset);
//        //顶部title渐变效果
//        mToolbar1.setBackgroundColor(Color.argb(mAlpha, 255, 255, 255));
//        mToolbarUsername.setTextColor(Color.argb(mAlpha, 0, 0, 0));

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

    public interface OnSelectPullListener{
        void onRefresh();
    }

    public interface OnAlbumPullListener{
        void onRefresh();
    }

    public void setOnSelectPullListener(OnSelectPullListener onSelectPullListener){
        this.onSelectPullListener = onSelectPullListener;
    }

    public void setOnAlbumPullListener(OnAlbumPullListener onAlbumPullListener) {
        this.onAlbumPullListener = onAlbumPullListener;
    }

    private OnSelectPullListener onSelectPullListener;

    private OnAlbumPullListener onAlbumPullListener;

}
