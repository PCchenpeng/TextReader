package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 钱包
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/account/query";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_record;
    private TextView tv_record;
    private RelativeLayout rl_to_recharge;
    private ImageView iv_amount;
    private TextView tv_amount;
    private RelativeLayout rl_card;
    private TextView tv_card;
    private RelativeLayout rl_coupon;
    private TextView tv_coupon;

    private WalletActivity mContext;

    private double amount = 0;  //用户余额
    private int card = 0;  //卡包数量
    private int coupon = 0;  //优惠券数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mContext = this;

        initView();
        initData();
        initEvents();
        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_record.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        new GetData(mContext).execute(url);
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        tv_record.setOnClickListener(this);
        rl_to_recharge.setOnClickListener(this);
        rl_card.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("钱包");

        rl_record = findViewById(R.id.rl_transaction_record_wallet);
        tv_record = findViewById(R.id.tv_transaction_record_wallet);

        rl_to_recharge = findViewById(R.id.rl_to_recharge_wallet);
        iv_amount = findViewById(R.id.iv_amount_wallet);
        tv_amount = findViewById(R.id.tv_amount_wallet);
        tv_amount.setText(String.valueOf(amount));

        rl_card = findViewById(R.id.rl_card_wallet);
        tv_card = findViewById(R.id.tv_card_wallet);

        rl_coupon = findViewById(R.id.rl_coupon_wallet);
        tv_coupon = findViewById(R.id.tv_coupon_wallet);

        if (!isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .transform(new GlideRoundImage(mContext, 8));
            Glide.with(mContext).load(R.drawable.image_wallet_bg)
                    .apply(options).into(iv_amount);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.tv_transaction_record_wallet:
                startActivity(new Intent(mContext, TransactionRecordActivity.class));
                break;
            case R.id.rl_to_recharge_wallet:
                startActivity(new Intent(mContext, RechargeActivity.class));
                break;
            case R.id.rl_card_wallet:
                startActivity(new Intent(mContext, CardActivity.class));
                break;
            case R.id.rl_coupon_wallet:
                Intent intent = new Intent(mContext, CouponActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 0) {
                boolean back = data.getBooleanExtra("back", false);
                if (back) {
                    finish();
                }
            }
        }
    }

    /**
     * 分析数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                amount = object.getDouble("amount");
                card = object.optInt("cardSize", 0);
                coupon = object.optInt("couponSize", 0);
                tv_amount.setText(DataUtil.double2String(amount));
                if (card == 0) {
                    tv_card.setText("更多优惠");
                } else {
                    String ct = String.valueOf(card) + "张";
                    tv_card.setText(ct);
                }
                if (coupon == 0) {
                    tv_coupon.setText("更多优惠");
                } else {
                    String cs = String.valueOf(coupon) + "张";
                    tv_coupon.setText(cs);
                }
            } else {
                showTips("网络繁忙，请稍后再试");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("网络繁忙，请稍后再试");
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        showTips("无网络连接");
    }

    /**
     * 显示提示信息
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 提交数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, WalletActivity> {

        protected GetData(WalletActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WalletActivity activity, String[] strings) {
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
        protected void onPostExecute(WalletActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
