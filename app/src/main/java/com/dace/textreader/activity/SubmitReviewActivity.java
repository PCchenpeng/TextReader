package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.bean.CardBean;
import com.dace.textreader.bean.CouponBean;
import com.dace.textreader.bean.PayResult;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.Utils;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 提交
 * 作文批改、微课购买
 */
public class SubmitReviewActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/correction/composition/id";
    private static final String writingEditorUrl = HttpUrlPre.HTTP_URL + "/correction/composition/";
    private static final String walletUrl = HttpUrlPre.HTTP_URL + "/account/query";
    private static final String buyLessonUrl = HttpUrlPre.HTTP_URL + "/course/purchase/account";
    private static final String phoneUrl = HttpUrlPre.HTTP_URL + "/get/hotline";
    //作文ID进行批改（ 微信支付）
    private static final String wxBuyWritingIdUrl = HttpUrlPre.HTTP_URL + "/app/wx/buy/writing/id";
    //编辑模式作文批改（微信支付）
    private static final String wxBuyWritingEditorUrl = HttpUrlPre.HTTP_URL + "/app/wx/buy/writing";
    //作文ID进行批改（ 支付宝支付）
    private static final String aliBuyWritingIdUrl = HttpUrlPre.HTTP_URL + "/app/alipay/buy/writing/id";
    //编辑模式作文批改（支付宝支付）
    private static final String aliBuyWritingEditorUrl = HttpUrlPre.HTTP_URL + "/app/alipay/buy/writing";
    //账单查询
    private static final String orderUrl = HttpUrlPre.HTTP_URL + "/bill/query/detail";
    //查询作文批改价格
    private static final String writingPriceUrl = HttpUrlPre.HTTP_URL + "/select/writing/correction/price";
    //查询微课价格
    private static final String lessonPriceUrl = HttpUrlPre.HTTP_URL + "/select/course/charge/condition";

    //提示信息
    private static final String writingTipsUrl = HttpUrlPre.HTTP_URL + "/text/load/dynamic";

    private LinearLayout rl_root;
    private RelativeLayout rl_back;
    private TextView tv_title;

    private RelativeLayout rl_call;
    private TextView tv_item;
    private TextView tv_price;
    private TextView tv_original_price;
    private TextView tv_count;
    private TextView tv_writing_title;
    private TextView tv_no_balance;
    private LinearLayout ll_coupon;
    private TextView tv_coupon;
    private LinearLayout ll_card;
    private TextView tv_card;
    private TextView tv_more_card;

    private LinearLayout ll_pay_way;
    private LinearLayout ll_pay_pythe;
    private ImageView iv_pay_pythe;
    private LinearLayout ll_pay_wechat;
    private ImageView iv_pay_wechat;
    private LinearLayout ll_pay_ali;
    private ImageView iv_pay_ali;
    private TextView tv_tips;

    private TextView tv_discount;
    private TextView tv_sure;

    private SubmitReviewActivity mContext;

    private String type = "";  //类型，writing作文，lesson微课

    private boolean isWritingEditor;
    private String id;  //作文ID
    private String productName;
    private String title;  //作文标题
    private String content = "";  //作文内容
    private int count;  //作文字数
    private int writing_area = 0;  //作文提交的区域
    private int writing_type = 0;  //作文本身类型;
    private String writing_cover = "";
    private String writing_match = "";  //作文的比赛ID
    private int writing_format = 1;

    private long lessonId;  //微课ID
    private String lessonTitle;  //微课标题
    private String lessonTeacher;  //微课老师
    private int lessonCount;  //微课节数
    private boolean isFromPlayService;

    private boolean isCheckingPrice = false;  //正在查询价格
    private String couponCode = "-1";  //优惠码，-1为默认优惠条件或者不使用优惠
    private String cardCode = "-1";  //卡包码，-1为默认优惠条件或者不使用优惠
    private String isUseDiscount = "1";  //是否使用优惠，默认使用,0为不使用
    private double price;
    private double discount;
    private CardBean cardBean;
    private CouponBean couponBean;
    private boolean isCardExist = true;

    private String phoneNum = "";
    private int pay_way = 0;  //购买方式，0表示用派豆钱包，1表示用微信支付,2表示支付宝支付
    private boolean no_balance = false;  //余额不足
    private boolean isBuying = false;  //正在购买
    private boolean isClickPhone = false;

    /**
     * 微信支付相关
     */
    private boolean isGoToWXPay = false;
    private int index = 5;  //查询微信账单的信息次数
    private String timeStamp = "";  //时间戳
    private String out_trade_no = "";
    private String paySign = "";  //支付签名
    private String nonceStr = "";  //随机字符串
    private String prepay_id = "";  //预支付ID

    //是否需要隐藏对话框，在activity处于onResume的时候隐藏，
    // 避免在onStop之后隐藏导致抛出异常
    // IllegalStateException: Can not perform this action after onSaveInstanceState
    private boolean dialogNeedHide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);

        mContext = this;

        type = getIntent().getStringExtra("type");

        if (type.equals("writing")) {
            isWritingEditor = getIntent().getBooleanExtra("isEditor", false);
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            count = getIntent().getIntExtra("count", 0);
            writing_match = getIntent().getStringExtra("writing_match");
            productName = getIntent().getStringExtra("productName");
            if (isWritingEditor) {
                content = getIntent().getStringExtra("writing_content");
                writing_cover = getIntent().getStringExtra("writing_cover");
                writing_format = getIntent().getIntExtra("writing_format", 1);
            } else {
                writing_area = getIntent().getIntExtra("writing_area", 5);
                writing_type = getIntent().getIntExtra("writing_type", 5);
            }
        } else if (type.equals("lesson")) {
            lessonId = getIntent().getLongExtra("lessonId", -1L);
            price = getIntent().getDoubleExtra("lessonPrice", 0);
            discount = getIntent().getDoubleExtra("lessonOriginalPrice", -1);
            lessonTitle = getIntent().getStringExtra("lessonTitle");
            lessonTeacher = getIntent().getStringExtra("lessonTeacher");
            lessonCount = getIntent().getIntExtra("lessonCount", 0);
            isFromPlayService = getIntent().getBooleanExtra("fromService", false);
        }

        initView();
        initEvents();
        setImmerseLayout();

        if (type.equals("writing")) {
            getWritingPrice();
        } else if (type.equals("lesson")) {
            getLessonPrice();
        } else {
            finish();
        }
        new GetPhoneNumber(mContext).execute(phoneUrl);
        new GetTipsData(mContext).execute(writingTipsUrl);
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_call.setPadding(0, statusBarHeight, 0, 0);
    }

    /**
     * 获取微课价格
     */
    private void getLessonPrice() {
        isCheckingPrice = true;
        couponBean = null;
        cardBean = null;
        isCardExist = true;
        new GetLessonPrice(mContext).execute(lessonPriceUrl, String.valueOf(lessonId),
                couponCode, cardCode, isUseDiscount);
    }

    /**
     * 获取作文价格
     */
    private void getWritingPrice() {
        isCheckingPrice = true;
        couponBean = null;
        cardBean = null;
        isCardExist = true;
        new GetWritingPrice(mContext).execute(writingPriceUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), couponCode, cardCode, isUseDiscount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGoToWXPay) {
            showLoadingView();
            isGoToWXPay = false;
            index = 5;
            new GetOrderData(mContext).execute(orderUrl, out_trade_no);
        }
        if (dialogNeedHide) {
            if (dialog_loading != null) {
                dialog_loading.dismiss();
            }
        }
    }

    private BaseNiceDialog dialog_loading;

    /**
     * 显示等待视图
     */
    private void showLoadingView() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_loading_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        ImageView imageView = holder.getView(R.id.iv_loading_dialog);
                        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, imageView);
                        dialog_loading = dialog;
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(40)
                .show(getSupportFragmentManager());
    }

    /**
     * 开始搜索订单信息
     */
    private void startSearchOrderInfo() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                    mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                case 0:
                    if (index >= 0) {
                        index = index - 1;
                        new GetOrderData(mContext).execute(orderUrl, out_trade_no);
                    }
                    break;
                case 1:
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        submitSuccess(out_trade_no);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showErrorView();
                        showTips("支付失败");
                    }
                    break;
            }
        }
    };

    private void initData() {
        new GetWalletData(mContext).execute(walletUrl);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        tv_no_balance.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        rl_call.setOnClickListener(this);
        ll_pay_pythe.setOnClickListener(this);
        ll_pay_wechat.setOnClickListener(this);
        ll_pay_ali.setOnClickListener(this);
        ll_coupon.setOnClickListener(this);
        ll_card.setOnClickListener(this);
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_submit_review);
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("支付");

        rl_call = findViewById(R.id.rl_call_submit_review);
        tv_item = findViewById(R.id.tv_item_submit_review);
        tv_writing_title = findViewById(R.id.tv_title_submit_review);
        tv_count = findViewById(R.id.tv_count_submit_review);
        tv_price = findViewById(R.id.tv_price_submit_review);
        tv_original_price = findViewById(R.id.tv_original_price_submit_review);
        tv_original_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        tv_no_balance = findViewById(R.id.tv_no_balance_submit_review);
        ll_coupon = findViewById(R.id.ll_coupon_submit_review);
        tv_coupon = findViewById(R.id.tv_coupon_submit_review);
        ll_card = findViewById(R.id.ll_card_submit_review);
        tv_card = findViewById(R.id.tv_card_submit_review);
        tv_more_card = findViewById(R.id.tv_more_card_submit_review);

        ll_pay_way = findViewById(R.id.ll_pay_way_submit_review);
        ll_pay_pythe = findViewById(R.id.ll_pay_way_pythe_submit_review);
        iv_pay_pythe = findViewById(R.id.iv_pythe_status_submit_review);
        ll_pay_wechat = findViewById(R.id.ll_pay_way_wechat_submit_review);
        iv_pay_wechat = findViewById(R.id.iv_wechat_status_submit_review);
        ll_pay_ali = findViewById(R.id.ll_pay_way_ali_submit_review);
        iv_pay_ali = findViewById(R.id.iv_ali_status_submit_review);
        tv_tips = findViewById(R.id.tv_tips_submit_review);
        tv_discount = findViewById(R.id.tv_discount_submit_review);
        tv_sure = findViewById(R.id.tv_sure_submit_review);

        if (type.equals("writing")) {
            tv_item.setText(productName);
            String priceInfo = DataUtil.double2String(price) + "派豆";
            tv_price.setText(priceInfo);
            tv_writing_title.setText(title);
            String writing_count = String.valueOf(count) + "个";
            tv_count.setText(writing_count);
            if (discount != -1) {
                String originalPriceInfo = DataUtil.double2String(discount) + "派豆";
                tv_original_price.setText(originalPriceInfo);
                tv_original_price.setVisibility(View.VISIBLE);
            }
            tv_tips.setVisibility(View.VISIBLE);
            ll_card.setVisibility(View.VISIBLE);
            ll_pay_wechat.setVisibility(View.VISIBLE);
            ll_pay_ali.setVisibility(View.VISIBLE);
        } else if (type.equals("lesson")) {
            TextView tv_one = findViewById(R.id.tv_one_submit_review);
            TextView tv_two = findViewById(R.id.tv_two_submit_review);
            TextView tv_three = findViewById(R.id.tv_three_submit_review);
            TextView tv_four = findViewById(R.id.tv_four_submit_review);
            tv_one.setText("微课");
            tv_two.setText("授课");
            tv_three.setText("节数");
            tv_four.setText("金额");

            tv_item.setText(lessonTitle);
            tv_writing_title.setText(lessonTeacher);
            String countInfo = lessonCount + "节";
            tv_count.setText(countInfo);
            String priceInfo = DataUtil.double2String(price) + "派豆";
            if (discount != -1) {
                String originalPriceInfo = DataUtil.double2String(discount) + "派豆";
                tv_original_price.setText(originalPriceInfo);
                tv_original_price.setVisibility(View.VISIBLE);
            }
            tv_price.setText(priceInfo);
            tv_tips.setVisibility(View.GONE);
            ll_card.setVisibility(View.GONE);
            ll_pay_wechat.setVisibility(View.GONE);
            ll_pay_ali.setVisibility(View.GONE);
        }

        tv_original_price.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.tv_no_balance_submit_review:
                turnToRecharge();
                break;
            case R.id.tv_sure_submit_review:
                if (isCheckingPrice) {
                    showTips("正在查询价格，请稍候...");
                } else {
                    if (!isBuying) {
                        isBuying = true;
                        if (type.equals("writing")) {
                            submitWriting();
                        } else if (type.equals("lesson")) {
                            buyMicroLesson();
                        }
                    }
                }
                break;
            case R.id.ll_pay_way_pythe_submit_review:
                if (pay_way != 0) {
                    pay_way = 0;
                    iv_pay_pythe.setImageResource(R.drawable.icon_edit_selected);
                    iv_pay_wechat.setImageResource(R.drawable.icon_edit_unselected);
                    iv_pay_ali.setImageResource(R.drawable.icon_edit_unselected);
                    if (no_balance) {
                        tv_no_balance.setVisibility(View.VISIBLE);
                    } else {
                        tv_no_balance.setVisibility(View.GONE);
                    }
                    changeUnit();
                }
                break;
            case R.id.ll_pay_way_wechat_submit_review:
                if (pay_way != 1) {
                    pay_way = 1;
                    iv_pay_wechat.setImageResource(R.drawable.icon_edit_selected);
                    iv_pay_pythe.setImageResource(R.drawable.icon_edit_unselected);
                    iv_pay_ali.setImageResource(R.drawable.icon_edit_unselected);
                    tv_no_balance.setVisibility(View.GONE);
                    changeUnit();
                }
                break;
            case R.id.ll_pay_way_ali_submit_review:
                if (pay_way != 2) {
                    pay_way = 2;
                    iv_pay_ali.setImageResource(R.drawable.icon_edit_selected);
                    iv_pay_pythe.setImageResource(R.drawable.icon_edit_unselected);
                    iv_pay_wechat.setImageResource(R.drawable.icon_edit_unselected);
                    tv_no_balance.setVisibility(View.GONE);
                    changeUnit();
                }
                break;
            case R.id.rl_call_submit_review:
                if (phoneNum.equals("")) {
                    showTips("正在获取客服号码...");
                    if (!isClickPhone) {
                        isClickPhone = true;
                        new GetPhoneNumber(mContext).execute(phoneUrl);
                    }
                } else {
                    showPhoneNumberDialog();
                }
                break;
            case R.id.ll_coupon_submit_review:
                turnToCouponChoose();
                break;
            case R.id.ll_card_submit_review:
                turnToCardChoose();
                break;
        }
    }

    /**
     * 前往选择卡包
     */
    private void turnToCardChoose() {
        Intent intent = new Intent(mContext, CardChooseActivity.class);
        if (cardBean != null) {
            intent.putExtra("id", cardBean.getId());
        }
        startActivityForResult(intent, 6);
    }

    /**
     * 前往选择优惠券
     */
    private void turnToCouponChoose() {
        Intent intent = new Intent(mContext, CouponChooseActivity.class);
        if (type.equals("writing")) {
            intent.putExtra("category", 1);
        } else if (type.equals("lesson")) {
            intent.putExtra("category", 2);
            intent.putExtra("lessonId", lessonId);
        }
        if (couponBean != null) {
            intent.putExtra("id", couponBean.getId());
        }
        startActivityForResult(intent, 5);
    }

    /**
     * 改变支付单位
     */
    private void changeUnit() {
        if (pay_way == 0) {
            String priceInfo = tv_sure.getText().toString();
            priceInfo = priceInfo.replace("元", "派豆");
            tv_sure.setText(priceInfo);

            String discountInfo = tv_discount.getText().toString();
            discountInfo = discountInfo.replace("元", "派豆");
            tv_discount.setText(discountInfo);
        } else if (pay_way == 1 || pay_way == 2) {
            String priceInfo = tv_sure.getText().toString();
            priceInfo = priceInfo.replace("派豆", "元");
            tv_sure.setText(priceInfo);

            String discountInfo = tv_discount.getText().toString();
            discountInfo = discountInfo.replace("派豆", "元");
            tv_discount.setText(discountInfo);
        }
    }

    /**
     * 显示电话号码对话框
     */
    private void showPhoneNumberDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_phone_number_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_number = holder.getView(R.id.tv_phone_number_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_phone_number_dialog);
                        TextView tv_call = holder.getView(R.id.tv_call_phone_number_dialog);
                        tv_number.setText(phoneNum);
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                call(phoneNum);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(true)
                .setMargin(56)
                .show(getSupportFragmentManager());
    }

    /**
     * 调起拨打电话的界面
     *
     * @param phone
     */
    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 购买微课
     */
    private void buyMicroLesson() {
        showTips("正在购买...");
        tv_sure.append("，正在购买...");
        tv_sure.setTextColor(Color.parseColor("#999999"));
        String couponCode = "";
        if (couponBean != null) {
            couponCode = couponBean.getCouponCode();
        }
        String cardCode = "";
        if (cardBean != null) {
            cardCode = cardBean.getCardCode();
        }

        new BuyLesson(mContext).execute(buyLessonUrl, String.valueOf(lessonId), couponCode, cardCode);
    }

    /**
     * 提交作文修改
     */
    private void submitWriting() {
        showTips("正在提交...");
        tv_sure.append("，正在购买...");
        tv_sure.setTextColor(Color.parseColor("#999999"));
        if (pay_way == 0) {
            String couponCode = "";
            if (couponBean != null) {
                couponCode = couponBean.getCouponCode();
            }
            String cardCode = "";
            if (cardBean != null) {
                cardCode = cardBean.getCardCode();
            }
            double o_price = price + discount;
            if (isWritingEditor) {
                new SubmitEditorWritingReview(mContext).execute(writingEditorUrl,
                        String.valueOf(NewMainActivity.STUDENT_ID),
                        id, String.valueOf(price), String.valueOf(o_price),
                        String.valueOf(4), writing_cover, title, content, writing_match,
                        String.valueOf(writing_type), String.valueOf(count), couponCode, cardCode,
                        String.valueOf(writing_format));
            } else {
                new SubmitReview(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                        id, String.valueOf(price), String.valueOf(o_price), String.valueOf(4),
                        String.valueOf(writing_area), String.valueOf(writing_type), writing_match,
                        couponCode, cardCode);
            }
        } else if (pay_way == 1) {
            wxUnifiedOrder();
        } else if (pay_way == 2) {
            aliUnifiedOrder();
        }
    }

    /**
     * 支付宝统一下单
     */
    private void aliUnifiedOrder() {
        String couponCode = "";
        if (couponBean != null) {
            couponCode = couponBean.getCouponCode();
        }
        String cardCode = "";
        if (cardBean != null) {
            cardCode = cardBean.getCardCode();
        }
        double o_price = price + discount;
        if (isWritingEditor) {
            new WXUnifiedOrderWritingEditor(mContext).execute(aliBuyWritingEditorUrl,
                    String.valueOf(o_price), String.valueOf(price), "APP",
                    Utils.getLocalIpAddress(), id, String.valueOf(writing_type), title, content,
                    String.valueOf(count), writing_cover, writing_match, couponCode, cardCode,
                    String.valueOf(writing_format));
        } else {
            new WXUnifiedOrderWritingEditor(mContext).execute(aliBuyWritingIdUrl,
                    String.valueOf(o_price), String.valueOf(price), "APP",
                    Utils.getLocalIpAddress(), id, String.valueOf(writing_area), couponCode, cardCode);
        }
    }

    /**
     * 微信购买批改
     */
    private void wxUnifiedOrder() {
        if (!App.api.isWXAppInstalled()) {
            MyToastUtil.showToast(mContext, "您还未安装微信客户端");
            return;
        }
        String couponCode = "";
        if (couponBean != null) {
            couponCode = couponBean.getCouponCode();
        }
        String cardCode = "";
        if (cardBean != null) {
            cardCode = cardBean.getCardCode();
        }
        double o_price = price + discount;
        if (isWritingEditor) {
            new WXUnifiedOrderWritingEditor(mContext).execute(wxBuyWritingEditorUrl,
                    String.valueOf(o_price), String.valueOf(price), "APP",
                    Utils.getLocalIpAddress(), id, String.valueOf(writing_type), title, content,
                    String.valueOf(count), writing_cover, writing_match, couponCode, cardCode,
                    String.valueOf(writing_format));
        } else {
            new WXUnifiedOrderWritingEditor(mContext).execute(wxBuyWritingIdUrl,
                    String.valueOf(o_price), String.valueOf(price), "APP",
                    Utils.getLocalIpAddress(), id, String.valueOf(writing_area), couponCode, cardCode);
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeSubmitData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String orderNum = object.getString("orderNum");
                submitSuccess(orderNum);
            } else if (300 == jsonObject.optInt("status", -1)) {
                submitError("充值");
            } else {
                showErrorView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorView();
        }
    }

    /**
     * 显示支付失败视图
     */
    private void showErrorView() {
        isBuying = false;
        if (isResume()) {
            if (dialog_loading != null) {
                dialog_loading.dismiss();
            }
        } else {
            dialogNeedHide = true;
        }
        TipsUtil tipsUtil = new TipsUtil(mContext);
        tipsUtil.showPayFailedView(rl_root, "");
    }

    /**
     * 提交成功
     */
    private void submitSuccess(String orderNum) {
        if (isWritingEditor) {
            Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("cover", writing_cover);
            intent.putExtra("count", count);
            intent.putExtra("format", writing_format);
            intent.putExtra("area", 5);
            intent.putExtra("index", 2);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(mContext, OperationResultActivity.class);
            intent.putExtra("operateType", "writing_correction");
            intent.putExtra("isSuccessful", true);
            intent.putExtra("content", title);
            intent.putExtra("price", price);
            intent.putExtra("pay_way", pay_way);
            intent.putExtra("orderNum", orderNum);
            startActivityForResult(intent, 1);
        }
    }

    /**
     * 提交失败
     */
    private void submitError(String errorInfo) {
        if (errorInfo.contains("充值")) {
            NiceDialog.init()
                    .setLayoutId(R.layout.dialog_writing_cancel)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            TextView textView = holder.getView(R.id.tv_cancel_tips_writing_dialog);
                            Button btn_sure = holder.getView(R.id.btn_save_writing_draft_dialog);
                            Button btn_no = holder.getView(R.id.btn_give_up_writing_draft_dialog);
                            textView.setText("余额不足，请充值");
                            btn_no.setText("下次再说");
                            btn_sure.setText("前往充值");
                            btn_no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            btn_sure.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    turnToRecharge();
                                }
                            });
                        }
                    })
                    .setMargin(40)
                    .setShowBottom(false)
                    .setOutCancel(false)
                    .show(getSupportFragmentManager());
        } else {
            showTips(errorInfo);
        }
    }

    /**
     * 前往充值
     */
    private void turnToRecharge() {
        Intent intent = new Intent(mContext, RechargeActivity.class);
        startActivityForResult(intent, 4);
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        isBuying = false;
        showTips("无网络连接");
    }

    /**
     * 分析微信统一下单数据
     *
     * @param s
     */
    private void analyzeWXUnifiedOrderData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject data = jsonObject.getJSONObject("data");
                timeStamp = data.getString("timeStamp");
                out_trade_no = data.getString("out_trade_no");
                paySign = data.getString("paySign");
                nonceStr = data.getString("nonceStr");
                prepay_id = data.getString("prepay_id");
                wxRecharge();
            } else {
                showErrorView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorView();
        }
    }

    /**
     * 调起微信支付
     */
    private void wxRecharge() {
        isGoToWXPay = true;
        PayReq req = new PayReq();
        req.appId = App.APP_ID;
        req.partnerId = App.WX_MCH_ID;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.sign = paySign;
        App.api.sendReq(req);
    }

    /**
     * 分析支付宝统一下单数据
     *
     * @param s
     */
    private void analyzeAliUnifiedOrderData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String result = object.getString("result");
                out_trade_no = object.getString("orderNum");
                aliPay(result);
            } else {
                showErrorView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorView();
        }
    }

    /**
     * 调起支付宝支付
     */
    private void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(mContext);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }



    /**
     * 提交批改
     */
    private static class SubmitReview
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected SubmitReview(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", strings[1]);
                jsonObject.put("writingId", strings[2]);
                jsonObject.put("discount_price", strings[3]);
                jsonObject.put("price", strings[4]);
                jsonObject.put("pay_channel", strings[5]);
                jsonObject.put("area", strings[6]);
                jsonObject.put("type", strings[7]);
                if (!strings[8].equals("") && !strings[8].equals("null")) {
                    jsonObject.put("matchId", strings[8]);
                }
                if (!strings[9].equals("")) {
                    jsonObject.put("couponCode", strings[9]);
                } else if (!strings[10].equals("")) {
                    jsonObject.put("cardCode", strings[10]);
                }
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeSubmitData(s);
            }
            String string = activity.tv_sure.getText().toString();
            string = string.replace("，正在购买...", "");
            activity.tv_sure.setText(string);
            activity.tv_sure.setTextColor(Color.parseColor("#ffffff"));
            activity.isBuying = false;
        }
    }

    /**
     * 提交编辑作文批改
     */
    private static class SubmitEditorWritingReview
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected SubmitEditorWritingReview(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", strings[1]);
                if (!strings[2].equals("") && !strings[2].equals("null")) {
                    jsonObject.put("writingId", strings[2]);
                    jsonObject.put("type", strings[10]);
                } else {
                    jsonObject.put("type", 1);
                }
                jsonObject.put("discount_price", strings[3]);
                jsonObject.put("price", strings[4]);
                jsonObject.put("pay_channel", strings[5]);
                if (!strings[6].equals("") && !strings[6].equals("null")) {
                    jsonObject.put("cover", strings[6]);
                }
                jsonObject.put("article", strings[7]);
                jsonObject.put("content", strings[8]);
                if (!strings[9].equals("") && !strings[9].equals("null")) {
                    jsonObject.put("matchId", strings[9]);
                }
                jsonObject.put("wordsNum", strings[11]);
                if (!strings[12].equals("")) {
                    jsonObject.put("couponCode", strings[12]);
                } else if (!strings[13].equals("")) {
                    jsonObject.put("cardCode", strings[13]);
                }
                jsonObject.put("format", strings[14]);
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeSubmitData(s);
            }
            String string = activity.tv_sure.getText().toString();
            string = string.replace("，正在购买...", "");
            activity.tv_sure.setText(string);
            activity.tv_sure.setTextColor(Color.parseColor("#ffffff"));
            activity.isBuying = false;
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeWalletData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                double amount = object.getDouble("amount");
                updateUi(amount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新视图
     *
     * @param amount
     */
    private void updateUi(double amount) {
        double rest;
        rest = amount - price;
        if (rest < 0) {
            tv_no_balance.setVisibility(View.VISIBLE);
            no_balance = true;
        } else {
            tv_no_balance.setVisibility(View.GONE);
            no_balance = false;
        }
    }

    /**
     * 分析购买课程的数据
     *
     * @param s
     */
    private void analyzeBuyLessonData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String orderNum = object.getString("orderNum");
                buyLessonSuccess(orderNum);
            } else if (300 == jsonObject.optInt("status", -1)) {
                submitError("请充值");
                isBuying = false;
            } else if (500 == jsonObject.optInt("status", -1)) {
                showErrorView();
            } else {
                showErrorView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorView();
        }
    }

    /**
     * 购买成功
     */
    private void buyLessonSuccess(String orderNum) {
        Intent intent = new Intent(mContext, OperationResultActivity.class);
        intent.putExtra("operateType", "buy_lesson");
        intent.putExtra("isSuccessful", true);
        intent.putExtra("content", lessonTitle);
        intent.putExtra("price", price);
        intent.putExtra("isFromPlayService", isFromPlayService);
        intent.putExtra("orderNum", orderNum);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            initData();
        } else if (requestCode == 5) {
            if (data != null) {
                couponCode = data.getStringExtra("couponCode");
                tv_coupon.setText(data.getStringExtra("title"));
                if (couponCode.equals("")) {
                    isUseDiscount = "0";
                } else {
                    isUseDiscount = "1";
                }
                cardCode = "";
                if (type.equals("writing")) {
                    getWritingPrice();
                } else if (type.equals("lesson")) {
                    getLessonPrice();
                }
            }
        } else if (requestCode == 6) {
            if (data != null) {
                cardCode = data.getStringExtra("cardCode");
                tv_card.setText(data.getStringExtra("title"));
                if (cardCode.equals("")) {
                    isUseDiscount = "0";
                } else {
                    isUseDiscount = "1";
                }
                couponCode = "";
                if (type.equals("writing")) {
                    getWritingPrice();
                } else if (type.equals("lesson")) {
                    getLessonPrice();
                }
            }
        } else {
            if (data != null) {
                boolean isSubmit = data.getBooleanExtra("submit", false);
                if (isSubmit) {
                    Intent intent = new Intent();
                    intent.putExtra("submit", true);
                    setResult(0, intent);
                    finish();
                }
            }
        }
    }

    /**
     * 分析客服号码数据
     *
     * @param s
     */
    private void analyzePhoneData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                phoneNum = object.getString("phoneNum");
                if (isClickPhone) {
                    showPhoneNumberDialog();
                }
                isClickPhone = false;
            } else {
                isClickPhone = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            isClickPhone = false;
        }
    }

    /**
     * 分析获取作文价格数据
     *
     * @param s
     */
    private void analyzeGetWritingPriceData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                price = object.getDouble("discountPrice");
                discount = object.getDouble("discount");
                productName = object.getString("productName");
                String coupon = object.getString("coupon");
                if (!coupon.equals("null") && !coupon.equals("")) {
                    JSONObject couponObject = new JSONObject(coupon);
                    couponBean = new CouponBean();
                    couponBean.setId(couponObject.optLong("id", -1));
                    couponBean.setStatus(couponObject.optInt("status", -1));
                    couponBean.setCouponId(couponObject.optInt("couponId", -1));
                    couponBean.setCategory(couponObject.optInt("category", -1));
                    couponBean.setType(couponObject.optInt("type", -1));
                    couponBean.setTitle(couponObject.getString("title"));
                    couponBean.setCouponCode(couponObject.getString("couponCode"));
                    couponBean.setConsumption(couponObject.optDouble("consumption", -1));
                    couponBean.setSubtract(couponObject.optDouble("subtract", -1));
                    couponBean.setDiscount(couponObject.optDouble("discount", -1));
                }
                String card = object.getString("card");
                if (!card.equals("null") && !card.equals("")) {
                    if (card.equals("{\"exist\":1}")) {
                        isCardExist = true;
                    } else if (card.equals("{\"exist\":0}")) {
                        isCardExist = false;
                    } else {
                        JSONObject cardObject = new JSONObject(card);
                        cardBean = new CardBean();
                        cardBean.setId(cardObject.optLong("id", -1));
                        cardBean.setFrequency(cardObject.optInt("frequency", 0));
                        cardBean.setType(cardObject.optInt("type", 0));
                        cardBean.setCardCode(cardObject.getString("cardCode"));
                        cardBean.setStatus(cardObject.optInt("status", 0));
                        cardBean.setDiscount(cardObject.optDouble("discount", -1));
                        cardBean.setTitle(cardObject.getString("title"));
                    }
                } else {
                    isCardExist = false;
                }
                updatePrice();
            } else {
                errorGetPrice();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorGetPrice();
        }
    }

    /**
     * 分析获取微课价格数据
     *
     * @param s
     */
    private void analyzeGetLessonPriceData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                price = object.getDouble("discountPrice");
                discount = object.getDouble("discount");
                String coupon = object.getString("coupon");
                if (!coupon.equals("null") && !coupon.equals("")) {
                    JSONObject couponObject = new JSONObject(coupon);
                    couponBean = new CouponBean();
                    couponBean.setId(couponObject.optLong("id", -1));
                    couponBean.setStatus(couponObject.optInt("status", -1));
                    couponBean.setCouponId(couponObject.optInt("couponId", -1));
                    couponBean.setCategory(couponObject.optInt("category", -1));
                    couponBean.setType(couponObject.optInt("type", -1));
                    couponBean.setTitle(couponObject.getString("title"));
                    couponBean.setCouponCode(couponObject.getString("couponCode"));
                    couponBean.setConsumption(couponObject.optDouble("consumption", -1));
                    couponBean.setSubtract(couponObject.optDouble("subtract", -1));
                    couponBean.setDiscount(couponObject.optDouble("discount", -1));
                }
