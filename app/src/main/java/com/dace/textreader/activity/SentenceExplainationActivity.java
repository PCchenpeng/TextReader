package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.WritingWorkTeacherAdapter;
import com.dace.textreader.bean.SentenceListBean;
import com.dace.textreader.bean.TeacherBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.WeakAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 名句解释
 */
public class SentenceExplainationActivity extends BaseActivity {

    private RelativeLayout rl_back;
    private TextView tv_title;
    private TextView tv_content_sentence;
    private TextView tv_author_sentence;
    private TextView tv_explaination;


    private SentenceExplainationActivity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_explaination);

        mContext = this;

        initView();
        initEvents();
    }

    private void initEvents() {
        tv_author_sentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToArticleDetail();
            }
        });
    }

    /**
     * 查看文章详细内容
     *
     * @param pos
     */
    private void turnToArticleDetail() {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra("essayId", getIntent().getLongExtra("articleId", -1) + "");
        intent.putExtra("imgUrl", getIntent().getStringExtra("imgUrl"));
        startActivity(intent);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        tv_title = findViewById(R.id.tv_page_title_top_layout);
        tv_title.setText(getIntent().getStringExtra("source"));
        tv_content_sentence = findViewById(R.id.tv_content_sentence);
        tv_author_sentence = findViewById(R.id.tv_author_sentence);
        tv_explaination = findViewById(R.id.tv_explaination);
        tv_content_sentence.setText(getIntent().getStringExtra("content"));
        tv_author_sentence.setText(getIntent().getStringExtra("source"));
        tv_explaination.setText(getIntent().getStringExtra("annotation"));

    }


}
