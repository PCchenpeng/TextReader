package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.CouponBean;
import com.dace.textreader.fragment.CouponFragment;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.tab.SmartTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 优惠券列表
 */
public class CouponActivity extends BaseActivity {

    private static final String exchangeCouponUrl = HttpUrlPre.HTTP_URL + "/coupon/exchange";
    private static final String imageUrl = HttpUrlPre.HTTP_URL + "/navigate/page?";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private EditText et_coupon;
    private TextView tv_coupon;

    private CouponActivity mContext;
    private ViewPagerAdapter viewPagerAdapter;
    private List<String> mList_title = new ArrayList<>();
    private List<Fragment> mList_fragment = new ArrayList<>();

    //兑换优惠券参数
    private List<CouponBean> mList_coupon = new ArrayList<>();
    private int index_coupon = 0;
    private String imagePath = "https://app.pythe.cn:446/image/app/popup_coupon_decoration.png";

    private CouponFragment couponUnusedFragment;
    private CouponFragment couponUsedFragment;
    private CouponFragment couponExpiredFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        mContext = this;

        initData();
        initView();
        initEvents();
    }

    private void initData() {
        mList_title.add("未使用");
        mList_title.add("已使用");
        mList_title.add("已失效");

        couponUnusedFragment = new CouponFragment();
        couponUnusedFragment.setStatus(1);
        mList_fragment.add(couponUnusedFragment);
        couponUsedFragment = new CouponFragment();
        couponUsedFragment.setStatus(2);
        mList_fragment.add(couponUsedFragment);
        couponExpiredFragment = new CouponFragment();
        couponExpiredFragment.setStatus(3);
        mList_fragment.add(couponExpiredFragment);

        new GetImagePath(mContext).execute(imageUrl + "name=exchange_coupon_image");
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_coupon.setCursorVisible(true);
            }
        });
        tv_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_coupon.getText().toString().trim().length() != 0) {
                    String code = et_coupon.getText().toString();
                    exchangeCoupon(code);
                }
            }
        });
        couponUnusedFragment.setOnCouponBuyLesson(new CouponFragment.OnCouponBuyLesson() {
            @Override
            public void onCouponBuyLesson(long lessonId) {
                buyLesson();
            }
        });
    }

    /**
     * 前往购买课程
     */
    private void buyLesson() {
        broadcastUpdate(HttpUrlPre.ACTION_BROADCAST_BUY_LESSON);

        Intent back = new Intent();
        back.putExtra("back", true);
        setResult(0, back);
        finish();
    }

    /**
     * 发送广播
     *
     * @param action 广播的Action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /**
     * 兑换优惠券
     */
    private void exchangeCoupon(String code) {
        try {
            JSONObject object = new JSONObject();
            object.put("studentId", NewMainActivity.STUDENT_ID);
            object.put("exchangeCode", code);
            String info = DataEncryption.encode(object.toString(),"C85A4c8d2G");
            new ExchangeCoupon(mContext).execute(exchangeCouponUrl, info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("优惠券");

        tabLayout = findViewById(R.id.tab_layout_coupon);
        viewPager = findViewById(R.id.view_pager_coupon);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setViewPager(viewPager);

        et_coupon = findViewById(R.id.et_exchange_coupon);
        tv_coupon = findViewById(R.id.tv_exchange_coupon);
        et_coupon.setCursorVisible(false);
    }

    /**
     * 分析兑换码兑换数据
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    CouponBean couponBean = new CouponBean();
                    couponBean.setId(object.optLong("id", -1));
                    couponBean.setCategory(object.optInt("category", -1));
                    couponBean.setType(object.optInt("type", -1));
                    couponBean.setTypeName(object.getString("typeName"));
                    couponBean.setTitle(object.getString("title"));
                    mList_coupon.add(couponBean);
                }
                et_coupon.setText("");
                showCouponExchangeDialog();
            } else if (700 == jsonObject.optInt("status", -1)) {
                showTips("该优惠套餐已兑换");
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    /**
     * 显示领取到的兑换券的对话框
     */
    private void showCouponExchangeDialog() {
        index_coupon = 0;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_coupon_exchange_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView imageView = holder.getView(R.id.iv_coupon_exchange_dialog);
                        final TextView tv_content = holder.getView(R.id.tv_content_coupon_exchange_dialog);
                        final TextView tv_title = holder.getView(R.id.tv_title_coupon_exchange_dialog);
                        final TextView tv_next = holder.getView(R.id.tv_next_coupon_exchange_dialog);
                        ImageView iv_close = holder.getView(R.id.iv_close_coupon_exchange_dialog);

                        GlideUtils.loadImageWithNoPlaceholder(mContext, imagePath, imageView);
                        if (mList_coupon.size() <= 1) {
                            tv_next.setVisibility(View.INVISIBLE);
                        } else {
                            tv_next.setVisibility(View.VISIBLE);
                        }
                        tv_content.setText(mList_coupon.get(index_coupon).getTitle());
                        tv_title.setText(mList_coupon.get(index_coupon).getTypeName());

                        tv_next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index = index_coupon + 1;
                                tv_content.setText(mList_coupon.get(index).getTitle());
                                tv_title.setText(mList_coupon.get(index).getTypeName());
                                if (index == mList_coupon.size() - 1) {
                                    tv_next.setVisibility(View.INVISIBLE);
                                } else {
                                    index_coupon = index;
                                }
                            }
                        });
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (couponUnusedFragment != null && couponUnusedFragment.isReady) {
                                    couponUnusedFragment.initData();
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setMargin(0)
                .setOutCancel(false)
                .setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    /**
     * 领取失败
     */
    private void errorData() {
        showTips("兑换码无效");
    }

    /**
     * 无网络连接
     */
    private void noConnect() {
        showTips("无网络连接，请连接网络后重试");
    }

    /**
     * 显示提示信息
     */
    private void showTips(String tip) {
        MyToastUtil.showToast(mContext, tip);
    }

    /**
     * 兑换优惠券
     */
    private static class ExchangeCoupon
            extends WeakAsyncTask<String, Void, String, CouponActivity> {

        protected ExchangeCoupon(CouponActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CouponActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("info", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .post(body)
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CouponActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取图片路径
     */
    private static class GetImagePath
            extends WeakAsyncTask<String, Integer, String, CouponActivity> {

        protected GetImagePath(CouponActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CouponActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CouponActivity activity, String s) {
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        activity.imagePath = jsonObject.getString("data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 适配器
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mList_title.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mList_fragment.get(position);
        }

        @Override
        public int getCount() {
            return mList_fragment.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }
}