//                String card = object.getString("card");
//                if (!card.equals("null") && !card.equals("")) {
//                    if (card.equals("{exist:1}")) {
//                        isCardExist = true;
//                    } else {
//                        JSONObject cardObject = new JSONObject(card);
//                        cardBean = new CardBean();
//                        cardBean.setId(cardObject.optInt("id", -1));
//                        cardBean.setFrequency(cardObject.optInt("frequency", 0));
//                        cardBean.setType(cardObject.optInt("type", 0));
//                        cardBean.setCardCode(cardObject.getString("cardCode"));
//                        cardBean.setStatus(cardObject.optInt("status", 0));
//                        cardBean.setDiscount(cardObject.optDouble("discount", -1));
//                        cardBean.setTitle(cardObject.getString("title"));
//                    }
//                }
                updatePrice();
            } else {
                errorGetPrice();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorGetPrice();
        }
    }

    /**
     * 更新价格
     */
    private void updatePrice() {
        tv_original_price.setVisibility(View.GONE);

        double p = price + discount;
        String priceInfo = DataUtil.double2String(p) + "派豆";
        tv_price.setText(priceInfo);
        String payPriceInfo = "确认支付" + DataUtil.double2String(price) + "派豆";
        tv_sure.setText(payPriceInfo);
        String discountInfo = "已优惠" + DataUtil.double2String(discount) + "派豆";
        tv_discount.setText(discountInfo);

        if (type.equals("writing")) {
            tv_item.setText(productName);

            if (isCardExist) {
                //卡包只有作文才能使用
                if (cardBean == null) {  //使用作文批改优惠卡更划算哦
                    tv_card.setText("暂不能使用卡包");
                    tv_more_card.setVisibility(View.GONE);
                    cardCode = "";
                } else {
                    tv_more_card.setVisibility(View.GONE);
                    tv_card.setText(cardBean.getTitle());
                    cardCode = cardBean.getCardCode();
                }
            } else {
                cardCode = "";
                tv_card.setText("使用作文批改优惠卡更划算哦");
            }
        }

        if (couponBean == null) {
            if (!cardCode.equals("") && !cardCode.equals("-1")) {
                tv_coupon.setText("暂不能使用优惠");
            } else {
                tv_coupon.setText("暂无优惠");
            }
            couponCode = "";
        } else {
            tv_coupon.setText(couponBean.getTitle());
            couponCode = couponBean.getCouponCode();
        }

        if (price == 0) {
            ll_pay_wechat.setVisibility(View.GONE);
            ll_pay_ali.setVisibility(View.GONE);
            pay_way = 0;
            iv_pay_pythe.setImageResource(R.drawable.icon_edit_selected);
            iv_pay_wechat.setImageResource(R.drawable.icon_edit_unselected);
            iv_pay_ali.setImageResource(R.drawable.icon_edit_unselected);
            if (no_balance) {
                tv_no_balance.setVisibility(View.VISIBLE);
            } else {
                tv_no_balance.setVisibility(View.GONE);
            }
        } else if (type.equals("writing")) {
            ll_pay_wechat.setVisibility(View.VISIBLE);
            ll_pay_ali.setVisibility(View.VISIBLE);
        }

        changeUnit();

        initData();
    }

    /**
     * 获取价格失败
     */
    private void errorGetPrice() {
        showTips("获取价格失败");
    }

    /**
     * 获取账户数据
     */
    private static class GetWalletData
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected GetWalletData(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", NewMainActivity.STUDENT_ID);
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s != null) {
                activity.analyzeWalletData(s);
            }
        }
    }

    /**
     * 购买课程
     */
    private static class BuyLesson
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected BuyLesson(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("courseId", strings[1]);
                object.put("payChannel", 4);
                if (!strings[2].equals("")) {
                    object.put("couponCode", strings[2]);
                } else if (!strings[3].equals("")) {
                    object.put("cardCode", strings[3]);
                }
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeBuyLessonData(s);
            }
            String string = activity.tv_sure.getText().toString();
            string = string.replace("，正在购买...", "");
            activity.tv_sure.setText(string);
            activity.tv_sure.setTextColor(Color.parseColor("#ffffff"));
            activity.isBuying = false;
        }
    }

    /**
     * 获取电话
     */
    private static class GetPhoneNumber
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected GetPhoneNumber(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(DataUtil.JSON, "");
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s != null) {
                activity.analyzePhoneData(s);
            } else {
                activity.isClickPhone = false;
            }
        }
    }

    /**
     * 编辑模式下微信支付购买批改
     */
    private static class WXUnifiedOrderWritingEditor
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected WXUnifiedOrderWritingEditor(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", NewMainActivity.STUDENT_ID);
                jsonObject.put("price", strings[1]);
                jsonObject.put("discount_price", strings[2]);
                jsonObject.put("trade_type", strings[3]);
                jsonObject.put("spbill_create_ip", strings[4]);
                if (activity.isWritingEditor) {
                    if (!strings[5].equals("") && !strings[5].equals("null")) {
                        jsonObject.put("writingId", strings[5]);
                        jsonObject.put("type", strings[6]);
                    } else {
                        jsonObject.put("type", 1);
                    }
                    jsonObject.put("article", strings[7]);
                    jsonObject.put("content", strings[8]);
                    jsonObject.put("wordsNum", strings[9]);
                    if (!strings[10].equals("") && !strings[10].equals("null")) {
                        jsonObject.put("cover", strings[10]);
                    }
                    if (!strings[11].equals("") && !strings[11].equals("null")) {
                        jsonObject.put("matchId", strings[11]);
                    }
                    if (!strings[12].equals("")) {
                        jsonObject.put("couponCode", strings[12]);
                    } else if (!strings[13].equals("")) {
                        jsonObject.put("cardCode", strings[13]);
                    }
                    jsonObject.put("format", strings[14]);
                } else {
                    jsonObject.put("writingId", strings[5]);
                    jsonObject.put("area", strings[6]);
                    if (!strings[7].equals("")) {
                        jsonObject.put("couponCode", strings[7]);
                    } else if (!strings[8].equals("")) {
                        jsonObject.put("cardCode", strings[8]);
                    }
                }
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                if (activity.pay_way == 1) {
                    activity.analyzeWXUnifiedOrderData(s);
                } else if (activity.pay_way == 2) {
                    activity.analyzeAliUnifiedOrderData(s);
                }
            }
            String string = activity.tv_sure.getText().toString();
            string = string.replace("，正在购买...", "");
            activity.tv_sure.setText(string);
            activity.tv_sure.setTextColor(Color.parseColor("#ffffff"));
            activity.isBuying = false;
        }
    }

    /**
     * 获取账单信息
     */
    private static class GetOrderData
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected GetOrderData(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("orderId", strings[1]);
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.errorOrderData();
            } else {
                activity.analyzeOrderData(s);
            }
        }
    }

    private void analyzeOrderData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                int status = object.optInt("status", -1);
                if (status == 1) {
                    submitSuccess(out_trade_no);
                } else {
                    if (index < 0) {  //5秒钟之后不再查询
                        showErrorView();
                    } else {
                        startSearchOrderInfo();
                    }
                }
            } else {
                errorOrderData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorOrderData();
        }
    }

    /**
     * 获取账单数据失败
     */
    private void errorOrderData() {
        isBuying = false;
        if (index < 0) {
            if (isResume()) {
                if (dialog_loading != null) {
                    dialog_loading.dismiss();
                }
            } else {
                dialogNeedHide = true;
            }
            if (!tv_tips.getText().toString().contains("查询作文支付结果失败")) {
                tv_tips.append("\n\n查询作文支付结果失败，请先保存草稿以免数据丢失。\n" +
                        "批改支付结果，可到个人中心-钱包-交易记录中查看。");
            }
            String tips = tv_tips.getText().toString();
            SpannableStringBuilder ssb = new SpannableStringBuilder(tips);
            ForegroundColorSpan foregroundColorSpan =
                    new ForegroundColorSpan(Color.parseColor("#FF9933"));
            ssb.setSpan(foregroundColorSpan, 57, tips.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_tips.setText(ssb);
        }
    }

    /**
     * 获取作文价格
     */
    private static class GetWritingPrice
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected GetWritingPrice(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", strings[1]);
                if (strings[2].equals("")) {
                    jsonObject.put("couponCode", -1);
                } else {
                    jsonObject.put("couponCode", strings[2]);
                }
                if (strings[3].equals("")) {
                    jsonObject.put("cardCode", -1);
                } else {
                    jsonObject.put("cardCode", strings[3]);
                }
                jsonObject.put("isDiscount", strings[4]);
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.errorGetPrice();
            } else {
                activity.analyzeGetWritingPriceData(s);
            }
            activity.isCheckingPrice = false;
        }
    }

    /**
     * 获取微课价格
     */
    private static class GetLessonPrice
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected GetLessonPrice(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", NewMainActivity.STUDENT_ID);
                jsonObject.put("courseId", strings[1]);
                if (strings[2].equals("")) {
                    jsonObject.put("couponCode", -1);
                } else {
                    jsonObject.put("couponCode", strings[2]);
                }
                if (strings[3].equals("")) {
                    jsonObject.put("cardCode", -1);
                } else {
                    jsonObject.put("cardCode", strings[3]);
                }
                jsonObject.put("isDiscount", strings[4]);
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s == null) {
                activity.errorGetPrice();
            } else {
                activity.analyzeGetLessonPriceData(s);
            }
            activity.isCheckingPrice = false;
        }
    }

    /**
     * 获取提示信息
     */
    private static class GetTipsData
            extends WeakAsyncTask<String, Void, String, SubmitReviewActivity> {

        protected GetTipsData(SubmitReviewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(SubmitReviewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("contentNum", 1);
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
        protected void onPostExecute(SubmitReviewActivity activity, String s) {
            if (s != null) {
                activity.analyzeTipsData(s);
            }
        }
    }

    /**
     * 分析提示信息数据
     *
     * @param s
     */
    private void analyzeTipsData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                tv_tips.setText(object.getString("content"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
