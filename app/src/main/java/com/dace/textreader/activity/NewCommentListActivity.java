package com.dace.textreader.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.CommentRecyclerViewAdapter;
import com.dace.textreader.bean.Comment;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
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
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/4/12 0012 上午 10:39.
 * Version   1.0;
 * Describe :  文章详情的评论部分
 * History:
 * ==============================================================================
 */

public class NewCommentListActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/essays/commentList?";
    private static final String commentUrl = HttpUrlPre.HTTP_URL + "/essays/comment";
    private static final String deleteUrl = HttpUrlPre.HTTP_URL + "/essays/comment/delete";

    private RelativeLayout rl_back;
    private TextView tv_title;

    private FrameLayout frameLayout;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private RelativeLayout rl_comment;

    private NewCommentListActivity mContext;

    private long essayId = -1;  //文章ID
    private int essayType = -1;  //文章类型
    private String essayTitle = "";  //文章标题

    private List<Comment> mList = new ArrayList<>();
    public static int mCommentNumber = 0;
    private CommentRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private String curTime;

    private int pageNum = 1;
    private boolean isLoading = false;
    private boolean isEnd = false;

    private int deletePosition = -1;  //要删除的评论的ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment_list);

        mContext = this;

        essayId = getIntent().getLongExtra("essayId", -1L);
        essayType = getIntent().getIntExtra("essayType", -1);
        essayTitle = getIntent().getStringExtra("essayTitle");
        curTime = DateUtil.getTodayDateTime();

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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    initData();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!isLoading && !isEnd) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnCommentItemClickListener(new CommentRecyclerViewAdapter.OnCommentItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                showCommentOptions(pos - 1);  //有一个topView，所以减一
            }
        });
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyDialog(-1, -1);
            }
        });
    }

    /**
     * 显示评论操作对话框
     *
     * @param pos
     */
    private void showCommentOptions(final int pos) {
        Comment comment = mList.get(pos);
        final int userId = comment.getCommentUserId();
        final long commentId = comment.getCommentId();
        final String commentContent = comment.getCommentContent();
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_comment_item_click_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_reply = holder.getView(R.id.tv_reply_comment_dialog);
                        TextView tv_copy = holder.getView(R.id.tv_copy_comment_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_comment_dialog);
                        if (userId == NewMainActivity.STUDENT_ID) {
                            tv_reply.setText("删除");
                        }
                        tv_reply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (userId == NewMainActivity.STUDENT_ID) {
                                    deleteComment(pos, commentId);
                                } else {
                                    replyDialog(userId, commentId);
                                }
                                dialog.dismiss();
                            }
                        });
                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                copyContent(commentContent);
                                dialog.dismiss();
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
                .show(getSupportFragmentManager());
    }

    /**
     * 删除自己的评论
     */
    private void deleteComment(int pos, long commentId) {
        this.deletePosition = pos;
        new DeleteCommentData(mContext).execute(deleteUrl, String.valueOf(commentId));
    }

    /**
     * 复制内容到截切板
     *
     * @param str 要复制的内容
     */
    private void copyContent(String str) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("Label", str);
        if (cm != null) {
            cm.setPrimaryClip(cd);
            showTips("复制成功");
        }
    }

    /**
     * 评论
     *
     * @param userId
     * @param commentId
     */
    private void replyDialog(final int userId, final long commentId) {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
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
                                            getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                        new CommitCommentData(mContext).execute(commentUrl,
                                                editText.getText().toString(),
                                                String.valueOf(essayId), String.valueOf(essayType),
                                                String.valueOf(userId), String.valueOf(commentId),
                                                essayTitle);
                                        InputMethodManager imm = (InputMethodManager)
                                                getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    .show(getSupportFragmentManager());
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    private void initData() {
        swipeRefreshLayout.setRefreshing(true);
        pageNum = 1;
        isLoading = true;
        isEnd = false;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetCommentData(this)
                .execute(url + "essayId=" + essayId + "&type=" + essayType + "&pageNum=" + pageNum +
                        "&pageSize=10");
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("评论");

        frameLayout = findViewById(R.id.frame_article_comment_fragment);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_article_comment_fragment);
        recyclerView = findViewById(R.id.recycler_view_article_comment_fragment);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        rl_comment = findViewById(R.id.rl_comment_article_comment_fragment);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            isLoading = true;
            new GetCommentData(this)
                    .execute(url + "essayId=" + essayId + "&type=" + essayType + "&pageNum=" + pageNum +
                            "&pageSize=10");
        }
    }

    /**
     * 分析数据
     *
     * @param s 获取得到的数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.optInt("status", -1) == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                mCommentNumber = data.optInt("size", 0);
                JSONArray jsonArray = data.getJSONArray("comment");
                if (mCommentNumber == 0 && jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setEssayId(json.optLong("essayId", -1));
                        comment.setEssayType(json.optInt("essayType", -1));
                        comment.setCommentId(json.optLong("originalCommentId", -1));
                        String commentTime = json.getString("originalCommentTime");
                        if (commentTime.equals("null")) {
                            commentTime = "2018-01-01 00:00";
                        } else {
                            commentTime = DateUtil.time2YMD(commentTime);
                        }
                        comment.setCommentTime(DateUtil.getTimeDiff_(commentTime, curTime));
                        comment.setCommentContent(json.getString("originalCommentContent"));
                        if (json.getString("originalCommenterId").equals("null")) {
                            comment.setCommentUserId(-1);
                        } else {
                            comment.setCommentUserId(json.optInt("originalCommenterId", -1));
                        }
                        comment.setCommentUsername(json.getString("commenterName"));
                        comment.setCommentUserImg(json.getString("commenterImg"));
                        if (json.getString("replyCommentId").equals("null")) {
                            comment.setReplyCommentId(-1);
                        } else {
                            comment.setReplyCommentId(json.optInt("replyCommentId", -1));
                        }
                        String replyTime = json.getString("replyCommentTime");
                        if (replyTime.equals("null")) {
                            replyTime = "2018-01-01 00:00";
                        } else {
                            replyTime = DateUtil.time2YMD(replyTime);
                        }
                        comment.setReplyCommentTime(DateUtil.getTimeDiff_(replyTime, curTime));
                        comment.setReplyCommentContent(json.getString("replyCommentContent"));
                        if (json.getString("replyCommenterId").equals("null")) {
                            comment.setReplyUserId(-1);
                        } else {
                            comment.setReplyUserId(json.optInt("replyCommenterId", -1));
                        }
                        comment.setReplyUsername(json.getString("replyCommenterName"));
                        comment.setReplyUserImg(json.getString("replyCommenterImg"));
                        mList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorConnect();
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty_comment, imageView);
            tv_tips.setText("暂无评论哦~");
            tv_reload.setVisibility(View.VISIBLE);
            tv_reload.setText("说点什么~");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyDialog(-1, -1);
                }
            });
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
            showTips("获取数据失败，请稍后重试");
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
                JSONObject json = jsonObject.getJSONObject("data");
                Comment comment = new Comment();
                comment.setEssayId(json.optLong("essayId", -1));
                comment.setEssayType(json.optInt("essayType", -1));
                if (json.getString("originalCommentId").equals("null")) {
                    comment.setCommentId(-1);
                } else {
                    comment.setCommentId(json.optLong("originalCommentId", -1));
                }
                String commentTime = json.getString("originalCommentTime");
                if (commentTime.equals("null")) {
                    commentTime = "2018-01-01 00:00";
                } else {
                    commentTime = DateUtil.time2YMD(commentTime);
                }
                comment.setCommentTime(DateUtil.getTimeDiff_(commentTime, curTime));
                comment.setCommentContent(json.getString("originalCommentContent"));
                if (json.getString("originalCommenterId").equals("null")) {
                    comment.setCommentUserId(-1);
                } else {
                    comment.setCommentUserId(json.optInt("originalCommenterId", -1));
                }
                comment.setCommentUsername(json.getString("commenterName"));
                comment.setCommentUserImg(json.getString("commenterImg"));
                if (json.getString("replyCommentId").equals("null")) {
                    comment.setReplyCommentId(-1);
                } else {
                    comment.setReplyCommentId(json.optInt("replyCommentId", -1));
                }
                String replyTime = json.getString("replyCommentTime");
                if (replyTime.equals("null")) {
                    replyTime = "2018-01-01 00:00";
                } else {
                    replyTime = DateUtil.time2YMD(replyTime);
                }
                comment.setReplyCommentTime(DateUtil.getTimeDiff_(replyTime, curTime));
                comment.setReplyCommentContent(json.getString("replyCommentContent"));
                if (json.getString("replyCommenterId").equals("null")) {
                    comment.setReplyUserId(-1);
                } else {
                    comment.setReplyUserId(json.optInt("replyCommenterId", -1));
                }
                comment.setReplyUsername(json.getString("replyCommenterName"));
                comment.setReplyUserImg(json.getString("replyCommenterImg"));
                mList.add(0, comment);
                mCommentNumber = mCommentNumber + 1;
                if (frameLayout.getVisibility() == View.VISIBLE) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                }
                adapter.notifyDataSetChanged();
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
        showTips("评论失败");
    }

    /**
     * 分析删除评论的返回数据
     *
     * @param s
     */
    private void analyzeDeleteCommentData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (deletePosition != -1) {
                    mList.remove(deletePosition);
                    mCommentNumber = mCommentNumber - 1;
                    adapter.notifyDataSetChanged();
                    deletePosition = -1;
                    if (mList.size() == 0) {
                        emptyData();
                    }
                }
            } else {
                errorDeleteComment();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorDeleteComment();
        }
    }

    /**
     * 删除评论失败
     */
    private void errorDeleteComment() {
        showTips("删除评论失败");
    }


    /**
     * 获取评论数据
     */
    private static class GetCommentData
            extends WeakAsyncTask<String, Integer, String, NewCommentListActivity> {

        protected GetCommentData(NewCommentListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewCommentListActivity activity, String[] params) {
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
        protected void onPostExecute(NewCommentListActivity activity, String s) {
            activity.swipeRefreshLayout.setRefreshing(false);
            activity.isLoading = false;
            if (s == null) {
                activity.errorConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 提交评论数据
     */
    private static class CommitCommentData
            extends WeakAsyncTask<String, Integer, String, NewCommentListActivity> {

        protected CommitCommentData(NewCommentListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewCommentListActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("commentContent", params[1]);
                json.put("commentUserId", NewMainActivity.STUDENT_ID);
                json.put("replyEssayId", params[2]);
                json.put("replyEssayType", params[3]);
                json.put("replyUserId", params[4]);
                json.put("replyCommentId", params[5]);
                json.put("replyEssayTitle", params[6]);
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
        protected void onPostExecute(NewCommentListActivity activity, String s) {
            if (s == null) {
                activity.errorComment();
            } else {
                activity.analyzeCommentData(s);
            }
        }
    }

    /**
     * 删除自己的评论数据
     */
    private static class DeleteCommentData
            extends WeakAsyncTask<String, Integer, String, NewCommentListActivity> {

        protected DeleteCommentData(NewCommentListActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(NewCommentListActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("commentUserId", NewMainActivity.STUDENT_ID);
                json.put("commentId", params[1]);
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
        protected void onPostExecute(NewCommentListActivity activity, String s) {
            if (s == null) {
                activity.errorDeleteComment();
            } else {
                activity.analyzeDeleteCommentData(s);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
