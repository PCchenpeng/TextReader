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
import com.dace.textreader.activity.DailySentenceActivity;
import com.dace.textreader.activity.NewCollectionActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.SentenceCollectionAdapter;
import com.dace.textreader.bean.SentenceBean;
import com.dace.textreader.listen.OnCollectionEditorListen;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/3/21 0021 上午 10:03.
 * Version   1.0;
 * Describe :每日一句收藏页
 * History:
 * ==============================================================================
 */

public class SentenceCollectionFragment extends Fragment {

    private final String url = HttpUrlPre.HTTP_URL + "/select/collect/sentenceEveryday?";
    private final String deleteUrl = HttpUrlPre.HTTP_URL + "/delete/collect/sentenceEveryday";

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

    private List<SentenceBean> mList = new ArrayList<>();
    private List<SentenceBean> mSelectedList = new ArrayList<>();
    private int selectedNum = 0;  //选中的item数量

    private SentenceCollectionAdapter adapter;

    private boolean isEditor = false;  //是否处于编辑模式
    private boolean hasSelected = false;
    private boolean refreshing = false;  //是否正在刷新
    private boolean isEnd = false;

    private int pageNum = 1;

    private Context mContext;

    private boolean hidden = true;

    private boolean isSelectAll = false;  //是否是全选

