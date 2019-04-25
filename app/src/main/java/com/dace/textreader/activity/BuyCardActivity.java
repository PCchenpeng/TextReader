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
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 购买卡包
 */
public class BuyCardActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/select/compostion/card/list";
    //微信统一下单
    private static final String wxUrl = HttpUrlPre.HTTP_URL + "/wxpay/purchase/card";
    //支付宝统一下单
    private static final String aliUrl = HttpUrlPre.HTTP_URL + "/alipay/purchase/card";
    //账单查询
    private static final String orderUrl = HttpUrlPre.HTTP_URL + "/bill/query/detail";

    private static final int WECHAT_PAY_OPTIONS = 1;  //微信支付方式
    private static final int ALI_PAY_OPTIONS = 2;  //支付宝支付方式

    private LinearLayout rl_root;
    private RelativeLayout rl_back;
    private TextView tv_title;

    private ImageView iv_buy_card;
    private RecyclerView recyclerView;
    private LinearLayout ll_wechat;
    private ImageView iv_wechat;
    private LinearLayout ll_ali;
    private ImageView iv_ali;
    private TextView tv_tip;
    private TextView tv_commit;

    private BuyCardActivity mContext;

    private long id = -1;
    private String imagePath = "";
    private String tips;
    private List<RechargeBean> mList = new ArrayList<>();
    private RechargeRecyclerViewAdapter adapter;

    private int mSelectedPosition = -1;  //已选择的充值选项
    private int status = -1;  //当前状态，1为正常，2为正在提交数据，0为无网络，-1为获取数据失败

    private int pay_way = 1;  //支付方式

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
    private double price;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_card);

        mContext = this;

        id = getIntent().getLongExtra("id", -1);

        initView();
        initData();
        initEvents();
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

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter.setOnItemClickListener(new RechargeRecyclerViewAdapter.OnRechargeItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);

                if (mSelectedPosition != -1) {
                    //不管在不在屏幕里 都需要改变数据
                    mList.get(mSelectedPosition).setSelected(false);
                    adapter.notifyItemChanged(mSelectedPosition);
                }

                //设置新Item的勾选状态
                mSelectedPosition = pos;
                mList.get(mSelectedPosition).setSelected(true);
                adapter.notifyItemChanged(mSelectedPosition);

                updateCommitButton();
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 1) {
                    if (pay_way == 1) {
                        payForWechat();
                    } else if (pay_way == 2) {
                        payForAli();
                    }
                } else if (status == 2) {
                    showTips("正在提交，请稍等...");
                } else if (status == 0) {
                    initData();
                }
            }
        });
        ll_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePaymentWay(WECHAT_PAY_OPTIONS);
            }
        });
        ll_ali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePaymentWay(ALI_PAY_OPTIONS);
            }
        });
    }

    /**
     * 支付宝支付
     */
    private void payForAli() {
        if (mSelectedPosition == -1) {
            showTips("请选择要充值的选项");
        } else {
            status = 2;
            showTips("支付中...");
            long id = mList.get(mSelectedPosition).getId();
            price = mList.get(mSelectedPosition).getPrice();
            content = mList.get(mSelectedPosition).getProductName();
            new AliUnifiedOrder(mContext).execute(aliUrl, String.valueOf(id));
        }
    }

    /**
     * 选择支付方式
     *
     * @param options
     */
    private void choosePaymentWay(int options) {
        if (options != pay_way) {
            pay_way = options;
            if (options == WECHAT_PAY_OPTIONS) {
                iv_wechat.setImageResource(R.drawable.icon_pay_options_selected);
                iv_ali.setImageResource(R.drawable.icon_edit_unselected);
            } else {
                iv_ali.setImageResource(R.drawable.icon_pay_options_selected);
                iv_wechat.setImageResource(R.drawable.icon_edit_unselected);
            }
        }
    }

    /**
     * 更新确认按钮
     */
    private void updateCommitButton() {
        if (mSelectedPosition != -1) {
            double price = mList.get(mSelectedPosition).getPrice();
            String text = "确认支付" + DataUtil.double2String(price) + "元";
            tv_commit.setText(text);
        }
    }

    /**
     * 用微信支付
     */
    private void payForWechat() {
        if (!App.api.isWXAppInstalled()) {
            showTips("您还未安装微信客户端");
            return;
        }
        if (mSelectedPosition == -1) {
            showTips("请选择要充值的选项");
        } else {
            status = 2;
            showTips("支付中...");
            long id = mList.get(mSelectedPosition).getId();
            price = mList.get(mSelectedPosition).getPrice();
            content = mList.get(mSelectedPosition).getProductName();
            String ip = Utils.getLocalIpAddress();
            new WXUnifiedOrder(mContext).execute(wxUrl, String.valueOf(id), "APP", ip);
        }
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

    private void initData() {
        new GetData(mContext).execute(url, String.valueOf(id));
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_buy_card);
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("卡包购买");

        iv_buy_card = findViewById(R.id.iv_buy_card);
        recyclerView = findViewById(R.id.recycler_view_buy_card);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2,
                GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RechargeRecyclerViewAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
        ll_wechat = findViewById(R.id.ll_pay_way_wechat_buy_card);
        iv_wechat = findViewById(R.id.iv_wechat_status_buy_card);
        ll_ali = findViewById(R.id.ll_pay_way_ali_buy_card);
        iv_ali = findViewById(R.id.iv_ali_status_buy_card);
        tv_tip = findViewById(R.id.tv_tips_buy_card);
        tv_commit = findViewById(R.id.tv_commit_buy_card);

        int width = DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 30);
        int height = (int) (width * 0.5625);
        ViewGroup.LayoutParams layoutParams = iv_buy_card.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;

    }

    /**
     * 购买成功
     */
    private void submitSuccess() {
        showTips("购买成功");
        status = 1;
        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }
        turnToOperateResult();
    }

    /**
     * 前往显示操作结果
     */
    private void turnToOperateResult() {
        Intent intent = new Intent(mContext, OperationResultActivity.class);
        intent.putExtra("operateType", "buy_card");
        intent.putExtra("isSuccessful", true);
        intent.putExtra("content", content);
        intent.putExtra("price", price);
        intent.putExtra("pay_way", 1);
        intent.putExtra("orderNum", out_trade_no);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1) {
                boolean submit = data.getBooleanExtra("submit", false);
                if (submit) {
                    Intent intent = new Intent();
                    intent.putExtra("submit", true);
                    setResult(0, intent);
                    finish();
                }
            }
        }
    }

    /**
     * 列表数据分析
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                imagePath = object.getString("topImage");
                tips = object.getString("tip");
                JSONArray array = object.getJSONArray("cards");
                if (array.length() == 0) {
                    errorData();
                } else {
                    mList.clear();
                    for (int i = 0; i < array.length(); i++) {
                        RechargeBean rechargeBean = new RechargeBean();
                        JSONObject json = array.getJSONObject(i);
                        rechargeBean.setId(json.optLong("id", -1));
                        int frequency = json.optInt("frequency", 0);
                        String content = String.valueOf(frequency) + "次/月";
                        rechargeBean.setContent(content);
                        double price = json.optDouble("discountPrice", 0);
                        rechargeBean.setPrice(price);
                        String title = DataUtil.double2String(price) + "元";
                        rechargeBean.setTitle(title);
                        rechargeBean.setProductName(json.getString("title"));
                        rechargeBean.setSelected(false);
                        mList.add(rechargeBean);
                    }
                    updateUi();
                }
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 更新界面
     */
    private void updateUi() {
        mList.get(0).setSelected(true);
        mSelectedPosition = 0;
        adapter.notifyDataSetChanged();
        status = 1;
        tv_tip.setText(tips);
        GlideUtils.loadImageWithNoPlaceholder(mContext, imagePath, iv_buy_card);
    }

    /**
     * 获取数据失败
     */
    private void errorData() {
        status = -1;
        tv_commit.setText("获取数据失败，请稍后再试");
        showTips("获取数据失败，请稍后再试");
    }

    /**
     * 无网络
     */
    private void noConnect() {
        status = 0;
        tv_commit.setText("无网络，请连接网络后重试");
        showTips("无网络，请连接网络后重试");
    }

    /**
     * 获取列表数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, BuyCardActivity> {

        protected GetData(BuyCardActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BuyCardActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("type", "android");
                object.put("recommend_card_id", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(BuyCardActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 微信统一下单接口
     */
    private static class WXUnifiedOrder
            extends WeakAsyncTask<String, Void, String, BuyCardActivity> {

        protected WXUnifiedOrder(BuyCardActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BuyCardActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("cardId", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("trade_type", strings[2]);
                object.put("spbill_create_ip", strings[3]);
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
        protected void onPostExecute(BuyCardActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeWXUnifiedOrderData(s);
            }
            activity.status = 1;
        }
    }

    /**
     * 获取账单信息
     */
    private static class GetOrderData
            extends WeakAsyncTask<String, Void, String, BuyCardActivity> {

        protected GetOrderData(BuyCardActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BuyCardActivity activity, String[] strings) {
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
        protected void onPostExecute(BuyCardActivity activity, String s) {
            if (s == null) {
                activity.errorOrderData();
            } else {
                activity.analyzeOrderData(s);
            }
        }
    }

    /**
     * 分析订单信息
     *
     * @param s
     */
    private void analyzeOrderData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                int status = object.optInt("status", -1);
                if (status == 1) {
                    submitSuccess();
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
                        turnToOperateResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        status = 1;
                        showTips("支付失败");
                    }
                    break;
            }
        }
    };

    /**
     * 显示支付失败视图
     */
    private void showErrorView() {
        status = 1;
        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }
        TipsUtil tipsUtil = new TipsUtil(mContext);
        tipsUtil.showPayFailedView(rl_root, "");
    }

    /**
     * 获取账单数据失败
     */
    private void errorOrderData() {
        status = 1;
        if (dialog_loading != null) {
            dialog_loading.dismiss();
        }
        showTips("获取账单数据失败");
    }

    /**
     * 支付宝统一下单
     */
    private static class AliUnifiedOrder
            extends WeakAsyncTask<String, Integer, String, BuyCardActivity> {

        public AliUnifiedOrder(BuyCardActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(BuyCardActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("cardId", strings[1]);
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
        protected void onPostExecute(BuyCardActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeAliUnifiedOrderData(s);
            }
            activity.status = 1;
        }
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
            status = 1;
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

}
