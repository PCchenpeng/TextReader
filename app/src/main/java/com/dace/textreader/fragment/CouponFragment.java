package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.binaryfork.spanny.Spanny;
import com.dace.textreader.App;
import com.dace.textreader.R;
import com.dace.textreader.activity.MicroLessonActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.WritingActivity;
import com.dace.textreader.adapter.CouponAdapter;
import com.dace.textreader.bean.CouponBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.Utils;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.RoundSpan;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.tencent.mm.opensdk.modelpay.PayReq;

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
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.fragment
 * Created by Administrator.
 * Created time 2018/8/24 0024 上午 9:04.
 * Version   1.0;
 * Describe :  优惠券
 * History:
 * ==============================================================================
 */

public class CouponFragment extends Fragment {

    private static final String url = HttpUrlPre.HTTP_URL + "/coupon/query";
    private static final String wxRechargeUrl = HttpUrlPre.HTTP_URL + "/app/account/charge";

    private View view;
    private FrameLayout frameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private Context mContext;
    private LinearLayoutManager layoutManager;

    private boolean available = false;  //是否可供选择
    private long id = -1;  //优惠券ID，用来默认选中
    private int status = 1;  //优惠券状态，1是未使用，2是已使用，3是过期
    private int category = -1;  //优惠券类型，-1所有  1 作文 2 微课
    private long courseId = -1;  //微课ID
    private String compositionId = "-1";  //作文ID

    private List<CouponBean> mList = new ArrayList<>();
    private CouponAdapter adapter;

    private int pageNum = 1;
    private boolean refreshing = false;
    private boolean isEnd = false;

    public boolean isReady = false;

    private boolean hidden = true;

    private int mSelectedPosition = -1;

    /**
     * 微信支付相关
     */
    private boolean recharging = false;  //是否正在支付
    private String timeStamp = "";  //时间戳
    private String out_trade_no = "";
    private String paySign = "";  //支付签名
    private String nonceStr = "";  //随机字符串
    private String prepay_id = "";  //预支付ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_coupon, container, false);

        initView();
        initEvents();

        isReady = true;

        return view;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public void setCompositionId(String compositionId) {
        this.compositionId = compositionId;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.hidden = !isVisibleToUser;
    }

