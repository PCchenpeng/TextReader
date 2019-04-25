package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.VerifyCodeView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 注册--微信绑定手机号码
 */
public class WXBindNumberActivity extends BaseActivity implements View.OnClickListener {

    private final String url = HttpUrlPre.HTTP_URL + "/thirdpartyLogin/weixinToken";
    private static final String sendCodeUrl = HttpUrlPre.HTTP_URL + "/message/verification/";

    private RelativeLayout rl_back;
    private EditText et_phone;
    private TextView tv_countdown;
    private RelativeLayout rl_code;
    private VerifyCodeView verifyCodeView;
    private RelativeLayout rl_sure;

    private WXBindNumberActivity mContext;

    private String access_token;
    private String openid;
    private String nickName = "";
    private String phoneNum = "";
    private String unionId = "";
    private String verifyCode = "";

    private boolean isInputPhoneView = true;
    private boolean isPhoneReady = false;
    private boolean isVerifyReady = false;
    private boolean isWaitTimeOver = true;
    private boolean isCommit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_bind_number);

        mContext = this;

        setNeedCheckCode(false);

        initData();
        initView();
        initEvents();
    }

    private void initData() {
        access_token = getIntent().getStringExtra("access_token");
        openid = getIntent().getStringExtra("openid");
        nickName = getIntent().getStringExtra("name");
        unionId = getIntent().getStringExtra("unionId");
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        tv_countdown.setOnClickListener(this);
        rl_sure.setOnClickListener(this);
        et_phone.addTextChangedListener(phoneNumTextWatcher);
        verifyCodeView.setOnInputListener(new VerifyCodeView.OnInputListener() {
            @Override
            public void onSucess(String code) {
                verifyCode = code;
                isVerifyReady = true;
                rl_sure.setSelected(true);
            }

            @Override
            public void onInput() {
                verifyCode = "";
                isVerifyReady = false;
                rl_sure.setSelected(false);
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
            if (phoneNum.length() == 11) {
                isPhoneReady = true;
                rl_sure.setSelected(true);
            }
        }
    };

    /**
     * 返回
     */
    private void backActivity() {
        if (isInputPhoneView) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("hideBack", NewMainActivity.isLoginHideBack);
            startActivity(intent);
            finish();
        } else {
            backToInputPhoneView();
        }
    }

    /**
     * 显示验证码视图
     */
    private void turnToCodeView() {
        isInputPhoneView = false;
        sendCode();
        rl_code.setVisibility(View.VISIBLE);
        et_phone.setFocusable(false);
        et_phone.setFocusableInTouchMode(false);
        tv_countdown.setVisibility(View.VISIBLE);
        hideInputMethod();
        rl_sure.setSelected(false);
    }

    /**
     * 回到输入手机号码视图
     */
    private void backToInputPhoneView() {
        isInputPhoneView = true;
        rl_code.setVisibility(View.GONE);
        et_phone.setFocusable(true);
        et_phone.setFocusableInTouchMode(true);
        et_phone.requestFocus();
        tv_countdown.setVisibility(View.GONE);
        et_phone.setSelection(et_phone.getText().toString().length());
        verifyCodeView.clearCode();
        rl_sure.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    /**
     * 提交验证码
     */
    private void commit() {
        isCommit = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("access_token", access_token);
                    json.put("openid", openid);
                    json.put("phoneNum", phoneNum);
                    json.put("verificationCode", verifyCode);
                    RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
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
                    isCommit = false;
                }
            }
        }.start();
    }

    /**
     * 注册结果
     *
     * @param data
     */
    private void signUpResult(String data) {
        try {
            JSONObject json = new JSONObject(data);
            int status = json.optInt("status", -1);
            if (status == 100) {
                Intent intent = new Intent(mContext, WXPerfectUserInfoActivity.class);
                intent.putExtra("phoneNum", phoneNum);
                intent.putExtra("access_token", access_token);
                intent.putExtra("openid", openid);
                intent.putExtra("name", nickName);
                intent.putExtra("unionId", unionId);
                startActivity(intent);
                finish();
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
                NewMainActivity.NEWS_COUNT = 0;
                NewMainActivity.DESCRIPTION = student.getString("description");
                phoneNum = student.getString("phonenum");
                NewMainActivity.PHONENUMBER = phoneNum;

                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("token", token);
                editor.putString("phoneNum", phoneNum);
                editor.apply();

                showTips("绑定成功");

                broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_JIGUANG_LOGIN);

                if (NewMainActivity.isLoginHideBack) {
                    broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_USER_EXIT);
                    backToMainActivity();
                }

                finish();
            } else if (status == 400) {
                if (data.contains("错误")) {
                    showTips("验证码错误");
                } else {
                    showTips("注册失败，请稍后再试！");
                }
            } else {
                showTips("注册失败，请稍后再试！");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("注册失败，请稍后再试！");
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
     * 发送验证码
     */
    private void sendCode() {
        isWaitTimeOver = false;
        tv_countdown.setTextColor(Color.parseColor("#999999"));
        timer.start();
        new SendCode(mContext).execute(sendCodeUrl, phoneNum, verifyCode);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String data = (String) msg.obj;
                    signUpResult(data);
                    isCommit = false;
                    break;
            }
        }
    };

    /**
     * 分析发送验证码的数据
     *
     * @param sms_data 从服务端返回的发送验证码的数据
     */
    private void analyzeSmsData(String sms_data) {
        try {
            JSONObject sms_object = new JSONObject(sms_data);
            if (200 == sms_object.optInt("status", -1)) {
                showTips("验证码已发送");
            } else {
                restoreSendButton();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            restoreSendButton();
        }
    }

    /**
     * 恢复发送验证码的按钮
     */
    private void restoreSendButton() {
        showTips("发送验证码失败，请稍后再试~");
        timer.onFinish();
        timer.cancel();
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_wx_bind_number);

        et_phone = findViewById(R.id.et_wx_phone);
        tv_countdown = findViewById(R.id.tv_countdown_wx_bind_number);
        rl_code = findViewById(R.id.rl_code_wx_bind_number);
        verifyCodeView = findViewById(R.id.verify_code_wx_bind_number);
        rl_sure = findViewById(R.id.rl_sure_wx_bind_number);

        tv_countdown.setVisibility(View.GONE);
    }

    /**
     * 比赛倒计时
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
            isWaitTimeOver = true;
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_wx_bind_number:
                backActivity();
                break;
            case R.id.rl_sure_wx_bind_number:
                if (isInputPhoneView) {
                    if (isPhoneReady) {
                        turnToCodeView();
                    }
                } else {
                    if (!isPhoneReady) {
                        showTips("请输入手机号码");
                    } else if (!isVerifyReady) {
                        showTips("请输入验证码");
                    } else {
                        if (!isCommit) {
                            commit();
                        }
                    }
                }
                break;
            case R.id.tv_countdown_wx_bind_number:
                if (isWaitTimeOver) {
                    if (isPhoneReady) {
                        showTips("请输入手机号码");
                    } else {
                        sendCode();
                    }
                }
                break;
        }
    }

    /**
     * 隐藏输入法
     */
    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(et_phone.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 发送验证码
     */
    private static class SendCode
            extends WeakAsyncTask<String, Void, String, WXBindNumberActivity> {

        protected SendCode(WXBindNumberActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WXBindNumberActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("mobile", strings[1]);
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
        protected void onPostExecute(WXBindNumberActivity activity, String s) {
            if (s == null) {
                activity.restoreSendButton();
            } else {
                activity.analyzeSmsData(s);
            }
        }
    }
}
