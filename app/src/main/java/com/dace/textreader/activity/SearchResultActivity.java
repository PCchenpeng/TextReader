package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dace.textreader.R;
import com.dace.textreader.adapter.SearchAuthorAdapter;
import com.dace.textreader.bean.SearchResultBean;

import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.fragment.SearchAlbumFragment;
import com.dace.textreader.fragment.SearchAllFragment;
import com.dace.textreader.fragment.SearchArticleFragment;
import com.dace.textreader.fragment.SearchAuthorFragment;
import com.dace.textreader.fragment.SearchWordsFragment;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.JsonParser;
import com.dace.textreader.util.SpeechRecognizerUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.tab.SmartTabLayout;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//精准搜索
public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    private String searchUrl = HttpUrlPre.SEARCHE_URL + "/search/search/full/text";

    private String searchWord;
    private SearchResultBean searchResultBean;
    private LinearLayout ll_vague,ll_accurate;
    private ImageView iv_back,iv_cancle;
    private EditText et_search;

    private Context mContext;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();
    private ViewPagerAdapter adapter;
    private SearchAllFragment searchAllFragment;
    private SearchWordsFragment searchWordsFragment;
    private SearchAuthorFragment searchAuthorFragment;
    private SearchAlbumFragment searchAlbumFragment;
    private SearchArticleFragment searchArticleFragment;
    private SearchAllFragment searchAllFragment_accure;
    private SearchWordsFragment searchWordsFragment_accure;
    private SearchAuthorFragment searchAuthorFragment_accure ;
    private SearchAlbumFragment searchAlbumFragment_accure;
    private SearchArticleFragment searchArticleFragment_accure;
    private FragmentManager fragmentManager;

    private LinearLayout ll_talk_small;
    private ImageView iv_playpause_small;

    // 语音听写对象
    private SpeechRecognizerUtil speechRecognizerUtil;
    private final static int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    private boolean isSpeeching = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mContext = this;

        // 使用SpeechRecognizer对象，可根据回调消息自定义界面
        speechRecognizerUtil = SpeechRecognizerUtil.getInstance();
        speechRecognizerUtil.init(this, mInitListener);

        checkUserPermission();

        initView();
        initData();
        initEvents();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_cancle = findViewById(R.id.iv_cancle);
        et_search = findViewById(R.id.et_search);
        ll_accurate = findViewById(R.id.ll_accurate);
        ll_vague = findViewById(R.id.ll_vague);
        tabLayout = findViewById(R.id.tab_search_result);
        ll_talk_small = findViewById(R.id.ll_talk_small);
        iv_playpause_small = findViewById(R.id.iv_playpause_small);
        tabLayout.setChangeTextSize(false);
        viewPager = findViewById(R.id.viewpager_search_result);
        fragmentManager = getSupportFragmentManager();

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)){
                    iv_cancle.setVisibility(View.VISIBLE);
                } else {
                    iv_cancle.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData() {
        mList_title.add("综合");
        mList_title.add("字词");
        mList_title.add("作者");
        mList_title.add("专辑");
        mList_title.add("文章");

        searchWord = getIntent().getStringExtra("searchWord");
        et_search.setText(searchWord);
        searchResult(searchWord);
    }

    private void searchResult(final String searchWord) {
        JSONObject params = new JSONObject();
        try {
            params.put("query",searchWord);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("width",750);
            params.put("height",420);
            params.put("pageNum",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(searchUrl, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                SearchResultBean searchResultBean = GsonUtil.GsonToBean(result.toString(),SearchResultBean.class);
                if(searchResultBean != null){
                    if(searchResultBean.getStatus() == 200){
                        String ret_type = searchResultBean.getData().getRet_type();
                        if(ret_type.equals("vague")){
                            ll_vague.setVisibility(View.VISIBLE);
                            ll_accurate.setVisibility(View.GONE);
                            if(mList_fragment.size() > 0){
                                searchAllFragment.setData(searchResultBean);
                                for(int i=0;i<searchResultBean.getData().getRet_array().size();i++ ){
                                    int type = searchResultBean.getData().getRet_array().get(i).getType();
                                    List<SubListBean> subListBean = searchResultBean.getData().getRet_array().get(i).getSubList();
                                    if(type == 2){
                                        searchWordsFragment.setData(subListBean,searchWord);
                                    }else if(type == 3){
                                        searchAuthorFragment.setData(subListBean,searchWord);
                                    }else if(type == 4){
                                        searchAlbumFragment.setData(subListBean,searchWord);
                                    }else if(type == 5){
                                        searchArticleFragment.setData(subListBean,searchWord);
                                    }
                                }
                            }else {
                                searchAllFragment = SearchAllFragment.newInstance(result.toString(),searchWord,false);
                                searchWordsFragment = SearchWordsFragment.newInstance(result.toString(),searchWord);
                                searchAuthorFragment = SearchAuthorFragment.newInstance(result.toString(),searchWord,false);
                                searchAlbumFragment = SearchAlbumFragment.newInstance(result.toString(),searchWord,false);
                                searchArticleFragment = SearchArticleFragment.newInstance(result.toString(),searchWord,false);

                                mList_fragment.add(searchAllFragment);
                                mList_fragment.add(searchWordsFragment);
                                mList_fragment.add(searchAuthorFragment);
                                mList_fragment.add(searchAlbumFragment);
                                mList_fragment.add(searchArticleFragment);

                                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                                viewPager.setAdapter(adapter);
                                viewPager.setOffscreenPageLimit(5);
                                tabLayout.setViewPager(viewPager);

                                searchAllFragment.setOnMoreClick(new SearchAllFragment.OnMoreClick() {
                                    @Override
                                    public void onClick(int type) {
                                        viewPager.setCurrentItem(type);
                                    }
                                });
                            }
                        }else {
                            ll_vague.setVisibility(View.GONE);
                            ll_accurate.setVisibility(View.VISIBLE);
                            switch (ret_type){
                                case "zici":
                                    searchWordsFragment_accure = SearchWordsFragment.newInstance(result.toString(),searchWord);
                                    fragmentManager.beginTransaction().replace(R.id.frame_accure, searchWordsFragment_accure, "note").commit();
                                    break;
                                case "author":
                                    searchAllFragment_accure = SearchAllFragment.newInstance(result.toString(),searchWord,true);
                                    fragmentManager.beginTransaction().replace(R.id.frame_accure, searchAllFragment_accure, "note").commit();
                                    break;
                                case "album":
                                    searchAlbumFragment_accure = SearchAlbumFragment.newInstance(result.toString(),searchWord,true);
                                    fragmentManager.beginTransaction().replace(R.id.frame_accure, searchAlbumFragment_accure, "note").commit();
                                    break;
                                case "article":
                                    searchArticleFragment_accure = SearchArticleFragment.newInstance(result.toString(),searchWord,true);
                                    fragmentManager.beginTransaction().replace(R.id.frame_accure, searchArticleFragment_accure, "note").commit();
                                    break;
                            }
                        }

                    }else if(searchResultBean.getStatus() == 400){

                    }
                }

            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvents() {
        iv_cancle.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(SearchResultActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchContext = et_search.getText().toString().trim();
                    if (TextUtils.isEmpty(searchContext)) {
                        showTips("请输入想要搜索的内容");
                    } else {
                        searchResult(searchContext);
                    }
                }
                return false;
            }
        });

        et_search.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                SearchResultActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight =  SearchResultActivity.this.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;

                if(heightDifference == 0){
                    ll_talk_small.setVisibility(View.GONE);
                }else {
                    ll_talk_small.setVisibility(View.VISIBLE);
                }
            }

        });

        iv_playpause_small.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(SearchResultActivity.this, Manifest.permission.RECORD_AUDIO)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                        REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                            } else {
                                startSpeech();
                            }
                        } else {
                            startSpeech();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopSpeech();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_cancle:
                et_search.setText("");
                break;
            default:
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


    /**
     * 开始听写
     */
    private void startSpeech() {
        isSpeeching = true;
        speechRecognizerUtil.setParams("1");

        int ret = speechRecognizerUtil.startVoice(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTips("听写失败");
        } else {
            showTips("正在听写");
        }
    }

    /**
     * 停止听写
     */
    private void stopSpeech() {
        speechRecognizerUtil.stopVoice();

        isSpeeching = false;
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTips("初始化失败");
            }
        }
    };

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
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
//            if (error.getErrorCode() == 14002) {
//                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
//            } else
            if (error.getErrorCode() == 10118) {
                showTips("您好像没有说话哦~");
                checkUserPermission();
            } else if (error.getErrorCode() == 20001) {
                showTips("请检查网络是否连接~");
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
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 显示听写结果
     *
     * @param resultString
     */
    private void showResult(String resultString) {
        String text = JsonParser.parseIatResult(resultString);
        Editable editable;
//        if (editHasFocus) {
        int index = et_search.getSelectionStart();
        editable = et_search.getEditableText();
        if (text.contains("。")) {
            text = text.replace("。", "");
        }
        if (text.contains("？")) {
            text = text.replace("？", "");
        }
        if (text.contains("！")) {
            text = text.replace("！", "");
        }
        if (index < 0 || index >= editable.length()) {
            editable.append(text);
        } else {
            editable.insert(index, text);
        }
//        } else {
//            richEditor.appendText(text);
//        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_CODE) {
            if (!verifyPermissions(grantResults)) {
                showTips("录音权限被拒绝");
            }
        }

//        else {
//            imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        }
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
     * 检查用户权限
     */
    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION_CODE);
            }
        }
    }


}
