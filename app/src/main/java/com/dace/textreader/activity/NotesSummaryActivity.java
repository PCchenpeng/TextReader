package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dace.textreader.R;
import com.dace.textreader.fragment.AfterReadingFragment;
import com.dace.textreader.fragment.ExcerptFragment;
import com.dace.textreader.fragment.GlossaryFragment;
import com.dace.textreader.fragment.GrammarFragment;
import com.dace.textreader.fragment.NoteFragment;
import com.dace.textreader.fragment.SentenceCollectionFragment;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.tab.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 写作--刻意练习
 */
public class NotesSummaryActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private ViewPager viewPager;
    private SmartTabLayout tabLayout;

    private NotesSummaryActivity mContext;
    private ViewPagerAdapter viewPagerAdapter;
    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_summary);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.LEFT).setDuration(300));
            getWindow().setExitTransition(new Slide(Gravity.RIGHT).setDuration(300));
        }

        initData();
        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("practiceType", 0);
        setResult(0, intent);
        super.onBackPressed();
    }

    private void initData() {
        mList_title.add("生词");
        mList_title.add("句子");
        mList_title.add("语法");
        mList_title.add("摘抄");
        mList_title.add("想法");
        mList_title.add("读后感");

        GlossaryFragment glossaryFragment = new GlossaryFragment();
        glossaryFragment.setShowPractice(true);
        mList_fragment.add(glossaryFragment);
        SentenceCollectionFragment sentenceFragment = new SentenceCollectionFragment();
        sentenceFragment.setShowPractice(true);
        mList_fragment.add(sentenceFragment);

        GrammarFragment grammarFragment = new GrammarFragment();
        mList_fragment.add(grammarFragment);

        ExcerptFragment excerptFragment = new ExcerptFragment();
        excerptFragment.setShowPractice(true);
        mList_fragment.add(excerptFragment);
        NoteFragment noteFragment = new NoteFragment();
        noteFragment.setAllNotes(true);
        noteFragment.setShowPractice(true);
        noteFragment.setEssayId(-1);
        mList_fragment.add(noteFragment);

        AfterReadingFragment afterReadingFragment = new AfterReadingFragment();
        mList_fragment.add(afterReadingFragment);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_notes_summary);

        tabLayout = findViewById(R.id.tab_layout_notes_summary);
        viewPager = findViewById(R.id.view_pager_notes_summary);
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
