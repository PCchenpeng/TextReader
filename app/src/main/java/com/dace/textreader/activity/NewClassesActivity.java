package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.HighSchoolFragment;
import com.dace.textreader.fragment.JuniorHighSchoolFragment;
import com.dace.textreader.fragment.PrimarySchoolFragment;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 课内文章
 */
public class NewClassesActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private RelativeLayout ll_search;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private NewClassesActivity mContext;

    private List<Fragment> mList = new ArrayList<>();

    public static int GRADE_CODE = -1;  //年级代码
    private PrimarySchoolFragment primarySchoolFragment;
    private JuniorHighSchoolFragment juniorHighSchoolFragment;
    private HighSchoolFragment highSchoolFragment;

    private PagerAdapter adapter;

    private List<String> titles = new ArrayList<>();

    private int[] images = new int[]{R.drawable.icon_primary_school,
            R.drawable.icon_junior_high_school, R.drawable.icon_high_school};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_classes);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.StatusBarLightMode(this, flag);

        initData();
        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        ll_search.setOnClickListener(this);
    }

    private void initData() {

        primarySchoolFragment = new PrimarySchoolFragment();
        mList.add(primarySchoolFragment);
        juniorHighSchoolFragment = new JuniorHighSchoolFragment();
        mList.add(juniorHighSchoolFragment);
        highSchoolFragment = new HighSchoolFragment();
        mList.add(highSchoolFragment);

        adapter = new PagerAdapter(mContext.getSupportFragmentManager());

        titles.add("小学");
        titles.add("初中");
        titles.add("高中");

        GRADE_CODE = DataUtil.gradeId2Code(NewMainActivity.GRADE_ID);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        tv_page_title.setText("在线课本");
        ll_search = findViewById(R.id.ll_search_toolbar_new_classes);

        viewPager = findViewById(R.id.viewPager_new_classes);
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tab_sliding_new_classes);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabStatus(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabStatus(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (GRADE_CODE > -1 && GRADE_CODE < 3) {
            viewPager.setCurrentItem(GRADE_CODE);  //跳转到用户对应的年级页面
        }
    }

    private void changeTabStatus(TabLayout.Tab tab, boolean selected) {
        View view = tab.getCustomView();
        ImageView imgTitle = view.findViewById(R.id.iv_icon_tab_layout);
        TextView txtTitle = view.findViewById(R.id.tv_title_tab_layout);
        imgTitle.setVisibility(View.VISIBLE);
        if (selected) {
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            txtTitle.setTextColor(Color.parseColor("#333333"));
        } else {
            view.setBackgroundColor(Color.parseColor("#F1F1F1"));
            txtTitle.setTextColor(Color.parseColor("#999999"));
        }
    }

    public View getTabView(final int position) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_tab_layout_view, null);
        TextView txtTitle = view.findViewById(R.id.tv_title_tab_layout);
        ImageView imgTitle = view.findViewById(R.id.iv_icon_tab_layout);
        imgTitle.setImageResource(images[position]);
        txtTitle.setText(titles.get(position));
        if (position == 0) {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            txtTitle.setTextColor(Color.parseColor("#333333"));
        } else {
            view.setBackgroundColor(Color.parseColor("#f1f1f1"));
            txtTitle.setTextColor(Color.parseColor("#999999"));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(position);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.ll_search_toolbar_new_classes:
                turnToSearchView();
                break;
        }
    }

    /**
     * 前往搜索界面
     */
    private void turnToSearchView() {
        Intent intent = new Intent(NewClassesActivity.this, SearchActivity.class);
        intent.putExtra("isClasses", true);
        startActivity(intent);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        }

    }

}
