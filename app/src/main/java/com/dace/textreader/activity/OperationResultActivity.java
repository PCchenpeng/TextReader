package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.bean.AutoSaveWritingBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.StatusBarUtil;

import org.litepal.LitePal;

/**
 * 操作结果展示
 */
public class OperationResultActivity extends BaseActivity implements View.OnClickListener {

    private final static String TYPE_OPERATE_RECHARGE = "recharge";
    private final static String TYPE_OPERATE_BUY_LESSON = "buy_lesson";
    private final static String TYPE_OPERATE_WRITING_CORRECTION = "writing_correction";
    private final static String TYPE_OPERATE_WRITING_EVENTS = "writing_events";
    private final static String TYPE_OPERATE_WRITING_PUBLIC = "writing_public";
    private final static String TYPE_OPERATE_BUY_CARD = "buy_card";
    private final static String TYPE_OPERATE_WRITING_WORK = "writing_work";

    private TextView tv_title;
    private TextView tv_sure;
    private ImageView iv_tips;
    private TextView tv_tips;
    private TextView tv_check;
    private RelativeLayout rl_info;
    private TextView tv_time;
    private TextView tv_content;
    private TextView tv_price;

    private OperationResultActivity mContext;

    private String type;  //操作类型
    private boolean isOperateSuccessful;  //是否操作成功
    private String orderNum = "";  //订单号
    private String content;  //如果是充值或者购买课程的活动，需要购买内容
    private double price;  //如果是充值或者购买课程的活动，需要价格信息
    private int pay_way = 0;
    private boolean isFromPlayService;  //微课是否是通过服务购买的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_result);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.StatusBarLightMode(this, flag);

        type = getIntent().getStringExtra("operateType");
        isOperateSuccessful = getIntent().getBooleanExtra("isSuccessful", true);
        content = getIntent().getStringExtra("content");
        price = getIntent().getDoubleExtra("price", 0);
        if (type.equals(TYPE_OPERATE_WRITING_CORRECTION) || type.equals(TYPE_OPERATE_BUY_CARD)) {
            pay_way = getIntent().getIntExtra("pay_way", 0);
        }
        if (type.equals(TYPE_OPERATE_WRITING_CORRECTION) || type.equals(TYPE_OPERATE_RECHARGE) ||
                type.equals(TYPE_OPERATE_BUY_LESSON) || type.equals(TYPE_OPERATE_BUY_CARD)) {
            orderNum = getIntent().getStringExtra("orderNum");
        }
        if (type.equals(TYPE_OPERATE_BUY_LESSON)) {
            isFromPlayService = getIntent().getBooleanExtra("isFromPlayService", false);
        }

        initView();
        initEvents();

