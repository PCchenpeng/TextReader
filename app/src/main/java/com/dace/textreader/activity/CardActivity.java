package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.CardFragment;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.tab.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡包列表
 */
public class CardActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_title;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;

    private CardActivity mContext;
    private ViewPagerAdapter viewPagerAdapter;
    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mContext = this;

        initData();
        initView();
        initEvents();
    }

    private void initData() {
        mList_title.add("可使用");
        mList_title.add("已失效");

        CardFragment effectiveFragment = new CardFragment();
        effectiveFragment.setStatus(1);
        mList_fragment.add(effectiveFragment);
        CardFragment invalidFragment = new CardFragment();
        invalidFragment.setStatus(0);
        mList_fragment.add(invalidFragment);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("卡包");

        tabLayout = findViewById(R.id.tab_layout_card);
        viewPager = findViewById(R.id.view_pager_card);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setViewPager(viewPager);
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