    private boolean showPractice = false;
    private boolean isChoose = false;
    private int mSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sentence_new_collection, container, false);

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
        if (mContext instanceof NewCollectionActivity) {
            if (!hidden) {
                ((NewCollectionActivity) mContext).setOnEditorClickListen(new OnCollectionEditorListen() {
                    @Override
                    public void OnEditorOpen(String tag) {
                        if (tag.equals("sentence")) {
                            editorOrCancel();
                        }
                    }

                    @Override
                    public void OnEditorCancel(String tag) {
                        if (tag.equals("sentence")) {
                            editorOrCancel();
                        }
                    }

                    @Override
                    public void OnSelectAll(String tag, boolean selectAll) {
                        if (tag.equals("sentence")) {
                            selectAllOrNot(selectAll);
                        }
                    }

                    @Override
                    public void OnDeleteData(String tag) {
                        if (tag.equals("sentence")) {
                            deleteSentence();
                        }
                    }
                });
            }
        }
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
                    deleteSentence();
                }
            }
        });


        if (mContext instanceof NewCollectionActivity) {
            ((NewCollectionActivity) mContext).setOnEditorClickListen(new OnCollectionEditorListen() {
                @Override
                public void OnEditorOpen(String tag) {
                    if (tag.equals("sentence")) {
                        editorOrCancel();
                    }
                }

                @Override
                public void OnEditorCancel(String tag) {
                    if (tag.equals("sentence")) {
                        editorOrCancel();
                    }
                }

                @Override
                public void OnSelectAll(String tag, boolean selectAll) {
                    if (tag.equals("sentence")) {
                        selectAllOrNot(selectAll);
                    }
                }

                @Override
                public void OnDeleteData(String tag) {
                    if (tag.equals("sentence")) {
                        deleteSentence();
                    }
                }
            });
        }
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!isEditor && !refreshing) {
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
        adapter.setOnSentenceCollectionItemClick(new SentenceCollectionAdapter.OnSentenceCollectionItemClick() {
            @Override
            public void onItemClick(View view) {

                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (isEditor) {
//                        itemSelected(pos);
                        selectedOrNot(pos);
                    }else if (isChoose) {
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
                    } else {
                        turnToArticleDetail(pos);
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
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
     * 练习
     */
    private void practice() {
        String practice = mList.get(mSelectedPosition).getContent();
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
            rl_editor.setVisibility(View.VISIBLE);
            iv_practice.setImageResource(R.drawable.icon_practice_cancle);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setSelected(false);
                mList.get(i).setEditor(true);
            }
        } else {
            iv_practice.setImageResource(R.drawable.icon_practice);
            rl_editor.setVisibility(View.GONE);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setEditor(false);
                mList.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 全选或者取消全选
     *
     * @param selectAll
     */
    private void selectAllOrNot(boolean selectAll) {
        for (int i = 0; i < mList.size(); i++) {
            if (selectAll) {
                checked(i);
            } else {
                unchecked(i);
            }
        }
        updateDeleteButtonBg();
    }

    /**
     * 查看文章详细内容
     *
     * @param pos
     */
    private void turnToArticleDetail(int pos) {
        SentenceBean sentenceBean = mList.get(pos);
        Intent intent = new Intent(getContext(), DailySentenceActivity.class);
        intent.putExtra("sentenceId", sentenceBean.getId());
        startActivity(intent);
    }

    /**
     * 选中或取消选中
     *
     * @param pos
     */
    private void selectedOrNot(int pos) {
        SentenceBean sentenceBean = mList.get(pos);
        if (sentenceBean.isSelected()) {
            unchecked(pos);
        } else {
            checked(pos);
        }
        updateDeleteButtonBg();
    }

    /**
     * 选中
     *
     * @param pos
     */
    private void checked(int pos) {
        mList.get(pos).setSelected(true);
        adapter.notifyItemChanged(pos);
        selectedNum++;
    }

    /**
     * 不选中
     *
     * @param pos
     */
    private void unchecked(int pos) {
        mList.get(pos).setSelected(false);
        adapter.notifyItemChanged(pos);
        selectedNum--;
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

    private void deleteSentence() {
        if (selectedNum != 0) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).isSelected()) {
                    mSelectedList.add(mList.get(i));
                    array.put(mList.get(i).getId());
                }
            }

            if (mSelectedList.size() != 0) {
                new DeleteData(this).execute(deleteUrl, array.toString());
            }

            for (int j = 0; j < mSelectedList.size(); j++) {
                mList.remove(mSelectedList.get(j));
            }
            mSelectedList.clear();

            adapter.notifyDataSetChanged();
        }

        editorOrCancel();
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        pageNum++;
        refreshing = true;
        new GetData(this).execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                "&pageNum=" + pageNum + "&pageSize=10");
    }

    /**
     * 点击了编辑按钮
     *
     * @param
     */
    public void editorOrCancel() {
        if(isEditor)
            isEditor = false;
        else
            isEditor = true;
//        for (int i = 0; i < mList.size(); i++) {
//            mList.get(i).setEditor(isEditor);
//            mList.get(i).setSelected(false);
//        }

        editorOrNot(isEditor);
//        adapter.notifyDataSetChanged();
    }

    public boolean getEditor(){
        return isEditor;
    }

    private void initData() {
        if (!refreshing) {
            pageNum = 1;
            refreshing = true;
            isEnd = false;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(this).execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                    "&pageNum=" + pageNum + "&pageSize=10");
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_sentence_new_collection);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_sentence_new_collection);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        recyclerView = view.findViewById(R.id.recycler_view_sentence_new_collection);
        recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new SentenceCollectionAdapter(getContext(), mList);
        recyclerView.setAdapter(adapter);

        rl_editor = view.findViewById(R.id.rl_editor_excerpt_fragment);
        ll_select_all = view.findViewById(R.id.ll_select_all_new_collection_bottom);
        iv_select_all = view.findViewById(R.id.iv_select_all_new_collection_bottom);
        tv_delete = view.findViewById(R.id.tv_delete_new_collection_bottom);

        tv_one = view.findViewById(R.id.tv_one_tips_note);
        tv_two = view.findViewById(R.id.tv_two_tips_note);
        tv_three = view.findViewById(R.id.tv_three_tips_note);
        tv_one.setText("在每日一句");
        tv_two.setText("进行收藏添加句子");
        tv_three.setText("");
        tv_three.setVisibility(View.GONE);

        tv_sure = view.findViewById(R.id.tv_sure_sentence_fragment);
        iv_practice = view.findViewById(R.id.iv_practice_sentence_fragment);
        if (showPractice) {
            iv_practice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据之后
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        SentenceBean sentenceBean = new SentenceBean();
                        sentenceBean.setId(object.optLong("id", -1));
                        if (object.getString("time").equals("")
                                || object.getString("time").equals("null")) {
                            sentenceBean.setDate("2018-01-01 00:00");
                        } else {
                            sentenceBean.setDate(DateUtil.time2Format(object.getString("time")));
                        }
                        sentenceBean.setAuthor(object.getString("author"));
                        sentenceBean.setContent(object.getString("content"));
                        sentenceBean.setEditor(isEditor);
                        sentenceBean.setSelected(false);
                        mList.add(sentenceBean);
                    }
                    adapter.notifyDataSetChanged();
                    if (mListen != null) {
                        mListen.onLoadResult(true);
                    }
                }
            } else if (400 == json.optInt("status", -1)) {
                emptyData();
            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 获取数据为空
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
            tv_one.setText("在每日一句");
            tv_two.setText("进行收藏添加句子");
            tv_three.setVisibility(View.GONE);
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

    private OnListDataOperateListen mListen;

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
            MyToastUtil.showToast(getContext(), tips);
        }
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, SentenceCollectionFragment> {

        protected GetData(SentenceCollectionFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SentenceCollectionFragment fragment, String[] params) {
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
        protected void onPostExecute(SentenceCollectionFragment fragment, String s) {
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
     * 删除文章
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Integer, String, SentenceCollectionFragment> {

        protected DeleteData(SentenceCollectionFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(SentenceCollectionFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("ids", params[1]);
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("status", 0);
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
        protected void onPostExecute(SentenceCollectionFragment fragment, String s) {

        }
    }

    public interface HasSentenceItemSelected {
        void hasSelected(boolean hasSelected);
    }

    private HasSentenceItemSelected mHasSentenceItemSelected;

    public void setHasSentenceItemSelectedListen(HasSentenceItemSelected hasSentenceItemSelectedListen) {
        this.mHasSentenceItemSelected = hasSentenceItemSelectedListen;
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
}
