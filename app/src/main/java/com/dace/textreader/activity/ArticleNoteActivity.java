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
import com.dace.textreader.fragment.ExcerptFragment;
import com.dace.textreader.fragment.GlossaryFragment;
import com.dace.textreader.fragment.HomeFragment;
import com.dace.textreader.fragment.NewAppreciationFragment;
import com.dace.textreader.fragment.NoteFragment;
import com.dace.textreader.fragment.RecommendFragment;
import com.dace.textreader.view.tab.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class ArticleNoteActivity extends BaseActivity implements View.OnClickListener {

    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();

    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private RelativeLayout rl_back;
    private String essayId;
    private TextView tv_edit;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articlenote);

        //修改状态栏的文字颜色为黑色
//        int flag = StatusBarUtil.StatusBarLightMode(this);
//        StatusBarUtil.StatusBarLightMode(this, flag);

//        mPlayer = new MediaPlayer();
        initData();
        initView();
        initEvents();

    }

    private void initData() {
        essayId = getIntent().getExtras().getString("essayId");

        mList_title.add("生词");
        mList_title.add("摘抄");
        mList_title.add("想法");
        mList_title.add("赏析");

        GlossaryFragment glossaryFragment = GlossaryFragment.newInstance(essayId,1);
        ExcerptFragment excerptFragment = ExcerptFragment.newInstance(essayId,1);
        NoteFragment noteFragment = NoteFragment.newInstance(essayId,1);
        NewAppreciationFragment newAppreciationFragment =  NewAppreciationFragment.newInstance(essayId);
        mList_fragment.add(glossaryFragment);
        mList_fragment.add(excerptFragment);
        mList_fragment.add(noteFragment);
        mList_fragment.add(newAppreciationFragment);
//        RecommendFragment recommendFragment = new RecommendFragment();
//        mList_fragment.add(recommendFragment);
//        HomeFragment homeFragment = new HomeFragment();
//        mList_fragment.add(homeFragment);
    }

    private void initView() {
        tabLayout = findViewById(R.id.tab_layout_new_reader_fragment);
        viewPager = findViewById(R.id.view_pager_new_reader_fragment);
        rl_back = findViewById(R.id.rl_back);
        tv_edit = findViewById(R.id.tv_edit);
        tv_edit.setText("编辑");
//        tv_edit.setVisibility(View.GONE);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                currentIndex = i;
                switch (currentIndex){
                    case 0:
                        GlossaryFragment glossaryFragment = (GlossaryFragment) mList_fragment.get(0);
                        if(glossaryFragment.getEditor()){
                            tv_edit.setText("完成");
                        }else {
                            tv_edit.setText("编辑");
                        }
                        break;
                    case 1:
                        ExcerptFragment excerptFragment = (ExcerptFragment) mList_fragment.get(1);
                        if(excerptFragment.getEditor()){
                            tv_edit.setText("完成");
                        }else {
                            tv_edit.setText("编辑");
                        }
                        break;
                    case 2:
                        NoteFragment noteFragment = (NoteFragment) mList_fragment.get(2);
                        if(noteFragment.getEditor()){
                            tv_edit.setText("完成");
                        }else {
                            tv_edit.setText("编辑");
                        }
                        break;
                    case 3:
                        NewAppreciationFragment newAppreciationFragment = (NewAppreciationFragment) mList_fragment.get(3);
                        if(newAppreciationFragment.getEditor()){
                            tv_edit.setText("完成");
                        }else {
                            tv_edit.setText("编辑");
                        }
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_edit:
                switch (currentIndex){
                    case 0:
                        GlossaryFragment glossaryFragment = (GlossaryFragment) mList_fragment.get(0);
                        if(glossaryFragment.getEditor()){
                            tv_edit.setText("编辑");
                        }else {
                            tv_edit.setText("完成");
                        }
                        glossaryFragment.setEditor();
                        break;
                    case 1:
                        ExcerptFragment excerptFragment = (ExcerptFragment) mList_fragment.get(1);
                        if(excerptFragment.getEditor()){
                            tv_edit.setText("编辑");
                        }else {
                            tv_edit.setText("完成");
                        }
                        excerptFragment.editorOpenOrClose();
                        break;

                    case 2:
                        NoteFragment noteFragment = (NoteFragment) mList_fragment.get(2);
                        if(noteFragment.getEditor()){
                            tv_edit.setText("编辑");
                        }else {
                            tv_edit.setText("完成");
                        }
                        noteFragment.editorOpenOrClose();
                        break;
                    case 3:
                        NewAppreciationFragment newAppreciationFragment = (NewAppreciationFragment) mList_fragment.get(3);
                        if(newAppreciationFragment.getEditor()){
                            tv_edit.setText("编辑");
                        }else {
                            tv_edit.setText("完成");
                        }
                        newAppreciationFragment.editorOpenOrClose();
                        break;
                }
                break;
        }
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