        LitePal.deleteAll(AutoSaveWritingBean.class);
    }

    private void initEvents() {
        tv_sure.setOnClickListener(this);
        tv_check.setOnClickListener(this);
        rl_info.setOnClickListener(this);
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title_operate_result);
        tv_sure = findViewById(R.id.tv_sure_operate_result);
        iv_tips = findViewById(R.id.iv_tips_operate_result);
        tv_tips = findViewById(R.id.tv_tips_operate_result);
        tv_check = findViewById(R.id.tv_check_operate_result);
        rl_info = findViewById(R.id.rl_info_operate_result);
        tv_time = findViewById(R.id.tv_time_operate_result);
        tv_content = findViewById(R.id.tv_content_operate_result);
        tv_price = findViewById(R.id.tv_price_operate_result);

        updateUi();
    }

    private void updateUi() {
        if (type.equals(TYPE_OPERATE_RECHARGE) || type.equals(TYPE_OPERATE_BUY_LESSON) ||
                type.equals(TYPE_OPERATE_WRITING_CORRECTION) || type.equals(TYPE_OPERATE_BUY_CARD)) {
            //需要支付的选项类型
            tv_title.setText("支付完成");
            if (!isDestroyed()) {
                RequestOptions options = new RequestOptions()
                        .override(DensityUtil.dip2px(mContext, 50),
                                DensityUtil.dip2px(mContext, 50));
                Glide.with(mContext)
                        .load(R.drawable.image_pay_complete)
                        .apply(options)
                        .into(iv_tips);
            }
            String time = DateUtil.getTodayDateTime();
            tv_time.setText(time);
            tv_content.setText(content);
            String p;
            if (type.equals(TYPE_OPERATE_WRITING_CORRECTION) ||
                    type.equals(TYPE_OPERATE_BUY_CARD)) {
                if (pay_way == 1) {
                    p = DataUtil.double2String(price) + "元";
                } else {
                    p = DataUtil.double2String(price) + "派豆";
                }
            } else {
                p = DataUtil.double2String(price) + "派豆";
            }
            tv_price.setText(p);
        } else {
            //没有交易的类型
            tv_title.setText("提交结果");
            if (!isDestroyed()) {
                RequestOptions options = new RequestOptions()
                        .override(DensityUtil.dip2px(mContext, 176),
                                DensityUtil.dip2px(mContext, 86));
                Glide.with(mContext)
                        .load(R.drawable.image_write_complete)
                        .apply(options)
                        .into(iv_tips);
            }
            rl_info.setVisibility(View.GONE);
        }

        if (!isOperateSuccessful) {
            rl_info.setVisibility(View.GONE);
        }

        switch (type) {
            case TYPE_OPERATE_RECHARGE:
                tv_tips.setText("充值成功！");
                tv_check.setText("查看交易记录>>");
                break;
            case TYPE_OPERATE_BUY_LESSON:
                tv_tips.setText("支付成功！");
                tv_check.setText("查看我的微课>>");
                break;
            case TYPE_OPERATE_WRITING_CORRECTION:
                tv_tips.setText("支付成功");
                tv_check.setText("查看我的作文>>");
                break;
            case TYPE_OPERATE_WRITING_WORK:
            case TYPE_OPERATE_WRITING_PUBLIC:
                tv_tips.setText("棒棒哒～提交成功！");
                tv_check.setText("查看我的作文>>");
                break;
            case TYPE_OPERATE_WRITING_EVENTS:
                tv_tips.setText("棒棒哒～提交成功！\n注：活动作文可在截稿日期前再次编辑提交～");
                tv_check.setText("查看我的作文>>");
                break;
            case TYPE_OPERATE_BUY_CARD:
                tv_tips.setText("支付成功！");
                tv_check.setText("查看我的卡包");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure_operate_result:
                backToActivity();
                break;
            case R.id.tv_check_operate_result:
                checkResult();
                break;
            case R.id.rl_info_operate_result:
                if (!orderNum.equals("")) {
                    turnToOrderDetail();
                }
                break;
        }
    }

    /**
     * 返回activity
     */
    private void backToActivity() {
        if (type.equals(TYPE_OPERATE_BUY_LESSON) ||
                type.equals(TYPE_OPERATE_WRITING_CORRECTION) ||
                type.equals(TYPE_OPERATE_BUY_CARD) ||
                type.equals(TYPE_OPERATE_WRITING_WORK)) {
            Intent intent = new Intent();
            intent.putExtra("submit", true);
            setResult(0, intent);
        }
        finish();
    }

    /**
     * 查看订单详情
     */
    private void turnToOrderDetail() {
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
        intent.putExtra("orderNum", orderNum);
        startActivity(intent);
    }

    /**
     * 查看结果
     */
    private void checkResult() {
        switch (type) {
            case TYPE_OPERATE_RECHARGE:
                startActivity(new Intent(mContext, TransactionRecordActivity.class));
                break;
            case TYPE_OPERATE_BUY_LESSON:
                if (isFromPlayService) {
                    turnToMicroLesson();
                } else {
                    startActivity(new Intent(mContext, BoughtLessonActivity.class));
                }
                break;
            case TYPE_OPERATE_WRITING_PUBLIC:
                Intent intent_public = new Intent(mContext, MyCompositionActivity.class);
                intent_public.putExtra("index", 1);
                startActivity(intent_public);
                break;
            case TYPE_OPERATE_WRITING_CORRECTION:
                Intent intent = new Intent(mContext, MyCompositionActivity.class);
                intent.putExtra("index", 2);
                startActivity(intent);
                break;
            case TYPE_OPERATE_WRITING_WORK:
                Intent intent_work = new Intent(mContext, MyCompositionActivity.class);
                intent_work.putExtra("index", 3);
                startActivity(intent_work);
                break;
            case TYPE_OPERATE_WRITING_EVENTS:
                Intent intent_events = new Intent(mContext, MyCompositionActivity.class);
                intent_events.putExtra("index", 4);
                startActivity(intent_events);
                break;
            case TYPE_OPERATE_BUY_CARD:
                backToActivity();
                break;
        }
    }

    /**
     * 前往微课
     */
    private void turnToMicroLesson() {
        Intent intent = new Intent(mContext, MicroLessonActivity.class);
        intent.putExtra("id", NewMainActivity.lessonId);
        intent.putExtra("isFromPlayService", isFromPlayService);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backToActivity();
    }

}
