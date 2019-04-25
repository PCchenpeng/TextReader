package com.dace.textreader.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 修改个人简介
 */
public class UpdateIntroductionActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/update/student/description";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_commit;
    private EditText editText;
    private TextView tv_number;

    private UpdateIntroductionActivity mContext;

    private String introduction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_introduction);

        mContext = this;

        initView();
        initEvents();
        setImmerseLayout();

    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_commit.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NewMainActivity.DESCRIPTION.equals("") || NewMainActivity.DESCRIPTION.equals("null")) {
            NewMainActivity.DESCRIPTION = "";
        } else {
            editText.setText(NewMainActivity.DESCRIPTION);
            editText.setSelection(NewMainActivity.DESCRIPTION.length());
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                introduction = s.toString();
                int len = 30 - introduction.length();
                tv_number.setText(String.valueOf(len));
            }
        });
        rl_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(introduction)) {
                    showTips("请输入简介内容");
                } else {
                    new UpdateUserIntroduction(mContext).execute(url, introduction);
                }
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("简介");

        rl_commit = findViewById(R.id.rl_commit_update_introduction);
        editText = findViewById(R.id.et_update_introduction);
        tv_number = findViewById(R.id.tv_number_update_introduction);

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
    }


    private static class UpdateUserIntroduction
            extends WeakAsyncTask<String, Void, String, UpdateIntroductionActivity> {

        protected UpdateUserIntroduction(UpdateIntroductionActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(UpdateIntroductionActivity activity, String[] strings) {

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", NewMainActivity.STUDENT_ID);
                jsonObject.put("description", strings[1]);
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
        protected void onPostExecute(UpdateIntroductionActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
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
                NewMainActivity.DESCRIPTION = introduction;
                showTips("更改简介成功");
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
     * 更改简介失败
     */
    private void errorData() {
        showTips("更改简介失败,请稍后再试~");
    }
}
