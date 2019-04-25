package com.dace.textreader.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.WordTagAdapter;
import com.dace.textreader.bean.ExplainWord;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.ClipRevealFrame;
import com.hhl.library.FlowTagLayout;
import com.hhl.library.OnTagSelectListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.shuyu.action.web.ActionSelectListener;
import com.shuyu.action.web.CustomActionWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 炸词解释
 */
public class WordExplainActivity extends BaseActivity {

    //现代文划线炸词
    private final String url = HttpUrlPre.HTTP_URL + "/xiandaiwen/bomb";
    //现代文双击炸词
    private final String doubleUrl = HttpUrlPre.HTTP_URL + "/xiandaiwen/double/bomb";
    //古文炸词
    private final String poetryUrl = HttpUrlPre.HTTP_URL + "/guwen/bomb";
    //词语解释
    private final String wordUrl = HttpUrlPre.HTTP_URL + "/xiandaiwen?";
    //古文词语解释
    private final String poetryWordUrl = HttpUrlPre.HTTP_URL + "/guwen/innotation?";

    //统计用户阅读点击行为（异步请求）
    private final String updateUrl = HttpUrlPre.HTTP_URL + "/statistics/huaci/update?id=";
    //再炸
    private final String nextUrl = HttpUrlPre.HTTP_URL + "/bomb/next";

    //添加生词到生词本
    private final String insertGlossaryUrl = HttpUrlPre.HTTP_URL + "/personal/word/insert?";
    //添加摘抄
    private final String addExcerptUrl = HttpUrlPre.HTTP_URL + "/personal/summary/insert";

    private RelativeLayout rl_back;  //返回按钮
    private FrameLayout frameLayout;
    private ScrollView scrollView;
    private FlowTagLayout flowTag;
    private CustomActionWebView webView;
    private SwitchButton switchButton;
    private LinearLayout ll_no_content;
    private ImageView iv_no_content;

    private LinearLayout ll_operate;
    private LinearLayout ll_show;
    private LinearLayout ll_add;

    private WordTagAdapter tagAdapter;
    //炸词
    private List<ExplainWord> mList = new ArrayList<>();
    //单个的字
    private List<ExplainWord> list = new ArrayList<>();
    private JSONArray array;

    //划词
    private String word;
    private int type;  //文章类型，0表示现代文，1表示古文
    private int readId;  //阅读ID，用来统计用户的点击词语解释的行为
    private int bomb;  //炸词方式，0表示双击炸词，1表示划线炸词

    private String glossaryTitle = "";
    private long glossaryId = -1;
    private int glossaryType = -1;
    private int glossarySourceType = -1;

    //自定义Item
    private List<String> actionItemList = new ArrayList<>();

    private ClipRevealFrame clipRevealFrame;

    private final String punctuationStr = "[\\\"（*%&@#$）〝〞，。？！：；·…,.?!:;、……“”‘’()【】＜＞<>》\\\\d+\\\\w+《＝ /-》]+\"";

