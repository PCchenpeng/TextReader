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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.SubmitReviewActivity;
import com.dace.textreader.activity.WritingActivity;
import com.dace.textreader.adapter.MyCompositionAdapter;
import com.dace.textreader.bean.WritingBean;
import com.dace.textreader.listen.OnShareClickListen;
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
 * Created time 2018/7/17 0017 下午 2:58.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class WritingMatchFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/composition/match/list";
    //删除草稿
    private static final String deleteUrl = HttpUrlPre.HTTP_URL + "/writing/delete";

    private View view;

    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private Context mContext;
    private LinearLayoutManager layoutManager;
    private List<WritingBean> mList = new ArrayList<>();
    private MyCompositionAdapter adapter;

    private int pageNum = 1;
    private boolean isEndForData = false;
    private boolean refreshing = false;  //是否正在刷新

    private int mSelectedPosition = -1; //当前选择操作的item索引

    public boolean isReady = false;

    private boolean hidden = true;

    private boolean isCorrection = false;  //是否是作文批改，是的话点击item跳转批改

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_writing_match, container, false);

        initView();
        initEvents();

        isReady = true;
        return view;
    }

    public void setCorrection(boolean correction) {
        isCorrection = correction;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.hidden = !isVisibleToUser;
        if (isVisibleToUser && isReady && !isCorrection) {
            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initEvents() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing && !isCorrection) {
                    initData();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isEndForData && !refreshing) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        adapter.setOnItemClickListen(new MyCompositionAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                if (!refreshing) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    if (position != -1 && position < mList.size()) {
                        if (isCorrection) {
                            int type = mList.get(position).getType();
                            if (type == 2) {
                                showTips("比赛作文不能提交批改~");
                            } else {
                                if (position != mSelectedPosition) {
                                    submitReview(position);
                                }
                            }
                        } else {
                            turnToWritingH5(position);
                        }
                    }
                }
            }
        });
        adapter.setOnItemOperateOneClick(new MyCompositionAdapter.OnItemOperateOneClick() {
            @Override
            public void onClick(int position) {
                int status = mList.get(position).getStatus();
                int type = mList.get(position).getType();
                if (status == 1) {
                    if (type == 2) {
                        turnToWriting(position);
                    } else {
                        submitReview(position);
                    }
                } else {
                    turnToWriting(position);
                }
            }
        });
        adapter.setOnItemOperateTwoClick(new MyCompositionAdapter.OnItemOperateTwoClick() {
            @Override
            public void onClick(int position) {
                int status = mList.get(position).getStatus();
                int type = mList.get(position).getType();
                if (status == 1) {
                    if (type == 2) {
                        WritingBean bean = mList.get(position);
                        onShareClickListen.onShare(bean.getId(), bean.getTitle(),
                                bean.getContent(), 2, bean.getFormat());
                    } else {
                        turnToWriting(position);
                    }
                } else {
                    WritingBean bean = mList.get(position);
                    onShareClickListen.onShare(bean.getId(), bean.getTitle(),
                            bean.getContent(), 2, bean.getFormat());
                }
            }
        });
        adapter.setOnItemOperateThreeClick(new MyCompositionAdapter.OnItemOperateThreeClick() {
            @Override
            public void onClick(int position) {
                int status = mList.get(position).getStatus();
                int type = mList.get(position).getType();
                if (status == 1) {
                    if (type == 2) {
                        deleteDraftData(position);
                    } else {
                        WritingBean bean = mList.get(position);
                        onShareClickListen.onShare(bean.getId(), bean.getTitle(),
                                bean.getContent(), 2, bean.getFormat());
                    }
                }
            }
        });
        adapter.setOnItemOperateMoreClick(new MyCompositionAdapter.OnItemOperateMoreClick() {
            @Override
            public void onClick(int position) {
                int status = mList.get(position).getStatus();
                int type = mList.get(position).getType();
                if (status == 1) {
                    if (type == 3) {
                        showOperateDialog(position);
                    }
                }
            }
        });
    }

    /**
     * 清除选中的item
     */
    public void clearSelectedItem() {
        if (mSelectedPosition != -1 && mSelectedPosition < mList.size()) {
            mList.get(mSelectedPosition).setSelected(false);
            adapter.notifyDataSetChanged();
        }
        mSelectedPosition = -1;
    }

    public interface OnItemCorrectionClick {
        void onClick(String id, String title, String taskId, int area, int type, int count);
    }

    private OnItemCorrectionClick onItemCorrectionClick;

    public void setOnItemCorrectionClick(OnItemCorrectionClick onItemCorrectionClick) {
        this.onItemCorrectionClick = onItemCorrectionClick;
    }

    /**
     * 前往作文H5页面
     */
    private void turnToWritingH5(int position) {
        String id = mList.get(position).getId();
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", id);
        intent.putExtra("area", 2);
        startActivity(intent);
    }

    /**
     * 显示操作对话框
     *
     * @param position
     */
    private void showOperateDialog(final int position) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_my_composition_operate_dialog)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        RelativeLayout rl_private =
                                holder.getView(R.id.rl_private_my_composition_operate_dialog);
                        RelativeLayout rl_work =
                                holder.getView(R.id.rl_work_my_composition_operate_dialog);
                        RelativeLayout rl_public =
                                holder.getView(R.id.rl_public_my_composition_operate_dialog);
                        RelativeLayout rl_events =
                                holder.getView(R.id.rl_events_my_composition_operate_dialog);
                        RelativeLayout rl_order =
                                holder.getView(R.id.rl_order_my_composition_operate_dialog);
                        RelativeLayout rl_delete =
                                holder.getView(R.id.rl_delete_my_composition_operate_dialog);
                        RelativeLayout rl_cancel =
                                holder.getView(R.id.rl_cancel_my_composition_operate_dialog);

                        rl_private.setVisibility(View.GONE);
                        rl_work.setVisibility(View.GONE);
                        rl_public.setVisibility(View.GONE);
                        rl_events.setVisibility(View.GONE);
                        rl_order.setVisibility(View.GONE);


                        rl_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDraftData(position);
                                dialog.dismiss();
                            }
                        });
                        rl_cancel.setOnClickListener(new View.OnClickListener() {
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

    public void initData() {
        if (!refreshing) {
            refreshing = true;
            swipeRefreshLayout.setRefreshing(true);
            isEndForData = false;
            mList.clear();
            adapter.notifyDataSetChanged();
            pageNum = 1;
            new GetData(this).execute(url,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum));
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_writing_match_fragment);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_writing_match_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_writing_match_fragment);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyCompositionAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            refreshing = true;
            new GetData(this).execute(url,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(pageNum));
        }
    }

    /**
     * 提交批改
     *
     * @param position
     */
    private void submitReview(int position) {
        String id = mList.get(position).getId();
        String title = mList.get(position).getTitle();
        int wordsNum = mList.get(position).getWordsNum();
        int type = mList.get(position).getType();
        String taskId = mList.get(position).getTaskId();
        if (isCorrection) {
            if (onItemCorrectionClick != null) {
                onItemCorrectionClick.onClick(id, title, taskId, 2, type, wordsNum);
            }
            mList.get(position).setSelected(true);
            adapter.notifyItemChanged(position);
            mSelectedPosition = position;
        } else {
            Intent intent = new Intent(mContext, SubmitReviewActivity.class);
            intent.putExtra("type", "writing");
            intent.putExtra("isEditor", false);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("count", wordsNum);
            intent.putExtra("writing_area", 2);
            intent.putExtra("writing_type", type);
            intent.putExtra("writing_match", taskId);
            startActivity(intent);
        }
    }

    /**
     * 前往写作界面重新编辑
     *
     * @param position
     */
    private void turnToWriting(int position) {
        WritingBean writingBean = mList.get(position);
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", writingBean.getId());
        intent.putExtra("area", 2);
        intent.putExtra("type", writingBean.getType());
        intent.putExtra("materialId", writingBean.getMaterialId());
        intent.putExtra("taskId", writingBean.getTaskId());
        if (writingBean.getStatus() == 1) {
            intent.putExtra("isCompetitionWriting", false);
        } else {
            intent.putExtra("isCompetitionWriting", true);
        }
        startActivityForResult(intent, 0);
    }

    /**
     * 显示删除弹窗
     *
     * @param position
     */
    private void showDeleteDialog(final int position) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_yes_not_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_yes_not_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_yes_not_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_yes_not_dialog);
                        tv_title.setText("是否确定删除？");
                        tv_left.setText("确定");
                        tv_right.setText("取消");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDraftData(position);
                                dialog.dismiss();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(64)
                .show(getChildFragmentManager());
    }

    /**
     * 删除草稿
     *
     * @param position
     */
    private void deleteDraftData(int position) {
        mSelectedPosition = position;
        JSONArray array = new JSONArray();
        array.put(mList.get(position).getId());
        int type = 0;
        new DeleteData(this)
                .execute(deleteUrl, array.toString(), String.valueOf(type));
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
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        WritingBean writingBean = new WritingBean();
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setCover(object.getString("cover"));
                        writingBean.setWordsNum(object.optInt("wordNum", 0));
                        if (object.getString("updateTime").equals("")
                                || object.getString("updateTime").equals("null")) {
                            writingBean.setDate("2018-01-01 00:00");
                        } else {
                            writingBean.setDate(
                                    DateUtil.timeslashData(object.getString("updateTime")));
                        }
                        writingBean.setType(object.optInt("type", -1));
                        writingBean.setTaskId(object.getString("taskId"));
                        writingBean.setTaskName(object.getString("matchName"));
                        writingBean.setFormat(object.optInt("format", 1));
                        writingBean.setStatus(object.optInt("status", -1));
                        writingBean.setEditor(isCorrection);
                        writingBean.setSelected(false);
                        writingBean.setIndex(4);
                        writingBean.setIsPublic(1);
                        mList.add(writingBean);
                    }
                    adapter.notifyDataSetChanged();
                    if (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE) {
                        frameLayout.setVisibility(View.GONE);
                        frameLayout.removeAllViews();
                    }
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
        isEndForData = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);

            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_write, imageView);
            tv_tips.setText("暂无活动作文");
            tv_reload.setText("露一手写一篇");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnToWriting();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 前往写作界面
     */
    private void turnToWriting() {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", "");
        intent.putExtra("taskId", "");
        intent.putExtra("area", 5);
        intent.putExtra("type", 5);
        intent.putExtra("isCorrection", isCorrection);
        startActivity(intent);
    }

    /**
     * 无网络
     */
    private void noConnect() {
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

    private OnShareClickListen onShareClickListen;

    public void setOnShareClickListen(OnShareClickListen onShareClickListen) {
        this.onShareClickListen = onShareClickListen;
    }

    /**
     * 分析删除草稿的数据
     *
     * @param s
     */
    private void analyzeDeleteData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                deleteSuccess();
            } else {
                deleteError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            deleteError();
        }
    }

    /**
     * 删除成功
     */
    private void deleteSuccess() {
        showTips("删除成功");
        mList.remove(mSelectedPosition);
        adapter.notifyDataSetChanged();
        emptyData();
        mSelectedPosition = -1;
    }

    /**
     * 删除失败
     */
    private void deleteError() {
        showTips("删除失败，请稍后再试");
        mSelectedPosition = -1;
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


    private static class GetData
            extends WeakAsyncTask<String, Void, String, WritingMatchFragment> {

        protected GetData(WritingMatchFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingMatchFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("pageNum", strings[2]);
                object.put("pageSize", 10);
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
        protected void onPostExecute(WritingMatchFragment fragment, String s) {
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
     * 删除草稿箱数据
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Void, String, WritingMatchFragment> {

        protected DeleteData(WritingMatchFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingMatchFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("writingIds", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("area", Integer.valueOf(strings[2]));
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
        protected void onPostExecute(WritingMatchFragment fragment, String s) {
            if (s == null) {
                fragment.deleteError();
            } else {
                fragment.analyzeDeleteData(s);
            }
        }
    }

}
