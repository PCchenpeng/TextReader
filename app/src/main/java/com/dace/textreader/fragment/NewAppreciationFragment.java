package com.dace.textreader.fragment;

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
import com.dace.textreader.bean.AppreciationBean;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

public class NewAppreciationFragment extends BaseFragment {
    private static String ESSAY_ID = "essayId";
    private View view;
    private TextView tv_title,tv_time,tv_content;
    private ImageView iv_edit;
    private String essayId;
    private String url = HttpUrlPre.HTTP_URL_+"/select/article/appreciation/list";
    private String studentId;
    private LinearLayout ll_content;
    private FrameLayout fly_exception;

    public static NewAppreciationFragment newInstance(String essayId) {

        Bundle args = new Bundle();
        args.putString(ESSAY_ID,essayId);
        NewAppreciationFragment fragment = new NewAppreciationFragment();
        fragment.setArguments(args);
        return fragment;
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
                }else {
                    showEmptyView(fly_exception);
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }
}
