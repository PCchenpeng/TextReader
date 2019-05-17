package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.biubiubiu.justifytext.library.JustifyTextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 读后感详情
 */
public class AfterReadingDetailActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/essay/personal/feeling?";
    private static final String updateLikeUrl = HttpUrlPre.HTTP_URL + "/personal/feeling_num/update?";
    //写读后感
    private final String updateAfterReadingUrl = HttpUrlPre.HTTP_URL + "/personal/feeling/update?";

    private ScrollView scrollView;
    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_like;
    private ImageView iv_like;
    private TextView tv_like;
    private ImageView iv_head;
    private TextView tv_username;
    private TextView tv_date;
    private RelativeLayout rl_editor;
    private JustifyTextView tv_content;
    private FrameLayout frameLayout;

    private AfterReadingDetailActivity mContext;

    private long essayId = -1;
    private int essayType = -1;
    private String essayTitle = "";
    private String id = "";
    private int studentId = -1;
    private String imagePath;
    private String username;
    private String date;
    private String content;
    private int likerNum;
    private int isPriviate;

    private boolean isLiker = false;
    private boolean isClickLiker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_reading_detail);

        mContext = this;

        id = getIntent().getStringExtra("afterReadingId");
        essayId = getIntent().getLongExtra("essayType", -1L);
        essayType = getIntent().getIntExtra("essayType", -1);
        essayTitle = getIntent().getStringExtra("essayTitle");

        initView();
        initData();
        initEvents();
        setImmerseLayout();

    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_like.setPadding(DensityUtil.dip2px(mContext, 15), statusBarHeight,
                DensityUtil.dip2px(mContext, 15), 0);
        scrollView.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backActivity();
            }
        });
        rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiker) {
                    MyToastUtil.showToast(mContext, "已点赞，不能再点");
                } else {
                    like();
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeAfterReading();
            }
        });
    }

    /**
     * 点赞
     */
    private void like() {
        new UpdateLikeData(mContext)
                .execute(updateLikeUrl +
                        "id=" + id +
                        "&studentId=" + NewMainActivity.STUDENT_ID +
                        "&essayId=" + essayId + "&type=" + essayType +
                        "&title=" + essayTitle);
        likerNum += 1;
        tv_like.setText(String.valueOf(likerNum));
        isLiker = true;
        isClickLiker = true;
        iv_like.setImageResource(R.drawable.bottom_points_selected);
    }

    /**
     * 写读后感
     */
    private void writeAfterReading() {
        Intent intent = new Intent(mContext, WriteAfterReadingActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("isPriviate", isPriviate);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                String s = data.getStringExtra("content");
                int type = data.getIntExtra("type", 1);
                addAfterReading(s, type);
            }
        }
    }

    /**
     * 插入读后感数据
     *
     * @param s
     * @param isPrivate //是否公开，1表示公开，0表示私有
     */
    private void addAfterReading(String s, int isPrivate) {
        content = s;
        new InsertAfterReading(mContext)
                .execute(updateAfterReadingUrl +
                        "feelingId=" + id +
                        "&feeling=" + s +
                        "&isPrivate=" + isPrivate);
    }

    private void initData() {
        showLoadingView(true);
        new GetData(mContext).execute(url + "feelingId=" + id +
                "&studentId=" + NewMainActivity.STUDENT_ID);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("读后感");

        scrollView = findViewById(R.id.scroll_view_after_reading_detail);
        frameLayout = findViewById(R.id.frame_after_reading_detail);
        iv_head = findViewById(R.id.iv_head_after_reading_detail);
        tv_username = findViewById(R.id.tv_username_after_reading_detail);
        tv_date = findViewById(R.id.tv_date_after_reading_detail);
        rl_editor = findViewById(R.id.rl_editor_after_reading_detail);
        tv_content = findViewById(R.id.tv_content_after_reading_detail);

        rl_like = findViewById(R.id.rl_like_after_reading_detail);
        iv_like = findViewById(R.id.iv_like_after_reading_detail);
        tv_like = findViewById(R.id.tv_like_after_reading_detail);

        rl_like.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示正在加载
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
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
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                essayId = object.optLong("essayid", -1);
                id = object.getString("id");
                studentId = object.optInt("studentid", -1);
                imagePath =
//                        HttpUrlPre.FILE_URL +
                                object.getString("userimg");
                username = object.getString("username");
                if (object.getString("feelingTime").equals("")
                        || object.getString("feelingTime").equals("null")) {
                    date = "2018-01-01 00:00";
                } else {
                    date = DateUtil.time2MD(object.getString("feelingTime"));
                }
                content = object.getString("feeling") + "\n";
                if (object.getString("feelingLikeNum").equals("")
                        || object.getString("feelingLikeNum").equals("null")) {
                    likerNum = 0;
                } else {
                    likerNum = object.optInt("feelingLikeNum", 0);
                }
                isPriviate = object.optInt("ispriviate", 1);
                if (!object.getString("likeOrNot").equals("")
                        && !object.getString("likeOrNot").equals("null")) {
                    if (1 == object.optInt("likeOrNot", 0)) {
                        isLiker = true;
                    } else {
                        isLiker = false;
                    }
                } else {
                    isLiker = false;
                }
                updateUi();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更新视图
     */
    private void updateUi() {
        if (isDestroyed()) {
            return;
        }
        GlideUtils.loadUserImage(mContext, imagePath, iv_head);
        tv_username.setText(username);
        tv_date.setText(date);
        tv_content.setText(content);
        tv_like.setText(String.valueOf(likerNum));
        if (studentId == NewMainActivity.STUDENT_ID && NewMainActivity.STUDENT_ID != -1) {
            rl_editor.setVisibility(View.VISIBLE);
        } else {
            rl_editor.setVisibility(View.GONE);
        }
        if (isLiker) {
            iv_like.setImageResource(R.drawable.bottom_points_selected);
        } else {
            iv_like.setImageResource(R.drawable.bottom_points_unselected);
        }
        rl_like.setVisibility(View.VISIBLE);
        showLoadingView(false);
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (isDestroyed()) {
            return;
        }
        MyToastUtil.showToast(mContext, "获取数据失败");
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

    private void analyzeUpdateData(String s) {
        if (s.contains("修改成功")) {
            content += "\n";
            tv_content.setText(content);
        } else {
            errorUpdate();
        }
    }

    private void errorUpdate() {
        MyToastUtil.showToast(mContext, "修改读后感失败，请稍后再试");
        content = tv_content.getText().toString();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity() {
        Intent intent = new Intent();
        intent.putExtra("clickLikeOrNot", isClickLiker);
        setResult(0, intent);
        finish();
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, AfterReadingDetailActivity> {

        protected GetData(AfterReadingDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AfterReadingDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AfterReadingDetailActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 点赞
     */
    private static class UpdateLikeData
            extends WeakAsyncTask<String, Integer, String, AfterReadingDetailActivity> {

        protected UpdateLikeData(AfterReadingDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AfterReadingDetailActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AfterReadingDetailActivity activity, String s) {

        }
    }

    /**
     * 添加读后感
     */
    private static class InsertAfterReading
            extends WeakAsyncTask<String, Integer, String, AfterReadingDetailActivity> {

        protected InsertAfterReading(AfterReadingDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AfterReadingDetailActivity activity, String[] params) {
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
        protected void onPostExecute(AfterReadingDetailActivity activity, String s) {
            //获取数据之后
            if (s == null) {
                activity.errorUpdate();
            } else {
                activity.analyzeUpdateData(s);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
