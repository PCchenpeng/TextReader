package com.dace.textreader.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.LiveShowLessonAdapter;
import com.dace.textreader.bean.LiveShowLessonBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
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
 * 直播课选择
 */
public class LiveShowLessonChooseActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/card/vip/broadcast/live/show";
    private static final String liveShowFirstUrl = HttpUrlPre.HTTP_URL + "/card/vip/broadcast/live/confirm";
    private static final String liveShowUrl = HttpUrlPre.HTTP_URL + "/card/broadcast/live/query";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private TextView tv_tips;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private TextView tv_commit;

    private LiveShowLessonChooseActivity mContext;

    private List<LiveShowLessonBean> mList = new ArrayList<>();
    private LiveShowLessonAdapter adapter;

    private boolean activated = false;
    private String cardId = "";
    private String cardRecordId = "";
    private boolean hasChoose = false;  //是否选过课程

    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_show_lesson_choose);

        mContext = this;

        cardId = String.valueOf(getIntent().getLongExtra("id", -1));
        activated = getIntent().getBooleanExtra("activated", false);
        cardRecordId = String.valueOf(getIntent().getLongExtra("recordId", -1));

        initView();
        initEvents();
        refreshLayout.autoRefresh();

    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
        adapter.setOnItemClickListen(new LiveShowLessonAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                if (activated) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (hasChoose) {
                        chooseOver(pos);
                    } else {
                        chooseItem(pos);
                    }
                }
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseOver(mPosition);
            }
        });
    }

    /**
     * 选择内容
     *
     * @param pos
     */
    private void chooseItem(int pos) {
        if (pos >= 0 && pos < mList.size()) {
            if (pos == mPosition) {
                mList.get(pos).setSelected(false);
                Bundle bundle = new Bundle();
                adapter.notifyItemChanged(pos, bundle);
                mPosition = -1;
            } else {
                if (mPosition != -1 && mPosition < mList.size()) {
                    mList.get(mPosition).setSelected(false);
                    Bundle bundle = new Bundle();
                    adapter.notifyItemChanged(mPosition, bundle);
                }
                mList.get(pos).setSelected(true);
                Bundle bundle_selected = new Bundle();
                adapter.notifyItemChanged(pos, bundle_selected);
                mPosition = pos;
            }
        }
        updateChooseStatus();
    }

    /**
     * 更新选中状态
     */
    private void updateChooseStatus() {
        if (mPosition == -1) {
            tv_commit.setBackgroundColor(Color.parseColor("#999999"));
        } else {
            tv_commit.setBackgroundColor(Color.parseColor("#ff9933"));
        }
    }

    /**
     * 选择结束
     */
    private void chooseOver(int position) {
        if (position == -1) {
            return;
        }
        long id = mList.get(position).getLessonId();
        if (hasChoose) {
            requestLiveShowData(String.valueOf(id));
        } else {
            JSONArray array = new JSONArray();
            array.put(id);
            requestFirstLiveShowData(array.toString());
        }

    }

    /**
     * 获取直播功能内容
     */
    private void requestFirstLiveShowData(String lessonId) {
        showTips("正在获取直播内容");
        new GetLiveShowFirstData(mContext).execute(liveShowFirstUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), cardId, cardRecordId, lessonId);
    }

    /**
     * 获取直播功能内容
     */
    private void requestLiveShowData(String itemId) {
        showTips("正在获取直播内容");
        new GetLiveShowData(mContext).execute(liveShowUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), itemId);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("作文直播课");

        frameLayout = findViewById(R.id.frame_live_show_lesson_choose);
        tv_tips = findViewById(R.id.tv_tips_live_show_lesson_choose);
        refreshLayout = findViewById(R.id.smart_refresh_live_show_lesson_choose);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setEnableLoadMore(false);
        recyclerView = findViewById(R.id.rv_live_show_lesson_choose);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LiveShowLessonAdapter(mContext, mList, activated);
        recyclerView.setAdapter(adapter);
        tv_commit = findViewById(R.id.tv_commit_live_show_lesson_choose);
    }

    private void initData() {
        hasChoose = false;
        mList.clear();
        adapter.notifyDataSetChanged();
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID), cardId);
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
                JSONObject object = jsonObject.getJSONObject("data");
                hasChoose = object.optBoolean("confirm", false);
                JSONArray array = object.getJSONArray("list");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        LiveShowLessonBean bean = new LiveShowLessonBean();
                        bean.setLessonId(json.optLong("id", 0));
                        bean.setTitle(json.getString("title"));
                        bean.setContent(json.getString("description"));
                        bean.setImage(json.getString("img"));
                        bean.setSelected(false);
                        mList.add(bean);
                    }
                    updateUi();
                }
            } else if (400 == jsonObject.optInt("status")) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    private void updateUi() {
        adapter.notifyDataSetChanged();
        if (hasChoose) {
            tv_tips.setVisibility(View.GONE);
            adapter.setActivated(false);
        } else {
            tv_tips.setVisibility(View.VISIBLE);
            if (!activated) {
                tv_tips.setText("学习卡专选一堂写作直播课");
            }
        }
        if (activated && !hasChoose) {
            tv_commit.setVisibility(View.VISIBLE);
        } else {
            tv_commit.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            tv_commit.setVisibility(View.GONE);
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            tv_tips.setText("暂无直播课内容");
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("没有更多了~");
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        if (isDestroyed()) {
            return;
        }
        if (mList.size() == 0) {
            tv_commit.setVisibility(View.GONE);
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            tv_tips.setText("获取数据失败，请稍后重试~");
            tv_reload.setText("获取推荐活动");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    frameLayout.removeAllViews();
                    refreshLayout.autoRefresh();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败，请稍后重试~");
        }
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, LiveShowLessonChooseActivity> {

        protected GetData(LiveShowLessonChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(LiveShowLessonChooseActivity activity, String[] strings) {

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("functionCardId", strings[2]);
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
        protected void onPostExecute(LiveShowLessonChooseActivity activity, String s) {
            activity.refreshLayout.finishRefresh();
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取直播内容
     */
    private static class GetLiveShowFirstData
            extends WeakAsyncTask<String, Void, String, LiveShowLessonChooseActivity> {

        protected GetLiveShowFirstData(LiveShowLessonChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(LiveShowLessonChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("functionCardId", strings[2]);
                object.put("functionCardRecordId", strings[3]);
                object.put("items", strings[4]);
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
        protected void onPostExecute(LiveShowLessonChooseActivity activity, String s) {
            if (s == null) {
                activity.showTips("获取直播内容失败，请检查网络是否可用");
            } else {
                activity.analyzeLiveShowFirstData(s);
            }
        }
    }

    /**
     * 获取直播内容
     */
    private static class GetLiveShowData
            extends WeakAsyncTask<String, Void, String, LiveShowLessonChooseActivity> {

        protected GetLiveShowData(LiveShowLessonChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(LiveShowLessonChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("itemId", strings[2]);
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
        protected void onPostExecute(LiveShowLessonChooseActivity activity, String s) {
            if (s == null) {
                activity.showTips("获取直播内容失败，请检查网络是否可用");
            } else {
                activity.analyzeLiveShowData(s);
            }
        }
    }

    /**
     * 分析直播内容数据
     *
     * @param s
     */
    private void analyzeLiveShowFirstData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    showTips("获取直播内容失败，请稍后重试~");
                    return;
                }
                JSONObject object = array.getJSONObject(0);
                String code = object.getString("code");
                JSONObject object_tips = object.getJSONObject("tips");
                String content = object_tips.getString("exchangeInstruction");
                showWxPublicNumberDialog(0, code, content);

                initData();
            } else {
                showTips("获取直播内容失败，请稍后重试~");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("获取直播内容失败，请稍后重试~");
        }
    }

    /**
     * 分析直播内容数据
     *
     * @param s
     */
    private void analyzeLiveShowData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String code = object.getString("code");
                JSONObject object_tips = object.getJSONObject("tips");
                String content = object_tips.getString("exchangeInstruction");
                showWxPublicNumberDialog(0, code, content);
            } else {
                showTips("获取直播内容失败，请稍后重试~");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("获取直播内容失败，请稍后重试~");
        }
    }

    /**
     * 显示前往微信公众号对话框
     *
     * @param type    0为直播，1为答疑
     * @param code
     * @param content
     */
    private void showWxPublicNumberDialog(final int type, final String code, final String content) {
        final int dp_size = DensityUtil.px2dip(mContext, DensityUtil.getScreenWidth(mContext));
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_wx_public_number_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        RelativeLayout rl_content = holder.getView(R.id.rl_content_wx_public_number_dialog);
                        int padding_content_top = dp_size * 36 / 375;
                        rl_content.setPadding(0, DensityUtil.dip2px(mContext, padding_content_top), 0, 0);
                        ViewGroup.LayoutParams layoutParams_content = rl_content.getLayoutParams();
                        layoutParams_content.width = DensityUtil.dip2px(mContext, dp_size);
                        layoutParams_content.height = DensityUtil.dip2px(mContext, dp_size + padding_content_top);

                        RelativeLayout rl_title = holder.getView(R.id.rl_title_wx_public_number_dialog);
                        ViewGroup.LayoutParams layoutParams_title = rl_title.getLayoutParams();
                        int width_title = dp_size * 213 / 375;
                        int height_title = dp_size * 81 / 375;
                        layoutParams_title.width = DensityUtil.dip2px(mContext, width_title);
                        layoutParams_title.height = DensityUtil.dip2px(mContext, height_title);

                        TextView tv_title = holder.getView(R.id.tv_title_wx_public_number_dialog);
                        int padding_left = dp_size * 74 / 375;
                        tv_title.setPadding(DensityUtil.dip2px(mContext, padding_left), 0, 0, 0);

                        ScrollView ll_code = holder.getView(R.id.ll_code_wx_public_number_dialog);
                        int padding_top = dp_size * 64 / 375;
                        int padding_bottom = dp_size * 64 / 375;
                        ll_code.setPadding(0, DensityUtil.dip2px(mContext, padding_top),
                                0, DensityUtil.dip2px(mContext, padding_bottom));

                        TextView tv_code = holder.getView(R.id.tv_code_wx_public_number_dialog);
                        TextView tv_copy = holder.getView(R.id.tv_copy_wx_public_number_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_wx_public_number_dialog);
                        ImageView iv_close = holder.getView(R.id.iv_close_wx_public_number_dialog);

                        String title;
                        String code_text;
                        String copy;
                        if (type == 0) {
                            title = "直播课程";
                            code_text = "直播课程兑换码为：" + code;
                            copy = "复制兑换码";
                        } else {
                            title = "一对一答疑";
                            code_text = "答疑兑换码为：" + code;
                            copy = "复制兑换码";
                        }
                        tv_title.setText(title);
                        tv_code.setText(code_text);
                        tv_copy.setText(copy);
                        tv_content.setText(content);

                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataUtil.copyContent(mContext, code);
                            }
                        });
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setWidth(dp_size)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

}
