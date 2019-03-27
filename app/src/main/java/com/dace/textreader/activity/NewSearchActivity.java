package com.dace.textreader.activity;

import android.os.Bundle;

import com.dace.textreader.R;
import com.dace.textreader.util.StatusBarUtil;

public class NewSearchActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.StatusBarLightMode(this, flag);


//        initView();
//        initData();
//        initEvents();

    }
}
