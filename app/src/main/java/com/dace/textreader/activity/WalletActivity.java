package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.adapter.WalletAdapter;
import com.dace.textreader.bean.WalletDataBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 钱包
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener ,PullListener{

//    private static final String url = HttpUrlPre.HTTP_URL + "/account/query";

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
    private PullRecyclerView rcl_wallet;
    private WalletAdapter walletAdapter;
    private WalletDataBean.DataBean mData;

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
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        tv_record.setOnClickListener(this);
        rl_to_recharge.setOnClickListener(this);
        rl_card.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);

        walletAdapter.setOnCouponClick(new WalletAdapter.OnCouponClick() {
            @Override
            public void onClick() {
                Intent intent = new Intent(mContext, CouponActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void initView() {
        rcl_wallet = findViewById(R.id.rcl_wallet);
        walletAdapter = new WalletAdapter(mContext,mData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        rcl_wallet.setHeadRefreshView(new SimpleRefreshHeadView(mContext))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(walletAdapter);

//        rcl_wallet.setLayoutManager(layoutManager);


//        rcl_wallet.setAdapter(walletAdapter);
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

        getData();

    }

    private void getData() {
        String url = HttpUrlPre.HTTP_URL_ + "/select/my/wallet";
        JSONObject params = new JSONObject();
        try {
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("width","750");
            params.put("height","420");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(mContext).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                WalletDataBean walletDataBean = GsonUtil.GsonToBean(result.toString(),WalletDataBean.class);
                if(walletDataBean != null && walletDataBean.getData() != null){
                    mData = walletDataBean.getData();
                    walletAdapter.setData(mData);
                    rcl_wallet.onPullComplete();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
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
     * 无网络连接
     */
    private void noConnect() {
        showTips("无网络连接");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    public void onLoadMore() {

    }
}
