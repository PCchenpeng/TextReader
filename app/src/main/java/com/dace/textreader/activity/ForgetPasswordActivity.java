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
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.view.VerifyCodeView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 重设密码
 */
public class ForgetPasswordActivity extends AppCompatActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/resetPassword";
    private final String sendCodeUrl = HttpUrlPre.HTTP_URL + "/message/verification/";

    private RelativeLayout rl_back;
    private EditText et_phone;
    private EditText et_password;
    private TextView tv_countdown;
    private VerifyCodeView verifyCodeView;
    private RelativeLayout rl_sure;
    private ImageView iv_sign_up_eye;

    //当前密码是否可见
    private boolean isVisiblePS = false;

    //是否等待结束
    private boolean isWaitTimeOver = true;

    //填入手机号码输入框的手机号（保存在本地缓存中，如果没有则填入空）
    private String phone = "";
    private String password = "";
    private String verifyCode = "";

    private boolean isOperating = false;
    private boolean isPhoneReady = false;
    private boolean isVerifyCodeReady = false;
    private boolean isPasswordReady = false;

    private boolean hideBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phoneNum", "");

        hideBack = getIntent().getBooleanExtra("hideBack", false);

        initView();
        initEvents();

        et_phone.setText(phone);
    }

    private void initEvents() {
        rl_back.setOnClickListener(onClickListener);
        iv_sign_up_eye.setOnClickListener(onClickListener);
        tv_countdown.setOnClickListener(onClickListener);
        rl_sure.setOnClickListener(onClickListener);
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString();
                if (phone.length() == 0){
                    et_phone.setTextSize(17);
                } else {
                    et_phone.setTextSize(20);
                }
                if (s.length() == 11) {
                    isPhoneReady = true;
                    if (isPasswordReady && isVerifyCodeReady) {
                        rl_sure.setSelected(true);
                    }
                } else {
                    isPhoneReady = false;
                    rl_sure.setSelected(false);
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
                    iv_sign_up_eye.setVisibility(View.VISIBLE);
                    if (password.length() >= 6) {
                        isPasswordReady = true;
                        if (isPhoneReady && isVerifyCodeReady) {
                            rl_sure.setSelected(true);
                        }
                    } else {
                        isPasswordReady = false;
                        rl_sure.setSelected(false);
                    }
                    et_password.setTextSize(20);
                } else {
                    iv_sign_up_eye.setVisibility(View.INVISIBLE);
                    isPasswordReady = false;
                    rl_sure.setSelected(false);
                    et_password.setTextSize(17);
                }
            }
        });
        verifyCodeView.setOnInputListener(new VerifyCodeView.OnInputListener() {
            @Override
            public void onSucess(String code) {
                isVerifyCodeReady = true;
                verifyCode = code;
                if (isPhoneReady && isPasswordReady) {
                    rl_sure.setSelected(true);
                }
            }

            @Override
            public void onInput() {
                isVerifyCodeReady = false;
                rl_sure.setSelected(false);
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_forget_password);
        et_phone = findViewById(R.id.et_forget_password_phone);
        tv_countdown = findViewById(R.id.tv_countdown_forget_password);
        et_password = findViewById(R.id.et_password_forget_password);
        verifyCodeView = findViewById(R.id.verify_code_forget_password);
        iv_sign_up_eye = findViewById(R.id.iv_forget_password_password_eye);
        rl_sure = findViewById(R.id.rl_sure_forget_password);

        et_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_back_forget_password:
                    backActivity();
                    break;
                case R.id.iv_forget_password_password_eye:
                    showOrHidePassword();
                    break;
                case R.id.rl_sure_forget_password:
                    if (!isOperating) {
                        if (!isPhoneReady) {
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "请输入手机号码");
                        } else if (!isPasswordReady) {
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "请输入密码");
                        } else if (!isVerifyCodeReady) {
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "请输入验证码");
                        } else {
                            forgetPassword();
                        }
                    }
                    break;
                case R.id.tv_countdown_forget_password:
                    if (isWaitTimeOver) {
                        if (!isPhoneReady) {
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "手机号不能为空");
                        } else {
                            timer.start();
                            tv_countdown.setTextColor(Color.parseColor("#999999"));
                            sendCode();
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 返回
     */
    private void backActivity() {
        Intent intent = new Intent(this, PasswordLoginActivity.class);
        intent.putExtra("hideBack", hideBack);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    //重设密码
    private void forgetPassword() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("phoneNum", phone);
                    json.put("newPassword", password);
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
                    MyToastUtil.showToast(ForgetPasswordActivity.this, "设置新密码失败，请稍后再试");
                }
            }
        }.start();
    }

    //发送验证码
    private void sendCode() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("mobile", phone);
                    RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                    Request request = new Request.Builder()
                            .url(sendCodeUrl)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = data;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    isWaitTimeOver = false;
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
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.optInt("status", -1);
                        if (status == 200) {
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "重置密码成功");

                            backActivity();

                        } else if (status == 400) {
                            if (jsonObject.getString("msg").contains("验证码错误")) {
                                MyToastUtil.showToast(ForgetPasswordActivity.this, "验证码错误");
                            } else if (jsonObject.getString("msg").contains("该手机号码未注册")) {
                                MyToastUtil.showToast(ForgetPasswordActivity.this, "该手机号码未注册");
                            } else {
                                MyToastUtil.showToast(ForgetPasswordActivity.this, "重置密码失败");
                            }
                        } else {
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "重置密码失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MyToastUtil.showToast(ForgetPasswordActivity.this, "设置新密码失败，请稍后再试");
                    }
                    break;
                case 2:
                    String sms_data = (String) msg.obj;
                    try {
                        JSONObject sms_object = new JSONObject(sms_data);
                        if (200 != sms_object.optInt("status", -1)) {
                            restoreSendButton();
                            MyToastUtil.showToast(ForgetPasswordActivity.this, "发送验证码失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        restoreSendButton();
                        MyToastUtil.showToast(ForgetPasswordActivity.this, "发送验证码失败");
                    }
                    break;
            }
        }
    };

    /**
     * 恢复发送验证码按钮的状态
     */
    private void restoreSendButton() {
        isWaitTimeOver = true;
        timer.onFinish();
        timer.cancel();
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
        }
    };

    /**
     * 显示或隐藏密码
     */
    private void showOrHidePassword() {
        if (isVisiblePS) {
            isVisiblePS = false;
            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iv_sign_up_eye.setImageResource(R.drawable.login_icon_eye_close);
        } else {
            isVisiblePS = true;
            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iv_sign_up_eye.setImageResource(R.drawable.login_icon_eye_open);
        }
        et_password.setSelection(et_password.getText().toString().length());
    }
}
