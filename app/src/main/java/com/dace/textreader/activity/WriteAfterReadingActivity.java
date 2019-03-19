package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 写读后感
 */
public class WriteAfterReadingActivity extends BaseActivity {

    private TextView tv_cancel;
    private EditText editText;
    private TextView textView;
    private LinearLayout ll_type;
    private ImageView iv_type;
    private TextView tv_type;
    private TextView tv_commit;

    private WriteAfterReadingActivity mContext;

    private String content = "";  //读后感内容
    private int type = 1;  //读后感公开或私密，1公开0私密

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_after_reading);

        mContext = this;

        initData();
        initView();
        initEvents();

    }

    private void initData() {
        content = getIntent().getStringExtra("content");
        type = getIntent().getIntExtra("isPriviate", 1);
    }

    private void initEvents() {
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText("" + (800 - editText.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ll_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    type = 0;
                    iv_type.setImageResource(R.drawable.icon_thought_secret);
                    tv_type.setText("私密");
                } else if (type == 0) {
                    type = 1;
                    iv_type.setImageResource(R.drawable.icon_thought_open);
                    tv_type.setText("公开");
                }
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString().replaceAll("\\s*", "");
                if (TextUtils.isEmpty(str)) {
                    MyToastUtil.showToast(mContext, "请输入内容");
                } else {
                    addAfterReading(editText.getText().toString(), type);
                }
            }
        });

    }

    /**
     * 添加读后感
     *
     * @param s
     * @param type
     */
    private void addAfterReading(String s, int type) {
        Intent intent = new Intent();
        intent.putExtra("content", s);
        intent.putExtra("type", type);
        setResult(0, intent);
        finish();
    }

    private void initView() {
        tv_cancel = findViewById(R.id.tv_dialog_after_reading_cancel);
        editText = findViewById(R.id.edit_input_after_reading);
        textView = findViewById(R.id.tv_content_number_after_reading);
        ll_type = findViewById(R.id.ll_after_reading_type_dialog);
        iv_type = findViewById(R.id.iv_after_reading_type_dialog);
        tv_type = findViewById(R.id.tv_after_reading_type_dialog);
        tv_commit = findViewById(R.id.tv_dialog_after_reading_commit);

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(800)});
        editText.setText(content);
        editText.setSelection(editText.getText().toString().length());

        tv_commit.setText("发表");
        tv_commit.setSelected(true);
        if (type == 1) {
            type = 1;
            iv_type.setImageResource(R.drawable.icon_thought_open);
            tv_type.setText("公开");
        } else {
            type = 0;
            iv_type.setImageResource(R.drawable.icon_thought_secret);
            tv_type.setText("私密");
        }
    }
}