    private void initEvents() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing) {
                    initData();
                }
            }
        });
        adapter.setOnItemClickListener(new CouponAdapter.OnCouponItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (!refreshing) {
                    int pos = recyclerView.getChildAdapterPosition(view);
                    if (mList.get(pos).getStatus() == 1) {
                        if (mList.get(pos).getIsActivated() == 0) {
                            showNeedActivatedDialog(pos);
                        } else {
                            if (available) {
                                updateList(pos);
                            } else {
                                int category = mList.get(pos).getCategory();
                                if (category != -1) {
                                    if (category == 1) {
                                        turnToWriting();
                                    } else if (category == 2) {
                                        if (mList.get(pos).getPrizeId().equals("") ||
                                                mList.get(pos).getPrizeId().equals("null")) {
                                            turnToMicroLesson();
                                        } else {
                                            long lessonId = Long.valueOf(mList.get(pos).getPrizeId());
                                            turnToMicroLesson(lessonId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!refreshing && !isEnd) {
                    if (mList.size() != 0) {
                        getMoreData(newState);
                    }
                }
            }
        });
    }

    /**
     * 更新列表选中状态
     */
    private void updateList(int pos) {
        String couponCode;
        String title;

        if (pos == mSelectedPosition) {
            mList.get(pos).setSelected(false);
            Bundle bundle = new Bundle();
            bundle.putBoolean("key_selected", false);
            adapter.notifyItemChanged(pos, bundle);
            mSelectedPosition = -1;
            couponCode = "";
            title = "";
        } else {
            if (mSelectedPosition >= 0 && mSelectedPosition < mList.size()) {
                mList.get(mSelectedPosition).setSelected(false);
                Bundle bundle_unselected = new Bundle();
                bundle_unselected.putBoolean("key_selected", false);
                adapter.notifyItemChanged(mSelectedPosition, bundle_unselected);
            }
            mList.get(pos).setSelected(false);
            Bundle bundle_selected = new Bundle();
            bundle_selected.putBoolean("key_selected", true);
            adapter.notifyItemChanged(mSelectedPosition, bundle_selected);
            mSelectedPosition = pos;
            couponCode = mList.get(pos).getCouponCode();
            title = mList.get(pos).getTitle();
        }

        if (mOnItemChooseListen != null) {
            mOnItemChooseListen.onItemChoose(couponCode, title);
        }
    }

    /**
     * 显示需要激活对话框
     */
    private void showNeedActivatedDialog(int position) {
        CouponBean couponBean = mList.get(position);
        final String content = couponBean.getActiveNote();
        final String price = DataUtil.double2String(couponBean.getChargePrice());
        final String packageId = couponBean.getPackageId();
        final String couponPackageId = couponBean.getCouponPackageId();
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("是否激活");
                        if (content.split("#").length > 1) {
                            Spanny spanny = new Spanny();
                            for (int i = 0; i < content.split("#").length; i++) {
                                if (i == 0) {
                                    spanny.append(content.split("#")[i]);
                                } else {
                                    String string = content.split("#")[i];
                                    spanny.append("\n" + string, new RoundSpan(mContext,
                                            Color.parseColor("#ff9933"), string));
                                }
                            }
                            tv_content.setGravity(Gravity.NO_GRAVITY);
                            tv_content.setText(spanny);
                        } else {
                            tv_content.setText(content);
                        }
                        tv_left.setText("取消");
                        tv_right.setText("确定");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                showChoosePayWayDialog(price, packageId, couponPackageId);
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setMargin(60)
                .show(getFragmentManager());
    }

    private BaseNiceDialog mDialog;

    /**
     * 选择支付方式
     */
    private void showChoosePayWayDialog(final String price,
                                        final String packageId, final String couponPackageId) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_choose_pay_way_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        mDialog = dialog;
                        final TextView tv_commit = holder.getView(R.id.tv_commit_choose_pay_way_dialog);
                        String text = "确认支付" + price + "派豆";
                        tv_commit.setText(text);
                        tv_commit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!recharging) {
                                    recharging = true;
                                    wxUnifiedOrder(packageId, couponPackageId);
                                    tv_commit.append("，支付中...");
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getFragmentManager());
    }

    /**
     * 微信充值
     */
    private void wxUnifiedOrder(String payId, String couponPackId) {
        new WXUnifiedOrder(this).execute(wxRechargeUrl,
                "APP", Utils.getLocalIpAddress(), String.valueOf(NewMainActivity.STUDENT_ID),
                payId, couponPackId);
    }

    /**
     * 前往微课界面
     * 未指明明确的微课课程
     */
    private void turnToMicroLesson() {
        if (onCouponBuyLesson != null) {
            onCouponBuyLesson.onCouponBuyLesson(id);
        }
    }

    /**
     * 前往微课界面
     * 指明了明确的微课课程
     *
     * @param id
     */
    private void turnToMicroLesson(long id) {
        Intent intent = new Intent(mContext, MicroLessonActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    /**
     * 前往写作
     */
    private void turnToWriting() {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", "");
        intent.putExtra("taskId", "");
        intent.putExtra("area", 5);
        intent.putExtra("type", 5);
        startActivity(intent);
    }

    /**
     * 获取更多数据
     */
    private void getMoreData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            refreshing = true;
            pageNum++;
            new GetData(this).execute(url, String.valueOf(pageNum), String.valueOf(status),
                    String.valueOf(category), String.valueOf(courseId), compositionId);
        }
    }

    public void initData() {
        refreshing = true;
        swipeRefreshLayout.setRefreshing(true);
        isEnd = false;
        mList.clear();
        adapter.notifyDataSetChanged();
        pageNum = 1;
        new GetData(this).execute(url, String.valueOf(pageNum), String.valueOf(status),
                String.valueOf(category), String.valueOf(courseId), compositionId);
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_coupon_fragment);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_coupon_unused);
        recyclerView = view.findViewById(R.id.recycler_view_coupon_unused);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CouponAdapter(mContext, mList, available);
        recyclerView.setAdapter(adapter);
    }

    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        CouponBean couponBean = new CouponBean();
                        couponBean.setId(object.optLong("id", -1));
                        if (object.getString("stopTime").equals("") ||
                                object.getString("stopTime").equals("null")) {
                            couponBean.setStopTime("2018-01-01");
                        } else {
                            couponBean.setStopTime(
                                    DateUtil.time2YMD(object.getString("stopTime")));
                        }
                        couponBean.setStatus(object.optInt("status", -1));
                        couponBean.setCouponId(object.optInt("couponId", -1));
                        couponBean.setCouponCode(object.getString("couponCode"));
                        couponBean.setCategory(object.optInt("category", -1));
                        couponBean.setType(object.optInt("type", -1));
                        couponBean.setTypeName(object.getString("typeName"));
                        couponBean.setContent(object.getString("content"));
                        couponBean.setTitle(object.getString("title"));
                        couponBean.setConsumption(object.optDouble("consumption", -1));
                        couponBean.setSubtract(object.optDouble("subtract", -1));
                        couponBean.setDiscount(object.optDouble("discount", -1));
                        couponBean.setPrizeId(object.getString("prizeid"));
                        couponBean.setCouponPackageId(object.getString("packageId"));
                        couponBean.setIsActivated(object.optInt("isActivated", 1));
                        if (couponBean.getIsActivated() == 0) {
                            JSONObject condition = object.getJSONObject("conditionActived");
                            couponBean.setChargePrice(condition.optDouble("chargePrice", -1));
                            couponBean.setPackageId(condition.getString("packageId"));
                            couponBean.setActiveNote(object.getString("activeNote"));
                        }
                        if (available && couponBean.getId() == id && id != -1) {
                            mSelectedPosition = i;
                            couponBean.setSelected(true);
                            mList.add(0, couponBean);
                            if (mOnItemChooseListen != null) {
                                mOnItemChooseListen.onItemChoose(couponBean.getCouponCode(), couponBean.getTitle());
                            }
                        } else {
                            couponBean.setSelected(false);
                            mList.add(couponBean);
                        }

                    }
                    if (frameLayout.getVisibility() == View.VISIBLE) {
                        frameLayout.setVisibility(View.GONE);
                    }
                    //刷新列表数据
                    adapter.notifyDataSetChanged();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                errorData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData();
        }
    }

    private void emptyData() {
        if (getActivity() == null) {
            return;
        }
        if (getActivity().isDestroyed()) {
            return;
        }
        isEnd = true;
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(getActivity(), R.drawable.image_state_empty_coupon, imageView);
            if (status == 1) {
                tv_tips.setText("暂无未使用优惠券");
            } else if (status == 2) {
                tv_tips.setText("暂无已使用优惠券");
            } else if (status == 3) {
                tv_tips.setText("暂无已失效优惠券");
            } else {
                tv_tips.setText("暂无优惠券");
            }
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    private void errorData() {
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            tv_tips.setText("获取数据失败，请稍后再试");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    initData();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            showTips("加载数据失败~");
        }
    }

    private void noConnect() {
        showTips("无网络连接，请连接网络后重试");
        if (mList.size() == 0) {
            View errorView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_loading_error_layout, null);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            tv_tips.setText("无网络连接，请连接网络后重试");
            tv_reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.setVisibility(View.GONE);
                    initData();
                }
            });
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
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
            } else {
                errorWXUnifiedOrder();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorWXUnifiedOrder();
        }
    }

    /**
     * 调起微信支付
     */
    private void wxRecharge() {
        PayReq req = new PayReq();
        req.appId = App.APP_ID;
        req.partnerId = App.WX_MCH_ID;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.sign = paySign;
        App.api.sendReq(req);
        recharging = false;
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 微信支付失败
     */
    private void errorWXUnifiedOrder() {
        recharging = false;
        if (mDialog != null) {
            mDialog.dismiss();
        }
        showTips("微信支付失败");
    }

    /**
     * 显示提示信息
     */
    private void showTips(String tips) {
        if (!hidden) {
            MyToastUtil.showToast(mContext, tips);
        }
    }

    private static class GetData
            extends WeakAsyncTask<String, Void, String, CouponFragment> {

        protected GetData(CouponFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(CouponFragment fragment, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", strings[1]);
                object.put("pageSize", 10);
                object.put("status", strings[2]);
                object.put("category", strings[3]);
                object.put("courseId", strings[4]);
                object.put("compositionId", strings[5]);
                OkHttpClient client = new OkHttpClient();
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
        protected void onPostExecute(CouponFragment fragment, String s) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                fragment.noConnect();
            } else {
                fragment.analyzeData(s);
            }
            fragment.refreshing = false;
        }
    }

    /**
     * 微信统一下单
     */
    private static class WXUnifiedOrder
            extends WeakAsyncTask<String, Integer, String, CouponFragment> {

        public WXUnifiedOrder(CouponFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(CouponFragment fragment, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("trade_type", strings[1]);
                object.put("spbill_create_ip", strings[2]);
                object.put("userId", strings[3]);
                object.put("packageId", strings[4]);
                object.put("couponPackId", strings[5]);
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
        protected void onPostExecute(CouponFragment fragment, String s) {
            if (s == null) {
                fragment.errorWXUnifiedOrder();
            } else {
                fragment.analyzeWXUnifiedOrderData(s);
            }
        }
    }

    public interface OnCouponBuyLesson {
        void onCouponBuyLesson(long lessonId);
    }

    private OnCouponBuyLesson onCouponBuyLesson;

    public void setOnCouponBuyLesson(OnCouponBuyLesson onCouponBuyLesson) {
        this.onCouponBuyLesson = onCouponBuyLesson;
    }

    public interface OnItemChooseListen {
        void onItemChoose(String couponCode, String title);
    }

    private OnItemChooseListen mOnItemChooseListen;

    public void setOnItemChooseListen(OnItemChooseListen onItemChooseListen) {
        this.mOnItemChooseListen = onItemChooseListen;
    }

}
