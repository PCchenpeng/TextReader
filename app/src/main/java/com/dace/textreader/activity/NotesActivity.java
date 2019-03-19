package com.dace.textreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.NoteFragment;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 笔记列表
 */
public class NotesActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_editor;

    private NoteFragment mFragment;

    private boolean isAllNotes = false;
    private long essayId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.LEFT).setDuration(300));
            getWindow().setExitTransition(new Slide(Gravity.RIGHT).setDuration(300));
        }

        isAllNotes = getIntent().getBooleanExtra("isAllNotes", false);
        if (!isAllNotes) {
            essayId = getIntent().getLongExtra("essayId", -1);
        }

        initView();
        initEvents();

    }

    private void initEvents() {
        rl_back.setOnClickListener(onClickListener);
        tv_editor.setOnClickListener(onClickListener);
        mFragment.setOnListDataListen(new OnListDataOperateListen() {
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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_back_notes:
                    finish();
                    break;
                case R.id.tv_notes_editor:
                    mFragment.editorOpenOrClose();
                    break;
            }
        }
    };

    private void initView() {
        rl_back = findViewById(R.id.rl_back_notes);
        tv_editor = findViewById(R.id.tv_notes_editor);

        mFragment = new NoteFragment();
        mFragment.setAllNotes(isAllNotes);
        mFragment.setEssayId(essayId);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.frame_notes, mFragment, "note").commit();
    }

}
