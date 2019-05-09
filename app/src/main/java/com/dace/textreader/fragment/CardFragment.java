package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.BuyCardActivity;
import com.dace.textreader.activity.MemberCentreActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.WritingActivity;
import com.dace.textreader.adapter.CardAdapter;
import com.dace.textreader.adapter.CardRecommendAdapter;
import com.dace.textreader.bean.CardBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.SmartScrollView;

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
 * Packname com.dace.textreader.fragment_card
 * Created by Administrator.
 * Created time 2018/8/23 0023 下午 3:47.
 * Version   1.0;
 * Describe :  //有效卡包
 * History:
 * ==============================================================================
 */

public class CardFragment extends Fragment {

    //用户作文批改
    private static final String url = HttpUrlPre.HTTP_URL + "/select/my/card/list";
    //用于查询卡包
    private static final String url_all = HttpUrlPre.HTTP_URL + "/select/my/card/list/all";
    private static final String url_recommend = HttpUrlPre.HTTP_URL + "/select/card/recommend/list";

    private View view;

    private FrameLayout frameLayout;
    private SmartScrollView scrollView;
    private LinearLayout ll_card;
    private RecyclerView recyclerView;
    private LinearLayout ll_recommend_card;
    private RecyclerView recyclerView_recommend;

    private Context mContext;

    private LinearLayoutManager mLayoutManager;

    private List<CardBean> mList = new ArrayList<>();
    private List<CardBean> mList_recommend = new ArrayList<>();
    private CardAdapter adapter;
    private CardRecommendAdapter adapter_recommend;

    private boolean available = false;  //是否可供选择
    private int id = -1;  //优惠券ID，用来默认选中
    private int status = 1;  //卡包状态，0为失效，1为可用
    private int mPosition = -1;  //上一个选中的item

