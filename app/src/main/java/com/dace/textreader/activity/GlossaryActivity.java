package com.dace.textreader.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.GlossaryFragment;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 生词列表
 */
public class GlossaryActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_title;
    private RelativeLayout rl_editor;
    private TextView tv_editor;
    private FrameLayout frameLayout;

    private GlossaryActivity mContext;

    private boolean isEditor = false;

    private GlossaryFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        mContext = this;

        initView();

        mFragment = new GlossaryFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.frame_glossary, mFragment, "glossary").commit();

        initEvents();
        setImmerseLayout();
    }

    protected void setImmerseLayout() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DensityUtil.getStatusBarHeight(mContext);
        frameLayout.setPadding(0, statusBarHeight, 0, 0);
        rl_editor.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        tv_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditor) {
                    isEditor = false;
                    mFragment.setEditor(isEditor);
                    tv_editor.setText("编辑");
                } else {
                    isEditor = true;
                    mFragment.setEditor(isEditor);
                    tv_editor.setText("完成");
                }
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("生词本");

        frameLayout = findViewById(R.id.frame_glossary);
        rl_editor = findViewById(R.id.rl_editor_glossary);
        tv_editor = findViewById(R.id.tv_editor_glossary);
    }

}
