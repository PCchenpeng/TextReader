package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.bean.CardDetailBean;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.bean.PayResult;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebViewActivity extends BaseActivity {
    private int pay_way;
    private String url;
    private static final String wxUrl = HttpUrlPre.HTTP_URL + "/card/vip/purchase/wxpay";
    private static final String aliUrl = HttpUrlPre.HTTP_URL + "/card/vip/purchase/alipay";
    private static final String cardDetailUrl = HttpUrlPre.HTTP_URL + "/card/vip";
    private String params;
    private BridgeWebView mWebview,mWebview_after;
    private FrameLayout frameLayout;
    private TextView tv_original_price;
    private TextView tv_price;
    private String cardId = "";  //卡ID
    private String code = "";  //优惠码
    private boolean isTurnToWx = false;

    private String timeStamp;
    private String out_trade_no;
    private String paySign;
    private String nonceStr;
    private String prepay_id;

    private String paramCode;
    private String paramContentHtml;
    private static final int REQUEST_CODE_LOGIN = 1;


    private boolean activated;
    private boolean isDiscount;

    private double card_post_price;
    private double card_original_price;

    private LinearLayout ll_price;

    private long id;
    private RelativeLayout rl_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initData();
        initView();
    }

    private void initData() {

        url = getIntent().getStringExtra("url");
//        url = "http://192.168.50.177:8848/test/new_file1.html";
        try {
            JSONObject urlObj = new JSONObject(url);
            url = urlObj.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cardId  = getIntent().getStringExtra("cardId");
        code  = getIntent().getStringExtra("code");
        activated = getIntent().getBooleanExtra("activated",false);
        isDiscount = getIntent().getBooleanExtra("activated",isDiscount);
        card_post_price  = getIntent().getDoubleExtra("card_post_price",0.0);
        card_original_price  = getIntent().getDoubleExtra("card_original_price",0.0);
        params = getIntent().getStringExtra("params");
        paramCode = getIntent().getStringExtra("paramCode");
        paramContentHtml = getIntent().getStringExtra("paramContentHtml");
        id = getIntent().getLongExtra("id",0);
    }

    private void initView() {

        mWebview = findViewById(R.id.webView);
        mWebview_after = findViewById(R.id.webView_after);
        frameLayout = findViewById(R.id.frame_member_centre);
        tv_original_price = findViewById(R.id.tv_original_price_member_centre);
        tv_price = findViewById(R.id.tv_price_member_centre);
        rl_back = findViewById(R.id.rl_back);
        tv_original_price.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        setWebview();
        mWebview.loadUrl(url);
        initEvents();

        ll_price = findViewById(R.id.ll_price_member_centre);
        ll_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    showPayWayDialog();
                }
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (activated) {
            ll_price.setVisibility(View.GONE);
        } else {
            ll_price.setVisibility(View.VISIBLE);
        }

        if (isDiscount) {
            String str1 = "原价：" + DataUtil.double2String(card_post_price) + "元";
            tv_original_price.setText(str1);
            tv_original_price.setVisibility(View.VISIBLE);
            String str2 = "优惠支付" + DataUtil.double2String(card_original_price) + "元";
            tv_price.setText(str2);
        } else {
            String post = " 原价" + DataUtil.double2String(card_post_price) + " ";
            String string = post + "    限时特惠¥" + DataUtil.double2String(card_original_price)
                    + "立即开通";
            tv_original_price.setVisibility(View.GONE);
            SpannableString ss = new SpannableString(string);
            ss.setSpan(new RelativeSizeSpan(0.9f), 0, post.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StrikethroughSpan(), 0, post.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.NORMAL), 0, post.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_price.setText(ss);
        }

    }

    private void initEvents() {
        //必须和js同名函数，注册具体执行函数，类似java实现类。
        //第一参数是订阅的java本地函数名字 第二个参数是回调Handler , 参数返回js请求的resqustData,function.onCallBack（）回调到js，调用function(responseData)
//        mWebview.registerHandler("vipCode", new BridgeHandler() {
//
//
//
//
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                Log.e("webview", "指定Handler接收来自web的数据：" + data);
//                function.onCallBack("指定Handler收到Web发来的数据，回传数据给你");
//            }
//        });

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code",paramCode);
            jsonObject.put("contentHtml",paramContentHtml);
            jsonObject.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        mWebview.callHandler("vipCode",jsonObject.toString(),new CallBackFunction(){
//            @Override
//            public void onCallBack(String data) {
//                Log.e("webview123", "来自web的回传数据：" + data);
//            }
//        });
        mWebview.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e("webview1122", "DefaultHandler接收全部来自web的数据："+data);
                function.onCallBack("派知语文");
            }
        });




        mWebview.callHandler("vipCode", jsonObject.toString(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
//                showTips(data);

            }
        });


    }


    private void setWebview(){
//        mWebview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message,
//                                     JsResult result) {
//                // TODO Auto-generated method stub
//                return super.onJsAlert(view, url, message, result);
//            }
//
//        });
//
//        mWebview_after.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message,
//                                     JsResult result) {
//                // TODO Auto-generated method stub
//                return super.onJsAlert(view, url, message, result);
//            }
//
//        });
    }



    /**
     * 选择支付方式
     */
    private void showPayWayDialog() {
        pay_way = 0;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_pay_way_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        LinearLayout ll_wechat = holder.getView(R.id.ll_wechat_pay_way_dialog);
                        final ImageView iv_wechat = holder.getView(R.id.iv_wechat_pay_way_dialog);
                        LinearLayout ll_ali = holder.getView(R.id.ll_ali_pay_way_dialog);
                        final ImageView iv_ali = holder.getView(R.id.iv_ali_pay_way_dialog);
                        LinearLayout ll_pay = holder.getView(R.id.ll_price_pay_way_dialog);
                        TextView tv_o_price_dialog = holder.getView(R.id.tv_original_price_pay_way_dialog);
                        TextView tv_price_dialog = holder.getView(R.id.tv_price_pay_way_dialog);

//                        if (isDiscount) {
                            String str1 = "原价：" + DataUtil.double2String(card_post_price) + "元";
                            tv_o_price_dialog.setText(str1);
                            tv_o_price_dialog.setVisibility(View.VISIBLE);
                            String str2 = "优惠支付" + DataUtil.double2String(card_original_price) + "元";
                            tv_price_dialog.setText(str2);
//                        } else {
//                            String string = "确认支付" + DataUtil.double2String(card_original_price) + "元";
//                            tv_o_price_dialog.setVisibility(View.GONE);
//                            tv_price_dialog.setText(string);
//                        }


                        ll_wechat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pay_way != 0) {
                                    pay_way = 0;
                                    iv_ali.setImageResource(R.drawable.icon_edit_unselected);
                                    iv_wechat.setImageResource(R.drawable.icon_edit_selected);
                                }
                            }
                        });
                        ll_ali.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pay_way != 1) {
                                    pay_way = 1;
                                    iv_wechat.setImageResource(R.drawable.icon_edit_unselected);
                                    iv_ali.setImageResource(R.drawable.icon_edit_selected);
                                }
                            }
                        });
                        ll_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NewMainActivity.STUDENT_ID == -1) {
                                    turnToLogin();
                                } else {
                                    if (pay_way == 0) {
                                        getWechatPayInfo();
                                    } else if (pay_way == 1) {
                                        getAliPayInfo();
                                    }
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取支付宝支付信息
     */
    private void getAliPayInfo() {
        showLoadingView(true);
        new AliUnifiedOrder(this).execute(aliUrl, code,
                String.valueOf(NewMainActivity.STUDENT_ID), cardId);
    }

    /**
     * 获取微信支付信息
     */
    private void getWechatPayInfo() {
        showLoadingView(true);
        new WXUnifiedOrder(this).execute(wxUrl, code,
                String.valueOf(NewMainActivity.STUDENT_ID), cardId, "APP", "");
    }

    /**
     * 显示正在加载
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(this).inflate(R.layout.view_author_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
            GlideUtils.loadGIFImageWithNoOptions(this, R.drawable.image_loading, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    /**
     * 支付宝统一下单
     */
    private static class AliUnifiedOrder
            extends WeakAsyncTask<String, Void, String, WebViewActivity> {

        public AliUnifiedOrder(WebViewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WebViewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (!strings[1].equals("")) {
                    object.put("code", strings[1]);
                }
                object.put("studentId", strings[2]);
                object.put("cardId", strings[3]);
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
        protected void onPostExecute(WebViewActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeAliUnifiedOrderData(s);
            }
        }
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        showTips("获取充值数据失败，请连接网络后重试");
    }

    /**
     * 显示吐丝
     *
     * @param tips
     */
//    private void showTips(String tips) {
//        MyToastUtil.showToast(this, tips);
//    }

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
            noConnect();
        }
    }

    /**
     * 调起支付宝支付
     */
    private void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(WebViewActivity.this);
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
                        checkResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        errorPay();
                    }
                    break;
                }
            }
        }
    };

    /**
     * 支付失败
     */
    private void errorPay() {
        showLoadingView(false);
        TipsUtil tipsUtil = new TipsUtil(this);
        tipsUtil.showPayFailedView(frameLayout, "");
    }



    /**
     * 前往登录
     */
    private void turnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }


    /**
     * 微信统一下单
     */
    private static class WXUnifiedOrder
            extends WeakAsyncTask<String, Void, String, WebViewActivity> {

        public WXUnifiedOrder(WebViewActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WebViewActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (!strings[1].equals("")) {
                    object.put("code", strings[1]);
                }
                object.put("studentId", strings[2]);
                object.put("cardId", strings[3]);
                object.put("trade_type", strings[4]);
                object.put("spbill_create_ip", strings[5]);
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
        protected void onPostExecute(WebViewActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeWXUnifiedOrderData(s);
            }
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
            noConnect();
        }
    }

    /**
     * 调起微信支付
     */
    private void wxRecharge() {
        isTurnToWx = true;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (isTurnToWx) {
            isTurnToWx = false;
            checkResult();
        }
    }

    private void checkResult(){
        ll_price.setVisibility(View.GONE);
        mWebview_after.setVisibility(View.VISIBLE);
        mWebview.setVisibility(View.GONE);
        loadCardDetailData(cardId);
        EventBus.getDefault().post(new MessageEvent("buy_card_success"));
    }


    private void loadCardDetailData(String cardId) {
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("cardId",cardId);
            params.put("width",DensityUtil.getScreenWidth(this));
            params.put("height",DensityUtil.getScreenWidth(this)*194/345);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(cardDetailUrl, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        showLoadingView(false);
                        CardDetailBean cardDetailBean = GsonUtil.GsonToBean(result.toString(),CardDetailBean.class);
                        String payUrl = "";
                        for (int i=0;i <cardDetailBean.getData().getFunctionRecord().size();i++){
                            if(id == cardDetailBean .getData().getFunctionRecord().get(i).getCardId())
                            payUrl = cardDetailBean .getData().getFunctionRecord().get(i).getSituation().toString();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(payUrl);
                            String url = jsonObject.getString("link");
                            paramCode = jsonObject.getString("code");
                            paramContentHtml = jsonObject.getString("contentHtml");
                            mWebview_after.loadUrl(url);

                            JSONObject json = new JSONObject();
                            json.put("code",paramCode);
                            json.put("contentHtml",paramContentHtml);
                            json.put("studentId",NewMainActivity.STUDENT_ID);
                            mWebview_after.callHandler("vipCode", json.toString(), new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                    }
                });
    }

}
