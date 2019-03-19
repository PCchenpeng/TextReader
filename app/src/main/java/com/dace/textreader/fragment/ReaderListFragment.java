package com.dace.textreader.fragment;

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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.LoginActivity;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.adapter.ChildRecyclerViewAdapter;
import com.dace.textreader.bean.Article;
import com.dace.textreader.util.DataUtil;
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
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dace.textreader.activity.NewReaderActivity.deleteTexts;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.activity
 * Created by Administrator.
 * Created time 2018/7/4 0004 下午 4:33.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class ReaderListFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/essays?";
    private static final String deleteUrl = HttpUrlPre.HTTP_URL + "/essay/feedback";

    private View view;

    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private List<Article> mList = new ArrayList<>();
    private ChildRecyclerViewAdapter adapter;

    private int position = -1;

    private boolean isEnd = false;
    private boolean isLoading = false;
    private boolean isRefresh = false;
    private int pageNum = 1;  //当前文章页
    private int type = 0;  //当前文章类型
    private int level = -1;  //当前文章等级

    private boolean hidden = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reader_list, container, false);

        initView();
        initEvents();

        return view;
    }

    private void initLocalData() {
        LitePal.where("type = ?", String.valueOf(type))
                .findAsync(Article.class).listen(new FindMultiCallback<Article>() {
            @Override
            public void onFinish(List<Article> list) {
                mList.addAll(list);
                adapter.notifyDataSetChanged();
                initData();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.hidden = !isVisibleToUser;
    }

    private void initEvents() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    initData();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!isEnd && !isLoading) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnItemClickListener(new ChildRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                turnToArticleDetail(position);
            }
        });
        adapter.setOnDeleteClickListen(new ChildRecyclerViewAdapter.OnDeleteClickListen() {
            @Override
            public void onDelete(int position) {
                if (!isLoading) {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        showDeleteArticleDialog(position);
                    }
                }
            }
        });
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_reader_list_fragment);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_reader_list_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_reader_list_fragment);
        layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChildRecyclerViewAdapter(getContext(), mList);
        recyclerView.setAdapter(adapter);

        initLocalData();
    }

    /**
     * 更新文章列表
     */
    public void initData() {
        if (!isLoading) {
            if (frameLayout.getVisibility() == View.VISIBLE) {
                frameLayout.removeAllViews();
                frameLayout.setVisibility(View.GONE);
            }
            swipeRefreshLayout.setRefreshing(true);
            isLoading = true;
            isRefresh = true;
            isEnd = false;
            pageNum = 1;
            new GetData(this).execute(url + "studentId=" + NewMainActivity.STUDENT_ID +
                    "&type=" + type + "&essayGrade=" + level + "&pageNum=" + pageNum);
        }
    }

    /**
     * 获取更多
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            isLoading = true;
            isRefresh = false;
            pageNum++;
            new GetData(this).execute(url + "studentId=" + NewMainActivity.STUDENT_ID + "&type=" + type +
                    "&essayGrade=" + level + "&pageNum=" + pageNum);
        }
    }

    /**
     * 跳转至文章详情页
     *
     * @param position
     */
    private void turnToArticleDetail(int position) {
        this.position = position;
        Intent intent = new Intent(getContext(), NewArticleDetailActivity.class);
        intent.putExtra("id", mList.get(position).getId());
        intent.putExtra("type", mList.get(position).getType());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (position != -1) {
                if (data.getBooleanExtra("clickLike", false)) {
                    int likeNum = mList.get(position).getLikeNum();
                    mList.get(position).setLikeNum(likeNum + 1);
                }
                int views = mList.get(position).getViews();
                mList.get(position).setViews(views + 1);
                adapter.notifyItemChanged(position);
                position = -1;
            }
        }
    }

    /**
     * 跳转到登录
     */
    private void turnToLogin() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    /**
     * 显示删除文章的对话框
     */
    private void showDeleteArticleDialog(final int pos) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_delete_article_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final TextView tv_one = holder.getView(R.id.tv_one_delete_article_dialog);
                        final TextView tv_two = holder.getView(R.id.tv_two_delete_article_dialog);
                        final TextView tv_three = holder.getView(R.id.tv_three_delete_article_dialog);
                        final TextView tv_four = holder.getView(R.id.tv_four_delete_article_dialog);
                        final TextView tv_five = holder.getView(R.id.tv_five_delete_article_dialog);
                        final TextView tv_six = holder.getView(R.id.tv_six_delete_article_dialog);
                        final EditText et_other = holder.getView(R.id.et_other_delete_article_dialog);
                        final TextView tv_commit = holder.getView(R.id.tv_commit_delete_article_dialog);
                        if (deleteTexts[0].equals("")) {
                            tv_one.setVisibility(View.GONE);
                        } else {
                            tv_one.setText(deleteTexts[0]);
                        }
                        if (deleteTexts[1].equals("")) {
                            tv_two.setVisibility(View.GONE);
                        } else {
                            tv_two.setText(deleteTexts[1]);
                        }
                        if (deleteTexts[2].equals("")) {
                            tv_three.setVisibility(View.GONE);
                        } else {
                            tv_three.setText(deleteTexts[2]);
                        }
                        if (deleteTexts[3].equals("")) {
                            tv_four.setVisibility(View.GONE);
                        } else {
                            tv_four.setText(deleteTexts[3]);
                        }
                        if (deleteTexts[4].equals("")) {
                            tv_five.setVisibility(View.GONE);
                        } else {
                            tv_five.setText(deleteTexts[4]);
                        }
                        if (deleteTexts[5].equals("")) {
                            tv_six.setVisibility(View.GONE);
                        } else {
                            tv_six.setText(deleteTexts[5]);
                        }
                        tv_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_one.isSelected()) {
                                    tv_one.setSelected(false);
                                    if (!tv_two.isSelected() && !tv_three.isSelected()
                                            && !tv_four.isSelected() && !tv_five.isSelected()
                                            && !tv_six.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_one.setSelected(true);
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        tv_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_two.isSelected()) {
                                    tv_two.setSelected(false);
                                    if (!tv_one.isSelected() && !tv_three.isSelected()
                                            && !tv_four.isSelected() && !tv_five.isSelected()
                                            && !tv_six.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_two.setSelected(true);
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        tv_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_three.isSelected()) {
                                    tv_three.setSelected(false);
                                    if (!tv_two.isSelected() && !tv_one.isSelected()
                                            && !tv_four.isSelected() && !tv_five.isSelected()
                                            && !tv_six.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_three.setSelected(true);
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        tv_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_four.isSelected()) {
                                    tv_four.setSelected(false);
                                    if (!tv_two.isSelected() && !tv_one.isSelected()
                                            && !tv_three.isSelected() && !tv_five.isSelected()
                                            && !tv_six.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_four.setSelected(true);
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        tv_five.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_five.isSelected()) {
                                    tv_five.setSelected(false);
                                    if (!tv_two.isSelected() && !tv_one.isSelected()
                                            && !tv_three.isSelected() && !tv_four.isSelected()
                                            && !tv_six.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_five.setSelected(true);
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        tv_six.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_six.isSelected()) {
                                    tv_six.setSelected(false);
                                    if (!tv_two.isSelected() && !tv_one.isSelected()
                                            && !tv_three.isSelected() && !tv_five.isSelected()
                                            && !tv_four.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_six.setSelected(true);
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        et_other.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                        et_other.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (et_other.getText().toString().length() == 0) {
                                    if (!tv_one.isSelected() && !tv_two.isSelected() && !tv_three.isSelected() && !tv_four.isSelected()) {
                                        tv_commit.setSelected(false);
                                    }
                                } else {
                                    tv_commit.setSelected(true);
                                }
                            }
                        });
                        tv_commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tv_commit.isSelected()) {
                                    String feedback = "";
                                    if (tv_one.isSelected()) {
                                        feedback = feedback + tv_one.getText().toString();
                                    }
                                    if (tv_two.isSelected()) {
                                        if (feedback.equals("")) {
                                            feedback = feedback + "，";
                                        }
                                        feedback = feedback + tv_two.getText().toString();
                                    }
                                    if (tv_three.isSelected()) {
                                        if (feedback.equals("")) {
                                            feedback = feedback + "，";
                                        }
                                        feedback = feedback + tv_three.getText().toString();
                                    }
                                    if (tv_four.isSelected()) {
                                        if (feedback.equals("")) {
                                            feedback = feedback + "，";
                                        }
                                        feedback = feedback + tv_four.getText().toString();
                                    }
                                    if (tv_five.isSelected()) {
                                        if (feedback.equals("")) {
                                            feedback = feedback + "，";
                                        }
                                        feedback = feedback + tv_five.getText().toString();
                                    }
                                    if (tv_six.isSelected()) {
                                        if (feedback.equals("")) {
                                            feedback = feedback + "，";
                                        }
                                        feedback = feedback + tv_six.getText().toString();
                                    }
                                    String other = et_other.getText().toString();
                                    if (!et_other.getText().toString().trim().isEmpty()) {
                                        if (feedback.equals("")) {
                                            feedback = feedback + "，";
                                        }
                                        feedback = feedback + other;
                                    }
                                    MyToastUtil.showToast(getContext(), "已为您减少相关内容");
                                    removeArticle(pos, feedback);
                                    dialog.dismiss();
                                } else {
                                    MyToastUtil.showToast(getContext(), "请选择或输入内容");
                                }
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setWidth(320)
                .setHeight(200)
                .setOutCancel(true)
                .show(getChildFragmentManager());
    }

    /**
     * 移除文章
     *
     * @param position
     */
    private void removeArticle(int position, String feedback) {
        long essayId = mList.get(position).getId();
        new DeleteData(this)
                .execute(deleteUrl, feedback, String.valueOf(essayId));
        mList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * 分析文章列表数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (json.getString("status").equals("200")) {
                JSONArray jsonArray = json.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    if (isRefresh) {  //如果是刷新的话就更新数据
                        LitePal.deleteAll(Article.class, "type = ?", String.valueOf(type));
                        mList.clear();
                    }
                    List<Article> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject essay = jsonObject.getJSONObject("essay");

                        Article article = new Article();
                        article.setId(essay.optLong("id", -1));
                        article.setType(essay.optInt("type", -1));
                        article.setTitle(essay.getString("title"));
                        article.setContent(essay.getString("content"));
                        article.setGrade(essay.optInt("grade", 110));
                        article.setPyScore(essay.getString("score"));
                        article.setStatus(essay.optInt("status", -1));
                        article.setViews(essay.optInt("pv", 0));
                        article.setImagePath(essay.getString("image"));
                        if (isRefresh) {
                            article.save();
                            mList.add(article);
                        } else {
                            list.add(article);
                        }
                    }
                    if (isRefresh) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.addData(list);
                    }
                }
            } else if (400 == json.optInt("status", -1)) {
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
     * 获取数据失败
     */
    private void errorConnect() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            tv_tips.setText("获取数据失败，请稍后再试");
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
            if (!hidden) {
                MyToastUtil.showToast(getContext(), "获取数据失败，请稍后再试");
            }
        }
    }

    /**
     * 获取的数据为空
     */
    private void emptyData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty, imageView);
            tv_tips.setText("筛选无内容～\n点击右上角换个py等级试试");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 获取文章列表数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, ReaderListFragment> {

        protected GetData(ReaderListFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ReaderListFragment fragment, String[] strings) {
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
        protected void onPostExecute(ReaderListFragment fragment, String s) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                fragment.errorConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.isLoading = false;
            fragment.isRefresh = false;
        }
    }

    /**
     * 删除数据
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Void, String, ReaderListFragment> {

        protected DeleteData(ReaderListFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(ReaderListFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("feedback", strings[1]);
                object.put("phoneNum", NewMainActivity.PHONENUMBER);
                object.put("essayId", strings[2]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ReaderListFragment fragment, String s) {

        }
    }
}
