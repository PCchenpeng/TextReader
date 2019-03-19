package com.dace.textreader.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SearchHistoryBean;
import com.dace.textreader.fragment.SearchArticleFragment;
import com.dace.textreader.fragment.SearchHistoryFragment;
import com.dace.textreader.fragment.SearchWordsFragment;
import com.dace.textreader.fragment.SearchWritingFragment;
import com.dace.textreader.util.JsonParser;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.SpeechRecognizerUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.tab.SmartTabLayout;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合搜索
 */
public class SearchComplexActivity extends BaseActivity {

    private RelativeLayout rl_root;
    private EditText et_search;
    private TextView tv_search;
    private ImageView iv_clear;

    private FrameLayout frameLayout;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;

    private LinearLayout ll_voice;
    private ImageView iv_voice;
    private TextView tv_voice;

    private SearchComplexActivity mContext;

    private FragmentManager fm;
    private SearchHistoryFragment historyFragment;

    private List<Fragment> mList_fragment = new ArrayList<>();
    private List<String> mList_title = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;
    private SearchWordsFragment wordsFragment;
    private SearchWritingFragment writingFragment;
    private SearchArticleFragment articleFragment;
//    private SearchUserFragment userFragment;
//    private SearchEventsFragment eventsFragment;
//    private SearchLessonFragment lessonFragment;

    private String mSearchContent = "";

    private boolean isWordsExist = false;  //搜索历史是否存在

