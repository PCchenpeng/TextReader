package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.VerifyCodeView;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/login/verification_code";
    private static final String verifyCodeUrl = HttpUrlPre.HTTP_URL + "/message/verification/";

    private RelativeLayout rl_left_back;
    private RelativeLayout rl_right_back;
    private TextView tv_title;
    private EditText et_phoneNum;
    private ImageView iv_clear;
    private TextView tv_countdown;
    private RelativeLayout rl_verify;
    private VerifyCodeView verifyCodeView;
    private RelativeLayout rl_sure;
    private TextView tv_sure;
    private TextView tv_other;
    private LinearLayout ll_wechat;

    private LoginActivity mContext;

    private String phoneNum;  //手机号码
    private String verifyCode = "";

    private boolean hideBack = false;  //用于异地登录之后的重新登录
    private boolean isPhoneNumReady = false;  //手机号码是否输入完成
    private boolean isVerifyCodeReady = false;  //验证码是否输入完成
    private boolean isInputPhoneNumView = true;  //是否是输入手机号码的视图
    private boolean isLogin = false;  //是否正在登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        mContext = this;

        hideBack = getIntent().getBooleanExtra("hideBack", false);

        NewMainActivity.isLoginHideBack = hideBack;

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        phoneNum = sharedPreferences.getString("phoneNum", "");

        setNeedCheckCode(false);

        initView();
        intiEvents();

        et_phoneNum.setText(phoneNum);
        et_phoneNum.setSelection(phoneNum.length());
    }

    private void intiEvents() {
        rl_left_back.setOnClickListener(this);
        rl_right_back.setOnClickListener(this);
        iv_clear.setOnClickListener(this);
        tv_countdown.setOnClickListener(this);
        rl_sure.setOnClickListener(this);
        tv_other.setOnClickListener(this);
        ll_wechat.setOnClickListener(this);
        et_phoneNum.addTextChangedListener(phoneNumTextWatcher);
        verifyCodeView.setOnInputListener(new VerifyCodeView.OnInputListener() {
            @Override
            public void onSucess(String code) {
                rl_sure.setSelected(true);
                isVerifyCodeReady = true;
                verifyCode = code;
            }

            @Override
            public void onInput() {
                rl_sure.setSelected(false);
                isVerifyCodeReady = false;
                verifyCode = "";
            }
        });
    }

    private TextWatcher phoneNumTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            phoneNum = s.toString();
            int length = phoneNum.length();
            if (length <= 0) {
                isPhoneNumReady = false;
                rl_sure.setSelected(false);
                iv_clear.setVisibility(View.GONE);
                et_phoneNum.setTextSize(17);
            } else {
                iv_clear.setVisibility(View.VISIBLE);
                isPhoneNumReady = true;
                rl_sure.setSelected(true);
                et_phoneNum.setTextSize(20);
            }
        }
    };

    private void initView() {
        rl_left_back = findViewById(R.id.rl_left_back_new_login);
        rl_right_back = findViewById(R.id.rl_right_back_new_login);
        tv_title = findViewById(R.id.tv_title_new_login);
        et_phoneNum = findViewById(R.id.et_phone_number_new_login);
        iv_clear = findViewById(R.id.iv_clear_new_login);
        tv_countdown = findViewById(R.id.tv_countdown_new_login);
        rl_verify = findViewById(R.id.rl_verify_new_login);
        verifyCodeView = findViewById(R.id.verify_code_new_login);
        rl_sure = findViewById(R.id.rl_sure_new_login);
        tv_sure = findViewById(R.id.tv_sure_new_login);
        tv_other = findViewById(R.id.tv_other_new_login);
        ll_wechat = findViewById(R.id.ll_wechat_new_login);

        iv_clear.setVisibility(View.GONE);

        backToInputPhoneNumView();

    }

    /**
     * 回到输入手机号码的视图
     */
    private void backToInputPhoneNumView() {
        isInputPhoneNumView = true;

        rl_left_back.setVisibility(View.GONE);
        rl_right_back.setVisibility(View.VISIBLE);

        tv_title.setText("派知语文");

        et_phoneNum.requestFocus();
        et_phoneNum.setSelection(et_phoneNum.getText().toString().length());

        tv_countdown.setVisibility(View.GONE);
        rl_verify.setVisibility(View.GONE);
        verifyCodeView.clearCode();

        tv_sure.setText("下一步");
        rl_sure.setSelected(false);
        isPhoneNumReady = false;

        tv_other.setVisibility(View.VISIBLE);
        ll_wechat.setVisibility(View.VISIBLE);

        timer.onFinish();
        timer.cancel();
    }

    /**
     * 显示输入验证码的视图
     */
    private void turnToVerifyCodeView() {
        sendCode();

        isInputPhoneNumView = false;

        rl_left_back.setVisibility(View.VISIBLE);
        rl_right_back.setVisibility(View.GONE);

        tv_title.setText("验证码已发送至");

        hideInputMethod();

        iv_clear.setVisibility(View.GONE);
        tv_countdown.setVisibility(View.VISIBLE);
        rl_verify.setVisibility(View.VISIBLE);

        tv_sure.setText("注册/登录");
        rl_sure.setSelected(false);

        tv_other.setVisibility(View.GONE);
        ll_wechat.setVisibility(View.GONE);

        timer.start();
        tv_countdown.setTextColor(Color.parseColor("#999999"));
    }

    /**
     * 返回
     */
    private void backActivity() {
        if (isInputPhoneNumView) {
            if (hideBack) {
                broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
                backToMainActivity();
            } else {
                finish();
            }
        } else {
            backToInputPhoneNumView();
            iv_clear.setVisibility(View.VISIBLE);
            rl_sure.setSelected(true);
            isPhoneNumReady = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_back_new_login:
                backActivity();
                break;
            case R.id.rl_right_back_new_login:
                backActivity();
                break;
            case R.id.iv_clear_new_login:
                clearPhoneNumInput();
                break;
            case R.id.tv_countdown_new_login:
                sendCode();
                break;
            case R.id.rl_sure_new_login:
                if (isInputPhoneNumView) {
                    if (isPhoneNumReady) {
                        turnToVerifyCodeView();
                    }
                } else {
                    if (isVerifyCodeReady && !isLogin) {
                        login();
                    }
                }
                break;
            case R.id.tv_other_new_login:
                turnToPasswordLogin();
                break;
            case R.id.ll_wechat_new_login:
                wxLogin();
                break;
        }
    }

    /**
     * 微信登录
     */
    private void wxLogin() {
        if (App.api == null) {
            showTips("初始化微信失败，请退出后重试~");
        }
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
     * 密码登录
     */
    private void turnToPasswordLogin() {
        Intent intent = new Intent(mContext, PasswordLoginActivity.class);
        intent.putExtra("hideBack", hideBack);
        startActivity(intent);
        finish();
    }

    /**
     * 注册/登录
     */
    private void login() {
        isLogin = true;
        new Login(mContext).execute(url, phoneNum, verifyCode);
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        timer.start();
        tv_countdown.setTextColor(Color.parseColor("#999999"));
        new SendVerifyCode(mContext).execute(verifyCodeUrl, phoneNum);
    }

    /**
     * 清除手机号码
     */
    private void clearPhoneNumInput() {
        et_phoneNum.setText("");
    }

    /**
     * 验证码倒计时
     */
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            int s = (int) (millisUntilFinished / 1000);
            String countdown = "(" + s + "s)";
            tv_countdown.setText(countdown);
        }

        @Override
        public void onFinish() {
            tv_countdown.setText("发送验证码");
            tv_countdown.setTextColor(Color.parseColor("#ff9933"));
        }
    };

    /**
     * 隐藏输入法
     */
    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_phoneNum.getApplicationWindowToken(), 0);
        }
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
                if (s.contains("该手机未注册")) {
                    //继续完成注册
                    turnToAddPassword();
                } else if (s.contains("验证码错误")) {
                    showTips("验证码错误");
                } else {
                    errorLogin();
                }
            } else if (status == 200) {
                JSONObject student = json.getJSONObject("data");
                String token = student.getString("token");
                int id = student.optInt("studentid", -1);
                NewMainActivity.TOKEN = token;
                NewMainActivity.STUDENT_ID = id;
                NewMainActivity.USERNAME = student.getString("username");
                NewMainActivity.USERIMG = student.getString("userimg");
                NewMainActivity.GRADE = student.optInt("level", -1);
                NewMainActivity.GRADE_ID = student.optInt("gradeid", 110);
                NewMainActivity.PY_SCORE = student.getString("score");
                NewMainActivity.LEVEL = student.optInt("level", -1);
                NewMainActivity.DESCRIPTION = student.getString("description");
                phoneNum = student.getString("phonenum");
                NewMainActivity.PHONENUMBER = phoneNum;

                SharedPreferences sharedPreferences =
                        getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("token", token);
                editor.putInt("studentId", id);
                editor.putString("phoneNum", phoneNum);
                editor.apply();

                broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_JIGUANG_LOGIN);

                if (hideBack) {
                    broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
                    backToMainActivity();
                }

                finish();
            } else {
                errorLogin();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorLogin();
        }
    }

    /**
     * 继续填写密码完成注册
     */
    private void turnToAddPassword() {
        Intent intent = new Intent(mContext, WxSetPasswordActivity.class);
        intent.putExtra("phoneNum", phoneNum);
        startActivity(intent);
        finish();
    }

    /**
     * 发送重新登录成功广播
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
        showTips("登录失败，请稍后再试~");
    }

    /**
     * 分析发送验证码数据
     *
     * @param s
     */
    private void analyzeSendVerifyCodeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showTips("验证码已发送");
            } else {
                errorSendCode();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorSendCode();
        }
    }

    /**
     * 发送验证码失败
     */
    private void errorSendCode() {
        timer.onFinish();
        timer.cancel();
        showTips("发送验证码失败");
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
     * 登录
     */
    private static class Login
            extends WeakAsyncTask<String, Void, String, LoginActivity> {

        protected Login(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(LoginActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("phoneNum", strings[1]);
                json.put("verificationCode", strings[2]);
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
        protected void onPostExecute(LoginActivity activity, String s) {
            activity.isLogin = false;
            if (s == null) {
                activity.errorLogin();
            } else {
                activity.analyzeLoginData(s);
            }
        }
    }

    /**
     * 发送验证码
     */
    private static class SendVerifyCode
            extends WeakAsyncTask<String, Void, String, LoginActivity> {

        protected SendVerifyCode(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(LoginActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("mobile", strings[1]);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, object.toString());
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
        protected void onPostExecute(LoginActivity activity, String s) {
            if (s == null) {
                activity.errorSendCode();
            } else {
                activity.analyzeSendVerifyCodeData(s);
            }
        }
    }

}
