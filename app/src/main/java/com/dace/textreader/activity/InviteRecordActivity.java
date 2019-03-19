package com.dace.textreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.InviteRecordAdapter;
import com.dace.textreader.bean.InviteRecord;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.StickyScrollView;

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
 * 邀请记录
 */
public class InviteRecordActivity extends BaseActivity {

    private static final String conditionUrl = HttpUrlPre.HTTP_URL + "/select/invitation/condition";
    private static final String listUrl = HttpUrlPre.HTTP_URL + "/select/invitation/list/detail";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private StickyScrollView scrollView;
    private ImageView iv_level;
    private TextView tv_number;
    private TextView tv_reward;
    private RecyclerView recyclerView;

    private InviteRecordActivity mContext;
    private List<InviteRecord> mList = new ArrayList<>();
    private InviteRecordAdapter adapter;

    private int inviteNum = 0;  //邀请数量
    private int ifcNum = 0;  //奖励数量
    private String level;

    private boolean refreshing = false;
    private boolean isEnd = false;
    private int pageNum = 1;  //列表页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_record);

        mContext = this;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                                           int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight()
                            - v.getMeasuredHeight())) {
                        //滑动到底部
                        if (!refreshing && !isEnd) {
                            getMoreListData();
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取更多列表数据
     */
    private void getMoreListData() {
        refreshing = true;
        pageNum++;
        new GetListData(mContext).execute(listUrl, String.valueOf(pageNum));
    }

    private void initData() {
        refreshing = true;
        isEnd = false;
        new GetConditionData(mContext).execute(conditionUrl);
        new GetListData(mContext).execute(listUrl, String.valueOf(pageNum));
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("邀请记录");
        scrollView = findViewById(R.id.nested_scroll_view_invite_record);
        iv_level = findViewById(R.id.iv_level_invite_record);
        tv_number = findViewById(R.id.tv_number_invite_record);
        tv_reward = findViewById(R.id.tv_reward_invite_record);
        recyclerView = findViewById(R.id.recycler_view_invite_record);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InviteRecordAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 分析用户邀请数据
     */
    private void analyzeConditionData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                inviteNum = object.optInt("inviteNum", 0);
                ifcNum = object.optInt("ifcNum", 0);
                level = object.getString("level");
                tv_number.setText(String.valueOf(inviteNum));
                tv_reward.setText(String.valueOf(ifcNum));
                switch (level) {
                    case "1":
                        iv_level.setImageResource(R.drawable.icon_invite_level_01);
                        iv_level.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        iv_level.setImageResource(R.drawable.icon_invite_level_02);
                        iv_level.setVisibility(View.VISIBLE);
                        break;
                    case "3":
                        iv_level.setImageResource(R.drawable.icon_invite_level_03);
                        iv_level.setVisibility(View.VISIBLE);
                        break;
                    case "4":
                        iv_level.setImageResource(R.drawable.icon_invite_level_04);
                        iv_level.setVisibility(View.VISIBLE);
                        break;
                    case "5":
                        iv_level.setImageResource(R.drawable.icon_invite_level_05);
                        iv_level.setVisibility(View.VISIBLE);
                        break;
                    case "6":
                        iv_level.setImageResource(R.drawable.icon_invite_level_06);
                        iv_level.setVisibility(View.VISIBLE);
                        break;
                    default:
                        iv_level.setVisibility(View.GONE);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析列表数据
     *
     * @param s
     */
    private void analyzeListData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorListData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        InviteRecord inviteRecord = new InviteRecord();
                        inviteRecord.setPhoneNumInvited(object.getString("phoneNumInvited"));
                        if (object.getString("registerTime").equals("") ||
                                object.getString("registerTime").equals("null")) {
                            inviteRecord.setRegisterTime("2018-01-01 00:00");
                        } else {
                            inviteRecord.setRegisterTime(DateUtil.time2YMD(
                                    object.getString("registerTime")));
                        }
                        inviteRecord.setIfcPrizeNum(object.optInt("ifcPrizeNum", 0));
                        mList.add(inviteRecord);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                errorListData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorListData();
        }
    }

    /**
     * 列表数据出错
     */
    private void errorListData() {
        isEnd = true;
    }

    /**
     * 获取用户邀请数据
     */
    private static class GetConditionData
            extends WeakAsyncTask<String, Void, String, InviteRecordActivity> {

        protected GetConditionData(InviteRecordActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(InviteRecordActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
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
        protected void onPostExecute(InviteRecordActivity activity, String s) {
            if (s != null) {
                activity.analyzeConditionData(s);
            }
        }
    }

    /**
     * 获取用户邀请列表数据
     */
    private static class GetListData
            extends WeakAsyncTask<String, Void, String, InviteRecordActivity> {

        protected GetListData(InviteRecordActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(InviteRecordActivity activity, String[] strings) {
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
        protected void onPostExecute(InviteRecordActivity activity, String s) {
            if (s == null) {
                activity.errorListData();
            } else {
                activity.analyzeListData(s);
            }
            activity.refreshing = false;
        }
    }

}
