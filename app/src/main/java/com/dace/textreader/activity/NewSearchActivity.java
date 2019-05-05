package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.adapter.SearchTestAdapter;
import com.dace.textreader.bean.HotSearchBean;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.TestSearchBean;
import com.dace.textreader.util.AnimationUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.JsonParser;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.SpeechRecognizerUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.LineWrapLayout;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;

public class NewSearchActivity extends BaseActivity implements View.OnClickListener{


    private ImageView iv_talk,iv_anim,iv_close,iv_playpause,iv_back,iv_playpause_small;
    private TextView tv_test;
    private LineWrapLayout lineWrapLayout;
    private LinearLayout ll_search_default,ll_talk_small,search_bottom;
    private RelativeLayout ll_search,rl_root;
    private ListView lv_test;
    private EditText et_search;
    // 语音听写对象
    private SpeechRecognizerUtil speechRecognizerUtil;
    private final static int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;

    private SearchTestAdapter searchTestAdapter;
    private boolean isSpeeching = false;
    private int randomId = -1;


    private String testUrl =HttpUrlPre.SEARCHE_URL +  "/search/select/search/tip/list";
    private String hotUrl = HttpUrlPre.SEARCHE_URL + "/search/select/search/word/list";
    private String searchUrl = HttpUrlPre.SEARCHE_URL + "/search/search/full/text";
    private Refreshpage resh = new Refreshpage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);

        // 使用SpeechRecognizer对象，可根据回调消息自定义界面
        speechRecognizerUtil = SpeechRecognizerUtil.getInstance();
        speechRecognizerUtil.init(this, mInitListener);

        checkUserPermission();



        initView();
        initData();
        initEvents();

    }

    private void initView() {
        rl_root = findViewById(R.id.ll_root);
        iv_talk = findViewById(R.id.iv_talk);
        tv_test = findViewById(R.id.tv_test);
        iv_anim = findViewById(R.id.iv_anim);
        iv_close = findViewById(R.id.iv_close);
        iv_playpause = findViewById(R.id.iv_playpause);
        iv_playpause_small = findViewById(R.id.iv_playpause_small);
        lineWrapLayout = findViewById(R.id.lineWrapLayout);
        ll_search = findViewById(R.id.ll_search);
        ll_talk_small = findViewById(R.id.ll_talk_small);
        iv_back = findViewById(R.id.iv_back);
        lv_test = findViewById(R.id.lv_test);
        search_bottom = findViewById(R.id.search_bottom);
        et_search = findViewById(R.id.et_search);

        iv_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_search.getVisibility() == View.VISIBLE){
                    ll_search.setVisibility(View.GONE);
                }else {
                    ll_search.setVisibility(View.VISIBLE);
//                    ll_search.setAnimation(AnimationUtil.moveToViewBottom());
                    ll_search.setAnimation(AnimationUtil.moveToViewLocation());
                }
            }
        });

        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(NewSearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String searchContext = et_search.getText().toString().trim();
                    if (TextUtils.isEmpty(searchContext)) {
                        showTips("请输入想要搜索的内容");
                    } else {
//                        searchResult(searchContext);
                        Intent intent = new Intent(NewSearchActivity.this,SearchResultActivity.class);
                        intent.putExtra("searchWord",searchContext);
                        startActivity(intent);
                    }
                }



                return false;
            }
        });

    }

    private void initData() {
        getTestData();
        getHotData();
        resh.handler.postDelayed(resh.runnable, 5000);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvents() {
        iv_back.setOnClickListener(this);
        iv_close.setOnClickListener(this);
//        iv_playpause.setOnClickListener(this);
        iv_talk.setOnClickListener(this);

//        rl_root.getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        //比较Activity根布局与当前布局的大小
//                        int heightDiff = rl_root.getRootView().getHeight() - rl_root.getHeight();
//
//                        if (heightDiff > 200) {
//                            showKeyboardOperate(true);
//                        } else {
//                            //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
//                            showKeyboardOperate(false);
//                        }
//                    }
//                });

        et_search.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                NewSearchActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight =  NewSearchActivity.this.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;

                if(heightDifference == 0){
                    showKeyboardOperate(false);
                }else {
                    showKeyboardOperate(true);
                }
            }

        });



        iv_playpause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(NewSearchActivity.this, Manifest.permission.RECORD_AUDIO)
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

        iv_playpause_small.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(NewSearchActivity.this, Manifest.permission.RECORD_AUDIO)
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


    /**
     * 显示或隐藏软键盘操作栏
     */
    private void showKeyboardOperate(boolean isKeyboardShow) {
        if (isKeyboardShow) {  //软件盘显示
            showOperate();
        } else {  //软键盘隐藏
            hideOperate();
        }
    }

    private void showOperate() {
        search_bottom.setVisibility(View.GONE);
        ll_talk_small.setVisibility(View.VISIBLE);

    }

    private void hideOperate() {
        ll_talk_small.setVisibility(View.GONE);
        search_bottom.setVisibility(View.VISIBLE);
    }

    private void getTestData() {

            JSONObject params = new JSONObject();
        try {
            params.put("randomId",randomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(testUrl, OkHttpManager.TYPE_POST_JSON, params,
                    new OkHttpManager.ReqCallBack<Object>() {
                        @Override
                        public void onReqSuccess(Object result) {
                            final TestSearchBean testSearchBean = GsonUtil.GsonToBean(result.toString(),TestSearchBean.class);
                            if(testSearchBean.getData() != null && testSearchBean.getData().getTipList().size() > 0){
                                final String firstText = testSearchBean.getData().getTipList().get(0).getTip();
                                tv_test.setText(firstText);
                                randomId = testSearchBean.getData().getRandomId();
                                tv_test.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(NewSearchActivity.this,SearchResultActivity.class);
                                        intent.putExtra("searchWord",firstText);
                                        startActivity(intent);
                                    }
                                });
                                searchTestAdapter = new SearchTestAdapter(NewSearchActivity.this,testSearchBean.getData().getTipList());
                                lv_test.setAdapter(searchTestAdapter);
                            }

                        }

                        @Override
                        public void onReqFailed(String errorMsg) {
                        }
                    });
    }

    private void getHotData() {

        JSONObject params = new JSONObject();
        try {
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("gradeId","131");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(hotUrl, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        final HotSearchBean hotSearchBean = GsonUtil.GsonToBean(result.toString(),HotSearchBean.class);

                        if(hotSearchBean.getData() != null){
                            for (int i = 0;i <hotSearchBean.getData().size();i++){
                                View child = View.inflate(NewSearchActivity.this,R.layout.item_search_hot,null);
                                TextView textView = child.findViewById(R.id.tv_num);
                                final String hotWord = hotSearchBean.getData().get(i).getWord();
                                textView.setText(hotSearchBean.getData().get(i).getWord());
                                lineWrapLayout.addView(child);

                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Toast.makeText(NewSearchActivity.this,hotWord,Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(NewSearchActivity.this,SearchResultActivity.class);
                                        intent.putExtra("searchWord",hotWord);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                    }
                });
    }

    private void searchResult(final String word){
        JSONObject params = new JSONObject();
        try {
            params.put("query",word);
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
                    Intent intent = new Intent(NewSearchActivity.this,SearchResultActivity.class);
                    intent.putExtra("searchResult",result.toString());
                    intent.putExtra("searchWord",word);
                    startActivity(intent);
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_close:
                if(ll_search.getVisibility() == View.VISIBLE){
                    ll_search.setVisibility(View.GONE);
                    ll_search.setAnimation(AnimationUtil.moveToViewBottom());
                }
                break;
            case R.id.iv_playpause:
                break;
            case R.id.iv_talk:
//                if(ll_search.getVisibility() == View.VISIBLE){
//                    ll_search.setVisibility(View.GONE);
//                }else {
                    ll_search.setVisibility(View.VISIBLE);
                    ll_search.setAnimation(AnimationUtil.moveToViewLocation());
//                }
                break;
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


    class Refreshpage {
         Handler handler = new Handler();
         Runnable runnable = new Runnable() {
             @Override
             public void run() {
                                 // 要做的事情
                 getTestData();
                 handler.postDelayed(this, 5000);
             }
         };
     }


    @Override
     public void onResume() {
         // 回到Activity的时候重新开始刷新；
         super.onResume();
//         resh.handler.postDelayed(resh.runnable, 5000);
     }

     @Override
     public void onPause() {
         // 离开Activity的时候停止刷新；
         super.onPause();
         resh.handler.removeCallbacks(resh.runnable);
     }

     @Override
     public void onDestroy() {
         super.onDestroy();
         resh.handler.removeCallbacks(resh.runnable);
     }

}
