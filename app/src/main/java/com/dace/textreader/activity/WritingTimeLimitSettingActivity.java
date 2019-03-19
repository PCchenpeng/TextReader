package com.dace.textreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * 限时设置
 */
public class WritingTimeLimitSettingActivity extends AppCompatActivity
        implements View.OnClickListener {

    private RelativeLayout rl_back;
    private SwitchButton switchButton_timing;
    private SwitchButton switchButton_countdown;

    private WritingTimeLimitSettingActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_time_limit_setting);

        mContext = this;

        initView();
        initEvents();
        setImmerseLayout();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        switchButton_timing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timing(isChecked);
                if (isChecked && switchButton_countdown.isChecked()) {
                    switchButton_countdown.setChecked(false);
                    countDown(false);
                }
            }
        });
        switchButton_countdown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                countDown(isChecked);
                if (isChecked && switchButton_timing.isChecked()) {
                    switchButton_timing.setChecked(false);
                    timing(false);
                }
            }
        });
    }

    /**
     * 计时
     */
    private void timing(boolean isChecked) {

    }

    /**
     * 倒计时
     */
    private void countDown(boolean isChecked) {

    }

    private void initView() {
        rl_back = findViewById(R.id.rl_close_time_limit_setting);
        switchButton_timing = findViewById(R.id.switch_timing_time_limit_setting);
        switchButton_countdown = findViewById(R.id.switch_countdown_time_limit_setting);
    }

    protected void setImmerseLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = DensityUtil.getStatusBarHeight(mContext)
                    + DensityUtil.dip2px(mContext, 10);
            rl_back.setPadding(0, statusBarHeight, 0, DensityUtil.dip2px(mContext, 12));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_close_time_limit_setting:
                finish();
                break;
        }
    }
}
