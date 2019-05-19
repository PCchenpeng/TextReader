package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.GlossaryRecyclerViewAdapter;
import com.dace.textreader.bean.GlossaryBean;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment_card
 * Created by Administrator.
 * Created time 2018/7/13 0013 上午 10:14.
 * Version   1.0;
 * Describe :  生词列表
 * History:
 * ==============================================================================
 */

public class GlossaryFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL_ + "/select/raw/word/list";

    private View view;

    private SmartRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_three;
    private TextView tv_sure;
    private ImageView iv_practice;

    private Context mContext;

    private LinearLayoutManager mLayoutManager;

    private GlossaryRecyclerViewAdapter adapter;
    private List<GlossaryBean> mList = new ArrayList<>();

    private static int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;
    private boolean isEditor = false;

    private OnListDataOperateListen mListen;

    private boolean hidden = true;

    private boolean showPractice = false;
    private boolean isChoose = false;
    private int mSelectedPosition = -1;
    private static String ESSAY_ID = "essay_id";
    private static String TYPE = "type";//1 文章页 2 个人中心
    private static String essayId;
    private int type;
    public static GlossaryFragment newInstance(String essayId,int type) {

        Bundle args = new Bundle();
        args.putString(ESSAY_ID,essayId);
        args.putInt(TYPE,type);
        GlossaryFragment fragment = new GlossaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_glossary, container, false);

        initView();
        swipeRefreshLayout.autoRefresh();
        initEvents();

        return view;
    }

    public void setShowPractice(boolean showPractice) {
        this.showPractice = showPractice;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
    }

    /**
     * 编辑与否
     */
    public void setEditor() {
        if(isEditor){
            isEditor = false;
        }else {
            isEditor = true;
        }
        adapter.setEditor(isEditor);
    }

    public boolean getEditor(){
        return isEditor;
    }

    private void initEvents() {
        iv_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshing) {
                    showTips("正在加载数据，请稍后...");
                } else {
                    if (isChoose) {
                        editorOrNot(false);
                    } else {
                        editorOrNot(true);
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (refreshing || isEditor) {
                    swipeRefreshLayout.finishRefresh();
                } else {
                    initData();
                }
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing && !isEnd && !isEditor && mList.size() != 0) {
                    getMoreData();
                } else {
                    swipeRefreshLayout.finishLoadMore();
                }
            }
        });
