package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.UnReadWritingCommentAdapter;
import com.dace.textreader.bean.UnReadWritingComment;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

import org.json.JSONArray;
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
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment_card
 * Created by Administrator.
 * Created time 2018/7/27 0027 下午 4:09.
 * Version   1.0;
 * Describe :  作文评论未读消息
 * History:
 * ==============================================================================
 */

public class UnReadWritingCommentsFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/me/unreadComments/composition?";
    private static final String commentUrl = HttpUrlPre.HTTP_URL + "/composition/comment";
    private static final String markUrl = HttpUrlPre.HTTP_URL + "/message/view/all";

    private View view;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private FrameLayout frameLayout;

    private Context mContext;

    private List<UnReadWritingComment> mList = new ArrayList<>();
    private UnReadWritingCommentAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;
    private String curTime;  //当前时间

    private boolean hidden = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_un_read_comments, container, false);

        initView();
        initData();
        initEvents();

        return view;
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
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing) {
                    initData();
                }
            }
        });
        //上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!refreshing && !isEnd) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnReplyClickListener(new UnReadWritingCommentAdapter.OnUnReadWritingCommentReplyClick() {
            @Override
            public void onItemClick(int position) {
                replyDialog(position);
            }
        });
        adapter.setOnWritingClickListener(new UnReadWritingCommentAdapter.OnUnReadCommentWritingClickListener() {
            @Override
            public void onItemClick(int position) {
                turnToArticle(position);
            }
        });
    }

    /**
     * 前往文章详情
     *
     * @param position
     */
    private void turnToArticle(int position) {
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", mList.get(position).getId());
        intent.putExtra("area", 0);
        intent.putExtra("orderNum", "");
        startActivity(intent);
    }

    /**
     * 回复
     */
    private void replyDialog(int position) {
        final String id = mList.get(position).getId();
        final int type = mList.get(position).getType();
        final int userId = mList.get(position).getCommentUserId();
        final long commentId = mList.get(position).getCommentId();
        final String title = mList.get(position).getTitle();
        NiceDialog.init()
                .setLayoutId(R.layout.write_comment_dialog_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final EditText editText = holder.getView(R.id.edit_input);
                        final TextView textView = holder.getView(R.id.tv_content_number);
                        final TextView tv_commit = holder.getView(R.id.tv_dialog_comment_commit);
                        TextView tv_cancel = holder.getView(R.id.tv_dialog_comment_cancel);
                        textView.setText(String.valueOf(100));
                        editText.setHint("请输入评论");
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                int left = 100 - editText.getText().toString().length();
                                textView.setText(String.valueOf(left));
                                if (left == 100) {
                                    tv_commit.setSelected(false);
                                } else {
                                    tv_commit.setSelected(true);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        editText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager)
                                        mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(editText, 0);
                            }
                        });
                        tv_commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String str = editText.getText().toString().trim();
                                if (TextUtils.isEmpty(str)) {
                                    MyToastUtil.showToast(mContext, "请输入内容");
                                } else {
                                    new CommitCommentData(UnReadWritingCommentsFragment.this)
                                            .execute(commentUrl,
                                                    editText.getText().toString(),
                                                    id, String.valueOf(type),
                                                    String.valueOf(userId), String.valueOf(commentId),
                                                    title);
                                    InputMethodManager imm = (InputMethodManager)
                                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null && imm.isActive()) {
                                        imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                                    }
                                    dialog.dismiss();
                                }
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getChildFragmentManager());
    }

    //加载更多
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            curTime = DateUtil.getTodayDateTime();
            new GetCommentData(this)
                    .execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                            "&type=-1" +
                            "&pageNum=" + pageNum);
        }
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        refreshing = true;
        isEnd = false;
        pageNum = 1;
        mList.clear();
        adapter.notifyDataSetChanged();
        curTime = DateUtil.getTodayDateTime();
        new GetCommentData(this)
                .execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                        "&type=-1" +
                        "&pageNum=" + pageNum);
    }

    /**
     * 获取评论数据
     */
    private static class GetCommentData
            extends WeakAsyncTask<String, Integer, String, UnReadWritingCommentsFragment> {

        protected GetCommentData(UnReadWritingCommentsFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(UnReadWritingCommentsFragment fragment, String[] params) {
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
        protected void onPostExecute(UnReadWritingCommentsFragment fragment, String s) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_msg, imageView);
            tv_tips.setText("暂无未读消息哦~");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无网络
     */
    private void noConnect() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mContext == null) {
            return;
        }
        if (mList.size() == 0) {
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
        } else {
            showTips("获取数据失败");
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
            if (jsonObject.optInt("status", -1) == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        UnReadWritingComment comment = new UnReadWritingComment();

                        JSONObject json = object.getJSONObject("commentInfo");
                        if (json.getString("originalCommentId").equals("") ||
                                json.getString("originalCommentId").equals("null")) {
                            comment.setCommentId(-1);
                        } else {
                            comment.setCommentId(json.optLong("originalCommentId", -1));
                        }
                        String commentTime = json.getString("originalCommentTime");
                        String t;
                        if (commentTime.equals("") || commentTime.equals("null")) {
                            t = "2018-01-01 00:00";
                        } else {
                            t = DateUtil.timeYMD(commentTime);
                        }
                        comment.setCommentTime(DateUtil.getTimeDiff_(t, curTime));
                        comment.setCommentUserId(json.optInt("originalCommenterId", -1));
                        comment.setCommentUsername(json.getString("originalCommenterName"));
                        comment.setCommentContent(json.getString("originalCommentContent"));
                        comment.setCommentUserImg(json.getString("originalCommenterImg"));

                        if (json.getString("replyCommenterId").equals("") ||
                                json.getString("replyCommenterId").equals("null")) {
                            comment.setReplyUserId(-1);
                        } else {
                            comment.setReplyUserId(json.optInt("replyCommenterId", -1));
                        }
                        comment.setReplyUsername(json.getString("replyCommenterName"));
                        comment.setReplyUserImg(json.getString("replyCommenterImg"));
                        comment.setReplyCommentContent(json.getString("replyCommentContent"));

                        if (json.getString("reReplyCommentId").equals("") ||
                                json.getString("reReplyCommentId").equals("null")) {
                            comment.setReReplyUserId(-1);
                            comment.setReReplyUsername("");
                            comment.setReReplyUserImg("");
                        } else {
                            comment.setReReplyUserId(json.optInt("reReplyCommentId", -1));
                            comment.setReReplyUsername(json.getString("reReplyCommenterName"));
                            comment.setReReplyUserImg(json.getString("reReplyCommenterImg"));
                        }
                        comment.setId(json.getString("compositionId"));
                        comment.setType(json.optInt("compositionType", -1));
                        comment.setTitle(json.getString("compositionTitle"));
                        mList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                    markHasRead();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
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
     * 标记消息已读
     */
    private void markHasRead() {
        new MarkHasRead(this).execute(markUrl, "comment");
    }

    /**
     * 提交评论数据
     */
    private static class CommitCommentData
            extends WeakAsyncTask<String, Integer, String, UnReadWritingCommentsFragment> {

        protected CommitCommentData(UnReadWritingCommentsFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(UnReadWritingCommentsFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("commentContent", params[1]);
                json.put("commentUserId", NewMainActivity.STUDENT_ID);
                json.put("replyCompositionId", params[2]);
                json.put("replyCompositionType", params[3]);
                json.put("replyUserId", params[4]);
                json.put("replyCommentId", params[5]);
                json.put("replyCompositionTitle", params[6]);
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
        protected void onPostExecute(UnReadWritingCommentsFragment fragment, String s) {
            if (s == null) {
                fragment.errorComment();
            } else {
                fragment.analyzeCommentData(s);
            }
        }
    }

    /**
     * 分析评论返回的数据
     *
     * @param s
     */
    private void analyzeCommentData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showTips("回复成功");
            } else {
                errorComment();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorComment();
        }
    }

    /**
     * 评论失败
     */
    private void errorComment() {
        showTips("回复失败");
    }

    /**
     * 显示提示
     *
     * @param tips
     */
    private void showTips(String tips) {
        if (!hidden) {
            MyToastUtil.showToast(mContext, tips);
        }
    }

    private void initView() {

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_unread_comment_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_unread_comment_fragment);
        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new UnReadWritingCommentAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        frameLayout = view.findViewById(R.id.frame_unread_comments_fragment);
    }

    /**
     * 标记已读
     */
    private static class MarkHasRead
            extends WeakAsyncTask<String, Void, String, UnReadWritingCommentsFragment> {

        protected MarkHasRead(UnReadWritingCommentsFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(UnReadWritingCommentsFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("type", strings[1]);
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
        protected void onPostExecute(UnReadWritingCommentsFragment fragment, String s) {

        }
    }

}
