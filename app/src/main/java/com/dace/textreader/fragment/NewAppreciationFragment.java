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
import android.widget.RelativeLayout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewAppreciationFragment extends BaseFragment implements View.OnClickListener{
    private static String ESSAY_ID = "essayId";
    private View view;
    private TextView tv_title,tv_time,tv_content;
    private ImageView iv_edit;
    private String essayId;
    private String url = HttpUrlPre.HTTP_URL_+"/select/article/appreciation/list";
    private String deleteUrl = HttpUrlPre.HTTP_URL_+"/delete/article/note";
    private String studentId;
    private LinearLayout ll_content;
    private FrameLayout fly_exception;
    private String title;
    private String content;
    private String noteId;
    private boolean isEditor = false;  //是否处于编辑状态
    private boolean isSelectAll = false;  //是否是全选
    private boolean hasSelected = false;  //是否有item被选中
    private RelativeLayout rl_editor;
    private LinearLayout ll_select_all,ll_item;
    private ImageView iv_select_all;
    private TextView tv_delete;
    private ImageView iv_select;

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
        iv_select = view.findViewById(R.id.iv_select);
        ll_item = view.findViewById(R.id.ll_item);

        rl_editor = view.findViewById(R.id.rl_editor_excerpt_fragment);
        ll_select_all = view.findViewById(R.id.ll_select_all_new_collection_bottom);
        iv_select_all = view.findViewById(R.id.iv_select_all_new_collection_bottom);
        tv_delete = view.findViewById(R.id.tv_delete_new_collection_bottom);

        iv_edit.setOnClickListener(this);

        iv_edit.setOnClickListener(this);
        ll_item.setOnClickListener(this);

        ll_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectAll) {
                    iv_select.setImageResource(R.drawable.icon_edit_unselected);
                    iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
                    tv_delete.setBackgroundResource(R.drawable.shape_text_gray);
                    isSelectAll = false;
                    hasSelected = false;
                } else {
                    iv_select_all.setImageResource(R.drawable.icon_edit_selected);
                    iv_select.setImageResource(R.drawable.icon_edit_selected);
                    tv_delete.setBackgroundResource(R.drawable.shape_text_orange);
                    isSelectAll = true;
                    hasSelected = true;
                }
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasSelected) {
                    deleteItems();
                }
            }
        });
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

    private void deleteItems() {
        rl_editor.setVisibility(View.GONE);
        JSONObject params = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            array.put(noteId);
            params.put("studentId",studentId);
            params.put("noteIds",array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(deleteUrl,OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                cancelEditorMode();
                getData();
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
            case R.id.ll_item:
                if(isEditor){
                    if(isSelectAll){
                        isSelectAll = false;
                        hasSelected = false;
                        iv_select.setImageResource(R.drawable.icon_edit_unselected);
                        iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
                        tv_delete.setBackgroundResource(R.drawable.shape_text_gray);
                    }else {
                        isSelectAll = true;
                        hasSelected = true;
                        iv_select_all.setImageResource(R.drawable.icon_edit_selected);
                        iv_select.setImageResource(R.drawable.icon_edit_selected);
                        tv_delete.setBackgroundResource(R.drawable.shape_text_orange);

                    }
                    editorMode();
                }
                break;
        }
    }

    public void editorOpenOrClose() {
        if (isEditor) {
            cancelEditorMode();
        } else {
            editorMode();
        }
    }

    public boolean getEditor(){
        return isEditor;
    }

    /**
     * 编辑模式
     */
    private void editorMode() {
        rl_editor.setVisibility(View.VISIBLE);
        iv_select.setVisibility(View.VISIBLE);
        isEditor = true;
    }

    /**
     * 取消编辑模式
     */
    private void cancelEditorMode() {
        rl_editor.setVisibility(View.GONE);
        iv_select.setVisibility(View.GONE);
        isEditor = false;
    }


}