//        adapter.setOnItemClickListen(new GlossaryRecyclerViewAdapter.OnItemClickListen() {
//            @Override
//            public void onClick(View view) {
//                if (isChoose) {
//                    int pos = recyclerView.getChildAdapterPosition(view);
//                    if (mSelectedPosition == -1) {
//                        mList.get(pos).setSelected(true);
//                        adapter.notifyItemChanged(pos);
//                        mSelectedPosition = pos;
//                        tv_sure.setBackgroundColor(Color.parseColor("#ff9933"));
//                    } else if (pos == mSelectedPosition) {
//                        mList.get(pos).setSelected(false);
//                        adapter.notifyItemChanged(pos);
//                        mSelectedPosition = -1;
//                        tv_sure.setBackgroundColor(Color.parseColor("#dddddd"));
//                    } else {
//                        mList.get(mSelectedPosition).setSelected(false);
//                        mList.get(pos).setSelected(true);
//                        adapter.notifyDataSetChanged();
//                        mSelectedPosition = pos;
//                        tv_sure.setBackgroundColor(Color.parseColor("#ff9933"));
//                    }
//                }
//            }
//        });

        adapter.setOnItemClickListen(new GlossaryRecyclerViewAdapter.OnItemClickListen() {
            @Override
            public void onClick(GlossaryBean glossaryBean) {
                if(type == 1)
                    return;
                Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                intent.putExtra("essayId", String.valueOf(glossaryBean.getEssayId()));
                intent.putExtra("imgUrl","");
                startActivity(intent);
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPosition != -1) {
                    practice();
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 练习
     */
    private void practice() {
        List<String> list = mList.get(mSelectedPosition).getList();
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            array.put(list.get(i));
        }
        String practice = array.toString();
        Intent intent = new Intent();
        intent.putExtra("practiceType", 2);
        intent.putExtra("practice", practice);
        getActivity().setResult(0, intent);
        getActivity().finish();
    }

    /**
     * 进入编辑或取消编辑
     *
     * @param editor
     */
    private void editorOrNot(boolean editor) {
        isChoose = editor;
        if (editor) {
            tv_sure.setVisibility(View.VISIBLE);
            iv_practice.setImageResource(R.drawable.icon_practice_cancle);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setChoose(true);
            }
        } else {
            iv_practice.setImageResource(R.drawable.icon_practice);
            tv_sure.setVisibility(View.GONE);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setChoose(false);
                mList.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        if (!refreshing) {
            mList.clear();
            adapter.notifyDataSetChanged();
            refreshing = true;
            isEnd = false;
            pageNum = 1;
            new GetData(this).execute(url);
        }
    }

    private void initView() {
        essayId = getArguments().getString(ESSAY_ID);
        type = getArguments().getInt(TYPE);
        frameLayout = view.findViewById(R.id.frame_glossary_fragment);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_glossary_fragment);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        recyclerView = view.findViewById(R.id.recycler_view_glossary_fragment);
        recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new GlossaryRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        tv_one = view.findViewById(R.id.tv_one_tips_note);
        tv_two = view.findViewById(R.id.tv_two_tips_note);
        tv_three = view.findViewById(R.id.tv_three_tips_note);
        tv_one.setText("在阅读时");
        tv_two.setText("可双击段落／长按选择文字炸词");
        tv_three.setText("查看/添加生词");

        tv_sure = view.findViewById(R.id.tv_sure_glossary_fragment);
        iv_practice = view.findViewById(R.id.iv_practice_glossary_fragment);
        if (showPractice) {
            iv_practice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        pageNum++;
        refreshing = true;
        new GetData(this)
                .execute(url);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, GlossaryFragment> {

        protected GetData(GlossaryFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(GlossaryFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", pageNum);
                object.put("essayId",essayId);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(params[0])
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
        protected void onPostExecute(GlossaryFragment fragment, String s) {
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
            fragment.swipeRefreshLayout.finishRefresh();
            fragment.swipeRefreshLayout.finishLoadMore();
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
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < data.length(); i++) {
                        GlossaryBean glossaryBean = new GlossaryBean();

                        JSONObject object = data.getJSONObject(i);
                        glossaryBean.setId(object.getString("wid"));
                        glossaryBean.setEssayId(object.optLong("essayid", -1L));
                        glossaryBean.setTitle(object.getString("title"));
                        glossaryBean.setType(object.optInt("type", -1));
                        glossaryBean.setSourceType(object.optInt("sourceType", -1));

                        JSONArray jsonArray = object.getJSONArray("wordList");
                        if (jsonArray.length() != 0) {
                            List<String> list = new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject wordJS = new JSONObject(jsonArray.getString(j));
                                list.add(wordJS.getString("word"));
                            }
                            glossaryBean.setList(list);
                            glossaryBean.setEditor(false);
                            mList.add(glossaryBean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (mListen != null) {
                        mListen.onLoadResult(true);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                noConnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 没有更多了
     */
    private void emptyData() {
        isEnd = true;
        if (mList.size() == 0) {
            if (mListen != null) {
                mListen.onLoadResult(false);
            }
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.notes_list_tips_layout, null);
            TextView tv_one = errorView.findViewById(R.id.tv_one_tips_note);
            TextView tv_two = errorView.findViewById(R.id.tv_two_tips_note);
            TextView tv_three = errorView.findViewById(R.id.tv_three_tips_note);
            tv_one.setText("在阅读时");
            tv_two.setText("可双击段落／长按选择文字炸词");
            tv_three.setText("查看/添加生词");
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        if (mList.size() == 0) {
            if (mListen != null) {
                mListen.onLoadResult(false);
            }
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.autoRefresh();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败，请稍后再试");
        }
    }

    public void setOnListDataOperateListen(OnListDataOperateListen onListDataOperateListen) {
        this.mListen = onListDataOperateListen;
    }

    /**
     * 显示提示信息
     *
     * @param tips
     */
    private void showTips(String tips) {
        if (!hidden) {
            MyToastUtil.showToast(mContext, tips);
        }
    }
}
