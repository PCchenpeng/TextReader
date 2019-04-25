package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 订单评论
 */
public class OrderAppraiseActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/insert/order/comment";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private ScaleRatingBar ratingBar;
    private View view;
    private EditText et_appraise;
    private RelativeLayout rl_number;
    private TextView tv_number;
    private RelativeLayout rl_commit;
    private TextView tv_commit;

    private OrderAppraiseActivity mContext;

    private String orderNumber = "";
    private int score = 0;
    private String appraise = "";
    private int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_appraise);

        mContext = this;

        orderNumber = getIntent().getStringExtra("orderNumber");
        score = getIntent().getIntExtra("score", 0);
        appraise = getIntent().getStringExtra("appraise");

        initView();
        initEvents();

        initData();

        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        rl_commit.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initData() {
        if (score != 0) {
            et_appraise.setText(appraise);
            et_appraise.setEnabled(false);
            et_appraise.setFocusable(false);
            et_appraise.setKeyListener(null);
            ratingBar.setIsIndicator(true);
            ratingBar.setRating(score);
            view.setVisibility(View.VISIBLE);
            tv_commit.setVisibility(View.GONE);
            rl_number.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
                score = (int) v;
            }
        });
        et_appraise.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                number = s.length();
                int last = 200 - number;
                tv_number.setText(String.valueOf(last));
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitAppraise();
            }
        });
    }

    /**
     * 提交评价
     */
    private void commitAppraise() {
        if (score == 0) {
            showTips("请评分");
        } else {
            if (et_appraise.getText().toString().trim().length() < 5) {
                showTips("请输入不小于5个字的评价");
            } else {
                appraise = et_appraise.getText().toString();
                new OrderAppraise(mContext).execute(url, orderNumber, String.valueOf(score), appraise);
            }
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("评价");

        ratingBar = findViewById(R.id.ratingBar_order_appraise);
        view = findViewById(R.id.view_order_appraise);
        et_appraise = findViewById(R.id.et_order_appraise);
        rl_number = findViewById(R.id.rl_number_order_appraise);
        tv_number = findViewById(R.id.tv_number_order_appraise);
        rl_commit = findViewById(R.id.rl_commit_order_appraise);
        tv_commit = findViewById(R.id.tv_commit_order_appraise);
        et_appraise.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
    }

    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                showTips("评价成功");
                JSONObject object = jsonObject.getJSONObject("data");
                score = object.optInt("score", 0);
                if (object.getString("comment").equals("")
                        || object.getString("comment").equals("null")) {
                    appraise = "";
                } else {
                    appraise = object.getString("comment");
                }
                closeActivity();
            } else {
                appraise = "";
                showTips("评价失败，请稍后再试");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交评价失败
     */
    private void errorAppraise() {
        showTips("提交评价失败,请检查网络设置");
    }


    private void closeActivity() {
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("appraise", appraise);
        setResult(0, intent);
        finish();
    }

    private static class OrderAppraise
            extends WeakAsyncTask<String, Void, String, OrderAppraiseActivity> {

        protected OrderAppraise(OrderAppraiseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(OrderAppraiseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("orderId", strings[1]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("score", strings[2]);
                object.put("comment", strings[3]);
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
        protected void onPostExecute(OrderAppraiseActivity activity, String s) {
            if (s == null) {
                activity.errorAppraise();
            } else {
                activity.analyzeData(s);
            }
        }
    }

}
