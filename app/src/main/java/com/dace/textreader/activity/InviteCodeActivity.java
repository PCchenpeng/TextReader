package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 邀请码绑定
 */
public class InviteCodeActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/new/invite/list";
    private static final String bindUrl = HttpUrlPre.HTTP_URL + "/insert/invite/relation";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_content;
    private LinearLayout ll_code;
    private EditText et_code;
    private TextView tv_code;
    private LinearLayout ll_user;
    private ImageView iv_user;
    private TextView tv_user;
    private TextView tv_grade;
    private FrameLayout frameLayout;

    private InviteCodeActivity mContext;

    private long userId = -1;
    private String username = "";
    private String userImage = "";
    private int userGrade = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

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
                if (string.contains("&friendInvitation")) {
                    string = string.substring(0, string.indexOf("&friendInvitation"));
                    et_code.setText(string);
                    et_code.setSelection(string.length());
                }
            }
        });
        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindCode();
            }
        });
        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToUserHomePage();
            }
        });
    }

    /**
     * 前往个人首页
     */
    private void turnToUserHomePage() {
        if (userId != -1) {
            Intent intent = new Intent(mContext, UserHomepageActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
    }

    /**
     * 绑定邀请码
     */
    private void bindCode() {
        String code = et_code.getText().toString();
        if (code.trim().equals("")) {
            showTips("请先输入邀请码");
        } else {
            showTips("正在提交，请稍后...");
            new BindCode(mContext).execute(bindUrl, code, String.valueOf(NewMainActivity.STUDENT_ID));
        }
    }

    private void initData() {
        showLoadingView(true);
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID));
    }

    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
            GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("邀请码");

        rl_content = findViewById(R.id.rl_content_invite_code);

        ll_code = findViewById(R.id.ll_code_invite_code);
        et_code = findViewById(R.id.et_code_invite_code);
        tv_code = findViewById(R.id.tv_code_invite_code);

        ll_user = findViewById(R.id.ll_user_invite_code);
        iv_user = findViewById(R.id.iv_user_invite_code);
        tv_user = findViewById(R.id.tv_user_invite_code);
        tv_grade = findViewById(R.id.tv_grade_invite_code);

        frameLayout = findViewById(R.id.frame_invite_code);
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            int status = jsonObject.optInt("status", -1);
            if (200 == status || 400 == status) {
                JSONObject object = jsonObject.getJSONObject("data");
                userId = object.optLong("studentid", -1);
                userImage =
//                        HttpUrlPre.FILE_URL +
                                object.getString("userimg");
                username = object.getString("username");
                userGrade = object.optInt("gradeid", 110);
                updateUi();
            } else {
                emptyData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            emptyData();
        }
    }

    /**
     * 更新界面
     */
    private void updateUi() {
        ll_code.setVisibility(View.GONE);
        ll_user.setVisibility(View.VISIBLE);
        GlideUtils.loadUserImage(mContext, userImage, iv_user);
        tv_user.setText(username);
        tv_grade.setText(DataUtil.gradeCode2Chinese(userGrade));
    }

    /**
     * 获取数据为空
     */
    private void emptyData() {
        ll_user.setVisibility(View.GONE);
        ll_code.setVisibility(View.VISIBLE);
    }

    /**
     * 分析绑定数据
     *
     * @param s
     */
    private void analyzeBindData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                userId = object.optLong("userId", -1);
                userImage =
//                        HttpUrlPre.FILE_URL +
                        object.getString("userImg");
                username = object.getString("userName");
                userGrade = object.optInt("gradeId", 110);
                updateUi();
            } else if (300 == jsonObject.optInt("status", -1)) {
                showTips("自己不能邀请自己哦~~");
            } else if (400 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                userId = object.optLong("userId", -1);
                userImage =
//                        HttpUrlPre.FILE_URL +
                                object.getString("userImg");
                username = object.getString("userName");
                userGrade = object.optInt("gradeId", 110);
                updateUi();
                showTips("你已经被邀请过了~");
            } else if (600 == jsonObject.optInt("status", -1)) {
                showTips("邀请码错误，请检查后重新输入~");
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 数据错误
     */
    private void errorData() {
        showTips("网络错误，请稍后重试~");
    }


    /**
     * 获取数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, InviteCodeActivity> {

        protected GetData(InviteCodeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(InviteCodeActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
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
        protected void onPostExecute(InviteCodeActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.emptyData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 绑定邀请码
     */
    private static class BindCode
            extends WeakAsyncTask<String, Void, String, InviteCodeActivity> {

        protected BindCode(InviteCodeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(InviteCodeActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("code", strings[1]);
                object.put("studentId", strings[2]);
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
        protected void onPostExecute(InviteCodeActivity activity, String s) {
            activity.showLoadingView(false);
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeBindData(s);
            }
        }
    }

}
