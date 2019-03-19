package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.shuyu.action.web.ActionSelectListener;
import com.shuyu.action.web.CustomActionWebView;

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
 * 生词解释
 * 后台返回h5格式文本，用WebView展示
 */
public class GlossaryWordExplainActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/xiandaiwen?word=";
    //古文词语解释
    private final String poetryWordUrl = HttpUrlPre.HTTP_URL + "/guwen/innotation?";
    //添加摘抄
    private final String addExcerptUrl = HttpUrlPre.HTTP_URL + "/personal/summary/insert";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private CustomActionWebView webView;

    private GlossaryWordExplainActivity mContext;

    private List<String> actionList = new ArrayList<>();

    private String words = "";  //词语
    private String title = "";  //词语所在文章的标题

    private String glossaryTitle = "";
    private long glossaryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary_word_explain);

        mContext = this;

        words = getIntent().getStringExtra("words");
        title = getIntent().getStringExtra("essayTitle");

        glossaryTitle = getIntent().getStringExtra("glossaryTitle");
        glossaryId = getIntent().getIntExtra("glossaryId", -1);

        if (glossaryTitle.equals("")) {
            glossaryTitle = words;
        }

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
        webView.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                if (s.equals("摘抄")) {
                    addExcerpt(s1);
                } else if (s.equals("复制")) {
                    DataUtil.copyContent(mContext, s1);
                } else {
                    Intent intent = new Intent(mContext, WordExplainActivity.class);
                    intent.putExtra("words", true);
                    intent.putExtra("word", s1);
                    intent.putExtra("type", 0);
                    intent.putExtra("readId", -1);
                    intent.putExtra("bomb", 1);

                    intent.putExtra("glossarySourceType", 2);
                    intent.putExtra("glossaryId", -1);
                    intent.putExtra("glossaryType", -1);
                    intent.putExtra("glossaryTitle", words);

                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 添加摘抄
     *
     * @param excerpt 摘抄的内容
     */
    private void addExcerpt(String excerpt) {
        new AddExcerpt(mContext).execute(addExcerptUrl, excerpt, String.valueOf(glossaryId),
                glossaryTitle);
    }

    private void initData() {
        showLoadingView(true);
        if (title.equals("")) {
            new GetWordData(mContext).execute(url + words);
        } else {
            new GetWordData(mContext).execute(poetryWordUrl + "word=" + words + "&title=" + title);
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("词语解释");

        frameLayout = findViewById(R.id.frame_glossary_word_explain);

        webView = findViewById(R.id.web_view_glossary_word_explain);
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
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        actionList.add("炸词");
        actionList.add("摘抄");
        actionList.add("复制");
        webView.setActionList(actionList);
    }

    /**
     * 是否显示加载视图
     *
     * @param show
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_author_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
            GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    /**
     * 获取数据
     */
    private static class GetWordData
            extends WeakAsyncTask<String, Integer, String, GlossaryWordExplainActivity> {

        protected GetWordData(GlossaryWordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(GlossaryWordExplainActivity activity, String[] params) {
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
        protected void onPostExecute(GlossaryWordExplainActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                //获取数据之后
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 分析数据
     *
     * @param s 获取到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject json = jsonObject.getJSONObject("data");
                glossaryId = json.optLong("id", -1);
                String content = json.getString("annotation");
                showExplain(content);
            } else if (400 == jsonObject.optInt("status", -1)) {
                noContent();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        View errorView = LayoutInflater.from(mContext)
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
    private void noContent() {
        if (isDestroyed()) {
            return;
        }
        MyToastUtil.showToast(mContext, "暂无词语解释");
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
        tv_tips.setText("抱歉，暂无该词语解释\n后续版本派知为亲努力更新~");
        tv_reload.setVisibility(View.GONE);
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
//        if (webView != null) {
//            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
//            webView.clearHistory();
//
//            ((ViewGroup) webView.getParent()).removeView(webView);
//            webView.destroy();
//            webView = null;
//        }
    }

    /**
     * 词语解释
     *
     * @param content
     */
    private void showExplain(String content) {
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
            webView.loadDataWithBaseURL("about:blank", getHtmlData(content),
                    "text/html", "utf-8", null);
        }
    }

    //获取完整的Html源码
    private String getHtmlData(String bodyHtml) {
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
        return style + bodyHtml;
    }

    /**
     * 添加摘抄内容
     */
    private static class AddExcerpt
            extends WeakAsyncTask<String, Integer, String, GlossaryWordExplainActivity> {

        protected AddExcerpt(GlossaryWordExplainActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(GlossaryWordExplainActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("summary", params[1]);
                json.put("essayId", params[2]);
                json.put("type", -1);
                json.put("title", params[3]);
                json.put("sourceType", 2);
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
        protected void onPostExecute(GlossaryWordExplainActivity activity, String s) {
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
}
