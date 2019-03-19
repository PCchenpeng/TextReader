package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.KnowledgeDetailAdapter;
import com.dace.textreader.adapter.KnowledgeDetailMenuAdapter;
import com.dace.textreader.bean.KnowledgeChildBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 知识点详情
 */
public class KnowledgeDetailActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL_ + "/knowledge/point/detail";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_menu;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;

    //菜单栏
    private View view_menu;
    private View view_menu_blank;
    private RecyclerView recyclerView_menu;

    private KnowledgeDetailActivity mContext;

    private List<KnowledgeChildBean> mList = new ArrayList<>();
    private KnowledgeDetailAdapter adapter;
    private KnowledgeDetailMenuAdapter adapter_menu;

    private long knowledgeId = -1;
    private String knowledgeTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_detail);

        mContext = this;

        knowledgeId = getIntent().getLongExtra("id", -1);
        knowledgeTitle = getIntent().getStringExtra("title");

        initView();
        initData();
        initEvents();
        setImmerseLayout();

    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_menu.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        view_menu_blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        adapter_menu.setOnItemClickListen(new KnowledgeDetailMenuAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView_menu.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList.size()) {
                    recyclerView.smoothScrollToPosition(pos);
                }
            }
        });
    }

    /**
     * 显示菜单
     */
    private void showMenu() {
        if (frameLayout.getChildCount() == 0) {
            frameLayout.addView(view_menu);
            recyclerView_menu.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_to_left_anim));
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.left_to_right_anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            recyclerView_menu.startAnimation(animation);
        }
    }

    private void initData() {
        new GetData(mContext).execute(url, String.valueOf(knowledgeId));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText(knowledgeTitle);

        rl_menu = findViewById(R.id.rl_directory_knowledge_detail);
        frameLayout = findViewById(R.id.frame_knowledge_detail);
        recyclerView = findViewById(R.id.rv_knowledge_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new KnowledgeDetailAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        view_menu = LayoutInflater.from(mContext)
                .inflate(R.layout.view_knowledge_detail_menu_layout, null);
        view_menu_blank = view_menu.findViewById(R.id.view_knowledge_detail_menu);
        recyclerView_menu = view_menu.findViewById(R.id.rv_knowledge_detail_menu);
        LinearLayoutManager layoutManager_menu = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_menu.setLayoutManager(layoutManager_menu);
        adapter_menu = new KnowledgeDetailMenuAdapter(mContext, mList);
        recyclerView_menu.setAdapter(adapter_menu);
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
                knowledgeTitle = object.getString("title");
                JSONArray array = object.getJSONArray("contents");
                for (int i = 0; i < array.length(); i++) {
                    KnowledgeChildBean knowledgeChildBean = new KnowledgeChildBean();
                    JSONObject json = array.getJSONObject(i);
                    knowledgeChildBean.setTitle(json.getString("words"));
                    knowledgeChildBean.setContent(json.getString("paraphrase"));
                    mList.add(knowledgeChildBean);
                }

                //刷新界面
                tv_title.setText(knowledgeTitle);
                adapter.notifyDataSetChanged();
                adapter_menu.notifyDataSetChanged();

            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 获取内容失败
     */
    private void noConnect() {
        if (mContext == null) {
            return;
        }
        if (mContext.isFinishing()) {
            return;
        }
        if (mList.size() != 0) {
            return;
        }
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

    @Override
    public void onBackPressed() {
        if (frameLayout.getChildCount() == 0) {
            super.onBackPressed();
        } else {
            showMenu();
        }
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, KnowledgeDetailActivity> {

        protected GetData(KnowledgeDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(KnowledgeDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("pointId", strings[1]);
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
        protected void onPostExecute(KnowledgeDetailActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

}
