package com.dace.textreader.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.ContactBean;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout rl_back;
    private LinearLayout ll_tell;
    private TextView tv_title,tv_tell,tv_email,tv_wechat,tv_qq,tv_address;
    private String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mContext = this;

        initData();
        initView();
        initEvents();
    }

    private void initData() {

    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        ll_tell = findViewById(R.id.ll_tell);
        tv_tell = findViewById(R.id.tv_tell);
        tv_email = findViewById(R.id.tv_email);
        tv_wechat = findViewById(R.id.tv_wechat);
        tv_qq = findViewById(R.id.tv_qq);
        tv_address = findViewById(R.id.tv_address);

        phoneNum = tv_tell.getText().toString();
        tv_title.setText("联系我们");
        getData();
    }

    private void getData() {
        String url = HttpUrlPre.HTTP_URL_ + "/contact/us";
        JSONObject params = new JSONObject();
        try {
            params.put("region","CN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(mContext).requestAsyn(url,OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                ContactBean contactBean = GsonUtil.GsonToBean(result.toString(),ContactBean.class);
                if (contactBean != null && contactBean.getData() != null){
                    tv_tell.setText(contactBean.getData().getInfo().getPhone());
                    tv_email.setText(contactBean.getData().getInfo().getMail());
                    tv_wechat.setText(contactBean.getData().getInfo().getOfficialAccount());
                    tv_qq.setText(contactBean.getData().getInfo().getQQ());
                    tv_address.setText(contactBean.getData().getInfo().getAddress());
                    phoneNum = contactBean.getData().getInfo().getPhone();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        ll_tell.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_tell:
                callPhone(phoneNum);
                break;
            case R.id.rl_page_back_top_layout:
                finish();
                break;
        }
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
}
