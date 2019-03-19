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
import com.dace.textreader.fragment.UnReadCommentsFragment;
import com.dace.textreader.fragment.UnReadWritingCommentsFragment;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.tab.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 未读评论消息
 */
public class NewUnReadCommentsActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private ViewPager viewPager;
    private SmartTabLayout tabLayout;

    private ViewPagerAdapter viewPagerAdapter;
    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_un_read_comments);

        initData();
        initView();
        initEvents();

    }

    private void initData() {
        mList_title.add("作文");
        mList_title.add("文章");

        UnReadWritingCommentsFragment unReadWritingCommentsFragment = new UnReadWritingCommentsFragment();
        mList_fragment.add(unReadWritingCommentsFragment);
        UnReadCommentsFragment unReadCommentsFragment = new UnReadCommentsFragment();
        mList_fragment.add(unReadCommentsFragment);

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
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        tv_page_title.setText("回复我的");

        tabLayout = findViewById(R.id.tab_layout_new_un_read_comments);
        viewPager = findViewById(R.id.view_pager_new_un_read_comments);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
