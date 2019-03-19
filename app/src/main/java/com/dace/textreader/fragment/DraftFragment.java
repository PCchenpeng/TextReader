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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.SubmitReviewActivity;
import com.dace.textreader.activity.WritingActivity;
import com.dace.textreader.activity.WritingWorkActivity;
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
import com.kyleduo.switchbutton.SwitchButton;

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
 * Created time 2018/7/17 0017 下午 2:15.
 * Version   1.0;
 * Describe :  作文草稿
 * History:
 * ==============================================================================
 */

public class DraftFragment extends Fragment {

    //获取草稿列表
    private static final String url = HttpUrlPre.HTTP_URL + "/writing/query/draft";
    //删除草稿
    private static final String deleteUrl = HttpUrlPre.HTTP_URL + "/writing/delete";
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
    private MyCompositionAdapter adapter;
    private List<WritingBean> mList = new ArrayList<>();

    //活动列表
    private List<CompetitionBean> competitionList = new ArrayList<>();

    private int pageNum = 1;
    private boolean isEndForData = false;
    private boolean refreshing = false;  //是否正在刷新

    private boolean isNoCompetition = false; //暂无活动

    private int mSelectedPosition = -1; //当前选择操作的item索引
    private String mSelectedTaskId = ""; //当前选择的活动ID
    private int mCompetitionPageNum = 1;  //活动页码

    public boolean isReady = false;

    private boolean hidden = true;

    private boolean isCorrection = false;  //是否是作文批改，是的话点击item跳转批改

