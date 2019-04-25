package com.dace.textreader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dace.textreader.R;
import com.dace.textreader.view.tab.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2019/2/27 0027 下午 4:34.
 * Version   1.0;
 * Describe :  新的阅读Fragment
 * History:
 * ==============================================================================
 */
public class NewReaderFragment extends Fragment {

    private View view;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout rl_search;

    private Context mContext;

    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();
    private ViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_reader, container, false);

        initData();
        initView();
        initEvents();

        return view;
    }

    private void initData() {
        mList_title.add("精选");
        mList_title.add("更新");
        mList_title.add("关注");

//        ReadRecommendationFragment readRecommendationFragment = new ReadRecommendationFragment();
//        mList_fragment.add(readRecommendationFragment);
//        ReaderLevelFragment readerLevelFragment = new ReaderLevelFragment();
//        mList_fragment.add(readerLevelFragment);
//        ReaderFragment readerFragment = new ReaderFragment();
//        mList_fragment.add(readerFragment);
//        ReadTextBookFragment readTextBookFragment = new ReadTextBookFragment();
//        mList_fragment.add(readTextBookFragment);
        NewHomeRecommendationFragment homeRecommendationFragment = new NewHomeRecommendationFragment();
        mList_fragment.add(homeRecommendationFragment);
        HomeFindFragment homeFindFragment = new HomeFindFragment();
        mList_fragment.add(homeFindFragment);
        HomeFollowFragment homeFollowFragment = new HomeFollowFragment();
        mList_fragment.add(homeFollowFragment);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView() {
        rl_search = view.findViewById(R.id.rl_search_new_reader_fragment);
        tabLayout = view.findViewById(R.id.tab_layout_new_reader_fragment);
        viewPager = view.findViewById(R.id.view_pager_new_reader_fragment);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);
    }

    private void initEvents() {
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
