package com.dace.textreader.activity;

import android.os.Bundle;
import android.view.View;

import com.dace.textreader.R;
import com.dace.textreader.adapter.ReaderTabAdapter;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;

public class ReaderTabActivity extends BaseActivity implements View.OnClickListener{

    private PullRecyclerView rlv_reader_tab;
    private int pageNum = 1;
    private boolean isRefresh = false;
    private ReaderTabAdapter readerTabAdapter;
//    private List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_tab);
//        essayId = getIntent().getStringExtra("id");

        initView();
//        album_view.setZOrderMediaOverlay(true);
        loadData();
    }
    private void initView() {
        rlv_reader_tab = findViewById(R.id.rlv_reader_tab);
    }

    private void loadData(){

    }


    @Override
    public void onClick(View v) {

    }
}
