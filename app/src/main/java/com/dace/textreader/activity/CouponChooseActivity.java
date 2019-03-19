package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.CouponFragment;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 优惠券选择
 */
public class CouponChooseActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_title;

    private CouponChooseActivity mContext;

    private long id;
    private long lessonId;
    private int category;  //1为作文优惠券，2为微课优惠券
    private int status = 1;

    private CouponFragment couponFragment;

    private String mCouponCode = "";
    private String mTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_choose);

        mContext = this;

        id = getIntent().getLongExtra("id", -1);
        lessonId = getIntent().getLongExtra("lessonId", -1);
        category = getIntent().getIntExtra("category", -1);

        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToActivity();
            }
        });
        couponFragment.setOnItemChooseListen(new CouponFragment.OnItemChooseListen() {
            @Override
            public void onItemChoose(String couponCode, String title) {
                mCouponCode = couponCode;
                mTitle = title;
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("可使用优惠券");

        FragmentManager fm = getSupportFragmentManager();
        couponFragment = new CouponFragment();
        couponFragment.setAvailable(true);
        couponFragment.setCategory(category);
        if (lessonId != -1) {
            couponFragment.setCourseId(lessonId);
        }
        couponFragment.setStatus(status);
        couponFragment.setId(id);
        fm.beginTransaction().add(R.id.frame_coupon_choose, couponFragment, "coupon").commit();
    }

    @Override
    public void onBackPressed() {
        backToActivity();
    }

    private void backToActivity() {
        Intent intent = new Intent();
        intent.putExtra("couponCode", mCouponCode);
        intent.putExtra("title", mTitle);
        setResult(0, intent);
        finish();
    }

}
