package com.dace.textreader.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/bill/query/detail";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_product;
    private TextView tv_status;
    private ImageView iv_status;
    private ImageView iv_product;
    private TextView tv_name;
    private TextView tv_price;
    private TextView tv_number;
    private TextView tv_copy;
    private TextView tv_time;
    private TextView tv_pay_way;
    private LinearLayout ll_coupon;
    private TextView tv_coupon;
    private TextView tv_pay;
    private TextView tv_pay_who;
    private LinearLayout ll_phone;
    private LinearLayout ll_comment;
    private TextView tv_comment;
    private LinearLayout ll_info;
    private LinearLayout ll_operate;
    private LinearLayout ll_actually_paid;
    private TextView tv_actually_paid;

    private OrderDetailActivity mContext;

    private String orderNum = "";
    private String name = "";
    private String productPrice = "";
    private String discountPrice = "";
    private int status = -1;
    private int category = -1;
    private String time = "";
    private int remark = 0;
    private String appraise = "";
    private String payChannel = "";
    private String hotline = "";
    private String imagePath = "";
    private String discountType = "";
    private String discountName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        mContext = this;

        orderNum = getIntent().getStringExtra("orderNum");

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
        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyContent();
            }
        });
        ll_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneNumberDialog();
            }
        });
        ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 1) {
                    turnToOrderAppraise();
                }
            }
        });
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
                        tv_number.setText(hotline);
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                call(hotline);
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
     * 前往订单评价
     */
    private void turnToOrderAppraise() {
        Intent intent = new Intent(mContext, OrderAppraiseActivity.class);
        intent.putExtra("orderNumber", orderNum);
        intent.putExtra("score", remark);
        intent.putExtra("appraise", appraise);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 0) {
                appraise = data.getStringExtra("appraise");
                remark = data.getIntExtra("score", 0);
                if (remark == 0) {
                    tv_comment.setText("去评价");
                } else {
                    String score = remark + "星";
                    tv_comment.setText(score);
                }
            }
        }
    }

    /**
     * 复制内容到截切板
     */
    private void copyContent() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("Label", orderNum);
        if (cm != null) {
            cm.setPrimaryClip(cd);
            MyToastUtil.showToast(mContext, "复制成功");
        }
    }

    private void initData() {
        tv_status.setText("获取数据中...");
        GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_status);
        new GetData(mContext).execute(url, orderNum);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("订单详情");

        rl_product = findViewById(R.id.rl_product_order_detail);
        iv_product = findViewById(R.id.iv_product_order_detail);
        tv_name = findViewById(R.id.tv_product_order_detail);
        tv_price = findViewById(R.id.tv_price_order_detail);
        tv_status = findViewById(R.id.tv_status_order_detail);
        iv_status = findViewById(R.id.iv_status_order_detail);
        tv_number = findViewById(R.id.tv_order_number_order_detail);
        tv_copy = findViewById(R.id.tv_copy_order_detail);
        tv_time = findViewById(R.id.tv_time_order_detail);
        tv_pay_way = findViewById(R.id.tv_pay_way_order_detail);
        ll_coupon = findViewById(R.id.ll_coupon_order_detail);
        tv_coupon = findViewById(R.id.tv_coupon_order_detail);
        tv_pay = findViewById(R.id.tv_pay_order_detail);
        tv_pay_who = findViewById(R.id.tv_pay_who_order_detail);
        ll_phone = findViewById(R.id.ll_call_phone_order_detail);
        ll_comment = findViewById(R.id.ll_comment_order_detail);
        tv_comment = findViewById(R.id.tv_comment_order_detail);
        ll_info = findViewById(R.id.ll_info_order_detail);
        ll_operate = findViewById(R.id.ll_operate_order_detail);

        ll_actually_paid = findViewById(R.id.ll_actually_paid_order_detail);
        tv_actually_paid = findViewById(R.id.tv_actually_paid_order_detail);
    }

    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                orderNum = object.getString("orderId");
                name = object.getString("productName");
                productPrice = object.getString("productPrice");
                discountPrice = object.getString("discountPrice");
                status = object.optInt("status", -1);
                category = object.optInt("category", 4);
                String t = object.getString("updateTime");
                if (t.equals("null") || t.equals("")) {
                    time = "2018-01-01 00:00";
                } else {
                    time = DateUtil.time2YMD(t);
                }
                remark = object.optInt("score", 0);
                if (object.getString("comment").equals("")
                        || object.getString("comment").equals("null")) {
                    appraise = "";
                } else {
                    appraise = object.getString("comment");
                }
                payChannel = object.getString("payChannel");
                hotline = object.getString("hotline");
                imagePath = object.getString("pic");
                discountType = object.getString("discountType");
                discountName = object.getString("discountName");
                updateUi();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    private void updateUi() {
        if (!isDestroyed()) {
            if (status == 1) {
                tv_status.setText("交易成功");
                GlideUtils.loadImageWithNoOptions(mContext, R.drawable.icon_order_state_succeed, iv_status);
            } else if (status == 3) {
                tv_status.setText("退款成功");
                GlideUtils.loadImageWithNoOptions(mContext, R.drawable.icon_order_state_succeed, iv_status);
            } else {
                tv_status.setText("交易失败");
                GlideUtils.loadImageWithNoOptions(mContext, R.drawable.icon_order_state_fail, iv_status);
            }
        }
        RequestOptions options;
        if (category == 0) {
            //派豆
            options = new RequestOptions()
                    .placeholder(R.drawable.icon_order_product_recharge)
                    .error(R.drawable.icon_order_product_recharge);
        } else if (category == 1) {
            //作文
            options = new RequestOptions()
                    .placeholder(R.drawable.icon_order_product_composition)
                    .error(R.drawable.icon_order_product_composition);
        } else if (category == 2) {
            //微课
            options = new RequestOptions()
                    .placeholder(R.drawable.image_micro_lesson_default)
                    .error(R.drawable.image_micro_lesson_default)
                    .centerCrop();
        } else {
            options = new RequestOptions()
                    .placeholder(R.drawable.icon_order_product_recharge)
                    .error(R.drawable.icon_order_product_recharge);
        }
        if (!isDestroyed()) {
            Glide.with(mContext)
                    .load(imagePath)
                    .apply(options)
                    .into(iv_product);
        }
        tv_name.setText(name);
        tv_number.setText(orderNum);
        tv_price.setText(discountPrice);
        tv_time.setText(time);
        if (payChannel.equals("0")) {
            tv_pay_way.setText("微信");
        } else if (payChannel.equals("1")) {
            tv_pay_way.setText("支付宝");
        } else if (payChannel.equals("2")) {
            tv_pay_way.setText("公众号");
        } else if (payChannel.equals("3")) {
            tv_pay_way.setText("ios内购");
        } else {
            tv_pay_way.setText("派知钱包");
        }
        if (category == 3) {
            tv_pay.setText("支付方");
        }
        tv_pay_who.setText("广州大策科技有限公司");
        if (remark == 0) {
            tv_comment.setText("去评价");
        } else {
            String score = remark + "星";
            tv_comment.setText(score);
        }

        if (discountType.equals("") || discountType.equals("null") ||
                discountName.equals("") || discountName.equals("null")) {
            ll_coupon.setVisibility(View.GONE);
        } else {
            tv_coupon.setText(discountName);
            ll_coupon.setVisibility(View.VISIBLE);
        }

        if (payChannel.equals("0") || payChannel.equals("1")
                || payChannel.equals("2") || payChannel.equals("3")) {
            tv_actually_paid.setText(productPrice);
            ll_actually_paid.setVisibility(View.VISIBLE);
        } else {
            ll_actually_paid.setVisibility(View.GONE);
        }

        rl_product.setVisibility(View.VISIBLE);
        ll_info.setVisibility(View.VISIBLE);
        ll_operate.setVisibility(View.VISIBLE);
        if (status != 1 || category == 0 || category == 3) {
            ll_comment.setVisibility(View.GONE);
        }
    }

    private void errorData() {
        if (isDestroyed()) {
            return;
        }
        tv_status.setText("获取数据失败，请稍后重试");
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, iv_status);
    }

    private static class GetData
            extends WeakAsyncTask<String, Void, String, OrderDetailActivity> {

        protected GetData(OrderDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(OrderDetailActivity activity, String[] strings) {
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
        protected void onPostExecute(OrderDetailActivity activity, String s) {
            if (s == null) {
                activity.errorData();
            } else {
                activity.analyzeData(s);
            }
        }
    }

}
