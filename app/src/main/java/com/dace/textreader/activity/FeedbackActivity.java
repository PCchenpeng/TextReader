package com.dace.textreader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/me/feedback?";

    private RelativeLayout rl_back;
    private TextView tv_title;
    private EditText et_feedback;
    private Button bt_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack();
            }
        });
    }

    //反馈
    private void feedBack() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    json.put("feedbackUserId", NewMainActivity.STUDENT_ID);
                    json.put("content", et_feedback.getText().toString());
                    RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    et_feedback.setText("");
                    MyToastUtil.showToast(FeedbackActivity.this, "反馈成功，感谢您宝贵的意见和建议");
                    finish();
                    break;
            }
        }
    };

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("意见反馈");

        et_feedback = findViewById(R.id.et_feedback);
        bt_feedback = findViewById(R.id.bt_feedback);
    }
}
