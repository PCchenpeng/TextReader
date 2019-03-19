package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 添加笔记
 */
public class AddNoteActivity extends BaseActivity {

    private final String url = HttpUrlPre.HTTP_URL + "/personal/note/insert";
    private final String updateUrl = HttpUrlPre.HTTP_URL + "/personal/note/update";

    private RelativeLayout rl_back;
    private ImageView iv_commit;
    private TextView tv_content;
    private TextView tv_length;
    private EditText et_note;

    private long essayId = 0;  //文章ID
    private String content = "";  //选中部分内容
    private String note = "";  //笔记内容
    private int start = 0;  //选中文字的起始位置
    private int length = 0;  //选中文字的长度

    private boolean isCommitNote = false;  //是否提交了笔记

    private boolean isUpdate = false;  //是否是修改笔记
    private String nid = "";  //笔记ID，用来修改笔记

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.StatusBarLightMode(this, flag);

        initData();
        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToActivity();
            }
        });
        iv_commit.setOnClickListener(onClickListener);
        et_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String length = String.valueOf(et_note.getText().toString().length());
                tv_length.setText(length);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_commit_add_note:
                    if (isUpdate) {  //修改笔记
                        updateNote();
                    } else {
                        commitNote();
                    }
                    break;
            }
        }
    };

    /**
     * 修改笔记
     */
    private void updateNote() {
        String str = et_note.getText().toString().replaceAll("\\s*", "");
        if (TextUtils.isEmpty(str)) {
            MyToastUtil.showToast(AddNoteActivity.this, "请输入内容");
        } else {
            note = et_note.getText().toString();
            isCommitNote = true;
            new UpdateNote(AddNoteActivity.this).execute(updateUrl);
        }
    }

    /**
     * 提交笔记
     */
    private void commitNote() {
        String str = et_note.getText().toString().replaceAll("\\s*", "");
        if (TextUtils.isEmpty(str)) {
            MyToastUtil.showToast(AddNoteActivity.this, "请输入内容");
        } else {
            note = et_note.getText().toString();
            isCommitNote = true;
            new AddNote(AddNoteActivity.this).execute(url);
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_add_note);
        iv_commit = findViewById(R.id.iv_commit_add_note);
        tv_content = findViewById(R.id.tv_add_note_content);
        tv_length = findViewById(R.id.tv_add_note_length);
        et_note = findViewById(R.id.et_add_note);

        tv_content.setText(content);
        tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        et_note.setFilters(new InputFilter[]{new InputFilter.LengthFilter(800)});

        et_note.setText(note);
    }

    private void initData() {
        essayId = getIntent().getLongExtra("essayId", 0);
        content = getIntent().getStringExtra("content");
        if (essayId == 0) {
            isUpdate = true;
            nid = getIntent().getStringExtra("nid");
            note = getIntent().getStringExtra("note");
        } else {
            start = getIntent().getIntExtra("loc", 0);
            length = getIntent().getIntExtra("len", 0);
        }
    }

    @Override
    public void onBackPressed() {
        backToActivity();
    }

    /**
     * 返回上一个Activity
     */
    private void backToActivity() {
        Intent intent = new Intent();
        intent.putExtra("commit", isCommitNote);
        setResult(0, intent);
        finish();
    }

    /**
     * 添加笔记
     */
    private static class AddNote
            extends WeakAsyncTask<String, Integer, String, AddNoteActivity> {

        protected AddNote(AddNoteActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            //获取数据之前
        }

        @Override
        protected String doInBackground(AddNoteActivity activity, String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("essayId", activity.essayId);
                json.put("content", activity.content);
                json.put("note", activity.note);
                json.put("loc", activity.start);
                json.put("len", activity.length);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AddNoteActivity activity, String s) {
            //获取数据之后
            activity.backToActivity();
        }
    }

    /**
     * 修改笔记
     */
    private static class UpdateNote
            extends WeakAsyncTask<String, Integer, String, AddNoteActivity> {

        protected UpdateNote(AddNoteActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            //获取数据之前
        }

        @Override
        protected String doInBackground(AddNoteActivity activity, String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("noteId", activity.nid);
                json.put("note", activity.note);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AddNoteActivity activity, String s) {
            //获取数据之后
            activity.backToActivity();
        }
    }
}
