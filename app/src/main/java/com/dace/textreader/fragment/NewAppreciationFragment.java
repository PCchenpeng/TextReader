package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.EditAppreciationActivity;
import com.dace.textreader.bean.AppreciationBean;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class NewAppreciationFragment extends BaseFragment implements View.OnClickListener{
    private static String ESSAY_ID = "essayId";
    private View view;
    private TextView tv_title,tv_time,tv_content;
    private ImageView iv_edit;
    private String essayId;
    private String url = HttpUrlPre.HTTP_URL_+"/select/article/appreciation/list";
    private String studentId;
    private LinearLayout ll_content;
    private FrameLayout fly_exception;
    private String title;
    private String content;
    private String noteId;

    public static NewAppreciationFragment newInstance(String essayId) {

        Bundle args = new Bundle();
        args.putString(ESSAY_ID,essayId);
        NewAppreciationFragment fragment = new NewAppreciationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        if(messageEvent.getMessage().equals("update_appreciation")){
            getData();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newappreciation, container, false);
        initData();
        initView();
        getData();
        return view;
    }



    private void initData() {
        essayId = getArguments().getString(ESSAY_ID);
        studentId = PreferencesUtil.getData(getContext(),"studentId","-1").toString();
    }

    private void initView() {
        tv_title = view.findViewById(R.id.tv_title);
        tv_time = view.findViewById(R.id.tv_time);
        tv_content = view.findViewById(R.id.tv_content);
        iv_edit = view.findViewById(R.id.iv_edit);
        ll_content = view.findViewById(R.id.ll_content);
        fly_exception = view.findViewById(R.id.fly_exception);

        iv_edit.setOnClickListener(this);
    }

    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("essayId",essayId);
            params.put("studentId",studentId);
            params.put("pageNum",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url, OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AppreciationBean appreciationBean = GsonUtil.GsonToBean(result.toString(),AppreciationBean.class);
                if(appreciationBean.getData() != null && appreciationBean.getData().getMyself() != null){
                    fly_exception.setVisibility(View.GONE);
                    tv_title.setText(appreciationBean.getData().getMyself().getEssay_title());
                    tv_time.setText(DateUtil.timedate(String.valueOf(appreciationBean.getData().getMyself().getTime())));
                    tv_content.setText(appreciationBean.getData().getMyself().getNote());
                    title = appreciationBean.getData().getMyself().getEssay_title();
                    content = appreciationBean.getData().getMyself().getNote();
                    noteId = appreciationBean.getData().getMyself().getId();
                }else {
                    showEmptyView(fly_exception);
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_edit:
                Intent intent = new Intent(getContext(),EditAppreciationActivity.class);
                intent.putExtra("essayId",essayId);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("noteId",noteId);
                startActivity(intent);
                break;
        }
    }
}
