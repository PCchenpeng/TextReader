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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.ExcerptRecyclerViewAdapter;
import com.dace.textreader.bean.ExcerptBean;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
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
 * Created time 2018/7/13 0013 上午 10:36.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class ExcerptFragment extends Fragment {

    private final String url = HttpUrlPre.HTTP_URL + "/personal/summary/select?";
    private final String deleteUrl = HttpUrlPre.HTTP_URL + "/personal/sumary/delete";

    private View view;

    private FrameLayout frameLayout;
    private SmartRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout rl_editor;
    private LinearLayout ll_select_all;
    private ImageView iv_select_all;
    private TextView tv_delete;

    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_three;
    private TextView tv_sure;
    private ImageView iv_practice;

    private LinearLayoutManager mLayoutManager;
    private ExcerptRecyclerViewAdapter adapter;
    private List<ExcerptBean> mList = new ArrayList<>();
    private List<ExcerptBean> mSelectItemList = new ArrayList<>();

    private Context mContext;  //上下文对象

    private int pageNum = 1;

    private boolean isEditor = false;  //是否处于编辑状态
    private boolean isSelectAll = false;  //是否是全选
    private boolean hasSelected = false;  //是否有item被选中

    //判断是否加载完全
    private boolean isEnd = false;
    private boolean refreshing = false;

    private OnListDataOperateListen mListen;

    private boolean hidden = true;

    private boolean showPractice = false;
    private boolean isChoose = false;
    private int mSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_excerpt, container, false);

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
        ll_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectAll) {
                    iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
                    selectAllItem(false);
                } else {
                    iv_select_all.setImageResource(R.drawable.icon_edit_selected);
                    selectAllItem(true);
                }
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasSelected) {
                    deleteItems();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing && !isEditor && !isChoose) {
                    initData();
                } else {
                    swipeRefreshLayout.finishRefresh();
                }
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing && !isEnd && mList.size() != 0) {
                    getMoreData();
                } else {
                    swipeRefreshLayout.finishLoadMore();
                }
            }
        });
        adapter.setOnItemClickListener(new ExcerptRecyclerViewAdapter.OnExcerptItemClick() {
            @Override
            public void onItemClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (isEditor) {
                        itemSelected(pos);
                    } else if (isChoose) {
                        if (mSelectedPosition == -1) {
                            mList.get(pos).setSelected(true);
                            adapter.notifyItemChanged(pos);
                            mSelectedPosition = pos;
                            tv_sure.setBackgroundColor(Color.parseColor("#ff9933"));
                        } else if (pos == mSelectedPosition) {
                            mList.get(pos).setSelected(false);
                            adapter.notifyItemChanged(pos);
                            mSelectedPosition = -1;
                            tv_sure.setBackgroundColor(Color.parseColor("#dddddd"));
                        } else {
                            mList.get(mSelectedPosition).setSelected(false);
                            mList.get(pos).setSelected(true);
                            adapter.notifyDataSetChanged();
                            mSelectedPosition = pos;
                            tv_sure.setBackgroundColor(Color.parseColor("#ff9933"));
                        }
                    }
                }
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
    }

    /**
     * 练习
     */
    private void practice() {
        String practice = mList.get(mSelectedPosition).getExcerpt();
        Intent intent = new Intent();
        intent.putExtra("practiceType", 1);
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
                mList.get(i).setSelected(false);
                mList.get(i).setEditor(true);
            }
        } else {
            iv_practice.setImageResource(R.drawable.icon_practice);
            tv_sure.setVisibility(View.GONE);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setEditor(false);
                mList.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void editorOpenOrClose() {
        if (!refreshing && mList.size() != 0) {
            if (isEditor) {
                cancelEditorMode();
            } else {
                editorMode();
            }
        }
    }

    private void initData() {
        if (!refreshing) {
            if (mListen != null) {
                mListen.onRefresh(true);
            }
            pageNum = 1;
            refreshing = true;
            isEnd = false;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this)
                    .execute(url + "studentId=" + NewMainActivity.STUDENT_ID);
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_excerpt_fragment);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_excerpt_fragment);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        recyclerView = view.findViewById(R.id.recycler_view_excerpt_fragment);
        recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ExcerptRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        rl_editor = view.findViewById(R.id.rl_editor_excerpt_fragment);
        ll_select_all = view.findViewById(R.id.ll_select_all_new_collection_bottom);
        iv_select_all = view.findViewById(R.id.iv_select_all_new_collection_bottom);
        tv_delete = view.findViewById(R.id.tv_delete_new_collection_bottom);

        tv_one = view.findViewById(R.id.tv_one_tips_note);
        tv_two = view.findViewById(R.id.tv_two_tips_note);
        tv_three = view.findViewById(R.id.tv_three_tips_note);
        tv_one.setText("在阅读时");
        tv_two.setText("可长按选择文本添加摘抄");
        tv_three.setText("");
        tv_three.setVisibility(View.GONE);

        tv_sure = view.findViewById(R.id.tv_sure_excerpt_fragment);
        iv_practice = view.findViewById(R.id.iv_practice_excerpt_fragment);
        if (showPractice) {
            iv_practice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 删除Item
     */
    private void deleteItems() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isSelected()) {
                mSelectItemList.add(mList.get(i));
                array.put(mList.get(i).getId());
            }
        }
        if (array.length() != 0) {
            for (int i = 0; i < mSelectItemList.size(); i++) {
                mList.remove(mSelectItemList.get(i));
            }
            mSelectItemList.clear();
            adapter.notifyDataSetChanged();
            new DeleteData(this).execute(deleteUrl, array.toString());
            cancelEditorMode();
        }
        if (mList.size() == 0) {
            emptyData();
        }
    }

    /**
     * 取消编辑模式
     */
    private void cancelEditorMode() {
        rl_editor.setVisibility(View.GONE);
        if (mListen != null) {
            mListen.onEditor(false);
        }
        isEditor = false;
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelected(false);
            mList.get(i).setEditor(false);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 全选
     */
    private void selectAllItem(boolean selectAll) {
        isSelectAll = selectAll;
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelected(selectAll);
        }
        adapter.notifyDataSetChanged();
        updateDeleteButtonBg();
    }

    /**
     * 编辑模式
     */
    private void editorMode() {
        rl_editor.setVisibility(View.VISIBLE);
        isEditor = true;
        if (mListen != null) {
            mListen.onEditor(true);
        }
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setEditor(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 编辑状态下选中Item
     *
     * @param position
     */
    private void itemSelected(int position) {
        if (mList.get(position).isSelected()) {
            mList.get(position).setSelected(false);
        } else {
            mList.get(position).setSelected(true);
        }
        adapter.notifyDataSetChanged();
        updateDeleteButtonBg();
    }

    /**
     * 更新删除按钮的背景
     */
    private void updateDeleteButtonBg() {
        hasSelected = false;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isSelected()) {
                hasSelected = true;
                break;
            }
        }
        if (hasSelected) {
            tv_delete.setBackgroundResource(R.drawable.shape_text_orange);
        } else {
            tv_delete.setBackgroundResource(R.drawable.shape_text_gray);
            iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
            isSelectAll = false;
        }
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        if (mListen != null) {
            mListen.onRefresh(true);
        }
        pageNum++;
        refreshing = true;
        new GetData(this)
                .execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                        "&pageNum=" + pageNum +
                        "&pageSize=10");
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.optInt("status", -1) == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ExcerptBean excerptBean = new ExcerptBean();
                        excerptBean.setId(object.getString("id"));
                        if (object.getString("time").equals("")
                                || object.getString("time").equals("null")) {
                            excerptBean.setTime("2018-01-01 00:00");
                        } else {
                            excerptBean.setTime(DateUtil.time2YMD(object.getString("time")));
                        }
                        excerptBean.setEssayId(object.optLong("essayid", -1L));
                        excerptBean.setEssayType(object.optInt("type", -1));
                        excerptBean.setEssayTitle(object.getString("title"));
                        excerptBean.setExcerpt(object.getString("summary"));
                        excerptBean.setSourceType(object.optInt("sourceType", -1));
                        mList.add(excerptBean);
                    }
                    adapter.notifyDataSetChanged();
                    if (mListen != null) {
                        mListen.onLoadResult(true);
                    }
                }
            } else if (jsonObject.optInt("status", -1) == 400) {
                emptyData();
            } else {
                errorConnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorConnect();
        }
    }

    /**
     * 没有更多
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
            tv_two.setText("可长按选择文本添加摘抄");
            tv_three.setText("");
            tv_three.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorConnect() {
        if (mList.size() == 0) {
            if (mListen != null) {
                mListen.onLoadResult(false);
            }
            View errorView = LayoutInflater.from(getContext())
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

    public void setOnListDataOperateListen(OnListDataOperateListen onListDataOperateListen) {
        this.mListen = onListDataOperateListen;
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, ExcerptFragment> {

        protected GetData(ExcerptFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ExcerptFragment fragment, String[] params) {
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
        protected void onPostExecute(ExcerptFragment fragment, String s) {

            if (s == null) {
                fragment.errorConnect();
            } else {
                fragment.analyzeData(s);
            }
            if (fragment.mListen != null) {
                fragment.mListen.onRefresh(false);
            }
            fragment.refreshing = false;
            fragment.swipeRefreshLayout.finishRefresh();
            fragment.swipeRefreshLayout.finishLoadMore();
        }
    }

    /**
     * 删除数据
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Integer, String, ExcerptFragment> {

        protected DeleteData(ExcerptFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ExcerptFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("status", 0);
                json.put("summaries", params[1]);
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
        protected void onPostExecute(ExcerptFragment fragment, String s) {

        }
    }

}
