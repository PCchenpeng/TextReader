package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.dace.textreader.adapter.NotesRecyclerViewAdapter;
import com.dace.textreader.bean.Notes;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

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
 * Created time 2018/7/13 0013 下午 1:53.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class NoteFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL_ + "/select/article/note/list";
//    private static final String allUrl = HttpUrlPre.HTTP_URL + "/personal/note/select?";
    private static final String deleteUrl = HttpUrlPre.HTTP_URL + "/personal/note/essay/delete";
    private static final String allDeleteUrl = HttpUrlPre.HTTP_URL + "/personal/note/all/delete";
    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/get/note/url";

    private View view;

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

    private FrameLayout frameLayout;

    private Context mContext;
    private LinearLayoutManager mLayoutManager;

    private NotesRecyclerViewAdapter adapter;
    private List<Notes> mList = new ArrayList<>();
    private List<Notes> mSelectedList = new ArrayList<>();

    private long essayId = -1;  //文章ID
    private boolean isAllNotes = true;//是否是所有笔记
    private String title = "";
    private String content = "";

    //是否为编辑模式
    private boolean isEditor = false;
    //是否全选
    private boolean isSelectedAll = false;
    private boolean hasSelected = false;  //是否有item被选中

    private static int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    private int type_share = -1;  //分享类型
    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;

    private WbShareHandler shareHandler;

    private OnListDataOperateListen mListen;

    private boolean hidden = true;

    private boolean showPractice = false;
    private boolean isChoose = false;
    private int mSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notes, container, false);

        initView();
        swipeRefreshLayout.autoRefresh();
        initEvents();

        shareHandler = new WbShareHandler(getActivity());
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));

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

    public void setOnListDataListen(OnListDataOperateListen listen) {
        this.mListen = listen;
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
        tv_delete.setOnClickListener(onClickListener);
        ll_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedAll) {
                    selectAll(false);
                } else {
                    selectAll(true);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!refreshing && !isEditor) {
                    initData();
                } else {
                    swipeRefreshLayout.finishRefresh();
                }
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (isAllNotes && !refreshing && !isEnd && !isChoose && mList.size() != 0) {
                    getMoreData();
                } else {
                    swipeRefreshLayout.finishLoadMore();
                }
            }
        });
        adapter.setOnItemClickListener(new NotesRecyclerViewAdapter.OnNotesItemClick() {
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
        adapter.setOnItemShareClickListener(new NotesRecyclerViewAdapter.OnNotesShareItemClick() {
            @Override
            public void onItemClick(int position) {
                if (!isEditor && !refreshing) {
                    shareNote(mList.get(position).getId());
                    title = mList.get(position).getTitle();
                    content = mList.get(position).getContent();
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
     * 练习
     */
    private void practice() {
        String practice = mList.get(mSelectedPosition).getNote();
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

    public void setAllNotes(boolean allNotes) {
        isAllNotes = allNotes;
    }

    public void setEssayId(long essayId) {
        this.essayId = essayId;
    }

    public void editorOpenOrClose() {
        if (isEditor) {
            editorMode(false);
        } else {
            if (mList.size() != 0) {
                editorMode(true);
            }
        }
    }

    private void initData() {
        if (!refreshing) {
            if (mListen != null) {
                mListen.onRefresh(true);
            }
            refreshing = true;
            mList.clear();
            adapter.notifyDataSetChanged();
            pageNum = 1;
            isEnd = false;
            if (isAllNotes) {
                new GetData(this)
                        .execute(url);
            } else {
                new GetData(this)
                        .execute(url);
            }
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_notes_fragment);
        rl_editor = view.findViewById(R.id.rl_editor_notes_fragment);
        ll_select_all = view.findViewById(R.id.ll_select_all_new_collection_bottom);
        iv_select_all = view.findViewById(R.id.iv_select_all_new_collection_bottom);
        tv_delete = view.findViewById(R.id.tv_delete_new_collection_bottom);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_notes_fragment);
        swipeRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        recyclerView = view.findViewById(R.id.recycler_view_notes_fragment);
        recyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new NotesRecyclerViewAdapter(mContext, mList, isAllNotes);
        recyclerView.setAdapter(adapter);

        tv_one = view.findViewById(R.id.tv_one_tips_note);
        tv_two = view.findViewById(R.id.tv_two_tips_note);
        tv_three = view.findViewById(R.id.tv_three_tips_note);
        tv_one.setText("在阅读时");
        tv_two.setText("可进入精度模式长按选择文本");
        tv_three.setText("添加想法");

        tv_sure = view.findViewById(R.id.tv_sure_notes_fragment);
        iv_practice = view.findViewById(R.id.iv_practice_notes_fragment);
        if (showPractice) {
            iv_practice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 分享笔记
     */
    private void shareNote(final String noteId) {
        type_share = -1;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_share_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.share_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_wechat, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIEND, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    getShareHtml(TYPE_SHARE_Weibo, noteId);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK, noteId);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setDimAmount(0.3f)
                .setShowBottom(true)
                .show(getChildFragmentManager());
    }

    /**
     * 获取分享链接
     *
     * @param type_share
     */
    private void getShareHtml(int type_share, String noteId) {
        this.type_share = type_share;
        showTips("正在准备分享内容...");
        new GetShareHtml(this).execute(shareUrl, noteId);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(getActivity(), url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享笔记到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, title, content,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(getActivity(), url, title, content, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData() {
        if (mListen != null) {
            mListen.onRefresh(true);
        }
        refreshing = true;
        pageNum++;
        new GetData(this)
                .execute(url);
    }

    /**
     * 刷新数据
     */
    private void update() {
        if (mListen != null) {
            mListen.onRefresh(true);
        }
        refreshing = true;
        mList.clear();
        adapter.notifyDataSetChanged();
        pageNum = 1;
        isEnd = false;
        if (isAllNotes) {
            new GetData(this)
                    .execute(url);
        } else {
            new GetData(this)
                    .execute(url);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_delete_new_collection_bottom:
                    if (hasSelected) {
                        deleteSelectedData();
                    }
                    break;
            }
        }
    };

    /**
     * item被点击
     *
     * @param position
     */
    private void itemSelected(int position) {
        if (mList.get(position).isSelected()) {
            mList.get(position).setSelected(false);
            isSelectedAll = false;
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
            isSelectedAll = false;
        }
    }

    /**
     * 删除选中数据
     */
    private void deleteSelectedData() {

        JSONArray json = new JSONArray();

        if (isSelectedAll) {
            if (isAllNotes) {
                new DeleteData(this).execute(allDeleteUrl);
            } else {
                new DeleteData(this).execute(deleteUrl);
            }
            mList.clear();
            adapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < mList.size(); i++) {
                boolean isSelected = mList.get(i).isSelected();
                if (isSelected) {
                    mSelectedList.add(mList.get(i));
                    json.put(mList.get(i).getId());
                }
            }

            if (mSelectedList.size() != 0) {
                if (isAllNotes) {
                    new DeleteData(this)
                            .execute(allDeleteUrl, json.toString());
                } else {
                    new DeleteData(this)
                            .execute(deleteUrl, json.toString());
                }

                for (int j = 0; j < mSelectedList.size(); j++) {
                    mList.remove(mSelectedList.get(j));
                }
                mSelectedList.clear();
            }
        }
        editorMode(false);
        if (mList.size() == 0) {
            emptyData();
        }
    }

    /**
     * 全选
     */
    private void selectAll(boolean selectedAll) {
        if (selectedAll) {
            iv_select_all.setImageResource(R.drawable.icon_edit_selected);
        } else {
            iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
        }
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelected(selectedAll);
        }
        isSelectedAll = selectedAll;
        adapter.notifyDataSetChanged();
        updateDeleteButtonBg();
    }

    /**
     * 是否为编辑模式
     *
     * @param isEditor
     */
    private void editorMode(boolean isEditor) {
        this.isEditor = isEditor;
        if (mListen != null) {
            mListen.onEditor(isEditor);
        }
        if (isEditor) {
            rl_editor.setVisibility(View.VISIBLE);
        } else {
            rl_editor.setVisibility(View.GONE);
        }
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setEditor(isEditor);
            if (!isEditor) {
                mList.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
        hasSelected = false;
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, NoteFragment> {

        protected GetData(NoteFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NoteFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("category", 0);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", pageNum);
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
        protected void onPostExecute(NoteFragment fragment, String s) {
            if (s == null) {
                fragment.errorConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
            if (fragment.mListen != null) {
                fragment.mListen.onEditor(false);
            }
            fragment.swipeRefreshLayout.finishRefresh();
            fragment.swipeRefreshLayout.finishLoadMore();
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
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        Notes notes = new Notes();
                        notes.setId(object.getString("id"));
                        notes.setEssayId(object.optLong("essayId", -1L));
                        notes.setEssayType(object.optInt("essayTitle", -1));
                        notes.setTitle(object.getString("essayTitle"));
                        if (object.getString("time").equals("")
                                || object.getString("time").equals("null")) {
                            notes.setTime("2018-01-01 00:00");
                        } else {
                            notes.setTime(DateUtil.timeYMD(object.getString("time")));
                        }
                        notes.setNote(object.getString("note"));
                        notes.setContent(object.getString("content"));
                        notes.setSelected(false);
                        notes.setEditor(false);
                        mList.add(notes);
                    }
                    adapter.notifyDataSetChanged();
                    if (mListen != null) {
                        mListen.onLoadResult(true);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
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
            tv_two.setText("可进入精读模式长按选择文本");
            tv_three.setText("添加想法");
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 分析分享链接数据
     *
     * @param s
     */
    private void analyzeShareData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                String url = jsonObject.getString("data");
                share(url);
            } else {
                errorShare();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorShare();
        }
    }

    /**
     * 分享
     *
     * @param url
     */
    private void share(String url) {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true, url);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false, url);
                break;
            case TYPE_SHARE_Weibo:
                shareToWeibo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url);
                break;
            case TYPE_SHARE_LINK:
                DataUtil.copyContent(mContext, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWeibo(shareHandler, url, title, content, thumb);

    }

    /**
     * 分享失败
     */
    private void errorShare() {
        showTips("分享失败，请稍后重试");
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
     * 删除数据
     */
    private static class DeleteData
            extends WeakAsyncTask<String, Integer, String, NoteFragment> {

        protected DeleteData(NoteFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NoteFragment fragment, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                if (!fragment.isAllNotes) {
                    json.put("essayId", fragment.essayId);
                }
                if (fragment.isSelectedAll) {
                    json.put("status", 1);
                    json.put("notes", "");
                } else {
                    json.put("status", 0);
                    json.put("notes", params[1]);
                }
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                fragment.isSelectedAll = false;
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(NoteFragment fragment, String s) {
            fragment.isSelectedAll = false;
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, NoteFragment> {

        protected GetShareHtml(NoteFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(NoteFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("noteId", strings[1]);
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
        protected void onPostExecute(NoteFragment fragment, String s) {
            if (s == null) {
                fragment.errorShare();
            } else {
                fragment.analyzeShareData(s);
            }
        }
    }
}
