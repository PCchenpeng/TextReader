package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.LevelRecyclerViewAdapter;
import com.dace.textreader.bean.LevelBean;
import com.dace.textreader.bean.TypeBean;
import com.dace.textreader.fragment.ReaderListFragment;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 分级阅读
 */
public class NewReaderActivity extends BaseActivity implements View.OnClickListener {

    private static final String type_url = HttpUrlPre.HTTP_URL + "/select/category";
    private static final String feedbackUrl = HttpUrlPre.HTTP_URL + "/essay/feedback/list";

    private RelativeLayout rl_back;
    private TextView tv_page_title;
    private RelativeLayout ll_search;
    private FrameLayout frameLayout;
    private RelativeLayout rl_choose;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private NewReaderActivity mContext;

    private List<TypeBean> typeBeanList = new ArrayList<>();
    private List<LevelBean> levelBeanList = new ArrayList<>();
    private int typePosition = -1;

    public static String[] deleteTexts = new String[]{"内容太差", "太简单", "太难了", "不感兴趣", "", ""};

    private List<ReaderListFragment> mList_fragment = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;

    private boolean isFirstStart = true;

    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reader);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.StatusBarLightMode(this, flag);

        index = getIntent().getIntExtra("index", -1);

        SharedPreferences firstStart = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        isFirstStart = firstStart.getBoolean("reader", true);

        initView();
        initLocalData();
        initEvents();
        initFeedBackData();
    }

    private void initLocalData() {
        LitePal.findAllAsync(LevelBean.class).listen(new FindMultiCallback<LevelBean>() {
            @Override
            public void onFinish(List<LevelBean> list) {
                if (list.size() != 0) {
                    levelBeanList.addAll(list);
                }
                initLocalTypeData();
            }
        });
    }

    private void initLocalTypeData() {
        LitePal.findAllAsync(TypeBean.class).listen(new FindMultiCallback<TypeBean>() {
            @Override
            public void onFinish(List<TypeBean> list) {
                if (list.size() != 0) {
                    typeBeanList.addAll(list);
                    for (int i = 0; i < typeBeanList.size(); i++) {
                        addFragment(i);
                    }
                    updateUi();
                }
                initData();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstStart && hasFocus) {
            isFirstStart = false;
            TipsUtil tipsUtil = new TipsUtil(mContext);
            tipsUtil.showTipBelowOffsetView(rl_choose, "筛选\n智能推荐等级阅读");
            SharedPreferences firstSP = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = firstSP.edit();
            editor.putBoolean("reader", false);
            editor.apply();
        }
    }

    /**
     * 初始化反馈数据
     */
    private void initFeedBackData() {
        new GetFeedBackData(mContext).execute(feedbackUrl);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        rl_choose.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                typePosition = tab.getPosition();
                updateLevelState(0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initData() {
        new GetTypeData(mContext).execute(type_url);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page_title = findViewById(R.id.tv_page_title_top_layout);
        tv_page_title.setText("分级阅读");
        ll_search = findViewById(R.id.ll_search_toolbar_new_reader);

        frameLayout = findViewById(R.id.frame_new_reader);
        showLoadingView();

        rl_choose = findViewById(R.id.rl_choose_level_new_reader);

        tabLayout = findViewById(R.id.tab_layout_new_reader);
        viewPager = findViewById(R.id.view_pager_new_reader);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.ll_search_toolbar_new_reader:
                turnToSearchView();
                break;
            case R.id.rl_choose_level_new_reader:
                chooseLevel();
                break;
        }
    }

    /**
     * 前往搜索界面
     */
    private void turnToSearchView() {
        Intent intent = new Intent(NewReaderActivity.this, SearchActivity.class);
        intent.putExtra("isClasses", false);
        startActivity(intent);
    }

    /**
     * 显示等级筛选视图
     */
    private void chooseLevel() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_level_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_doubt = holder.getView(R.id.ll_doubt_level_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_level_dialog);
                        final RecyclerView recyclerView = holder.getView(R.id.recycler_view_level_dialog);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                                LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        LevelRecyclerViewAdapter adapter =
                                new LevelRecyclerViewAdapter(mContext, levelBeanList);
                        recyclerView.setAdapter(adapter);
                        ll_doubt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                turnToDoubtView();
                                dialog.dismiss();
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        adapter.setOnItemClickListener(new LevelRecyclerViewAdapter.OnLevelItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int pos = recyclerView.getChildAdapterPosition(view);
                                if (pos != getLevelSelectedState()) {
                                    updateLevelState(pos);
                                    updateFragmentData();
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 更新fragment中的数据
     */
    private void updateFragmentData() {
        int level = levelBeanList.get(getLevelSelectedState()).getGrade();
        mList_fragment.get(typePosition).setLevel(level);
        mList_fragment.get(typePosition).initData();
    }

    /**
     * 前往疑问解释视图
     */
    private void turnToDoubtView() {
        Intent intent = new Intent(mContext, EventsActivity.class);
        intent.putExtra("pageName", "py_activity");
        startActivity(intent);
    }

    /**
     * 更新等级选择视图
     *
     * @param pos
     */
    private void updateLevelState(int pos) {
        if (levelBeanList.size() != 0) {
            for (int i = 0; i < levelBeanList.size(); i++) {
                levelBeanList.get(i).setSelected(false);
            }
            levelBeanList.get(pos).setSelected(true);
        }
    }

    /**
     * 获取当前选择的等级
     */
    private int getLevelSelectedState() {
        int pos = -1;
        for (int i = 0; i < levelBeanList.size(); i++) {
            if (levelBeanList.get(i).isSelected()) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * 显示正在加载数据的视图
     */
    private void showLoadingView() {
        frameLayout.removeAllViews();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
        ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
        frameLayout.addView(view);
    }

    /**
     * 分析文章类型数据
     *
     * @param s
     */
    private void analyzeTypeData(String s) {
        LitePal.deleteAll(LevelBean.class);
        LitePal.deleteAll(TypeBean.class);
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject data = jsonObject.getJSONObject("data");

                JSONArray level = data.getJSONArray("level");

                List<LevelBean> levelList = new ArrayList<>();
                for (int i = 0; i < level.length(); i++) {
                    JSONObject object_l = level.getJSONObject(i);

                    LevelBean levelBean = new LevelBean();
                    levelBean.setGrade(object_l.optInt("grade", 110));
                    levelBean.setGradeName(object_l.getString("gradename"));
                    if (i == 0) {
                        levelBean.setSelected(true);
                    } else {
                        levelBean.setSelected(false);
                    }
                    levelBean.save();
                    levelList.add(levelBean);
                }

                JSONArray category = data.getJSONArray("category");

                List<TypeBean> list = new ArrayList<>();
                for (int i = 0; i < category.length(); i++) {
                    JSONObject object_c = category.getJSONObject(i);

                    TypeBean typeBean = new TypeBean();
                    typeBean.setType(object_c.optInt("type", 0));
                    typeBean.setTypeName(object_c.getString("name"));
                    if (i == 0) {
                        typeBean.setSelected(true);
                    } else {
                        typeBean.setSelected(false);
                    }
                    typeBean.save();
                    list.add(typeBean);
                }

                if (levelBeanList.size() == 0) {
                    levelBeanList.addAll(levelList);
                }

                if (typeBeanList.size() == 0) {
                    for (int i = 0; i < list.size(); i++) {
                        typeBeanList.add(list.get(i));
                        addFragment(i);
                    }
                    updateUi();
                }

            } else {
                typeErrorConnect(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            typeErrorConnect(true);
        }
    }

    /**
     * 更新Ui
     */
    private void updateUi() {
        typePosition = 0;
        viewPagerAdapter.notifyDataSetChanged();

        if (frameLayout.getVisibility() == View.VISIBLE) {
            frameLayout.removeAllViews();
            frameLayout.setVisibility(View.GONE);
        }

        if (index != -1 && index < typeBeanList.size()) {
            viewPager.setCurrentItem(index);
        }
    }

    /**
     * 添加碎片视图
     *
     * @param i
     */
    private void addFragment(int i) {
        ReaderListFragment fragment = new ReaderListFragment();
        fragment.setLevel(-1);
        fragment.setType(typeBeanList.get(i).getType());
        mList_fragment.add(fragment);
    }

    /**
     * 更新Fragment
     *
     * @param i
     */
    private void updateFragment(int i) {
        mList_fragment.get(i).setType(typeBeanList.get(i).getType());
    }

    /**
     * 获取文章类型失败
     *
     * @param isEmpty
     */
    private void typeErrorConnect(boolean isEmpty) {
        if (typeBeanList.size() == 0 || levelBeanList.size() == 0) {
            String tips;
            if (isEmpty) {  //网络访问成功，但数据为空
                tips = "暂无数据";
            } else {  //网络访问失败
                tips = "获取数据失败，请稍后重试";
            }
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            tv_tips.setText(tips);
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
    }

    /**
     * 获取文章类型数据
     */
    private static class GetTypeData
            extends WeakAsyncTask<String, Void, String, NewReaderActivity> {

        protected GetTypeData(NewReaderActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewReaderActivity activity, String[] strings) {
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
        protected void onPostExecute(NewReaderActivity activity, String s) {
            if (s == null) {
                activity.typeErrorConnect(false);
            } else {
                activity.analyzeTypeData(s);
            }
        }
    }

    /**
     * 获取反馈信息
     */
    private static class GetFeedBackData
            extends WeakAsyncTask<String, Void, String, NewReaderActivity> {

        protected GetFeedBackData(NewReaderActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewReaderActivity activity, String[] strings) {
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
        protected void onPostExecute(NewReaderActivity activity, String s) {
            if (s != null) {
                activity.analyzeFeedBackData(s);
            }
        }
    }

    /**
     * 分析反馈信息的内容
     *
     * @param s
     */
    private void analyzeFeedBackData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() != 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        deleteTexts[i] = object.getString("feedback");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
            return typeBeanList.get(position).getTypeName();
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
        super.onDestroy();
    }
}
