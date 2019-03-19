package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.adapter.RechargeRecyclerViewAdapter;
import com.dace.textreader.bean.PayResult;
import com.dace.textreader.bean.RechargeBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.Utils;
import com.dace.textreader.util.WeakAsyncTask;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 派豆充值
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    private static final String url = HttpUrlPre.HTTP_URL + "/combo/detail";
    private static final String wxRechargeUrl = HttpUrlPre.HTTP_URL + "/app/account/charge";
    private static final String aliRechargeUrl = HttpUrlPre.HTTP_URL + "/app/account/charge/alipay";

    private static final int WECHAT_PAY_OPTIONS = 1;  //微信支付方式
    private static final int ALI_PAY_OPTIONS = 2;  //支付宝支付方式

    private RelativeLayout rl_back;
    private TextView tv_title;

    private RelativeLayout rl_loading;

    private RecyclerView recyclerView;
    private LinearLayout ll_wechat;
    private ImageView iv_wechat;
    private LinearLayout ll_ali;
    private ImageView iv_ali;
    private Button btn_recharge;
    private TextView tv_service_agreement;

    private RechargeActivity mContext;

    /**
     * 微信支付相关
     */
    private String timeStamp = "";  //时间戳
    private String out_trade_no = "";
    private String paySign = "";  //支付签名
    private String nonceStr = "";  //随机字符串
    private String prepay_id = "";  //预支付ID

    private RechargeRecyclerViewAdapter adapter;
    private List<RechargeBean> mList = new ArrayList<>();
    private int rechargePosition = -1;  //当前选中的充值列表

    private int pay_options = 1;  //支付方式，默认微信支付，2是支付宝支付

    private boolean recharging = false;

    public static boolean isPaymentSuccessful = false;  //是否支付成功
    private boolean isGoToWXPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        mContext = this;

        initView();
        initData();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGoToWXPay) {
            rl_loading.setVisibility(View.GONE);
            if (isPaymentSuccessful) {
                showTips("充值成功");
                turnToSuccessful();
            } else {
                showTips("充值失败");
                isPaymentSuccessful = false;
                isGoToWXPay = false;
            }
            recharging = false;
        }
    }

    /**
     * 前往充值成功页
     */
    private void turnToSuccessful() {
        double price = mList.get(rechargePosition).getPrice();
        Intent intent = new Intent(mContext, OperationResultActivity.class);
        intent.putExtra("operateType", "recharge");
        intent.putExtra("isSuccessful", true);
        intent.putExtra("content", "充值");
        intent.putExtra("orderNum", out_trade_no);
        intent.putExtra("price", price);
        startActivity(intent);
        finish();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        adapter.setOnItemClickListener(new RechargeRecyclerViewAdapter.OnRechargeItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);

                if (rechargePosition != -1) {
                    //不管在不在屏幕里 都需要改变数据
                    mList.get(rechargePosition).setSelected(false);
                    adapter.notifyItemChanged(rechargePosition);
                }

                //设置新Item的勾选状态
                rechargePosition = pos;
                mList.get(rechargePosition).setSelected(true);
                adapter.notifyItemChanged(rechargePosition);
            }
        });
        ll_wechat.setOnClickListener(this);
        ll_ali.setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
        rl_loading.setOnClickListener(this);
        tv_service_agreement.setOnClickListener(this);
    }

    private void initData() {
        rl_loading.setVisibility(View.VISIBLE);
        new GetData(RechargeActivity.this).execute(url);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("充值");

        rl_loading = findViewById(R.id.rl_loading_recharge);

        recyclerView = findViewById(R.id.recycler_view_recharge);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RechargeRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        ll_wechat = findViewById(R.id.ll_wechat_pay_recharge);
        iv_wechat = findViewById(R.id.iv_wechat_pay_recharge);
        ll_ali = findViewById(R.id.ll_ali_pay_recharge);
        iv_ali = findViewById(R.id.iv_ali_pay_recharge);
        btn_recharge = findViewById(R.id.btn_recharge);
        tv_service_agreement = findViewById(R.id.tv_service_agreement_recharge);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_page_back_top_layout:
                finish();
                break;
            case R.id.ll_wechat_pay_recharge:
                choosePaymentWay(WECHAT_PAY_OPTIONS);
                break;
            case R.id.ll_ali_pay_recharge:
                choosePaymentWay(ALI_PAY_OPTIONS);
                break;
            case R.id.btn_recharge:
                if (!recharging) {
                    recharge();
                }
                break;
            case R.id.rl_loading_recharge:
                loading();
                break;
            case R.id.tv_service_agreement_recharge:
                turnToServiceAgreement();
                break;
        }
    }

    /**
     * 前往服务协议界面
     */
    private void turnToServiceAgreement() {
        Intent intent = new Intent(mContext, ServiceAgreementActivity.class);
        intent.putExtra("isRecharge", true);
        startActivity(intent);
    }

    /**
     * 等待中
     */
    private void loading() {

    }

    /**
     * 充值
     */
    private void recharge() {
        recharging = true;
        if (pay_options == WECHAT_PAY_OPTIONS) {
            wxUnifiedOrder();
        } else if (pay_options == ALI_PAY_OPTIONS) {
            aliUnifiedOrder();
        }
    }

    /**
     * 支付宝充值
     */
    private void aliUnifiedOrder() {
        if (rechargePosition == -1) {
            showTips("请选择要充值的选项");
        } else {
            rl_loading.setVisibility(View.VISIBLE);
            RechargeBean bean = mList.get(rechargePosition);
            long payId = bean.getId();

            new AliUnifiedOrder(mContext).execute(aliRechargeUrl,
                    String.valueOf(NewMainActivity.STUDENT_ID), String.valueOf(payId),
                    "APP", Utils.getLocalIpAddress());
        }
    }

    /**
     * 微信充值
     */
    private void wxUnifiedOrder() {
        if (!App.api.isWXAppInstalled()) {
            MyToastUtil.showToast(mContext, "您还未安装微信客户端");
            recharging = false;
            return;
        }
        if (rechargePosition == -1) {
            showTips("请选择要充值的选项");
            recharging = false;
        } else {
            rl_loading.setVisibility(View.VISIBLE);
            RechargeBean bean = mList.get(rechargePosition);
            long payId = bean.getId();

            new WXUnifiedOrder(mContext).execute(wxRechargeUrl,
                    "APP", Utils.getLocalIpAddress(), String.valueOf(NewMainActivity.STUDENT_ID),
                    String.valueOf(payId));
        }
    }

    /**
     * 选择支付方式
     *
     * @param options
     */
    private void choosePaymentWay(int options) {
        if (options != pay_options) {
            pay_options = options;
            if (options == WECHAT_PAY_OPTIONS) {
                iv_wechat.setImageResource(R.drawable.icon_pay_options_selected);
                iv_ali.setImageResource(R.drawable.icon_round_unselected);
            } else {
                iv_ali.setImageResource(R.drawable.icon_pay_options_selected);
                iv_wechat.setImageResource(R.drawable.icon_round_unselected);
            }
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        rl_loading.setVisibility(View.GONE);
        recharging = false;
        showTips("获取充值数据失败，请连接网络后重试");
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
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {  //充值列表为空
                    showTips("充值列表为空");
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RechargeBean rechargeBean = new RechargeBean();
                        JSONObject object = jsonArray.getJSONObject(i);
                        rechargeBean.setId(object.optLong("id", -1));
                        rechargeBean.setTitle(object.getString("title"));
                        rechargeBean.setContent(object.getString("content"));
                        rechargeBean.setStatus(object.optInt("status", -1));
                        rechargeBean.setPrice(object.getDouble("price"));
                        rechargeBean.setGiving(object.optInt("giving", -1));
                        rechargeBean.setSelected(false);
                        mList.add(rechargeBean);
                    }
                    mList.get(0).setSelected(true);
                    rechargePosition = 0;
                    adapter.notifyDataSetChanged();
                }
            } else {  //服务器返回异常
                showTips("服务器返回异常");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTips("服务器返回异常");
        }
        rl_loading.setVisibility(View.GONE);
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
            recharging = false;
        }
    }

    /**
     * 调起微信支付
     */
    private void wxRecharge() {
        isGoToWXPay = true;
        isPaymentSuccessful = false;
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
     * 支付宝支付统一下单数据
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
            recharging = false;
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    recharging = false;
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
                        turnToSuccessful();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        rl_loading.setVisibility(View.GONE);
                        showTips("支付失败");
                    }
                    break;
                }
            }
        }
    };

    /**
     * 显示提示
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取充值数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, RechargeActivity> {

        protected GetData(RechargeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(RechargeActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(RechargeActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 微信统一下单
     */
    private static class WXUnifiedOrder
            extends WeakAsyncTask<String, Integer, String, RechargeActivity> {

        public WXUnifiedOrder(RechargeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(RechargeActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("trade_type", strings[1]);
                object.put("spbill_create_ip", strings[2]);
                object.put("userId", strings[3]);
                object.put("packageId", strings[4]);
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
        protected void onPostExecute(RechargeActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeWXUnifiedOrderData(s);
            }
            activity.recharging = false;
        }
    }

    /**
     * 支付宝统一下单
     */
    private static class AliUnifiedOrder
            extends WeakAsyncTask<String, Integer, String, RechargeActivity> {

        public AliUnifiedOrder(RechargeActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(RechargeActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("userId", strings[1]);
                object.put("packageId", strings[2]);
                object.put("trade_type", strings[3]);
                object.put("spbill_create_ip", strings[4]);
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
        protected void onPostExecute(RechargeActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeAliUnifiedOrderData(s);
            }
            activity.recharging = false;
        }
    }

}