    //点击的词语
    private String word_update = "";
    private long word_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_explain);

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        flowTag.setOnTagSelectListener(onTagSelectListener);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                word_update = "";
                clipRevealFrame.addOnLayoutChangeListener(onLayoutChangeListener);
                webView.loadDataWithBaseURL("about:blank", getHtmlData(""),
                        "text/html", "utf-8", null);
                if (ll_no_content.getVisibility() == View.VISIBLE) {
                    ll_no_content.setVisibility(View.GONE);
                }
                if (isChecked) {
                    if (list.size() == 0) {
                        showLoadingView(true);
                        new GetNextTagData(WordExplainActivity.this).execute(nextUrl);
                    } else {
                        showWords(isChecked);
                    }
                } else {
                    showWords(isChecked);
                }
            }
        });
        webView.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                if (s.equals("摘抄")) {
                    addExcerpt(s1);
                } else if (s.equals("复制")) {
                    DataUtil.copyContent(WordExplainActivity.this, s1);
                } else {
                    Intent intent = new Intent(WordExplainActivity.this, WordExplainActivity.class);
                    intent.putExtra("readId", readId);
                    intent.putExtra("word", s1);
                    intent.putExtra("type", 0);
                    intent.putExtra("bomb", 1);

                    intent.putExtra("glossarySourceType", 2);
                    intent.putExtra("glossaryId", word_id);
                    intent.putExtra("glossaryType", -1);
                    intent.putExtra("glossaryTitle", word_update);
                    startActivity(intent);
                }
            }
        });
        ll_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID != -1) {
                    startActivity(new Intent(WordExplainActivity.this, GlossaryActivity.class));
                } else {
                    turnToLogin();
                }
            }
        });
        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID != -1) {
                    if (word_update.equals("")) {
                        showTips("还未选中词语");
                    } else {
                        new AddGlossary(WordExplainActivity.this)
                                .execute(insertGlossaryUrl +
                                        "word=" + word_update +
                                        "&studentId=" + NewMainActivity.STUDENT_ID +
                                        "&essayId=" + glossaryId +
                                        "&title=" + glossaryTitle +
                                        "&sourceType=" + glossarySourceType +
                                        "&type=" + glossaryType);
                    }
                } else {
                    turnToLogin();
                }
            }
        });
    }

    /**
     * Loading状态
     *
     * @param showLading
     */
    private void showLoadingView(boolean showLading) {
        if (isDestroyed()) {
            return;
        }
        if (showLading) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_author_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
            GlideUtils.loadGIFImageWithNoOptions(this, R.drawable.image_loading, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private OnTagSelectListener onTagSelectListener = new OnTagSelectListener() {
        @Override
        public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {

            int position = 0;

            if (selectedList.size() > 0) {
                position = selectedList.get(0);
            } else {
                return;
            }

            String w = (String) parent.getAdapter().getItem(position);
            if (w.equals(word_update)) {
                return;
            } else {
                word_update = w;
            }

            if (punctuationStr.contains(word_update)) {
                word_update = "";
            } else {
                if (ll_no_content.getVisibility() == View.VISIBLE) {
                    ll_no_content.setVisibility(View.GONE);
                }
                if (type == 0) {
                    new GetWordData(WordExplainActivity.this)
                            .execute(wordUrl + "word=" + word_update);
                } else {
                    new GetWordData(WordExplainActivity.this)
                            .execute(poetryWordUrl + "word=" + word_update +
                                    "&title=" + glossaryTitle);
                }
            }
        }
    };

    /**
     * 添加摘抄
     *
     * @param excerpt 摘抄的内容
     */
    private void addExcerpt(String excerpt) {
        new AddExcerpt(WordExplainActivity.this).execute(addExcerptUrl, excerpt,
                String.valueOf(word_id), String.valueOf(-1), word_update,
                String.valueOf(2));
    }

    //显示炸词结果
    private void showWords(boolean isChecked) {
        webView.clearHistory();
        List<String> l = new ArrayList<>();
        l.clear();
        if (isChecked) {
            tagAdapter.clear();
            for (int i = 0; i < list.size(); i++) {
                l.add(list.get(i).getWord());
            }
        } else {
            tagAdapter.clear();
            for (int i = 0; i < mList.size(); i++) {
                l.add(mList.get(i).getWord());
            }
        }
        tagAdapter.onlyAddAll(l);
        flowTag.clearAllOption();
    }

    private void initData() {
        showLoadingView(true);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                word = uri.getQueryParameter("id");
            }
        } else {
            word = getIntent().getStringExtra("word");
        }
        readId = getIntent().getIntExtra("readId", -1);
        type = getIntent().getIntExtra("type", 0);
        bomb = getIntent().getIntExtra("bomb", 0);

        glossarySourceType = getIntent().getIntExtra("glossarySourceType", -1);
        glossaryId = getIntent().getLongExtra("glossaryId", -1);
        glossaryType = getIntent().getIntExtra("glossaryType", -1);
        glossaryTitle = getIntent().getStringExtra("glossaryTitle");

        if (type == 0) {  //现代文炸词
            if (bomb == 0) {  //双击炸词
                new GetTagData(WordExplainActivity.this).execute(doubleUrl);
            } else {  //划线炸词
                new GetTagData(WordExplainActivity.this).execute(url);
            }
        } else {  //古文炸词
            new GetTagData(WordExplainActivity.this).execute(poetryUrl);
        }
    }

    /**
     * 获取数据
     */
    private static class GetTagData
            extends WeakAsyncTask<String, Integer, String, WordExplainActivity> {

        protected GetTagData(WordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("sentence", activity.word);
                if (activity.type == 1) {
                    json.put("title", activity.glossaryTitle);
                }
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WordExplainActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorConnection();
            } else {
                //获取数据之后
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    activity.array = jsonArray;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ExplainWord explainWord = new ExplainWord();
                        explainWord.setWord(jsonArray.getString(i));
                        activity.mList.add(explainWord);
                    }
                    activity.showWords(false);
                    activity.showLoadingView(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.errorConnection();
                }
            }
        }
    }

    /**
     * 获取词语解释
     */
    private static class GetWordData
            extends WeakAsyncTask<String, Integer, String, WordExplainActivity> {

        protected GetWordData(WordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WordExplainActivity activity, String s) {
            if (s == null) {
                activity.noWordExplain();
            } else {  //获取数据之后
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        JSONObject json = jsonObject.getJSONObject("data");
                        activity.word_id = json.optLong("id", -1);
                        String content = json.getString("annotation");
                        activity.showExplain(content);
                    } else {
                        activity.noWordExplain();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.noWordExplain();
                }
            }
        }
    }

    /**
     * 获取信息失败
     */
    private void errorConnection() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, getHtmlData(""),
                    "text/html", "utf-8", null);
        }
        View errorView = LayoutInflater.from(this)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                initData();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 暂无词语解释
     */
    private void noWordExplain() {
        MyToastUtil.showToast(this, "暂无该词语解释");
        if (webView != null) {
            webView.loadDataWithBaseURL(null, getHtmlData(""),
                    "text/html", "utf-8", null);
        }
        frameLayout.setVisibility(View.GONE);
        ll_no_content.setVisibility(View.VISIBLE);
    }

    /**
     * 词语解释
     *
     * @param content
     */
    private void showExplain(String content) {
        ll_no_content.setVisibility(View.GONE);
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
            webView.loadDataWithBaseURL("about:blank", getHtmlData(content),
                    "text/html", "utf-8", null);
        }
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.smoothScrollTo(0, ll_operate.getBottom()
                        + DensityUtil.dip2px(WordExplainActivity.this, 20));
            }
        });
        if (NewMainActivity.STUDENT_ID != -1 && readId != -1) {
            new UpdateTagData(WordExplainActivity.this)
                    .execute(updateUrl + readId + "&word=" + word_update);
        }
    }

    /**
     * 再炸
     */
    private static class GetNextTagData
            extends WeakAsyncTask<String, Integer, String, WordExplainActivity> {

        protected GetNextTagData(WordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, activity.array.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WordExplainActivity activity, String s) {
            if (s == null) {
                activity.errorConnection();
            } else {
                //获取数据之后
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String data = jsonObject.getString("data");
                    String[] strings = data.split(",");
                    for (int i = 0; i < strings.length; i++) {
                        ExplainWord explainWord = new ExplainWord();
                        if (i == 0) {
                            explainWord.setWord(strings[i].split("\\[")[1]);
                        } else if (i == strings.length - 1) {
                            explainWord.setWord(strings[i].split("]")[0]);
                        } else {
                            explainWord.setWord(strings[i]);
                        }
                        explainWord.setWord(explainWord.getWord().substring(1, 2));
                        activity.list.add(explainWord);
                    }
                    activity.showWords(true);
                    activity.showLoadingView(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.errorConnection();
                }
            }
        }
    }

    //获取完整的Html源码
    private String getHtmlData(String bodyHtml) {
        String head = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"Generator\" content=\"EditPlus®\">\n" +
                "  <meta name=\"Author\" content=\"\">\n" +
                "  <meta name=\"Keywords\" content=\"\">\n" +
                "  <meta name=\"Description\" content=\"\">\n" +
                "  <title>Document</title>";
        String style = "<style> body{background:#ffffff;line-height: 1.5;  " +
                "font-family:华文细黑;font-size:1.0rem; padding:1em 1em 1em 1em;" +
                " text-align:justify;text-justify:distribute;" +
                "    -webkit-text-size-adjust: none;" +
                "    word-wrap: break-word;" +
                "} " +
                "ul{margin-left:-15px; line-height: 1.2;} " +
                "li{line-height: 1.5;} " +
                "dd{margin-left:20px;}" +
                "</style>";
        String body = "</head>\n" +
                " <body>";
        String over = "</body>" +
                "</html>";
        return head + style + body + bodyHtml + over;
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_word_explain);
        frameLayout = findViewById(R.id.frame_word_explain);
        scrollView = findViewById(R.id.scroll_view_word_explain);
        clipRevealFrame = findViewById(R.id.clip_reveal_frame);
        clipRevealFrame.addOnLayoutChangeListener(onLayoutChangeListener);

        switchButton = findViewById(R.id.switch_button_word_explain);

        flowTag = findViewById(R.id.flow_tag_word_explain);
        tagAdapter = new WordTagAdapter<>(this);
        flowTag.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        flowTag.setAdapter(tagAdapter);

        webView = findViewById(R.id.web_view_word);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.linkJSInterface();
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setHorizontalScrollBarEnabled(false);//禁止水平滚动
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        actionItemList.add("炸词");
        actionItemList.add("摘抄");
        actionItemList.add("复制");
        webView.setActionList(actionItemList);

        ll_no_content = findViewById(R.id.ll_no_content_word_explain);
        iv_no_content = findViewById(R.id.iv_no_content_word_explain);
        GlideUtils.loadImageWithNoOptions(this, R.drawable.image_state_empty, iv_no_content);

        ll_operate = findViewById(R.id.ll_operate_word_explain);
        ll_show = findViewById(R.id.ll_show_glossary_word_explain);
        ll_add = findViewById(R.id.ll_add_glossary_word_explain);
    }

    //画面发生改变时的动画
    private View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            float radius = (float) Math.hypot(right, bottom);
            Animator reveal = createCheckoutRevealAnimator((ClipRevealFrame) v,
                    right, top, 28f, radius);
            reveal.start();
        }
    };

    /**
     * 统计用户阅读点击行为（异步请求）
     */
    private static class UpdateTagData
            extends WeakAsyncTask<String, Integer, String, WordExplainActivity> {

        protected UpdateTagData(WordExplainActivity wordExplainActivity) {
            super(wordExplainActivity);
        }

        @Override
        protected String doInBackground(WordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WordExplainActivity wordExplainActivity, String s) {

        }
    }

    /**
     * 添加生词本
     */
    private static class AddGlossary
            extends WeakAsyncTask<String, Integer, String, WordExplainActivity> {

        protected AddGlossary(WordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WordExplainActivity activity, String s) {
            //获取数据之后
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                activity.analyzeAddExcerptData(s);
            }
        }
    }

    /**
     * 分析添加到生词本数据
     */
    private void analyzeAddExcerptData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                showTips("添加成功");
            } else if (400 == json.optInt("status", -1)) {
                if (s.contains("该词已在生词本中")) {
                    showTips("该词已在生词本中");
                } else {
                    showTips("添加生词失败");
                }
            } else {
                showTips("添加生词失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("添加生词失败");
        }
    }

    /**
     * 添加摘抄内容
     */
    private static class AddExcerpt
            extends WeakAsyncTask<String, Integer, String, WordExplainActivity> {

        protected AddExcerpt(WordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("summary", params[1]);
                json.put("essayId", params[2]);
                json.put("type", params[3]);
                json.put("title", params[4]);
                json.put("sourceType", params[5]);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WordExplainActivity activity, String s) {
            if (s != null && s.contains("摘抄成功")) {
                MyToastUtil.showToast(activity, "摘抄成功");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    protected Animator createCheckoutRevealAnimator(final ClipRevealFrame view, int x, int y, float startRadius, float endRadius) {
        Animator retAnimator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            retAnimator = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            view.setClipRadius(startRadius);
            retAnimator = ObjectAnimator.ofFloat(view, "clipRadius", startRadius, endRadius);
        }
        retAnimator.setDuration(850L);
        retAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setClipOutLines(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        retAnimator.setInterpolator(new AccelerateInterpolator(2.0f));
        return retAnimator;
    }

}
