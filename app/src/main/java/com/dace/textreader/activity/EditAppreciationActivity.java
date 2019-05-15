package com.dace.textreader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.MessageEvent;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.PreferencesUtil;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class EditAppreciationActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_cancle;
    private TextView tv_title,tv_complete;
    private EditText edit_content;
    private String title;
    private String content;
    private String insertUrl = HttpUrlPre.HTTP_URL_+"/insert/article/note";
    private String updateUrl = HttpUrlPre.HTTP_URL_+"/update/article/note";
    private String essayId;
    private String studentId;
    private String note;
    private String noteId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editappreciation);

        initData();
        initView();
        initEvents();

    }

    private void initData() {
        title = getIntent().getExtras().getString("title");
        content = getIntent().getExtras().getString("content");
        essayId = getIntent().getExtras().getString("essayId");
        studentId = PreferencesUtil.getData(this,"studentId","-1").toString();
    }

    private void initView() {
        rl_cancle = findViewById(R.id.rl_cancle);
        tv_complete = findViewById(R.id.tv_complete);
        tv_title = findViewById(R.id.tv_title);
        edit_content = findViewById(R.id.edit_content);

        tv_title.setText(title);
        edit_content.setText(content);
    }

    private void initEvents() {
        rl_cancle.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_cancle:
                finish();
                break;
            case R.id.tv_complete:
                note = edit_content.getText().toString();
                if(!note.equals("")){
                    submit();
                }else {
                    MyToastUtil.showToast(this,"请输入赏析内容");
                }
                break;

        }
    }

    private void submit() {
        String url;
        JSONObject params = new JSONObject();
        if(content.equals("")){
            url = insertUrl;
            try {
                params.put("studentId",studentId);
                params.put("essayId",essayId);
                params.put("note",note);
                params.put("essayTitle",title);
                params.put("category",2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            url = updateUrl;
            noteId = getIntent().getExtras().getString("noteId");
            try {
                params.put("studentId",studentId);
                params.put("noteId",noteId);
                params.put("note",note);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        OkHttpManager.getInstance(this).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                try {
                    JSONObject response = new JSONObject(result.toString());
                    if(response.getInt("status") == 200){
                        EventBus.getDefault().post(new MessageEvent("update_appreciation"));
                        finish();
//                        EventBus.getDefault()
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });

    }
}
