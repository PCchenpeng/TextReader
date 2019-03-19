package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dace.textreader.R;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 注册--微信--设置密码
 */
public class WxSetPasswordActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private EditText et_password;
    private ImageView iv_show;
    private RelativeLayout rl_sure;

    private WxSetPasswordActivity mContext;

    private String access_token;
    private String openid;
    private String phoneNum;
    private String nickName;
    private String unionId;
    private String password = "";

    private boolean isWxLogin = false;  //是否是微信
    private boolean isPasswordReady = false;  //是否密码准备好
    private boolean isShowPassword = false;  //是否显示密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_set_password);

        mContext = this;

        setNeedCheckCode(false);

        initData();
        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backActivity();
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
                        rl_sure.setSelected(true);
                    } else {
                        isPasswordReady = false;
                        rl_sure.setSelected(false);
                    }
                } else {
                    iv_show.setVisibility(View.INVISIBLE);
                    isPasswordReady = false;
                    rl_sure.setSelected(false);
                }
            }
        });
        iv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHidePassword();
            }
        });
        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordReady) {
                    if (isWxLogin) {
                        Intent intent = new Intent(mContext, WXPerfectUserInfoActivity.class);
                        intent.putExtra("phoneNum", phoneNum);
                        intent.putExtra("access_token", access_token);
                        intent.putExtra("openid", openid);
                        intent.putExtra("name", nickName);
                        intent.putExtra("unionId", unionId);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(mContext, PerfectUserInfoActivity.class);
                        intent.putExtra("phoneNum", phoneNum);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    MyToastUtil.showToast(mContext, "请输入密码");
                }
            }
        });
    }

    /**
     * 返回
     */
    private void backActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("hideBack", NewMainActivity.isLoginHideBack);
        startActivity(intent);
        finish();
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_wx_set_password);
        et_password = findViewById(R.id.et_password_wx_set_password);
        iv_show = findViewById(R.id.iv_show_wx_set_password);
        rl_sure = findViewById(R.id.rl_sure_wx_set_password);

        et_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

    }

    private void initData() {
        isWxLogin = getIntent().getBooleanExtra("isWxLogin", false);
        if (isWxLogin) {
            access_token = getIntent().getStringExtra("access_token");
            openid = getIntent().getStringExtra("openid");
            nickName = getIntent().getStringExtra("name");
            unionId = getIntent().getStringExtra("unionId");
        }
        phoneNum = getIntent().getStringExtra("phoneNum");
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

    @Override
    public void onBackPressed() {
        backActivity();
    }
}