    // 语音听写对象
    private SpeechRecognizerUtil speechRecognizerUtil;
    private boolean isInitSpeechSuccess = true;  //初始化语音输入成功
    private boolean isSpeeching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_complex);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        initData();
        initView();
        initEvents();

        fm = getSupportFragmentManager();
        historyFragment = new SearchHistoryFragment();
        historyFragment.setOnWordsClickListen(new SearchHistoryFragment.OnWordsClickListen() {
            @Override
            public void onClick(String words) {
                mSearchContent = words;
                et_search.setText(words);
                et_search.setSelection(words.length());
                searchInfo();
                frameLayout.setVisibility(View.GONE);
                frameLayout.removeAllViews();
                hideInputMethod();
            }
        });
        fm.beginTransaction().add(R.id.frame_search_complex, historyFragment, "history").commit();

        speechRecognizerUtil = SpeechRecognizerUtil.getInstance();
        speechRecognizerUtil.init(mContext, mInitListener);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                isInitSpeechSuccess = false;
            }
        }
    };

    private void initEvents() {
        rl_root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //比较Activity根布局与当前布局的大小
                        int heightDiff = rl_root.getRootView().getHeight() - rl_root.getHeight();

                        //大小小于200时，为不显示虚拟键盘或虚拟键盘隐藏
                        if (heightDiff > 200 && isInitSpeechSuccess) {
                            showVoiceInput(true);
                        } else {
                            showVoiceInput(false);
                        }
                    }
                });
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOrNot();
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.isEmpty()) {
                    tv_search.setText("取消");
                    iv_clear.setVisibility(View.INVISIBLE);
                } else {
                    tv_search.setText("搜索");
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        if (!et_search.getText().toString().trim().isEmpty()) {
                            mSearchContent = et_search.getText().toString();
                            saveSearchHistory();
                            searchInfo();
                            frameLayout.setVisibility(View.GONE);
                            frameLayout.removeAllViews();
                            hideInputMethod();
                        }
                        break;
                }
                return true;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                hideInputMethod();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        ll_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                            } else {
                                startSpeech();
                            }
                        } else {
                            startSpeech();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isSpeeching) {
                            stopSpeech();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 显示语音输入
     *
     * @param show
     */
    private void showVoiceInput(boolean show) {
        if (show) {
            ll_voice.setVisibility(View.VISIBLE);
        } else {
            ll_voice.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mList_title.add("词堆");
        mList_title.add("作文");
        mList_title.add("阅读");
//        mList_title.add("微课");
//        mList_title.add("用户");
//        mList_title.add("活动");

        wordsFragment = new SearchWordsFragment();
        mList_fragment.add(wordsFragment);
        writingFragment = new SearchWritingFragment();
        mList_fragment.add(writingFragment);
        articleFragment = new SearchArticleFragment();
        mList_fragment.add(articleFragment);
//        lessonFragment = new SearchLessonFragment();
//        mList_fragment.add(lessonFragment);
//        userFragment = new SearchUserFragment();
//        mList_fragment.add(userFragment);
//        eventsFragment = new SearchEventsFragment();
//        mList_fragment.add(eventsFragment);
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_search_complex);
        et_search = findViewById(R.id.et_search);
        tv_search = findViewById(R.id.tv_searchOrNot);
        iv_clear = findViewById(R.id.iv_clear_search);
        iv_clear.setVisibility(View.INVISIBLE);
        et_search.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        frameLayout = findViewById(R.id.frame_search_complex);
        tabLayout = findViewById(R.id.tab_layout_search_complex);
        viewPager = findViewById(R.id.view_pager_search_complex);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setViewPager(viewPager);

        ll_voice = findViewById(R.id.ll_voice_input_search_complex);
        iv_voice = findViewById(R.id.iv_voice_search_complex);
        tv_voice = findViewById(R.id.tv_voice_search_complex);
    }

    /**
     * 搜索或者取消搜索
     */
    private void searchOrNot() {
        if (tv_search.getText().toString().equals("搜索")) {
            mSearchContent = et_search.getText().toString();
            if (!TextUtils.isEmpty(mSearchContent)) {
                saveSearchHistory();
                searchInfo();
                frameLayout.setVisibility(View.GONE);
                frameLayout.removeAllViews();
                hideInputMethod();
            }
        } else {
            finish();
        }
    }

    /**
     * 保存搜索历史
     */
    private void saveSearchHistory() {
        isWordsExist = false;
        LitePal.findAllAsync(SearchHistoryBean.class).listen(new FindMultiCallback<SearchHistoryBean>() {
            @Override
            public void onFinish(List<SearchHistoryBean> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getWords().equals(mSearchContent)) {
                        isWordsExist = true;
                        break;
                    }
                }
                if (!isWordsExist) {
                    SearchHistoryBean bean = new SearchHistoryBean();
                    bean.setWords(mSearchContent);
                    bean.save();
                }
            }
        });
    }

    /**
     * 搜索
     */
    private void searchInfo() {
        if (wordsFragment != null) {
            wordsFragment.setSearchContent(mSearchContent);
        }
        if (writingFragment != null) {
            writingFragment.setSearchContent(mSearchContent);
        }
        if (articleFragment != null) {
            articleFragment.setSearchContent(mSearchContent);
        }
//        if (lessonFragment != null) {
//            lessonFragment.setSearchContent(mSearchContent);
//        }
//        if (userFragment != null) {
//            userFragment.setSearchContent(mSearchContent);
//        }
//        if (eventsFragment != null) {
//            eventsFragment.setSearchContent(mSearchContent);
//        }
    }

    /**
     * 检查用户权限
     */
    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }
        }
    }

    /**
     * 开始听写
     */
    private void startSpeech() {
        isSpeeching = true;
        iv_voice.setImageResource(R.drawable.icon_write_bottom_voice_pause);
        tv_voice.setText("正在语音识别...");

        int ret = speechRecognizerUtil.startVoice(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败");
        } else {
            showTip("正在听写");
        }
    }

    /**
     * 停止听写
     */
    private void stopSpeech() {
        isSpeeching = false;
        iv_voice.setImageResource(R.drawable.icon_write_bottom_voice_input);
        tv_voice.setText("按住说话搜索内容");
        speechRecognizerUtil.stopVoice();
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        }

        @Override
        public void onError(SpeechError error) {
            if (error.getErrorCode() == 10118) {
                showTip("您好像没有说话哦~");
                checkUserPermission();
            } else if (error.getErrorCode() == 20001) {
                showTip("请检查网络是否连接~");
            }
            stopSpeech();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            stopSpeech();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            showResult(results.getResultString());
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
        }
    };

    /**
     * 显示听写结果
     *
     * @param resultString
     */
    private void showResult(String resultString) {
        String text = JsonParser.parseIatResult(resultString);
        Editable editable = et_search.getEditableText();
        int index = et_search.getSelectionStart();
        if (index < 0 || index >= editable.length()) {
            editable.append(text);
        } else {
            editable.insert(index, text);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (!verifyPermissions(grantResults)) {
                showTip("录音权限被拒绝");
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
    private void showTip(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 隐藏输入法
     */
    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) et_search.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_search.getApplicationWindowToken(), 0);
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

    @Override
    protected void onDestroy() {

        if (speechRecognizerUtil != null) {
            speechRecognizerUtil.release();
        }

        super.onDestroy();
    }
}
