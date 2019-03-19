package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.ExcerptFragment;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 摘抄列表
 */
public class ExcerptActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_editor;

    private ExcerptActivity mContext;  //上下文对象

    private ExcerptFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excerpt);

        mContext = this;

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
        tv_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.editorOpenOrClose();
            }
        });
        mFragment.setOnListDataOperateListen(new OnListDataOperateListen() {
            @Override
            public void onRefresh(boolean refresh) {

            }

            @Override
            public void onLoadResult(boolean success) {
                if (success) {
                    tv_editor.setVisibility(View.VISIBLE);
                } else {
                    tv_editor.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEditor(boolean editor) {
                if (editor) {
                    tv_editor.setText("取消");
                } else {
                    tv_editor.setText("编辑");
                }
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_excerpt);
        tv_editor = findViewById(R.id.tv_editor_excerpt);
        mFragment = new ExcerptFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.frame_excerpt, mFragment, "excerpt").commit();
    }
}
