package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.adapter.ReaderTabSelectAdapter;
import com.dace.textreader.bean.ReaderTabBean;
import com.dace.textreader.fragment.ReadRecommendationFragment;
import com.dace.textreader.fragment.ReaderTabAlbumFragment;
import com.dace.textreader.fragment.ReaderTabSelectFragment;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.view.MyRefreshHeader;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabActivity extends BaseActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] titles = new String[]{"精选","专辑"};
    private List<Fragment> mList_fragment = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;
    private SmartRefreshLayout smartRefreshLayout;

    private String type;
    private String imgUrl;
    private int currentIndex;
    private ImageView iv_img;
    private ReaderTabBean readerTabBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_tab);
//
        loadData();
        initView();

    }
    private void initView() {
        smartRefreshLayout = findViewById(R.id.smart_refresh);
        iv_img = findViewById(R.id.iv_img);
        GlideApp.with(this)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_img);

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
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadData(){
        type = getIntent().getStringExtra("type");
        imgUrl = getIntent().getStringExtra("imgurl");

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