    private boolean refreshing = false;
    private boolean isEnd = false;
    private int pageNum = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_card, container, false);

        initView();
        initEvents();

        return view;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initEvents() {
        scrollView.setScanScrollChangedListener(new SmartScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                if (!available && status == 1 && !refreshing && !isEnd && scrollView.isScrolledToBottom()) {
                    getMoreData();
                }
            }

            @Override
            public void onScrolledToTop() {

            }
        });
        adapter.setOnCardItemClick(new CardAdapter.OnCardItemClick() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (status == 1) {
                    if (available) {
                        updateList(pos);
                    } else {
                        CardBean cardBean = mList.get(pos);
                        if (cardBean.getCategory() == 5) {
                            long id = cardBean.getId();
                            turnToMemberCentre(String.valueOf(id));
                        } else {
                            turnToWriting();
                        }
                    }
                } else {
                    long id = mList.get(pos).getRecommendCardId();
                    if (id == -1) {
                        MyToastUtil.showToast(mContext, "暂不支持购买激活");
                    } else {
                        turnToBuyCard(id);
                    }

                }
            }
        });
        adapter_recommend.setOnCardRecommendItemClickListen(
                new CardRecommendAdapter.OnCardRecommendItemClick() {
                    @Override
                    public void onItemClick(View view) {
                        int pos = recyclerView_recommend.getChildAdapterPosition(view);
                        if (mList_recommend.get(pos).getStatus() == 1) {
                            long id = mList_recommend.get(pos).getId();
                            turnToBuyCard(id);
                        }
                    }
                });
    }

    /**
     * 前往多功能卡页面
     *
     * @param id
     */
    private void turnToMemberCentre(String id) {
        Intent intent = new Intent(getActivity(), MemberCentreActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("code", "");
        startActivity(intent);
    }

    /**
     * 更新列表选中状态
     */
    private void updateList(int pos) {
        String cardCode;
        String title;

        if (pos == mPosition) {
            mList.get(pos).setSelected(false);
            Bundle bundle = new Bundle();
            bundle.putBoolean("key_selected", false);
            adapter.notifyItemChanged(pos, bundle);
            mPosition = -1;
            cardCode = "";
            title = "";
        } else {
            if (mPosition >= 0 && mPosition < mList.size()) {
                mList.get(mPosition).setSelected(false);
                Bundle bundle_unselected = new Bundle();
                bundle_unselected.putBoolean("key_selected", false);
                adapter.notifyItemChanged(mPosition, bundle_unselected);
            }
            mList.get(pos).setSelected(true);
            Bundle bundle_selected = new Bundle();
            bundle_selected.putBoolean("key_selected", false);
            adapter.notifyItemChanged(pos, bundle_selected);
            mPosition = pos;
            cardCode = mList.get(pos).getCardCode();
            title = mList.get(pos).getTitle();
        }
        if (mOnItemChooseListen != null) {
            mOnItemChooseListen.onItemChoose(cardCode, title);
        }
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
     * 前往购买卡包
     */
    private void turnToBuyCard(long id) {
        Intent intent = new Intent(mContext, BuyCardActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void getMoreData() {
        refreshing = true;
        pageNum++;
        new GetRecommendData(this).execute(url_recommend, String.valueOf(pageNum));
    }

    private void initData() {
        if (!refreshing) {
            refreshing = true;
            showLoadingView();
            ll_card.setVisibility(View.VISIBLE);
            if (available) {
                ll_recommend_card.setVisibility(View.GONE);
            } else {
                ll_recommend_card.setVisibility(View.VISIBLE);
            }
            pageNum = 1;
            mList.clear();
            adapter.notifyDataSetChanged();
            mList_recommend.clear();
            adapter_recommend.notifyDataSetChanged();
            if (available) {
                new GetData(this).execute(url, String.valueOf(status));
            } else {
                new GetData(this).execute(url_all, String.valueOf(status));
            }
        }
    }

    private void initView() {
        frameLayout = view.findViewById(R.id.frame_card_fragment);
        scrollView = view.findViewById(R.id.scroll_view_card_fragment);
        ll_card = view.findViewById(R.id.ll_card);
        recyclerView = view.findViewById(R.id.recycler_view_card_fragment);
        ll_recommend_card = view.findViewById(R.id.ll_recommend_card);
        recyclerView_recommend = view.findViewById(R.id.recycler_view_recommend_card_fragment);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView_recommend.setNestedScrollingEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardAdapter(mContext, mList, available);
        recyclerView.setAdapter(adapter);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_recommend.setLayoutManager(mLayoutManager);
        adapter_recommend = new CardRecommendAdapter(mContext, mList_recommend);
        recyclerView_recommend.setAdapter(adapter_recommend);

        if (status == 0) {
            ll_recommend_card.setVisibility(View.GONE);
        }
    }

    /**
     * 显示加载等待视图
     */
    private void showLoadingView() {
        if (mContext != null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_loading, null);
            ImageView iv_loading = view.findViewById(R.id.iv_loading_content);

            GlideUtils.loadGIFImageWithNoOptions(mContext, R.drawable.image_loading, iv_loading);

            frameLayout.removeAllViews();
            frameLayout.addView(view);
        }
    }

    /**
     * 分析我的卡包数据
     */
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
                        CardBean cardBean = new CardBean();
                        cardBean.setId(object.optLong("id", -1));
                        if (object.getString("endTime").equals("") ||
                                object.getString("endTime").equals("null")) {
                            cardBean.setStopTime("2018-01-01 00:00");
                        } else {
                            cardBean.setStopTime(DateUtil.time2YMD(object.getString("endTime")));
                        }
                        cardBean.setFrequency(object.optInt("frequency", -1));
                        cardBean.setType(object.optInt("type", 0));
                        cardBean.setCardCode(object.getString("cardCode"));
                        cardBean.setStatus(object.optInt("status", 0));
                        cardBean.setDiscount(object.optDouble("discount", -1));
                        cardBean.setTitle(object.getString("title"));
                        cardBean.setValidImage(object.getString("validImage"));
                        cardBean.setInvalidImage(object.getString("invalidImage"));
                        cardBean.setRecommendCardId(object.optInt("recommendCardId", -1));
                        cardBean.setCategory(object.optInt("category", 1));
                        if (available && id != -1 && id == cardBean.getId()) {
                            cardBean.setSelected(true);
                            mList.add(0, cardBean);
                            if (mOnItemChooseListen != null) {
                                mOnItemChooseListen.onItemChoose(cardBean.getCardCode(), cardBean.getTitle());
                            }
                        } else {
                            cardBean.setSelected(false);
                            mList.add(cardBean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (!available && status == 1) {
                        new GetRecommendData(this).execute(url_recommend, String.valueOf(pageNum));
                    } else {
                        ll_recommend_card.setVisibility(View.GONE);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyData();
            } else {
                emptyData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            emptyData();
        }
    }

    /**
     * 获取我的卡包数据为空
     */
    private void emptyData() {
        if (mList.size() == 0) {
            ll_card.setVisibility(View.GONE);
            if (status == 1 && !available) {
                new GetRecommendData(this).execute(url_recommend, String.valueOf(pageNum));
            } else {
                ll_recommend_card.setVisibility(View.GONE);
                showNoContentView();
            }
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 分析推荐购买卡包的数据
     *
     * @param s
     */
    private void analyzeRecommendData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    emptyRecommendData();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        CardBean cardBean = new CardBean();
                        cardBean.setId(object.optLong("id", -1));
                        cardBean.setStatus(object.optInt("status", 1));
                        cardBean.setValidImage(object.getString("indexImage"));
                        mList_recommend.add(cardBean);
                    }
                    adapter_recommend.notifyDataSetChanged();
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyRecommendData();
            } else {
                emptyRecommendData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            emptyRecommendData();
        }
    }

    /**
     * 推荐购买卡包的数据为空
     */
    private void emptyRecommendData() {
        if (mList_recommend.size() == 0) {
            ll_recommend_card.setVisibility(View.GONE);
            if (mList.size() == 0) {
                showNoContentView();
            }
        } else {
            isEnd = true;
        }
    }

    /**
     * 显示无内容视图
     */
    private void showNoContentView() {
        if (mContext != null) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_loading_error_layout, null);
            ImageView imageView = errorView.findViewById(R.id.iv_state_list_loading_error);
            TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
            TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_state_empty, imageView);
            if (status == 1) {
                tv_tips.setText("暂无可使用卡包");
            } else {
                tv_tips.setText("暂无已失效卡包");
            }
            tv_reload.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取我的卡包数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, CardFragment> {

        protected GetData(CardFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(CardFragment fragment, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("status", strings[1]);
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
        protected void onPostExecute(CardFragment fragment, String s) {
            fragment.frameLayout.setVisibility(View.GONE);
            fragment.frameLayout.removeAllViews();
            if (s == null) {
                fragment.emptyData();
            } else {
                fragment.analyzeData(s);
            }
        }
    }

    /**
     * 获取推荐卡包数据
     */
    private static class GetRecommendData
            extends WeakAsyncTask<String, Void, String, CardFragment> {

        protected GetRecommendData(CardFragment fragment) {
            super(fragment);
        }

        @Override
        protected String doInBackground(CardFragment fragment, String[] strings) {
            try {
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("pageNum", strings[1]);
                object.put("pageSize", 10);
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
        protected void onPostExecute(CardFragment fragment, String s) {
            if (s == null) {
                fragment.emptyRecommendData();
            } else {
                fragment.analyzeRecommendData(s);
            }
            fragment.refreshing = false;
        }
    }

    public interface OnItemChooseListen {
        void onItemChoose(String cardCode, String title);
    }

    private OnItemChooseListen mOnItemChooseListen;

    public void setOnItemChooseListen(OnItemChooseListen onItemChooseListen) {
        this.mOnItemChooseListen = onItemChooseListen;
    }

}
