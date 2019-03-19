package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.GlideLoader;

import java.util.ArrayList;

import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;

/**
 * 引导页
 */
public class GuideActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, OnPageClickListener {

    private TextView tv_root;
    private InfiniteIndicator infiniteIndicator;

    private ImageView iv_one;
    private ImageView iv_two;
    private ImageView iv_three;

    private GuideActivity mContext;

    private ArrayList<Page> pageArrayList = new ArrayList<>();

    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        initData();
        initView();
        initEvents();
    }

    private void initData() {
        pageArrayList.add(new Page("one", R.drawable.image_guide_one, this));
        pageArrayList.add(new Page("two", R.drawable.image_guide_two, this));
        pageArrayList.add(new Page("three", R.drawable.image_guide_three, this));
    }

    private void initEvents() {
        tv_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences firstSP = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = firstSP.edit();
                editor.putBoolean("main", false);
                editor.apply();

                startActivity(new Intent(GuideActivity.this, NewMainActivity.class));
                finish();
            }
        });
    }

    private void initView() {
        tv_root = findViewById(R.id.tv_root_guide);
        infiniteIndicator = findViewById(R.id.infinite_indicator_guide);
        IndicatorConfiguration ic = new IndicatorConfiguration.Builder()
                .internal(Integer.MAX_VALUE)
                .isLoop(false)
                .isStopWhileTouch(true)
                .isAutoScroll(false)
                .onPageChangeListener(this)
                .onPageClickListener(this)
                .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                .imageLoader(new GlideLoader())
                .build();
        infiniteIndicator.init(ic);
        infiniteIndicator.notifyDataChange(pageArrayList);

        iv_one = findViewById(R.id.iv_guide_one);
        iv_two = findViewById(R.id.iv_guide_two);
        iv_three = findViewById(R.id.iv_guide_three);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.mPosition = position;
        initIndicatorBg();
        if (position == 0) {
            iv_one.setImageResource(R.drawable.shape_round_selected);
        } else if (position == 1) {
            iv_two.setImageResource(R.drawable.shape_round_selected);
        } else {
            iv_three.setImageResource(R.drawable.shape_round_selected);
        }
        if (position == pageArrayList.size() - 1) {
            tv_root.setVisibility(View.VISIBLE);
        } else {
            tv_root.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化
     */
    private void initIndicatorBg() {
        iv_one.setImageResource(R.drawable.shape_round);
        iv_two.setImageResource(R.drawable.shape_round);
        iv_three.setImageResource(R.drawable.shape_round);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageClick(int position, Page page) {

    }
}
