package com.dace.textreader.activity;

import android.graphics.Color;
import android.os.Bundle;
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
 * 修改年级
 */
public class UpdateGradeActivity extends BaseActivity {

    private final String gradeUrl = HttpUrlPre.HTTP_URL + "/register/grade";
    private static final String updateGradeUrl = HttpUrlPre.HTTP_URL + "/update/student/grade";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RecyclerView recyclerView;

    private UpdateGradeActivity mContext;
    private ChooseGradeAdapter adapter;
    private List<GradeBean> mList = new ArrayList<>();

    private int grade = 0;
    private int mSelectedPosition = -1;
    private String mSelectedText = "";
    private boolean isOperating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_grade);

        mContext = this;

        initView();
        initData();
        initEvents();
    }

    private void initData() {
        new GetGradeData(this).execute(gradeUrl);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                } else {
                    MyToastUtil.showToast(mContext, "正在提交，请稍后~");
                }
            }
        });
    }

    /**
     * 提交
     */
    private void commit() {
        isOperating = true;
        new UpdateGradeData(mContext).execute(updateGradeUrl, String.valueOf(grade));
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

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_title_update_grade);
        recyclerView = findViewById(R.id.recycler_view_update_grade);
        recyclerView.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChooseGradeAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeGradeData(String s) {
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
     * 分析更改年级数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                NewMainActivity.GRADE_ID = grade;
                MyToastUtil.showToast(mContext, "更改年级成功");
                finish();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更改年级失败
     */
    private void errorData() {

    }

    /**
     * 获取年级数据
     */
    private static class GetGradeData
            extends WeakAsyncTask<String, Integer, String, UpdateGradeActivity> {

        protected GetGradeData(UpdateGradeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UpdateGradeActivity activity, String[] params) {
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
        protected void onPostExecute(UpdateGradeActivity activity, String s) {
            if (s == null) {
                activity.addLocalData();
            } else {
                activity.analyzeGradeData(s);
            }
        }
    }

    /**
     * 更改级数据
     */
    private static class UpdateGradeData
            extends WeakAsyncTask<String, Integer, String, UpdateGradeActivity> {

        protected UpdateGradeData(UpdateGradeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UpdateGradeActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("gradeId", params[1]);
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
        protected void onPostExecute(UpdateGradeActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
            activity.isOperating = false;
        }
    }

}
