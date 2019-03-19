package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 消息
 */
public class NewsActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/system/message/notify";

    private RelativeLayout rl_back;
    private TextView tv_title;

    private LinearLayout ll_points;
    private TextView tv_points;
    private LinearLayout ll_comment;
    private TextView tv_comment;
    private LinearLayout ll_writing;
    private TextView tv_writing;
    private LinearLayout ll_myself;
    private TextView tv_myself;
    private LinearLayout ll_system;
    private TextView tv_system;

    private NewsActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mContext = this;

        initView();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        ll_points.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_writing.setOnClickListener(this);
        ll_myself.setOnClickListener(this);
        ll_system.setOnClickListener(this);
    }

    private void initData() {
        new GetData(mContext).execute(url);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("消息");

        ll_points = findViewById(R.id.ll_points_news);
        tv_points = findViewById(R.id.tv_points_count_news);
        ll_comment = findViewById(R.id.ll_comment_news);
        tv_comment = findViewById(R.id.tv_comment_count_news);
        ll_writing = findViewById(R.id.ll_writing_news);
        tv_writing = findViewById(R.id.tv_writing_count_news);
        ll_myself = findViewById(R.id.ll_myself_news);
        tv_myself = findViewById(R.id.tv_myself_count_news);
        ll_system = findViewById(R.id.ll_system_news);
        tv_system = findViewById(R.id.tv_system_count_news);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.ll_points_news:
                startActivity(new Intent(mContext, PointsNewsActivity.class));
                break;
            case R.id.ll_comment_news:
                startActivity(new Intent(mContext, NewUnReadCommentsActivity.class));
                break;
            case R.id.ll_writing_news:
                startActivity(new Intent(mContext, WritingNewsActivity.class));
                break;
            case R.id.ll_myself_news:
                startActivity(new Intent(mContext, MyselfNewsActivity.class));
                break;
            case R.id.ll_system_news:
                startActivity(new Intent(mContext, SystemNewsActivity.class));
                break;
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                int points_count = object.optInt("like", 0);
                int essay_comment_count = object.optInt("essayComment", 0);
                int writing_count = object.optInt("writing", 0);
                int system_count = object.optInt("system", 0);
                int writing_comment_count = object.optInt("compositionComment", 0);
                int myself_count = object.optInt("notice", 0);
                showNewsTip(points_count, essay_comment_count, writing_count, system_count,
                        writing_comment_count, myself_count);
            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 显示消息提示
     *
     * @param points_count
     * @param comment_count
     * @param writing_count
     * @param system_count
     */
    private void showNewsTip(int points_count, int comment_count, int writing_count,
                             int system_count, int writing_comment, int myself_count) {
        if (points_count != 0) {
            tv_points.setVisibility(View.VISIBLE);
            tv_points.setText(count2String(points_count));
        } else {
            tv_points.setVisibility(View.GONE);
        }
        if ((comment_count + writing_comment) != 0) {
            tv_comment.setVisibility(View.VISIBLE);
            tv_comment.setText(count2String(comment_count + writing_comment));
        } else {
            tv_comment.setVisibility(View.GONE);
        }
        if (writing_count != 0) {
            tv_writing.setVisibility(View.VISIBLE);
            tv_writing.setText(count2String(writing_count));
        } else {
            tv_writing.setVisibility(View.GONE);
        }
        if (myself_count != 0) {
            tv_myself.setVisibility(View.VISIBLE);
            tv_myself.setText(count2String(myself_count));
        } else {
            tv_myself.setVisibility(View.GONE);
        }
        if (system_count != 0) {
            tv_system.setVisibility(View.VISIBLE);
            tv_system.setText(count2String(system_count));
        } else {
            tv_system.setVisibility(View.GONE);
        }
    }

    /**
     * 消息数量转换成字符串
     *
     * @param count
     * @return
     */
    private String count2String(int count) {
        String string;
        if (count == 0) {
            string = "";
        } else if (count > 99) {
            string = "99+";
        } else {
            string = String.valueOf(count);
        }
        return string;
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        tv_points.setVisibility(View.GONE);
        tv_comment.setVisibility(View.GONE);
        tv_writing.setVisibility(View.GONE);
        tv_system.setVisibility(View.GONE);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, NewsActivity> {

        protected GetData(NewsActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewsActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(NewsActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }
}
