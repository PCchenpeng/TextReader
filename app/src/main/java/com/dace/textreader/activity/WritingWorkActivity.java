package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.WritingWorkTeacherAdapter;
import com.dace.textreader.bean.TeacherBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
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
 * 写作文--提交作业
 */
public class WritingWorkActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/teacher/relation/query";
    private static final String submitForIdUrl = HttpUrlPre.HTTP_URL + "/correction/homework/id";
    private static final String submitForEditorUrl = HttpUrlPre.HTTP_URL + "/correction/homework";
    private static final String unbindUrl = HttpUrlPre.HTTP_URL + "/regularRelation/relieve";
    //保存到草稿箱
    private static final String SAVE_WRITING_DRAFT = HttpUrlPre.HTTP_URL + "/writing/save";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout ll_no_bind;
    private LinearLayout ll_add_bind;
    private RelativeLayout rl_add_bind;
    private TextView tv_add_bind;
    private TextView tv_submit;

    private WritingWorkActivity mContext;

    private LinearLayoutManager mLayoutManager;
    private List<TeacherBean> mList = new ArrayList<>();
    private WritingWorkTeacherAdapter adapter;

    private boolean isSubmit = false;  //是否是提交作业

    private boolean isEditor = false;
    private long teacherId;
    private String writingId;
    private String writingTitle;
    private String writingContent;
    private String writingCover;
    private int writingCount;
    private String writingArea;
    private int writingType;
    private int writingFormat;

    private int pageNum = 1;
    private boolean refreshing = false;  //是否正在刷新中
    private boolean isEnd = false;  //是否加载完所有数据
    private boolean isOperating = false;  //是否正在操作中

    //提交按钮的状态，-1表示没有可选择的老师，0表示未选择老师，1表示已选择老师
    private int submitStatus = -1;

    private int mOperatePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_work);

        mContext = this;

        isSubmit = getIntent().getBooleanExtra("isSubmit", false);
        if (isSubmit) {
            isEditor = getIntent().getBooleanExtra("isEditor", false);
            writingId = getIntent().getStringExtra("writingId");
            if (isEditor) {
                writingTitle = getIntent().getStringExtra("writingTitle");
                writingContent = getIntent().getStringExtra("writingContent");
                writingCover = getIntent().getStringExtra("writingCover");
                writingFormat = getIntent().getIntExtra("writingFormat", 1);
                writingCount = getIntent().getIntExtra("writingCount", 0);
            } else {
                writingTitle = "";
                writingContent = "";
                writingCover = "";
                writingFormat = 1;
                writingCount = 0;
            }
            writingArea = getIntent().getStringExtra("writingArea");
            writingType = getIntent().getIntExtra("writingType", 6);
        }

        initView();
        initEvents();
        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_add_bind.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSubmit) {
            tv_submit.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setPadding(0, 0, 0, tv_submit.getHeight());
                }
            });
        }
        initData();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_add_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBind();
            }
        });
        tv_add_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBind();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOperating) {
                    if (submitStatus == -1) {
                        isOperating = true;
                        showTips("正在保存...");
                        new SaveToDraft(mContext).execute(SAVE_WRITING_DRAFT,
                                writingContent, writingTitle,
                                String.valueOf(NewMainActivity.STUDENT_ID),
                                String.valueOf(writingCount), writingCover,
                                String.valueOf(writingFormat));
                    } else if (submitStatus == 1 && teacherId != -1) {
                        isOperating = true;
                        showTips("正在提交...");
                        String submitUrl;
                        if (isEditor) {
                            submitUrl = submitForEditorUrl;
                        } else {
                            submitUrl = submitForIdUrl;
                        }
                        new SubmitToTeacher(mContext).execute(submitUrl,
                                String.valueOf(NewMainActivity.STUDENT_ID),
                                String.valueOf(teacherId), writingId, writingTitle,
                                writingContent, writingCover, String.valueOf(writingCount),
                                writingArea, String.valueOf(writingType), String.valueOf(writingFormat));
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing) {
                    initData();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!refreshing && !isEnd && !isOperating) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
        adapter.setOnItemClick(new WritingWorkTeacherAdapter.OnTeacherListItemClickListen() {
            @Override
            public void onClick(View view) {
                if (isSubmit && !refreshing && mList.size() != 0) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    updateItem(pos);
                }
            }
        });
        adapter.setOnTeacherItemUnBindClick(new WritingWorkTeacherAdapter.OnTeacherItemUnBindClickListen() {
            @Override
            public void onClick(int position) {
                if (!isSubmit && !refreshing) {
                    if (isOperating) {
                        showTips("另一项操作正在进行中，请稍后...");
                    } else {
                        if (mList.get(position).getRelationStatus() == 1) {
                            showMakeSureUnbindDialog(position);
                        }
                    }
                }
            }
        });
    }

    /**
     * @param position
     */
    private void showMakeSureUnbindDialog(final int position) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("是否解除绑定？");
                        String content = "解除绑定之后，不能提交作业给" +
                                mList.get(position).getTeacherName() + "。";
                        tv_content.setText(content);
                        tv_left.setText("确定");
                        tv_right.setText("取消");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unBindTeacher(position);
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
                .setMargin(60)
                .show(getSupportFragmentManager());
    }

    /**
     * 解除绑定
     *
     * @param position
     */
    private void unBindTeacher(int position) {
        mOperatePosition = position;
        long teacherId = mList.get(position).getTeacherId();
        new UnBindTeacher(mContext).execute(unbindUrl, String.valueOf(teacherId));
    }

    /**
     * 更新按钮
     *
     * @param pos
     */
    private void updateItem(int pos) {
        boolean isSelected = mList.get(pos).isSelected();
        if (mList.get(pos).getRelationStatus() == 1) {
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setSelected(false);
            }
            if (!isSelected) {
                mList.get(pos).setSelected(true);
            }
            adapter.notifyDataSetChanged();
            updateSubmitButtonState();
        } else {
            showTips("老师未绑定");
        }
    }

    /**
     * 添加绑定
     */
    private void addBind() {
        startActivity(new Intent(mContext, BindTeacherActivity.class));
    }

    /**
     * 更新提交按钮状态
     */
    private void updateSubmitButtonState() {
        submitStatus = -1;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getRelationStatus() == 1) {
                submitStatus = 0;
                break;
            }
        }
        if (submitStatus != -1) {
            for (int j = 0; j < mList.size(); j++) {
                if (mList.get(j).isSelected()) {
                    submitStatus = 1;
                    teacherId = mList.get(j).getTeacherId();
                    break;
                }
            }
        }
        if (submitStatus == -1) {  //没有可选老师
            teacherId = -1;
            tv_submit.setBackgroundColor(Color.parseColor("#FF9933"));
            tv_submit.setText("老师尚未确认，保存草稿");
            if (writingTitle.equals("") || writingContent.equals("")) {
                tv_submit.setVisibility(View.GONE);
            } else {
                tv_submit.setVisibility(View.VISIBLE);
            }
        } else if (submitStatus == 0) {  //未选择老师
            teacherId = -1;
            tv_submit.setBackgroundColor(Color.parseColor("#999999"));
            tv_submit.setText("请选择老师");
        } else if (submitStatus == 1) {  //已选择老师
            tv_submit.setBackgroundColor(Color.parseColor("#FF9933"));
            tv_submit.setText("确认提交");
        }
    }

    private void initData() {
        if (!refreshing) {
            refreshing = true;
            if (ll_no_bind.getVisibility() == View.VISIBLE) {
                ll_no_bind.setVisibility(View.GONE);
            }
            swipeRefreshLayout.setRefreshing(true);
            isEnd = false;
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(mContext).execute(url, String.valueOf(pageNum));
        }
    }

    /**
     * 获取更多数据
     *
     * @param newState
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                mLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            refreshing = true;
            pageNum++;
            new GetData(mContext).execute(url, String.valueOf(pageNum));
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("我的老师");

        frameLayout = findViewById(R.id.frame_writing_work);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_writing_work);
        recyclerView = findViewById(R.id.recycler_view_writing_work);
        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new WritingWorkTeacherAdapter(mContext, mList, isSubmit);
        if (!isSubmit) {
            adapter.setShowHasBind(false);
        }
        recyclerView.setAdapter(adapter);
        ll_no_bind = findViewById(R.id.ll_no_bind_writing_work);
        ll_add_bind = findViewById(R.id.ll_add_bind_writing_work);
        rl_add_bind = findViewById(R.id.rl_add_bind_writing_work);
        tv_add_bind = findViewById(R.id.tv_add_bind_writing_work);
        tv_submit = findViewById(R.id.tv_submit_writing_work);

        if (isSubmit) {
            tv_submit.setVisibility(View.VISIBLE);
        } else {
            tv_submit.setVisibility(View.GONE);
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
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        TeacherBean teacherBean = new TeacherBean();
                        teacherBean.setTeacherId(object.optLong("teacherId", -1L));
                        teacherBean.setTeacherName(object.getString("realname"));
                        teacherBean.setOrganization(object.getString("organization"));
                        teacherBean.setRelationStatus(object.optInt("relationStatus", -2));
                        teacherBean.setSelected(false);
                        mList.add(teacherBean);
                    }
                    adapter.notifyDataSetChanged();
                    if (tv_add_bind.getVisibility() == View.GONE) {
                        tv_add_bind.setVisibility(View.VISIBLE);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
        if (isSubmit) {
            updateSubmitButtonState();
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        isEnd = true;
        if (mList.size() == 0) {
            ll_no_bind.setVisibility(View.VISIBLE);
            tv_add_bind.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
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
            showTips("获取数据失败~");
        }
    }

    /**
     * 分析提交老师作业数据
     *
     * @param s
     */
    private void analyzeSubmitData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                submitSuccess();
            } else {
                submitError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            submitError();
        }
    }

    /**
     * 提交成功
     */
    private void submitSuccess() {
        if (writingTitle.equals("") || writingContent.equals("")) {
            Intent intent = new Intent(mContext, OperationResultActivity.class);
            intent.putExtra("operateType", "writing_work");
            intent.putExtra("isSuccessful", true);
            intent.putExtra("content", "");
            intent.putExtra("price", 0);
            startActivityForResult(intent, 0);
        } else {
            Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
            intent.putExtra("id", writingId);
            intent.putExtra("title", writingTitle);
            intent.putExtra("content", writingContent);
            intent.putExtra("cover", writingCover);
            intent.putExtra("count", writingCount);
            intent.putExtra("format", writingFormat);
            intent.putExtra("area", 6);
            intent.putExtra("index", 3);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                boolean isSubmit = data.getBooleanExtra("submit", false);
                if (isSubmit) {
                    Intent intent = new Intent();
                    intent.putExtra("submit", true);
                    setResult(0, intent);
                    finish();
                }
            }
        }
    }

    /**
     * 提交作业失败
     */
    private void submitError() {
        showTips("提交作业失败");
    }

    /**
     * 分析解除绑定数据
     *
     * @param s
     */
    private void analyzeUnBindData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showTips("解除绑定成功");
                if (mOperatePosition != -1 && mOperatePosition < mList.size()) {
                    mList.remove(mOperatePosition);
                    adapter.notifyDataSetChanged();
                    mOperatePosition = -1;
                }
            } else {
                showTips("解除绑定失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("解除绑定失败");
        }
    }

    /**
     * 分析调用了保存到草稿箱接口后返回的信息
     *
     * @param s
     */
    private void analyzeSaveToDraftData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String id = object.getString("id");
                saveSuccess(id);
            } else {
                saveFailed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            saveFailed();
        }
    }

    /**
     * 保存到草稿箱失败
     */
    private void saveFailed() {
        showTips("保存失败，请稍后重试！");
    }

    /**
     * 保存到草稿箱成功
     */
    private void saveSuccess(String id) {
        showTips("保存成功！");
        Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", writingTitle);
        intent.putExtra("content", writingContent);
        intent.putExtra("cover", writingCover);
        intent.putExtra("count", writingCount);
        intent.putExtra("format", writingFormat);
        intent.putExtra("area", 6);
        intent.putExtra("index", 3);
        startActivityForResult(intent, 0);
    }

    /**
     * 显示提示信息
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
            extends WeakAsyncTask<String, Void, String, WritingWorkActivity> {

        protected GetData(WritingWorkActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingWorkActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", strings[1]);
                object.put("pageSize", 10);
                OkHttpClient client = new OkHttpClient();
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
        protected void onPostExecute(WritingWorkActivity activity, String s) {
            activity.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
            activity.refreshing = false;
        }
    }

    /**
     * 提交给老师
     */
    private static class SubmitToTeacher
            extends WeakAsyncTask<String, Void, String, WritingWorkActivity> {

        protected SubmitToTeacher(WritingWorkActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingWorkActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("teacherId", strings[2]);
                if (activity.isEditor) {
                    if (!strings[3].equals("") && !strings[3].equals("null")) {
                        object.put("id", strings[3]);
                    }
                    if (!strings[4].equals("") && !strings[4].equals("null")) {
                        object.put("article", strings[4]);
                    }
                    if (!strings[5].equals("") && !strings[5].equals("null")) {
                        object.put("content", strings[5]);
                    }
                    if (!strings[6].equals("") && !strings[6].equals("null")) {
                        object.put("cover", strings[6]);
                    }
                    object.put("wordsNum", strings[7]);
                    object.put("format", strings[10]);
                } else {
                    object.put("id", strings[3]);
                    object.put("area", strings[8]);
                }

                object.put("type", strings[9]);

                OkHttpClient client = new OkHttpClient();
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
        protected void onPostExecute(WritingWorkActivity activity, String s) {
            if (s == null) {
                activity.submitError();
            } else {
                activity.analyzeSubmitData(s);
            }
            activity.isOperating = false;
        }
    }

    /**
     * 绑定老师
     */
    private static class UnBindTeacher
            extends WeakAsyncTask<String, Void, String, WritingWorkActivity> {

        protected UnBindTeacher(WritingWorkActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingWorkActivity activity, String[] strings) {
            try {
                JSONArray array = new JSONArray();
                array.put(NewMainActivity.STUDENT_ID);
                JSONObject object = new JSONObject();
                object.put("studentIds", array.toString());
                object.put("teacherId", strings[1]);
                OkHttpClient client = new OkHttpClient();
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
        protected void onPostExecute(WritingWorkActivity activity, String s) {
            if (s == null) {
                activity.showTips("解除绑定失败");
            } else {
                activity.analyzeUnBindData(s);
            }
            activity.isOperating = false;
        }
    }

    /**
     * 保存到草稿箱
     */
    private static class SaveToDraft
            extends WeakAsyncTask<String, Void, String, WritingWorkActivity> {

        protected SaveToDraft(WritingWorkActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingWorkActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", strings[1]);
                jsonObject.put("article", strings[2]);
                jsonObject.put("studentId", strings[3]);
                jsonObject.put("wordsNum", Integer.valueOf(strings[4]));
                if (activity.writingArea.equals("5") && activity.writingType == 5
                        && !activity.writingId.equals("")) {
                    jsonObject.put("id", activity.writingId);
                }
                if (!strings[5].equals("") && !strings[5].equals("null")) {
                    jsonObject.put("cover", strings[5]);
                }
                jsonObject.put("format", strings[6]);
                RequestBody body = RequestBody.create(DataUtil.JSON, jsonObject.toString());
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
        protected void onPostExecute(WritingWorkActivity activity, String s) {
            if (s == null) {
                activity.saveFailed();
            } else {
                activity.analyzeSaveToDraftData(s);
            }
        }
    }

}
