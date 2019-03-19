package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.CardFragment;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 卡包选择
 */
public class CardChooseActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_title;

    private CardChooseActivity mContext;

    private int id;
    private int status = 1;

    private String mCardCode = "";
    private String mTitle = "";
    private CardFragment cardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_choose);

        mContext = this;

        id = getIntent().getIntExtra("id", -1);

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
        cardFragment.setOnItemChooseListen(new CardFragment.OnItemChooseListen() {
            @Override
            public void onItemChoose(String cardCode, String title) {
                mCardCode = cardCode;
                mTitle = title;
            }
        });
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText("可使用卡包");

        FragmentManager fm = getSupportFragmentManager();
        cardFragment = new CardFragment();
        cardFragment.setAvailable(true);
        cardFragment.setStatus(status);
        cardFragment.setId(id);
        fm.beginTransaction().add(R.id.frame_card_choose, cardFragment, "card").commit();
    }

    @Override
    public void onBackPressed() {
        backToActivity();
    }

    private void backToActivity() {
        Intent intent = new Intent();
        intent.putExtra("cardCode", mCardCode);
        intent.putExtra("title", mTitle);
        setResult(0, intent);
        finish();
    }

}
