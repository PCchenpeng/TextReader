package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.ChooseGradeAdapter;
import com.dace.textreader.bean.GradeBean;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

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
 * 注册--完善信息
 */
public class PerfectUserInfoActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/update/app/user/info";
    private final String gradeUrl = HttpUrlPre.HTTP_URL + "/register/grade";
    private final String imageUrl = HttpUrlPre.HTTP_URL + "/navigate/page?";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RecyclerView recyclerView;

    private PerfectUserInfoActivity mContext;
    private ChooseGradeAdapter adapter;
    private List<GradeBean> mList = new ArrayList<>();

    private int grade = 0;
    private String phoneNum = "";
    private String password = "";
    private String token = "";

    private String imagePath = "";
    private int status = 0;  //获取礼包图片的状态

    private boolean isOperating = false;

    private int mSelectedPosition = -1;
    private String mSelectedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_user_info);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        setNeedCheckCode(false);

        initView();
        initData();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backActivity();
            }
        });
        adapter.setOnGradeItemClick(new ChooseGradeAdapter.OnGradeItemClick() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (!isOperating && pos != -1 && pos < mList.size()) {
                    if (mList.get(pos).isSelected()) {
                        commit();
                    } else {
                        chooseGrade(pos);
                    }
                }
            }
        });
    }

    /**
     * 选择年级
     *
     * @param pos
     */
    private void chooseGrade(int pos) {
        if (mSelectedPosition != pos) {
            if (mSelectedPosition != -1) {
                mList.get(mSelectedPosition).setGrade(mSelectedText);
                mList.get(mSelectedPosition).setSelected(false);
            }

            mSelectedText = mList.get(pos).getGrade();

            mList.get(pos).setSelected(true);
            mList.get(pos).setGrade("确认");
            adapter.notifyDataSetChanged();

            mSelectedPosition = pos;

            grade = mList.get(pos).getGradeId();

            String text = "已选" + mSelectedText;
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            ForegroundColorSpan foregroundColorSpan =
                    new ForegroundColorSpan(Color.parseColor("#ff9933"));
            ssb.setSpan(foregroundColorSpan, 2, text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_title.setText(ssb);
        }
    }

    private void backActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("hideBack", NewMainActivity.isLoginHideBack);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void initData() {
        phoneNum = getIntent().getStringExtra("phoneNum");
        password = getIntent().getStringExtra("password");

        new GetGradeData(this).execute(gradeUrl);
        new GetImagePath(this).execute(imageUrl + "name=register_prize_image");
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_perfect_user_info);
        tv_title = findViewById(R.id.tv_title_perfect_user_info);
        recyclerView = findViewById(R.id.recycler_view_perfect_user_info);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChooseGradeAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    private void commit() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("phoneNum", phoneNum);
                    json.put("password", password);
                    json.put("userType", "android");
                    json.put("gradeid", grade);

                    //加密处理之后再上传
                    String info = DataEncryption.encode(json.toString());

                    JSONObject object = new JSONObject();
                    object.put("info", info);

                    RequestBody requestBody = RequestBody.create(DataUtil.JSON, object.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String data = (String) msg.obj;
                    try {
                        JSONObject json = new JSONObject(data);
                        int status = json.optInt("status", -1);
                        if (status == 400) {
                            isOperating = false;
                            MyToastUtil.showToast(PerfectUserInfoActivity.this, "注册失败，请稍后再试");
                        } else if (status == 200) {
                            JSONObject user = json.getJSONObject("data");
                            token = user.getString("token");
                            NewMainActivity.TOKEN = token;
                            NewMainActivity.STUDENT_ID = user.optInt("studentid", -1);
                            NewMainActivity.USERNAME = user.getString("username");
                            NewMainActivity.USERIMG = user.getString("userimg");
                            NewMainActivity.GRADE = user.optInt("level", -1);
                            NewMainActivity.GRADE_ID = user.optInt("gradeid", 110);
                            NewMainActivity.PY_SCORE = user.getString("score");
                            NewMainActivity.LEVEL = user.optInt("level", -1);
                            NewMainActivity.DESCRIPTION = user.getString("description");
                            signSuccess();
                        } else {
                            isOperating = false;
                            MyToastUtil.showToast(PerfectUserInfoActivity.this, "注册失败，请稍后再试");
                        }
                    } catch (JSONException e) {
                        isOperating = false;
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * 注册成功
     */
    private void signSuccess() {
        NewMainActivity.TOKEN = token;
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("token", token);
        editor.apply();
        if (imagePath.equals("")) {
            status = 1;
            new GetImagePath(PerfectUserInfoActivity.this).execute(imageUrl + "name=register_prize_image");
        } else {
            showPrizeImage();
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
            if (jsonObject.optInt("status", -1) == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    GradeBean gradeBean = new GradeBean();
                    JSONObject object = jsonArray.getJSONObject(i);
                    gradeBean.setGradeId(object.optInt("gradeid", 110));
                    gradeBean.setGrade(object.getString("gradename"));
                    gradeBean.setSelected(false);
                    mList.add(gradeBean);
                }
                adapter.notifyDataSetChanged();
            } else {
                addLocalData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            addLocalData();
        }
    }

    /**
     * 添加本地数据
     */
    private void addLocalData() {

        mList.clear();
        adapter.notifyDataSetChanged();

        GradeBean gradeBean_110 = new GradeBean();
        gradeBean_110.setGrade("一年级");
        gradeBean_110.setGradeId(110);
        gradeBean_110.setSelected(false);
        mList.add(gradeBean_110);

        GradeBean gradeBean_120 = new GradeBean();
        gradeBean_120.setGrade("二年级");
        gradeBean_120.setGradeId(120);
        gradeBean_120.setSelected(false);
        mList.add(gradeBean_120);

        GradeBean gradeBean_130 = new GradeBean();
        gradeBean_130.setGrade("三年级");
        gradeBean_130.setGradeId(130);
        gradeBean_130.setSelected(false);
        mList.add(gradeBean_130);

        GradeBean gradeBean_140 = new GradeBean();
        gradeBean_140.setGrade("四年级");
        gradeBean_140.setGradeId(140);
        gradeBean_140.setSelected(false);
        mList.add(gradeBean_140);

        GradeBean gradeBean_150 = new GradeBean();
        gradeBean_150.setGrade("五年级");
        gradeBean_150.setGradeId(150);
        gradeBean_150.setSelected(false);
        mList.add(gradeBean_150);

        GradeBean gradeBean_160 = new GradeBean();
        gradeBean_160.setGrade("六年级");
        gradeBean_160.setGradeId(160);
        gradeBean_160.setSelected(false);
        mList.add(gradeBean_160);

        GradeBean gradeBean_210 = new GradeBean();
        gradeBean_210.setGrade("初一");
        gradeBean_210.setGradeId(210);
        gradeBean_210.setSelected(false);
        mList.add(gradeBean_210);

        GradeBean gradeBean_220 = new GradeBean();
        gradeBean_220.setGrade("初二");
        gradeBean_220.setGradeId(220);
        gradeBean_220.setSelected(false);
        mList.add(gradeBean_220);

        GradeBean gradeBean_230 = new GradeBean();
        gradeBean_230.setGrade("初三");
        gradeBean_230.setGradeId(230);
        gradeBean_230.setSelected(false);
        mList.add(gradeBean_230);

        GradeBean gradeBean_310 = new GradeBean();
        gradeBean_310.setGrade("高一");
        gradeBean_310.setGradeId(310);
        gradeBean_310.setSelected(false);
        mList.add(gradeBean_310);

        GradeBean gradeBean_320 = new GradeBean();
        gradeBean_320.setGrade("高二");
        gradeBean_320.setGradeId(320);
        gradeBean_320.setSelected(false);
        mList.add(gradeBean_320);

        GradeBean gradeBean_330 = new GradeBean();
        gradeBean_330.setGrade("高三");
        gradeBean_330.setGradeId(330);
        gradeBean_330.setSelected(false);
        mList.add(gradeBean_330);

        adapter.notifyDataSetChanged();
    }

    /**
     * 获取年级数据
     */
    private static class GetGradeData
            extends WeakAsyncTask<String, Integer, String, PerfectUserInfoActivity> {

        protected GetGradeData(PerfectUserInfoActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(PerfectUserInfoActivity activity, String[] params) {
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
        protected void onPostExecute(PerfectUserInfoActivity activity, String s) {
            if (s == null) {
                activity.addLocalData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取图片路径
     */
    private static class GetImagePath
            extends WeakAsyncTask<String, Integer, String, PerfectUserInfoActivity> {

        protected GetImagePath(PerfectUserInfoActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(PerfectUserInfoActivity activity, String[] strings) {

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
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
        protected void onPostExecute(PerfectUserInfoActivity activity, String s) {
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        activity.imagePath = jsonObject.getString("data");
                        if (activity.status == 1) {
                            activity.showPrizeImage();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 显示奖励大礼包
     */
    private void showPrizeImage() {
        Intent intent = new Intent(this, TransitActivity.class);
        intent.putExtra("imagePath", imagePath);
        startActivity(intent);

        if (NewMainActivity.isLoginHideBack) {
            broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
            backToMainActivity();
        }

        finish();
    }

    /**
     * 发送广播
     *
     * @param action 广播的Action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

}
