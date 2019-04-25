package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.WritingWorkTeacherAdapter;
import com.dace.textreader.bean.TeacherBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 绑定老师
 */
public class BindTeacherActivity extends BaseActivity {

    private static final String bindUrl = HttpUrlPre.HTTP_URL + "/regularRelation/setup";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private EditText et_code;
    private TextView tv_code;
    private RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    private List<TeacherBean> mList = new ArrayList<>();
    private WritingWorkTeacherAdapter adapter;

    private BindTeacherActivity mContext;

    private boolean refreshing = false;
    private String teacherCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_teacher);

        mContext = this;

        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (string.contains("&teacher")) {
                    string = string.substring(0, string.indexOf("&teacher"));
                    et_code.setText(string);
                    et_code.setSelection(string.length());
                }
            }
        });
        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherCode = et_code.getText().toString();
                if (teacherCode.trim().isEmpty()) {
                    showTips("请输入要搜索的内容");
                } else {
                    initData();
                }
            }
        });
    }

    private void initData() {
        if (!refreshing) {
            refreshing = true;
            mList.clear();
            adapter.notifyDataSetChanged();
            new GetData(mContext).execute(bindUrl, teacherCode);
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("绑定老师");

        et_code = findViewById(R.id.et_code_bind_teacher);
        tv_code = findViewById(R.id.tv_code_bind_teacher);

        recyclerView = findViewById(R.id.recycler_view_bind_teacher);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new WritingWorkTeacherAdapter(mContext, mList, false);
        adapter.setShowHasBind(true);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)
                    || 700 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                TeacherBean teacherBean = new TeacherBean();
                teacherBean.setTeacherId(object.optLong("teacherId", -1L));
                teacherBean.setTeacherName(object.getString("realname"));
                teacherBean.setOrganization(object.getString("organization"));
                teacherBean.setRelationStatus(object.optInt("relationStatus", -2));
                teacherBean.setSelected(false);
                mList.add(teacherBean);
                adapter.notifyDataSetChanged();
            } else {
                emptyData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            emptyData();
        }
    }

    /**
     * 获取老师数据为空
     */
    private void emptyData() {
        showTips("未搜索到老师");
    }



    /**
     * 获取老师数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, BindTeacherActivity> {

        protected GetData(BindTeacherActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BindTeacherActivity activity, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("teacherCode", strings[1]);
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
        protected void onPostExecute(BindTeacherActivity activity, String s) {
            if (s == null) {
                activity.emptyData();
            } else {
                activity.analyzeData(s);
            }
            activity.refreshing = false;
        }
    }

}
