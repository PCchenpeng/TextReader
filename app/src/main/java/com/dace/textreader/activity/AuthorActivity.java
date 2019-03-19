package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
 * 作者信息
 */
public class AuthorActivity extends BaseActivity {

    private String url = HttpUrlPre.HTTP_URL + "/essay/author?";
    //添加摘抄
    private final String addExcerptUrl = HttpUrlPre.HTTP_URL + "/personal/summary/insert";

    private RelativeLayout rl_back;  //返回按钮
    private TextView tv_page_title;
    private ScrollView scrollView;
    private ImageView iv_author;
    private TextView tv_author;
    private CustomActionWebView tv_detail;

    private FrameLayout frameLayout;

    private long authorId = -1;
    private String author_name;
    private String imagePath = "";
    private String author_detail = "";

    private long id;  //文章ID
    private long readID;  //阅读ID

    //自定义Item
    private List<String> actionItemList = new ArrayList<>();
    //是否双击的标识
    private boolean isClick = false;

    private AuthorActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        mContext = this;

        author_name = getIntent().getStringExtra("author");

        id = getIntent().getLongExtra("id", -1);
        readID = getIntent().getLongExtra("readId", -1);

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
        tv_detail.setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String s, String s1) {
                if (s.equals("摘抄")) {
                    addExcerpt(s1);
                } else if (s.equals("复制")) {
                    DataUtil.copyContent(mContext, s1);
                } else {
                    Intent intent = new Intent(mContext, WordExplainActivity.class);
                    intent.putExtra("word", s1);
                    intent.putExtra("type", 0);
                    intent.putExtra("readId", readID);
                    intent.putExtra("bomb", 1);

                    intent.putExtra("glossarySourceType", 3);
                    intent.putExtra("glossaryId", authorId);
                    intent.putExtra("glossaryType", -1);
                    intent.putExtra("glossaryTitle", author_name);

                    startActivity(intent);
                }
            }
        });
        tv_page_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doubleClick();
            }
        });
    }

    /**
     * 添加摘抄
     *
     * @param excerpt 摘抄的内容
     */
    private void addExcerpt(String excerpt) {

        new AddExcerpt(mContext).execute(addExcerptUrl, excerpt, String.valueOf(authorId),
                author_name);
    }

    //双击回到顶部
    private void doubleClick() {
        if (isClick) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        } else {
            isClick = true;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isClick = false;
                }
            }.start();
        }
    }

    private void initData() {
        showLoadingView(true);

        author_detail = "";

        new GetData(mContext).execute(url + "authorName=" + author_name);
    }

    /**
     * 显示加载等待视图
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.loading_h5_layout, null);
            ImageView iv_loading = view.findViewById(R.id.iv_h5_loading);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_placeholder_h5, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        tv_page_title.setText("作者");

        scrollView = findViewById(R.id.scrollView_author);
        iv_author = findViewById(R.id.iv_author_head);
        tv_author = findViewById(R.id.tv_author_name);
        tv_detail = findViewById(R.id.tv_author_detail);

        frameLayout = findViewById(R.id.frame_layout_author);

        tv_detail.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();//还需要判断host
                if (TextUtils.equals("app", scheme)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.putExtra("words", true);
                    intent.putExtra("type", 0);
                    intent.putExtra("readId", readID);
                    intent.putExtra("bomb", 1);

                    intent.putExtra("glossarySourceType", 3);
                    intent.putExtra("glossaryId", authorId);
                    intent.putExtra("glossaryType", -1);
                    intent.putExtra("glossaryTitle", author_name);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        tv_detail.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    postRemoveLoading();
                }
            }
        });
        tv_detail.linkJSInterface();
        tv_detail.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        tv_detail.getSettings().setJavaScriptEnabled(true);
        tv_detail.getSettings().setDefaultTextEncodingName("utf-8");

        actionItemList.add("炸词");
        actionItemList.add("摘抄");
        actionItemList.add("复制");
        tv_detail.setActionList(actionItemList);
    }

    /**
     * 延迟移除loading
     */
    private void postRemoveLoading() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLoadingView(false);
        }
    };

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, AuthorActivity> {

        protected GetData(AuthorActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AuthorActivity activity, String[] params) {
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
        protected void onPostExecute(AuthorActivity activity, String s) {
            if (s == null) {
                activity.noContent();
            } else {
                //获取数据之后JSON
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 添加摘抄内容
     */
    private static class AddExcerpt
            extends WeakAsyncTask<String, Integer, String, AuthorActivity> {

        protected AddExcerpt(AuthorActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AuthorActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("summary", params[1]);
                json.put("essayId", params[2]);
                json.put("type", -1);
                json.put("title", params[3]);
                json.put("sourceType", 3);
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
        protected void onPostExecute(AuthorActivity activity, String s) {
            if (s != null && s.contains("摘抄成功")) {
                MyToastUtil.showToast(activity, "摘抄成功");
            }
        }
    }

    /**
     * 没有获取到数据(没网络)
     */
    private void noContent() {
        frameLayout.removeAllViews();
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
     * 分析数据
     *
     * @param s 获取到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (json.optInt("status", -1) == 200) {
                JSONObject content = json.getJSONObject("data");
                authorId = content.optLong("id", -1);
                author_name = content.getString("author");
                imagePath = content.getString("image");
                author_detail = content.getString("content");
                updateUI();
            } else {
                noAuthorContent();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noAuthorContent();
        }
    }

    //更新UI
    private void updateUI() {
        if (isDestroyed()) {
            return;
        }
        if (imagePath.equals("") || imagePath.equals("null")) {
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.guren, iv_author);
        } else {
            GlideUtils.loadUserImage(mContext, imagePath, iv_author);
        }
        tv_author.setText(author_name);
        if (author_detail.equals("")) {
            noAuthorContent();
        } else {
            if (tv_detail != null) {
                tv_detail.loadDataWithBaseURL("about:blank", getHtmlData(author_detail),
                        "text/html", "utf-8", null);
            }
        }
    }

    /**
     * 没有作者信息
     */
    private void noAuthorContent() {
        if (isDestroyed()) {
            return;
        }
        iv_author.setVisibility(View.GONE);
        tv_author.setVisibility(View.GONE);
        if (tv_detail != null) {
            tv_detail.setVisibility(View.GONE);
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
        tv_tips.setText("暂无此作者内容");
        tv_reload.setVisibility(View.GONE);
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
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
        String style = "<style> body{background:#ffffff;line-height: 1.8;" +
                "font-family:华文细黑;font-size:1.0rem; padding:1em 1em 1em 1em;" +
                "text-align:justify;text-justify:distribute;" +
                "-webkit-text-size-adjust: none;" +
                "word-wrap: break-word;" +
                "p{padding:8px 0; line-height: 2;} " +
                "} " +
                "</style>";
        String body = "</head>\n" +
                " <body>";
        String script_1 = "<script src=\"file:///android_asset/js/hammer.min.js\"></script>\n";
        String script_2 = "<script src=\"file:///android_asset/js/index.js\"></script>\n";
        String over = "</body>\n" +
                "</html>";
        return head + style + body + script_1 + bodyHtml + script_2 + over;
    }

    @Override
    protected void onDestroy() {
        if (tv_detail != null) {
            tv_detail.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            tv_detail.clearHistory();

            ((ViewGroup) tv_detail.getParent()).removeView(tv_detail);
            tv_detail.destroy();
            tv_detail = null;
        }
        super.onDestroy();
    }
}