    private boolean isNeedRefresh = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_draft, container, false);

        initView();
        initData();
        initEvents();
        initCompetitionData();

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
        if (!refreshing && isNeedRefresh && DataUtil.isDraftNeedRefresh) {
            isNeedRefresh = false;
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
                super.onScrollStateChanged(recyclerView, newState);
                if (!isEndForData && !refreshing) {
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
                            turnToWriting(position);
                        }
                    }
                }
            }
        });
        adapter.setOnItemOperateOneClick(new MyCompositionAdapter.OnItemOperateOneClick() {
            @Override
            public void onClick(int position) {
                if (!refreshing && position != -1 && position < mList.size()) {
                    if (!isCorrection) {
                        submitReview(position);
                    }

                }
            }
        });
        adapter.setOnItemOperateTwoClick(new MyCompositionAdapter.OnItemOperateTwoClick() {
            @Override
            public void onClick(int position) {
                if (!refreshing && position != -1 && position < mList.size()) {
                    if (!isCorrection) {
                        showPublicDraftDialog(position);
                    }
                }
            }
        });
        adapter.setOnItemOperateThreeClick(new MyCompositionAdapter.OnItemOperateThreeClick() {
            @Override
            public void onClick(int position) {
                if (!refreshing && position != -1 && position < mList.size()) {
                    if (!isCorrection) {
                        WritingBean bean = mList.get(position);
                        onShareClickListen.onShare(bean.getId(), bean.getTitle(),
                                bean.getContent(), 5, bean.getFormat());
                    }
                }
            }
        });
        adapter.setOnItemOperateMoreClick(new MyCompositionAdapter.OnItemOperateMoreClick() {
            @Override
            public void onClick(int position) {
                if (!refreshing && position != -1 && position < mList.size()) {
                    if (!isCorrection) {
                        showOperateDialog(position);
                    }
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
        frameLayout = view.findViewById(R.id.frame_draft_fragment);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_draft_fragment);
        recyclerView = view.findViewById(R.id.recycler_view_draft_fragment);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyCompositionAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
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
                        rl_public.setVisibility(View.GONE);
                        rl_order.setVisibility(View.GONE);

                        rl_work.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitWork(position);
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
                        rl_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDeleteDialog(position);
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
     * 提交作业
     *
     * @param position
     */
    private void submitWork(int position) {
        isNeedRefresh = true;
        String id = mList.get(position).getId();
        int type = mList.get(position).getType();
        Intent intent = new Intent(mContext, WritingWorkActivity.class);
        intent.putExtra("isSubmit", true);
        intent.putExtra("writingId", id);
        intent.putExtra("writingArea", "5");
        intent.putExtra("writingType", type);
        startActivity(intent);
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

    private int isPublic = 1;  //发布作文默认公开

    /**
     * 显示发布作文对话框
     */
    private void showPublicDraftDialog(int position) {
        mSelectedPosition = position;
        final String id = mList.get(position).getId();
        final String taskId = mList.get(position).getTaskId();
        final int type = mList.get(position).getType();
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_public_setting_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        SwitchButton switchButton = holder.getView(R.id.switch_writing_public_setting_dialog);
                        switchButton.setChecked(true);
                        isPublic = 1;
                        TextView tv_sure = holder.getView(R.id.tv_sure_writing_public_setting_dialog);
                        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    isPublic = 1;
                                } else {
                                    isPublic = 0;
                                }
                            }
                        });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                publicDraft(id, taskId, type);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getChildFragmentManager());
    }

    /**
     * 发布作文
     *
     * @param id
     * @param taskId
     */
    private void publicDraft(String id, String taskId, int type) {
        showTips("正在发布作文...");
        int from_type = 5;
        new PublicDraft(this)
                .execute(publishUrl, id, String.valueOf(type), String.valueOf(from_type),
                        taskId, String.valueOf(isPublic));
    }

    /**
     * 提交批改
     */
    private void submitReview(int position) {
        String id = mList.get(position).getId();
        String title = mList.get(position).getTitle();
        int wordsNum = mList.get(position).getWordsNum();
        int type = mList.get(position).getType();
        String taskId = mList.get(position).getTaskId();
        if (isCorrection) {
            if (onItemCorrectionClick != null) {
                onItemCorrectionClick.onClick(id, title, taskId, 5, type, wordsNum);
            }
            mList.get(position).setSelected(true);
            adapter.notifyItemChanged(position);
            mSelectedPosition = position;
        } else {
            isNeedRefresh = true;
            Intent intent = new Intent(mContext, SubmitReviewActivity.class);
            intent.putExtra("type", "writing");
            intent.putExtra("isEditor", false);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("count", wordsNum);
            intent.putExtra("writing_area", 5);
            intent.putExtra("writing_type", type);
            intent.putExtra("writing_match", taskId);
            startActivity(intent);
        }
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
     * 检查用户在这个活动中有没有投稿
     */
    private void checkCompetition(int position, String taskId) {
        mSelectedPosition = position;
        mSelectedTaskId = taskId;
        new CheckCompetition(this).execute(checkCompetitionUrl, taskId);
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
     * 前往写作界面重新编辑
     *
     * @param position
     */
    private void turnToWriting(int position) {
        isNeedRefresh = true;
        DataUtil.isDraftNeedRefresh = true;
        WritingBean writingBean = mList.get(position);
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", writingBean.getId());
        intent.putExtra("area", 5);
        intent.putExtra("type", writingBean.getType());
        intent.putExtra("taskId", writingBean.getTaskId());
        startActivityForResult(intent, 0);
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
        int type = 5;
        new DeleteData(this)
                .execute(deleteUrl, array.toString(), String.valueOf(type));
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
                        writingBean.setFormat(object.optInt("format", 1));
                        if (object.getString("saveTime").equals("")
                                || object.getString("saveTime").equals("null")) {
                            writingBean.setDate("2018-01-01 00:00");
                        } else {
                            writingBean.setDate(
                                    DateUtil.timeslashData(object.getString("saveTime")));
                        }
                        writingBean.setType(object.optInt("type", 5));
                        writingBean.setWordsNum(object.optInt("wordNum", 0));
                        writingBean.setTaskId(object.getString("taskId"));
                        writingBean.setTaskName(object.getString("matchName"));
                        writingBean.setEditor(isCorrection);
                        writingBean.setSelected(false);
                        writingBean.setIndex(0);
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
        isEndForData = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty_write, imageView);
            tv_tips.setText("暂无草稿");
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
     * 无网络
     */
    private void noConnect() {
        showTips("获取数据失败");
        adapter.notifyDataSetChanged();
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
        }
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
     * 分析提交到活动、比赛的数据
     *
     * @param s
     */
    private void analyzeSubmitCompetition(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showTips("参加活动成功");
                initData();
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
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
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
        mList.remove(mSelectedPosition);
        adapter.notifyDataSetChanged();
        emptyData();
        mSelectedPosition = -1;
    }

    /**
     * 发布作文失败
     */
    private void publicDraftError() {
        showTips("发布失败");
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

    /**
     * 获取草稿箱数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, DraftFragment> {

        protected GetData(DraftFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(DraftFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("pageNum", Integer.valueOf(strings[2]));
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
        protected void onPostExecute(DraftFragment fragment, String s) {
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
            extends WeakAsyncTask<String, Void, String, DraftFragment> {

        protected DeleteData(DraftFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(DraftFragment fragment, String[] strings) {
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
        protected void onPostExecute(DraftFragment fragment, String s) {
            if (s == null) {
                fragment.deleteError();
            } else {
                fragment.analyzeDeleteData(s);
            }
        }
    }

    /**
     * 获取比赛、活动数据
     */
    private static class GetCompetitionData
            extends WeakAsyncTask<String, Void, String, DraftFragment> {

        protected GetCompetitionData(DraftFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(DraftFragment fragment, String[] strings) {
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
        protected void onPostExecute(DraftFragment fragment, String s) {
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
            extends WeakAsyncTask<String, Void, String, DraftFragment> {

        protected CheckCompetition(DraftFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(DraftFragment fragment, String[] strings) {
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
        protected void onPostExecute(DraftFragment fragment, String s) {
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
            extends WeakAsyncTask<String, Void, String, DraftFragment> {

        protected SubmitCompetition(DraftFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(DraftFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("area", 5);
                object.put("type", strings[2]);
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
        protected void onPostExecute(DraftFragment fragment, String s) {
            if (s == null) {
                fragment.errorSubmitCompetition();
            } else {
                fragment.analyzeSubmitCompetition(s);
            }
        }
    }

    /**
     * 发布作文
     */
    private static class PublicDraft
            extends WeakAsyncTask<String, Void, String, DraftFragment> {

        protected PublicDraft(DraftFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(DraftFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("type", Integer.valueOf(strings[2]));
                object.put("area", Integer.valueOf(strings[3]));
                if (!strings[4].equals("") && !strings[4].equals("null")) {
                    object.put("taskId", strings[4]);
                }
                object.put("isPublic", Integer.valueOf(strings[5]));
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
        protected void onPostExecute(DraftFragment fragment, String s) {
            if (s == null) {
                fragment.publicDraftError();
            } else {
                fragment.publicDraftData(s);
            }
        }
    }

}
