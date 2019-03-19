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
import com.dace.textreader.adapter.CompetitionRecyclerViewAdapter;
import com.dace.textreader.adapter.MyCompositionAdapter;
import com.dace.textreader.bean.CompetitionBean;
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
 * Created time 2018/7/17 0017 下午 2:40.
 * Version   1.0;
 * Describe :  作文作业
 * History:
 * ==============================================================================
 */

public class WritingWorkFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/homework/list";
    //发布作文
    private static final String publishUrl = HttpUrlPre.HTTP_URL + "/release/writing/id";
    //作文比赛活动
    private static final String competitionUrl = HttpUrlPre.HTTP_URL + "/writing/match/query";
    //判断学生是否已提交过稿到征稿比赛中
    private static final String checkCompetitionUrl = HttpUrlPre.HTTP_URL + "/is/not/commit/match";
    //提交作文到比赛活动
    private static final String submitCompetitionUrl = HttpUrlPre.HTTP_URL + "/commit/match/id";

    private View view;

    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private Context mContext;

    private LinearLayoutManager layoutManager;

    private List<WritingBean> mList = new ArrayList<>();
    private MyCompositionAdapter adapter;

    private boolean refreshing = false;  //是否正在加载数据
    private boolean isEnd = false;  //是否加载到最后
    private int pageNum = 1;  //页码

    private int mSelectedPosition = -1; //当前选择操作的item索引

    public boolean isReady = false;

    private boolean hidden = true;

    private boolean isNoCompetition = false; //暂无活动
    //活动列表
    private List<CompetitionBean> competitionList = new ArrayList<>();
    private String mSelectedTaskId = ""; //当前选择的活动ID
    private int mCompetitionPageNum = 1;  //活动页码

    private boolean isCorrection = false;  //是否是作文批改，是的话点击item跳转批改

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_writing_work, container, false);

        initView();
        initData();
        initCompetitionData();
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

    /**
     * 获取活动、比赛数据
     */
    private void initCompetitionData() {
        new GetCompetitionData(this).execute(competitionUrl);
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
                if (!isEnd && !refreshing) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnItemClickListen(new MyCompositionAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                if (!refreshing) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    if (position != -1 && position < mList.size()) {
                        if (isCorrection) {
                            if (position != mSelectedPosition) {
                                submitReview(position);
                            }
                        } else {
                            WritingBean writingBean = mList.get(position);
                            Intent intent = new Intent(mContext, CompositionDetailActivity.class);
                            intent.putExtra("writingId", writingBean.getId());
                            intent.putExtra("area", 6);
                            intent.putExtra("orderNum", "");
                            startActivity(intent);
                        }
                    }
                }
            }
        });
        adapter.setOnItemOperateOneClick(new MyCompositionAdapter.OnItemOperateOneClick() {
            @Override
            public void onClick(int position) {
                if (!isCorrection) {
                    submitReview(position);
                }
            }
        });
        adapter.setOnItemOperateTwoClick(new MyCompositionAdapter.OnItemOperateTwoClick() {
            @Override
            public void onClick(int position) {
                if (!isCorrection) {
                    turnToWriting(position);
                }
            }
        });
        adapter.setOnItemOperateThreeClick(new MyCompositionAdapter.OnItemOperateThreeClick() {
            @Override
            public void onClick(int position) {
                if (!isCorrection) {
                    WritingBean bean = mList.get(position);
                    onShareClickListen.onShare(bean.getId(), bean.getTitle(),
                            bean.getContent(), 6, bean.getFormat());
                }
            }
        });
        adapter.setOnItemOperateMoreClick(new MyCompositionAdapter.OnItemOperateMoreClick() {
            @Override
            public void onClick(int position) {
                if (!isCorrection) {
                    showOperateDialog(position);
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
     * 显示操作对话框
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
                        rl_order.setVisibility(View.GONE);
                        rl_delete.setVisibility(View.GONE);

                        rl_public.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                publicDraft(position);
                                dialog.dismiss();
                            }
                        });
                        rl_events.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isNoCompetition) {
                                    showTips("暂无活动");
                                } else {
                                    showCompetitionDialog(position);
                                }
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

    /**
     * 显示活动、比赛列表
     *
     * @param position
     */
    private void showCompetitionDialog(final int position) {
        mSelectedTaskId = "";
        updateCompetitionList();
        String taskId = mList.get(position).getTaskId();
        selectedTask(taskId);
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_submit_competition_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final RecyclerView recyclerView = holder.getView(R.id.recycler_view_submit_competition_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_submit_competition_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_submit_competition_dialog);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                                LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        final CompetitionRecyclerViewAdapter adapter =
                                new CompetitionRecyclerViewAdapter(mContext, competitionList);
                        recyclerView.setAdapter(adapter);
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                if (competitionList.size() != adapter.getItemCount()) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                        adapter.setOnItemClickListener(
                                new CompetitionRecyclerViewAdapter.OnCompetitionItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int pos = recyclerView.getChildAdapterPosition(view);
                                        int status = competitionList.get(pos).getStatus();
                                        if (pos != getCompetitionSelectedPos() && status == 1) {
                                            updateCompetitionList();
                                            competitionList.get(pos).setSelected(true);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos = getCompetitionSelectedPos();
                                if (pos == -1) {
                                    showTips("请选择活动项目");
                                } else {
                                    String id = competitionList.get(pos).getId();
                                    checkCompetition(position, id);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getChildFragmentManager());
    }

    /**
     * 检查用户在这个活动中有没有投稿
     */
    private void checkCompetition(int position, String taskId) {
        mSelectedPosition = position;
        mSelectedTaskId = taskId;
        new CheckCompetition(this).execute(checkCompetitionUrl, taskId);
    }

    /**
     * 选中比赛
     */
    private void selectedTask(String taskId) {
        for (int i = 0; i < competitionList.size(); i++) {
            if (taskId.equals(competitionList.get(i).getId())) {
                CompetitionBean competitionBean = competitionList.get(i);
                competitionBean.setSelected(true);
                competitionList.remove(i);
                competitionList.add(0, competitionBean);
                break;
            }
        }
    }

    /**
     * 获取当前选中的活动、比赛
     *
     * @return
     */
    private int getCompetitionSelectedPos() {
        int i = -1;
        for (int j = 0; j < competitionList.size(); j++) {
            if (competitionList.get(j).isSelected()) {
                i = j;
                break;
            }
        }
        return i;
    }

    /**
     * 更新活动、比赛列表
     */
    private void updateCompetitionList() {
        for (int i = 0; i < competitionList.size(); i++) {
            competitionList.get(i).setSelected(false);
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
                onItemCorrectionClick.onClick(id, title, taskId, 6, type, wordsNum);
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
            intent.putExtra("writing_area", 6);
            intent.putExtra("writing_type", type);
            intent.putExtra("writing_match", taskId);
            startActivity(intent);
        }
    }

    /**
     * 发布作文
     */
    private void publicDraft(int position) {
        showTips("正在发布作文...");
        String id = mList.get(position).getId();
        String taskId = mList.get(position).getTaskId();
        int type = mList.get(position).getType();
        int from_type = 6;
        new PublicDraft(this)
                .execute(publishUrl, id, String.valueOf(type), String.valueOf(from_type), taskId);
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
        intent.putExtra("area", 6);
        intent.putExtra("type", writingBean.getType());
        intent.putExtra("taskId", writingBean.getTaskId());
        startActivityForResult(intent, 0);
    }

    public void initData() {
        if (!refreshing) {
            refreshing = true;
            swipeRefreshLayout.setRefreshing(true);
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            isEnd = false;
            new GetData(this).execute(url, String.valueOf(pageNum));
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_writing_work_fragment);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_writing_work_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_writing_work_fragment);
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
            new GetData(this).execute(url, String.valueOf(pageNum));
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
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    noData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        WritingBean writingBean = new WritingBean();
                        writingBean.setId(object.getString("compositionId"));
                        writingBean.setTitle(object.getString("article"));
                        writingBean.setContent(object.getString("content"));
                        writingBean.setCover(object.getString("cover"));
                        writingBean.setWordsNum(object.optInt("wordNum", 0));
                        writingBean.setTaskId("");
                        if (object.getString("studentStatus").equals("null") ||
                                object.getString("studentStatus").equals("")) {
                            writingBean.setStatus(0);
                        } else {
                            writingBean.setStatus(object.optInt("studentStatus", 0));
                        }
                        if (object.getString("updateTime").equals("")
                                || object.getString("updateTime").equals("null")) {
                            writingBean.setDate("2018-01-01 00:00");
                        } else {
                            writingBean.setDate(DateUtil.time2YMD(object.getString("updateTime")));
                        }
                        writingBean.setFormat(object.optInt("format", 1));
                        writingBean.setType(object.optInt("type", 6));
                        writingBean.setEditor(isCorrection);
                        writingBean.setSelected(false);
                        writingBean.setIndex(3);
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
                noData();
            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 无数据返回
     */
    private void noData() {
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
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_write, imageView);
            tv_tips.setText("暂无作业");
            tv_reload.setText("露一手写一篇");
            tv_reload.setVisibility(View.VISIBLE);
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
     * 无网络连接
     */
    private void noConnect() {
        showTips("获取数据失败，请稍后再试");
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_tips.setText("暂无作业");
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
     * 发布作文数据返回处理
     *
     * @param s
     */
    private void publicDraftData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                publicDraftSuccess();
            } else if (300 == jsonObject.optInt("status", -1)) {
                String msg = jsonObject.getString("msg");
                showTips(msg);
            } else {
                publicDraftError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            publicDraftError();
        }
    }

    /**
     * 发布成功
     */
    private void publicDraftSuccess() {
        showTips("发布成功");
    }

    /**
     * 发布作文失败
     */
    private void publicDraftError() {
        showTips("发布失败");
    }

    private OnShareClickListen onShareClickListen;

    public void setOnShareClickListen(OnShareClickListen onShareClickListen) {
        this.onShareClickListen = onShareClickListen;
    }

    /**
     * 分析活动、比赛数据
     *
     * @param s
     */
    private void analyzeCompetitionData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    noCompetition();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        CompetitionBean competitionBean = new CompetitionBean();
                        competitionBean.setId(object.getString("id"));
                        competitionBean.setTitle(object.getString("title"));
                        competitionBean.setImage(object.getString("image"));
                        competitionBean.setStatus(object.optInt("status", -1));
                        competitionBean.setContent(object.getString("description"));
                        competitionBean.setSelected(false);
                        competitionList.add(competitionBean);
                    }
                    mCompetitionPageNum = mCompetitionPageNum + 1;
                    initCompetitionData();
                }
            } else {
                noCompetition();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noCompetition();
        }
    }

    /**
     * 暂无活动
     */
    private void noCompetition() {
        if (competitionList.size() == 0) {
            isNoCompetition = true;
        }
    }

    private void analyzeCheckCompetition(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (400 == jsonObject.optInt("status", -1)) {
                showNeedCoverDialog();
            } else {
                submitToCompetition(mSelectedPosition, mSelectedTaskId);
            }
        } catch (JSONException e) {
            submitToCompetition(mSelectedPosition, mSelectedTaskId);
        }
    }

    /**
     * 显示是否覆盖原稿
     */
    private void showNeedCoverDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("是否确定覆盖原文");
                        tv_content.setText("提交“活动”操作将覆盖原文\n在截稿期限前您可再次编辑您的作文");
                        tv_left.setText("确定");
                        tv_right.setText("取消");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitToCompetition(mSelectedPosition, mSelectedTaskId);
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
                .setMargin(40)
                .show(getChildFragmentManager());
    }

    /**
     * 参加活动、比赛
     */
    private void submitToCompetition(int position, String taskId) {
        String draftID = mList.get(position).getId();
        int type = mList.get(position).getType();
        new SubmitCompetition(this).execute(submitCompetitionUrl, draftID,
                String.valueOf(type), taskId);
    }

    /**
     * 分析提交到活动、比赛的数据
     *
     * @param s
     */
    private void analyzeSubmitCompetition(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showTips("参加活动成功");
//                initData();
            } else {
                errorSubmitCompetition();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorSubmitCompetition();
        }
    }

    /**
     * 提交到活动、比赛失败
     */
    private void errorSubmitCompetition() {
        showTips("参加活动失败，请稍后再试");
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

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, WritingWorkFragment> {

        protected GetData(WritingWorkFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingWorkFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", strings[1]);
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
        protected void onPostExecute(WritingWorkFragment fragment, String s) {
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
     * 发布作文
     */
    private static class PublicDraft
            extends WeakAsyncTask<String, Void, String, WritingWorkFragment> {

        protected PublicDraft(WritingWorkFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingWorkFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("type", strings[2]);
                object.put("area", strings[3]);
                if (!strings[4].equals("") && !strings[4].equals("null")) {
                    object.put("taskId", strings[4]);
                }
                object.put("isPublic", 1);
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
        protected void onPostExecute(WritingWorkFragment fragment, String s) {
            if (s == null) {
                fragment.publicDraftError();
            } else {
                fragment.publicDraftData(s);
            }
        }
    }

    /**
     * 获取比赛、活动数据
     */
    private static class GetCompetitionData
            extends WeakAsyncTask<String, Void, String, WritingWorkFragment> {

        protected GetCompetitionData(WritingWorkFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingWorkFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("pageNum", fragment.mCompetitionPageNum);
                object.put("pageSize", 10);
                object.put("type", 3);
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
        protected void onPostExecute(WritingWorkFragment fragment, String s) {
            if (s == null) {
                fragment.noCompetition();
            } else {
                fragment.analyzeCompetitionData(s);
            }
        }
    }

    /**
     * 检查比赛、活动是否已投稿
     */
    private static class CheckCompetition
            extends WeakAsyncTask<String, Void, String, WritingWorkFragment> {

        protected CheckCompetition(WritingWorkFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingWorkFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("matchId", strings[1]);
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
        protected void onPostExecute(WritingWorkFragment fragment, String s) {
            if (s == null) {
                fragment.submitToCompetition(fragment.mSelectedPosition, fragment.mSelectedTaskId);
            } else {
                fragment.analyzeCheckCompetition(s);
            }
        }
    }

    /**
     * 提交到比赛、活动
     */
    private static class SubmitCompetition
            extends WeakAsyncTask<String, Void, String, WritingWorkFragment> {

        protected SubmitCompetition(WritingWorkFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(WritingWorkFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("area", 6);
                object.put("type", Integer.valueOf(strings[2]));
                object.put("matchId", strings[3]);
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
        protected void onPostExecute(WritingWorkFragment fragment, String s) {
            if (s == null) {
                fragment.errorSubmitCompetition();
            } else {
                fragment.analyzeSubmitCompetition(s);
            }
        }
    }

}
