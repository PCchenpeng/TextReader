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
import android.widget.ImageView;
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
 * Created time 2019/3/12 0027 上午 10:18.
 * Version   1.0;
 * Describe :  新的主Fragment
 * History:
 * ==============================================================================
 */

public class NewHomeFragment extends Fragment {

    private View view;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout rl_search;

    private Context mContext;

    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();
    private ViewPagerAdapter adapter;

    private ImageView iv_search;
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
        mList_title.add("推荐");
        mList_title.add("创作");

        RecommendFragment recommendFragment = new RecommendFragment();
        recommendFragment.setOnSearchMissListener(new RecommendFragment.OnSearchMissListener() {
            @Override
            public void onMiss() {
                iv_search.setVisibility(View.GONE);
            }

            @Override
            public void onShow() {
                iv_search.setVisibility(View.VISIBLE);
            }
        });
        mList_fragment.add(recommendFragment);
        HomeFragment homeFragment = new HomeFragment();
        mList_fragment.add(homeFragment);
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
        iv_search = view.findViewById(R.id.iv_search);
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
