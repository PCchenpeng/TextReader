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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.fragment.ReaderTabAlbumDetailBookFragment;
import com.dace.textreader.fragment.ReaderTabAlbumDetailListFragment;
import com.dace.textreader.fragment.ReaderTabAlbumDetailSentenceFragment;
import com.dace.textreader.fragment.ReaderTabAlbumFragment;
import com.dace.textreader.fragment.ReaderTabSelectFragment;
import com.dace.textreader.view.MyRefreshHeader;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailActivity extends BaseActivity implements View.OnClickListener{

    private SmartRefreshLayout smartRefreshLayout;
    private ExpandableTextView expTv1;
    private ImageView iv_img;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int currentIndex ;
    private List<Fragment> mList_fragment = new ArrayList<>();
    private String[] titles;
    private ViewPagerAdapter viewPagerAdapter;
    private int format;
    private String sentenceNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_tab_album_detail);
//
        loadData();
        initView();

    }



    private void loadData() {
        format = getIntent().getIntExtra("format",-1);
        sentenceNum = getIntent().getStringExtra("sentenceNum");
        //format
        if(!sentenceNum.equals("null")&&!sentenceNum.equals("0")){
            if(format == 0){
                titles = new String[]{"章节","名句"};
                ReaderTabAlbumDetailListFragment readerTabAlbumDetailListFragment = new  ReaderTabAlbumDetailListFragment();
                ReaderTabAlbumDetailSentenceFragment readerTabAlbumDetailSentenceFragment = new ReaderTabAlbumDetailSentenceFragment();
                mList_fragment.add(readerTabAlbumDetailListFragment);
                mList_fragment.add(readerTabAlbumDetailSentenceFragment);

            }else if(format == 1){
                ReaderTabAlbumDetailBookFragment readerTabAlbumDetailBookFragment = new  ReaderTabAlbumDetailBookFragment();
                ReaderTabAlbumDetailSentenceFragment readerTabAlbumDetailSentenceFragment = new ReaderTabAlbumDetailSentenceFragment();
                mList_fragment.add(readerTabAlbumDetailBookFragment);
                mList_fragment.add(readerTabAlbumDetailSentenceFragment);
                titles = new String[]{"书本","名句"};
            }
        }else {
            if(format == 0){
                titles = new String[]{"章节"};
                ReaderTabAlbumDetailListFragment readerTabAlbumDetailListFragment = new  ReaderTabAlbumDetailListFragment();
                mList_fragment.add(readerTabAlbumDetailListFragment);
            }else if(format == 1){
                titles = new String[]{"书本"};
                ReaderTabAlbumDetailBookFragment readerTabAlbumDetailBookFragment = new  ReaderTabAlbumDetailBookFragment();
                mList_fragment.add(readerTabAlbumDetailBookFragment);
            }
        }

    }

    private void initView() {
         expTv1 = findViewById(R.id.expand_text_view);

        expTv1.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
            }
        });

        expTv1.setText("杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将杀案还将");

        smartRefreshLayout = findViewById(R.id.smart_refresh);
        iv_img = findViewById(R.id.iv_img);
//        GlideApp.with(this)
//                .load(imgUrl)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(iv_img);

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
        tabLayout.setupWithViewPager(viewPager);



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
