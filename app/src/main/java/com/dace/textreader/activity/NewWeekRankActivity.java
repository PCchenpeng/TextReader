package com.dace.textreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.RankRecyclerViewAdapter;
import com.dace.textreader.bean.WeekRankUser;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 阅读排行榜
 */
public class NewWeekRankActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/statistics/rank/week/duration?studentId=";

    private RelativeLayout rl_top_back;
    private ImageView iv_top_back;
    private FrameLayout frameLayout;
    private ImageView iv_bg;
    private ImageView iv_head;
    private TextView tv_user;
    private TextView tv_rank;
    private TextView tv_time;
    private RecyclerView recyclerView;

    private NewWeekRankActivity mContext;

    private List<WeekRankUser> mList = new ArrayList<>();
    private RankRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_week_rank);

        mContext = this;

        initView();
        setImmerseLayout();
        initData();
        initEvents();
    }

    // view为标题栏
    protected void setImmerseLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = DensityUtil.getStatusBarHeight(getBaseContext());
            rl_top_back.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    private void initEvents() {
        rl_top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initData() {
        showLoadingView(true);
        new GetData(mContext)
                .execute(url + NewMainActivity.STUDENT_ID);
    }

    private void initView() {
        rl_top_back = findViewById(R.id.rl_back_new_week_rank);
        iv_top_back = findViewById(R.id.iv_back_new_week_rank);
        frameLayout = findViewById(R.id.frame_new_week_rank);
        iv_bg = findViewById(R.id.iv_bg_new_week_rank);
        iv_head = findViewById(R.id.iv_head_new_week_rank);
        tv_user = findViewById(R.id.tv_username_new_week_rank);
        tv_rank = findViewById(R.id.tv_rank_new_week_rank);
        tv_time = findViewById(R.id.tv_total_time_new_week_rank);

        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_week_rank_bg, iv_bg);
        GlideUtils.loadUserImage(mContext,
//                HttpUrlPre.FILE_URL +
                        NewMainActivity.USERIMG, iv_head);
        tv_user.setText(NewMainActivity.USERNAME);

        recyclerView = findViewById(R.id.recycler_view_new_week_rank);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RankRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
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
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, NewWeekRankActivity> {

        protected GetData(NewWeekRankActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewWeekRankActivity activity, String[] params) {
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
        protected void onPostExecute(NewWeekRankActivity activity, String s) {
            if (s == null) {
                activity.errorContent();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 未获取到数据
     */
    private void errorContent() {
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            iv_top_back.setImageResource(R.drawable.icon_back);
            View errorView = LayoutInflater.from(this)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("获取排行榜内容失败，请稍后再试~");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
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
                JSONObject data = jsonObject.getJSONObject("data");
                int index;
                if (data.getString("meChart").contains("未进入") &&
                        !data.getString("meChart").contains("studentid")) {
                    tv_rank.setText("未进入排行榜");
                    String time = 0 + "\n阅读时长（分钟）";
                    tv_time.setText(time);
                    index = 0;
                } else {
                    JSONObject my = data.getJSONObject("meChart");
                    String time = my.getString("weekDuration");
                    String rank = my.optInt("rank", -1) + "\n名次";
                    tv_rank.setText(rank);
                    tv_time.setText(time.split("\\.")[0] + "\n阅读时长（分钟）");
                    index = 2;
                }

                int length_rank = tv_rank.getText().toString().length();
                SpannableString spanString_rank = new SpannableString(tv_rank.getText().toString());
                spanString_rank.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(mContext, 15)),
                        length_rank - index, length_rank, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_rank.setText(spanString_rank);

                int length = tv_time.getText().toString().length();
                SpannableString spanString = new SpannableString(tv_time.getText().toString());
                spanString.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(mContext, 15)),
                        length - 8, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spanString.setSpan(new RelativeSizeSpan(0.8f),
                        length - 4, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_time.setText(spanString);

                JSONArray total = data.getJSONArray("totalChart");
                for (int i = 0; i < total.length(); i++) {
                    JSONObject object = total.getJSONObject(i);
                    WeekRankUser weekRankUser = new WeekRankUser();
                    weekRankUser.setId(object.optLong("studentid", -1));
                    weekRankUser.setName(object.getString("username"));
                    weekRankUser.setImage(object.getString("userimg"));
                    String t = object.getString("weekDuration");
                    weekRankUser.setDuration(t.split("\\.")[0]);
                    weekRankUser.setRank(object.optInt("rank", -1));
                    mList.add(weekRankUser);
                }
                adapter.notifyDataSetChanged();
                showLoadingView(false);
                //修改状态栏的文字颜色为黑色
                int flag = StatusBarUtil.StatusBarLightMode(this);
                StatusBarUtil.StatusBarDarkMode(this, flag);
            } else {
                errorContent();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorContent();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
