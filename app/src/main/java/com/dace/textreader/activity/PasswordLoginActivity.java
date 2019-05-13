package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 账号密码登录
 */
public class PasswordLoginActivity extends BaseActivity implements View.OnClickListener {

    private final String url = HttpUrlPre.HTTP_URL + "/login";

    private RelativeLayout rl_right_back;
    private EditText et_phoneNum;
    private ImageView iv_clear;
    private EditText et_password;
    private ImageView iv_show;
    private RelativeLayout rl_sure;
    private TextView tv_sure;
    private TextView tv_sms;
    private TextView tv_forget;
    private LinearLayout ll_wechat;

    private PasswordLoginActivity mContext;

    private String phoneNum;
    private String password;

    private boolean hideBack = false; //是否隐藏返回
    private boolean isPhoneNumReady = false;  //是否手机号码准备好
    private boolean isPasswordReady = false;  //是否密码准备好
    private boolean isShowPassword = false;  //是否显示密码
    private boolean isLogin = false;  //是否正在登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_login);

        mContext = this;

        hideBack = getIntent().getBooleanExtra("hideBack", false);

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        phoneNum = sharedPreferences.getString("phoneNum", "");

        setNeedCheckCode(false);

        initView();
        initEvents();

        et_phoneNum.setText(phoneNum);
        et_phoneNum.setSelection(phoneNum.length());
    }

    private void initEvents() {
        rl_right_back.setOnClickListener(this);
        iv_clear.setOnClickListener(this);
        iv_show.setOnClickListener(this);
        rl_sure.setOnClickListener(this);
        tv_sms.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        ll_wechat.setOnClickListener(this);
        et_phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneNum = s.toString();
                if (phoneNum.length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                    isPhoneNumReady = true;
                    if (isPasswordReady) {
                        rl_sure.setSelected(true);
                    }
                } else {
                    iv_clear.setVisibility(View.GONE);
                }
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    et_password.setText(str1);
                    et_password.setSelection(start);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
                if (password.length() > 0) {
                    iv_show.setVisibility(View.VISIBLE);
                    if (password.length() >= 6) {
                        isPasswordReady = true;
                        if (isPhoneNumReady) {
                            rl_sure.setSelected(true);
                        }
                    } else {
                        isPasswordReady = false;
                        rl_sure.setSelected(false);
                    }
                } else {
                    iv_show.setVisibility(View.GONE);
                    isPasswordReady = false;
                    rl_sure.setSelected(false);
                }
            }
        });
    }

    private void initView() {
        rl_right_back = findViewById(R.id.rl_right_back_password_login);
        et_phoneNum = findViewById(R.id.et_phone_number_password_login);
        iv_clear = findViewById(R.id.iv_clear_password_login);
        et_password = findViewById(R.id.et_password_password_login);
        iv_show = findViewById(R.id.iv_show_password_login);
        rl_sure = findViewById(R.id.rl_sure_password_login);
        tv_sure = findViewById(R.id.tv_sure_password_login);
        tv_sms = findViewById(R.id.tv_sms_password_login);
        tv_forget = findViewById(R.id.tv_forget_password_login);
        ll_wechat = findViewById(R.id.ll_wechat_password_login);

        iv_clear.setVisibility(View.GONE);
        iv_show.setVisibility(View.GONE);

        et_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_right_back_password_login:
                backActivity();
                break;
            case R.id.iv_clear_password_login:
                et_phoneNum.setText("");
                break;
            case R.id.iv_show_password_login:
                showOrHidePassword();
                break;
            case R.id.rl_sure_password_login:
                if (!isPhoneNumReady) {
                    showTips("请输入手机号码");
                } else if (!isPasswordReady) {
                    showTips("请输入密码");
                } else if (!isLogin) {
                    login();
                }
                break;
            case R.id.tv_sms_password_login:
                turnToSmsLogin();
                break;
            case R.id.tv_forget_password_login:
                turnToForget();
                break;
            case R.id.ll_wechat_password_login:
                wxLogin();
                break;
        }
    }

    /**
     * 显示和隐藏密码
     */
    private void showOrHidePassword() {
        if (isShowPassword) {
            isShowPassword = false;
            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_show.setImageResource(R.drawable.login_icon_eye_close);
        } else {
            isShowPassword = true;
            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_show.setImageResource(R.drawable.login_icon_eye_open);
        }
        et_password.setSelection(password.length());
    }

    /**
     * 微信登录
     */
    private void wxLogin() {
        if (!App.api.isWXAppInstalled()) {
            showTips("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "pythe_wxlogin";
        App.api.sendReq(req);
        finish();
    }

    /**
     * 忘记密码
     */
    private void turnToForget() {
        Intent intent = new Intent(mContext, ForgetPasswordActivity.class);
        intent.putExtra("hideBack", hideBack);
        startActivity(intent);
        finish();
    }

    /**
     * 验证码登录
     */
    private void turnToSmsLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("hideBack", hideBack);
        startActivity(intent);
        finish();
    }

    /**
     * 登录
     */
    private void login() {
        isLogin = true;
        tv_sure.setText("登录中...");
        rl_sure.setSelected(false);
        new Login(mContext).execute(url, phoneNum, password);
    }

    /**
     * 返回上一个界面
     */
    private void backActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("hideBack", hideBack);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    /**
     * 分析登录数据
     *
     * @param s
     */
    private void analyzeLoginData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            int status = json.optInt("status", -1);
            if (status == 400) {
                if (s.contains("错误")) {
                    showTips("登录失败，密码错误！");
                } else if (s.contains("不存在")) {
                    showTips("登录失败，用户不存在！");
                } else {
                    errorLogin();
                }
            } else if (status == 200) {
                loginSuccess(json);
            } else {
                errorLogin();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorLogin();
        }
    }

    /**
     * 登录成功，返回首页
     *
     * @param json
     */
    private void loginSuccess(JSONObject json) {
        try {
            JSONObject student = json.getJSONObject("data");
            String token = student.getString("token");
            long id = student.optLong("studentid", -1);
            int gradeId = student.optInt("gradeid", 110);
            NewMainActivity.TOKEN = token;
            NewMainActivity.STUDENT_ID = id;
            NewMainActivity.USERNAME = student.getString("username");
            NewMainActivity.USERIMG = student.getString("userimg");
            NewMainActivity.GRADE = student.optInt("level", -1);
            NewMainActivity.GRADE_ID = gradeId;
            NewMainActivity.PY_SCORE = student.getString("score");
            NewMainActivity.LEVEL = student.optInt("level", -1);
            NewMainActivity.DESCRIPTION = student.getString("description");
            phoneNum = student.getString("phonenum");
            NewMainActivity.PHONENUMBER = phoneNum;

            SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putString("token", token);
            editor.putString("phoneNum", phoneNum);
            editor.apply();

            PreferencesUtil.saveData(PasswordLoginActivity.this,"studentId",id + "");
            PreferencesUtil.saveData(PasswordLoginActivity.this,"gradeId",gradeId + "");
            PreferencesUtil.saveData(PasswordLoginActivity.this,"token",token);
            PreferencesUtil.saveData(PasswordLoginActivity.this,"phoneNum",phoneNum);
            EventBus.getDefault().postSticky(new MessageEvent(""));

            broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_JIGUANG_LOGIN);

            if (hideBack) {
                broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
                backToMainActivity();
            }

            showTips("登录成功");
            EventBus.getDefault().postSticky(new MessageEvent(""));

            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            errorLogin();
        }
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

    /**
     * 登录失败
     */
    private void errorLogin() {
        showTips("登录失败，请稍后再试！");
    }


    private static class Login
            extends WeakAsyncTask<String, Void, String, PasswordLoginActivity> {

        protected Login(PasswordLoginActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(PasswordLoginActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("phoneNum", strings[1]);
                json.put("password", strings[2]);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PasswordLoginActivity activity, String s) {
            if (s == null) {
                activity.errorLogin();
            } else {
                activity.analyzeLoginData(s);
            }
            activity.tv_sure.setText("登录");
            activity.rl_sure.setSelected(true);
            activity.isLogin = false;

        }
    }

}
