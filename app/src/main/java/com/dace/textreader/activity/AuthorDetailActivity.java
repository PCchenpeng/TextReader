package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.AuthorDetailAdapter;
import com.dace.textreader.bean.AuthorDetailBean;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.ExpandableTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AuthorDetailActivity extends BaseActivity implements View.OnClickListener{

    private String url = HttpUrlPre.HTTP_URL_ + "/select/author/detail";
    private String authorId;
    private ExpandableTextView expandableTextView;
    private ImageView iv_topimg ,iv_playvideo;
    private RelativeLayout rl_back_copy;
    private ImageView iv_author,iv_audio;
    private TextView tv_author,tv_follow;
    private RecyclerView rcl_author_detail,rcl_author_works;
    private List<AuthorDetailBean.DataBean.DescriptionListBean> detailList = new ArrayList<>();
    private AuthorDetailAdapter authorDetailAdapter;
//    private LinearLayout ll_des_1,ll_des_2,ll_des_3,ll_des_4,ll_des_5;
//    private RelativeLayout rl_des_1,rl_des_2,rl_des_3,rl_des_4,rl_des_5;
//    private TextView tv_title_1,tv_title_2,tv_title_3,tv_title_4,tv_title_5;
//    private TextView tv_des_1,tv_des_2,tv_des_3,tv_des_4,tv_des_5;
//    private LinearLayout[] ll_desArr;
//    private RelativeLayout[] rl_desArr;
//    private TextView[] tv_desArr;
//    private TextView[] tv_titleArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);

        initData();
        initView();
        initEvents();
    }

    private void initData() {
        authorId = getIntent().getStringExtra("authorId");
    }

    private void initView() {
        expandableTextView = findViewById(R.id.expand_text_view);
        iv_topimg = findViewById(R.id.iv_topimg);
        iv_topimg = findViewById(R.id.iv_topimg);
        iv_playvideo = findViewById(R.id.iv_playvideo);
        rl_back_copy = findViewById(R.id.rl_back_copy);
        iv_author = findViewById(R.id.iv_author);
        iv_audio = findViewById(R.id.iv_audio);
        tv_author = findViewById(R.id.tv_author);
        tv_follow = findViewById(R.id.tv_follow);
        rcl_author_detail = findViewById(R.id.rcl_author_detail);
        rcl_author_works = findViewById(R.id.rcl_author_works);

        authorDetailAdapter = new AuthorDetailAdapter(this,detailList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcl_author_detail.setLayoutManager(linearLayoutManager);
        rcl_author_detail.setAdapter(authorDetailAdapter);
//        ll_des_1 = findViewById(R.id.ll_des_1);
//        ll_des_2 = findViewById(R.id.ll_des_2);
//        ll_des_3 = findViewById(R.id.ll_des_3);
//        ll_des_4 = findViewById(R.id.ll_des_4);
//        ll_des_5 = findViewById(R.id.ll_des_5);
//        rl_des_1 = findViewById(R.id.rl_des_1);
//        rl_des_2 = findViewById(R.id.rl_des_2);
//        rl_des_3 = findViewById(R.id.rl_des_3);
//        rl_des_4 = findViewById(R.id.rl_des_4);
//        rl_des_5 = findViewById(R.id.rl_des_5);
//        tv_title_1 = findViewById(R.id.tv_title_1);
//        tv_title_2 = findViewById(R.id.tv_title_2);
//        tv_title_3 = findViewById(R.id.tv_title_3);
//        tv_title_4 = findViewById(R.id.tv_title_4);
//        tv_title_5 = findViewById(R.id.tv_title_5);
//        tv_des_1 = findViewById(R.id.tv_des_1);
//        tv_des_2 = findViewById(R.id.tv_des_2);
//        tv_des_3 = findViewById(R.id.tv_des_3);
//        tv_des_4 = findViewById(R.id.tv_des_4);
//        tv_des_5 = findViewById(R.id.tv_des_5);
//
//        ll_desArr = new LinearLayout[]{ll_des_1,ll_des_2,ll_des_3,ll_des_4,ll_des_5};
//        rl_desArr = new RelativeLayout[]{rl_des_1,rl_des_2,rl_des_3,rl_des_4,rl_des_5};
//        tv_desArr = new TextView[]{tv_des_1,tv_des_2,tv_des_3,tv_des_4,tv_des_5};
//        tv_titleArr = new TextView[]{tv_title_1,tv_title_2,tv_title_3,tv_title_4,tv_title_5};
        expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
            }
        });

        getData();
    }

    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("authorId",authorId);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("width",750);
            params.put("height",420);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(url, OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AuthorDetailBean authorDetailBean = GsonUtil.GsonToBean(result.toString(),AuthorDetailBean.class);
                String text = authorDetailBean.getData().getContent();
                String textTest = text.replaceAll("\n","\n\n");
                List<AuthorDetailBean.DataBean.DescriptionListBean> descriptionListBeanList = authorDetailBean.getData().getDescriptionList();
                detailList.addAll(descriptionListBeanList);
                authorDetailAdapter.notifyDataSetChanged();
                for(int i=0;i<descriptionListBeanList.size();i++){
//                    ll_desArr[i].setVisibility(View.VISIBLE);
//                    tv_titleArr[i].setText(descriptionListBeanList.get(i).getNameStr());
//                    tv_desArr[i].setText(descriptionListBeanList.get(i).getCont());
                }
                expandableTextView.setText(textTest);
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void initEvents() {
        rl_back_copy.setOnClickListener(this);
//        rl_des_1.setOnClickListener(this);
//        rl_des_2.setOnClickListener(this);
//        rl_des_3.setOnClickListener(this);
//        rl_des_4.setOnClickListener(this);
//        rl_des_5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
