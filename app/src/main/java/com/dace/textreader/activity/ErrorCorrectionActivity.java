package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.adapter.ErrorCorrectionAdapter;
import com.dace.textreader.adapter.ErrorCorrectionChooseAdapter;
import com.dace.textreader.adapter.ErrorCorrectionTipsAdapter;
import com.dace.textreader.bean.ErrorClickBean;
import com.dace.textreader.bean.ErrorCorrectionBean;
import com.dace.textreader.bean.ErrorCorrectionChooseBean;
import com.dace.textreader.bean.ErrorCorrectionTipsBean;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.MyBackgroundColorSpan;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;

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
 * 写作纠错
 */
public class ErrorCorrectionActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/writing/rectify";

    private static final String key = "B8C7D50";

    private RelativeLayout rl_back;
    private TextView tv_page;
    private FrameLayout frameLayout;
    private ImageView iv_cover;
    private TextView tv_number;
    private TextView tv_title;
    private RecyclerView recyclerView;

    private ErrorCorrectionActivity mContext;

    private String writingCover;
    private String writingTitle;
    private String writingContent;
    private int writingNumber;

    private List<ErrorCorrectionBean> mList_error = new ArrayList<>();

    private List<ErrorClickBean> mList = new ArrayList<>();
    private ErrorCorrectionAdapter adapter;

    private boolean isNeedBack = false;

    private String code;
    private String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_correction);

        mContext = this;

        writingCover = getIntent().getStringExtra("cover");
        writingTitle = getIntent().getStringExtra("title");
        writingContent = getIntent().getStringExtra("content");
        writingNumber = getIntent().getIntExtra("wordNum", 0);

        initView();
        initData();
        initEvents();

    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backActivity();
            }
        });
    }

    /**
     * 返回写作界面
     */
    private void backActivity() {
        if (isNeedBack) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < mList.size(); i++) {
                try {
                    if (mList.get(i).getType() == 1) {
                        JSONObject object = new JSONObject();
                        object.put("type", "text");
                        object.put("content", mList.get(i).getContent().toString());
                        array.put(object);
                    } else {
                        JSONObject object = new JSONObject();
                        object.put("type", "image");
                        object.put("content", mList.get(i).getImagePath());
                        array.put(object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent();
            intent.putExtra("content", array.toString());
            setResult(0, intent);
        }
        finish();
    }

    private void initData() {
        showLoadingView(true);
        code = DataEncryption.getRandomString(6);
        sign = DataEncryption.errorCorrectionEncode(key, code, NewMainActivity.STUDENT_ID);

        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                writingContent, code, sign);
    }

    /**
     * 显示正在加载视图
     *
     * @param show
     */
    private void showLoadingView(boolean show) {
        if (show) {
            View errorView = LayoutInflater.from(mContext)
                    .inflate(R.layout.error_correction_loading_layout, null);
            frameLayout.removeAllViews();
            frameLayout.addView(errorView);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_page = findViewById(R.id.tv_page_title_top_layout);
        tv_page.setText("纠错");

        frameLayout = findViewById(R.id.frame_error_correction);
        tv_title = findViewById(R.id.tv_title_error_correction);
        tv_number = findViewById(R.id.tv_number_error_correction);
        iv_cover = findViewById(R.id.iv_cover_error_correction);
        recyclerView = findViewById(R.id.recycler_view_error_correction);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ErrorCorrectionAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);
    }

    private static class GetData
            extends WeakAsyncTask<String, Void, String, ErrorCorrectionActivity> {

        protected GetData(ErrorCorrectionActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(ErrorCorrectionActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("content", strings[2]);
                object.put("code", strings[3]);
                object.put("sign", strings[4]);
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
        protected void onPostExecute(ErrorCorrectionActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
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
                JSONArray array = jsonObject.getJSONArray("data");
                if (array.length() == 0) {
                    errorData(400);
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        ErrorCorrectionBean bean = new ErrorCorrectionBean();
                        bean.setError(object.getString("error"));
                        bean.setTips(object.getString("tip"));
                        bean.setSection(object.optInt("section", -1));
                        bean.setPosition(object.optInt("pos", -1));
                        bean.setLength(object.optInt("len", -1));
                        bean.setCorrected(object.optInt("corrected", 1));
                        mList_error.add(bean);
                    }
                    updateUi();
                }
            } else if (300 == jsonObject.optInt("status", -1)) {
                errorData(300);
            } else if (400 == jsonObject.optInt("status", -1)) {
                errorData(400);
            } else {
                errorData(-1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorData(-1);
        }
    }

    /**
     * 更新视图
     */
    private void updateUi() {
        if (isDestroyed()) {
            return;
        }
        if (writingCover.equals("") || writingCover.equals("null")) {
            iv_cover.setVisibility(View.GONE);
        } else {
            if (!isDestroyed()) {
                RequestOptions options = new RequestOptions()
                        .transform(new GlideRoundImage(mContext, 4));
                Glide.with(mContext)
                        .asBitmap()
                        .load(writingCover)
                        .apply(options)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                iv_cover.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                iv_cover.setImageBitmap(resource);
                            }
                        });
            }
        }
        tv_title.setText(writingTitle);
        tv_number.setText(String.valueOf(writingNumber));

        updateContent();
    }

    /**
     * 更新内容
     */
    private void updateContent() {
        try {
            JSONArray array = new JSONArray(writingContent);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                ErrorClickBean clickBean = new ErrorClickBean();
                if (object.getString("type").equals("text")) {
                    String content = object.getString("content");
                    SpannableString spannableString = new SpannableString(content);
                    List<ErrorCorrectionBean> list = new ArrayList<>();
                    for (int j = 0; j < mList_error.size(); j++) {
                        if (mList_error.get(j).getSection() - 1 == i) {
                            list.add(mList_error.get(j));
                            int start = mList_error.get(j).getPosition();
                            int end = start + mList_error.get(j).getLength();
                            if (start < content.length() && end < content.length()) {
                                final int pos = i;
                                final int index = list.size() - 1;
                                spannableString.setSpan(
                                        new MyBackgroundColorSpan(Color.parseColor("#B3D3FF")),
                                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        if (mList_error.get(index).getCorrected() == 1) {
                                            showClickPopup(pos, index);
                                        } else {
                                            showTipsPopup(pos, index);
                                        }
                                    }

                                    @Override
                                    public void updateDrawState(@NonNull TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }
                                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                    clickBean.setErrorList(list);
                    clickBean.setContent(spannableString);
                    clickBean.setType(1);
                } else {
                    clickBean.setImagePath(object.getString("content"));
                    clickBean.setType(0);
                }
                mList.add(clickBean);
            }
            adapter.notifyDataSetChanged();
            showLoadingView(false);
        } catch (JSONException e) {
            e.printStackTrace();
            errorData(-1);
        }
    }

    /**
     * 显示提示信息
     *
     * @param index
     */
    private void showTipsPopup(final int pos, final int index) {

        String error = mList.get(pos).getErrorList().get(index).getError();
        String tips = mList.get(pos).getErrorList().get(index).getTips();
        final List<ErrorCorrectionTipsBean> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(tips);
            for (int i = 0; i < array.length(); i++) {
                ErrorCorrectionTipsBean bean = new ErrorCorrectionTipsBean();
                bean.setTips(array.getString(i));
                bean.setError(error);
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorCorrectionTipsBean bean = new ErrorCorrectionTipsBean();
            bean.setTips(tips);
            bean.setError(error);
            list.add(bean);
        }

        NiceDialog.init()
                .setLayoutId(R.layout.popup_window_error_correction_tip_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        RecyclerView recyclerView =
                                holder.getView(R.id.recycler_view_error_correction_tip);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                                LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        final ErrorCorrectionTipsAdapter adapter =
                                new ErrorCorrectionTipsAdapter(mContext, list);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .setShowBottom(false)
                .setMargin(24)
                .show(getSupportFragmentManager());
    }

    /**
     * 显示错误词语点击弹窗
     *
     * @param index
     */
    private void showClickPopup(final int pos, final int index) {
        String text = mList.get(pos).getErrorList().get(index).getTips();
        final List<ErrorCorrectionChooseBean> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(text);
            for (int i = 0; i < array.length(); i++) {
                ErrorCorrectionChooseBean bean = new ErrorCorrectionChooseBean();
                if (i == 0) {
                    bean.setSelected(true);
                    text = array.getString(i);
                } else {
                    bean.setSelected(false);
                }
                bean.setText(array.getString(i));
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrorCorrectionChooseBean bean = new ErrorCorrectionChooseBean();
            if (list.size() == 0) {
                bean.setSelected(true);
            } else {
                bean.setSelected(false);
            }
            bean.setText(text);
            list.add(bean);
        }

        NiceDialog.init()
                .setLayoutId(R.layout.popup_window_error_correction_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_accept = holder.getView(R.id.tv_accept_error_correction_popup_window);
                        TextView tv_error = holder.getView(R.id.tv_error_correction_popup_window);
                        final RecyclerView error = holder.getView(R.id.recycler_view_error_correction_popup_window);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                                LinearLayoutManager.VERTICAL, false);
                        error.setLayoutManager(layoutManager);
                        final ErrorCorrectionChooseAdapter adapter_choose =
                                new ErrorCorrectionChooseAdapter(mContext, list);
                        error.setAdapter(adapter_choose);

                        tv_error.setText(mList_error.get(index).getError());

                        tv_accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).isSelected()) {
                                        String content = mList.get(pos).getContent().toString();
                                        int s = mList_error.get(index).getPosition();
                                        int e = s + mList_error.get(index).getLength();
                                        String str1 = content.substring(0, s);
                                        String str2 = content.substring(e);
                                        content = str1 + list.get(i).getText() + str2;

                                        mList.get(pos).getErrorList().remove(index);

                                        SpannableString spannableString = new SpannableString(content);
                                        for (int j = 0; j < mList.get(pos).getErrorList().size(); j++) {
                                            final int index = j;
                                            int start = mList.get(pos).getErrorList().get(j).getPosition();
                                            int end = start + mList.get(pos).getErrorList().get(j).getLength();
                                            spannableString.setSpan(
                                                    new MyBackgroundColorSpan(Color.parseColor("#B3D3FF")),
                                                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannableString.setSpan(new ClickableSpan() {
                                                @Override
                                                public void onClick(@NonNull View widget) {
                                                    if (mList_error.get(index).getCorrected() == 1) {
                                                        showClickPopup(pos, index);
                                                    } else {
                                                        showTipsPopup(pos, index);
                                                    }
                                                }

                                                @Override
                                                public void updateDrawState(@NonNull TextPaint ds) {
                                                    ds.setUnderlineText(false);
                                                }
                                            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        }
                                        mList.get(pos).setContent(spannableString);
                                        adapter.notifyDataSetChanged();
                                        isNeedBack = true;
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                        adapter_choose.setOnItemClickListen(new ErrorCorrectionChooseAdapter.OnItemClickListen() {
                            @Override
                            public void onClick(View view) {
                                int pos = error.getChildAdapterPosition(view);
                                for (int i = 0; i < list.size(); i++) {
                                    list.get(pos).setSelected(false);
                                }
                                list.get(pos).setSelected(true);
                                adapter_choose.notifyDataSetChanged();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setMargin(24)
                .show(getSupportFragmentManager());
    }

    /**
     * 获取数据失败
     *
     * @param status
     */
    private void errorData(int status) {
        if (isDestroyed()) {
            return;
        }
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.error_correction_loading_layout, null);
        ImageView imageView = errorView.findViewById(R.id.iv_state_error_correction_loading);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_error_correction_loading);
        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_error_correction_failed, imageView);
        if (status == 300) {
            tv_tips.setText("纠错系统维护中，敬请见谅~");
        } else if (status == 400) {
            tv_tips.setText("没有找到任何内容错误~");
        } else {
            tv_tips.setText("oh~Ai机器人出了点问题...");
        }
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 无网络链接
     */
    private void noConnect() {
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_tips = errorView.findViewById(R.id.tv_tips_list_loading_error);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_tips.setText("获取数据失败，请连接网络后重试~");
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                frameLayout.removeAllViews();
                initData();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }
}
