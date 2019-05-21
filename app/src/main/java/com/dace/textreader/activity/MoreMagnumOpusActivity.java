package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.HomeRecommendAdapter;
import com.dace.textreader.adapter.MoreAuthorWorksAdapter;
import com.dace.textreader.bean.AuthorWorksBean;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.PullListener;
import com.dace.textreader.view.weight.pullrecycler.PullRecyclerView;
import com.dace.textreader.view.weight.pullrecycler.SimpleRefreshHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreMagnumOpusActivity extends BaseActivity  implements PullListener {
    private String worksUrl = HttpUrlPre.HTTP_URL_ + "/select/author/article/list";
    private int pageNum = 1;
    private boolean isRefresh = true;
    PullRecyclerView mRecyclerView;
    MoreAuthorWorksAdapter moreAuthorWorksAdapter;
    private List<AuthorWorksBean.DataBean> worksList = new ArrayList<>();
    private String authorId;
    private String author;
    private RelativeLayout rl_back;
    private TextView tv_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_magnum_opus);

        initView();
        initEvents();
        getWorksData();
    }


    private void initView() {
        mRecyclerView = findViewById(R.id.rlv_magnum_opus);
        rl_back = findViewById(R.id.rl_page_back_top_layout);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = findViewById(R.id.tv_page_title_top_layout);
    }

    private void initEvents() {
        authorId = getIntent().getStringExtra("authorId");
        author = getIntent().getStringExtra("author");
        tv_title.setText(author);
        moreAuthorWorksAdapter = new MoreAuthorWorksAdapter(this, worksList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setHeadRefreshView(new SimpleRefreshHeadView(this))
                .setUseLoadMore(true)
                .setUseRefresh(true)
                .setPullLayoutManager(layoutManager)
                .setPullListener(this)
                .setPullItemAnimator(null)
                .build(moreAuthorWorksAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int childCount = recyclerView.getChildCount();
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (firstVisibleItem + childCount == (itemCount + 1)) {
                        isRefresh = false;
                        pageNum++;
                        getWorksData();
                    }
                }
            }
        });
        moreAuthorWorksAdapter.setOnItemClickListener(new MoreAuthorWorksAdapter.OnItemClickListener() {
            @Override
            public void onClick(int type, String id, String imgUrl, int flag, int py) {
                Intent intent;
                switch (type) {
                    case HomeRecommendAdapter.AUDIO_PIC:

                        if (flag == 1) {
                            intent = new Intent(MoreMagnumOpusActivity.this, HomeAudioDetailActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("py", py);
                        } else {
                            intent = new Intent(MoreMagnumOpusActivity.this, ArticleDetailActivity.class);
                            intent.putExtra("essayId", id);
                            intent.putExtra("imgUrl", imgUrl);
                        }
                        startActivity(intent);
                        break;

                    case HomeRecommendAdapter.IMG:
                        intent = new Intent(MoreMagnumOpusActivity.this, ArticleDetailActivity.class);
                        intent.putExtra("essayId", id);
                        intent.putExtra("imgUrl", imgUrl);
                        startActivity(intent);
                        break;

                    case HomeRecommendAdapter.VIDEO:
                        intent = new Intent(MoreMagnumOpusActivity.this, ArticleDetailActivity.class);
                        intent.putExtra("essayId", id);
                        intent.putExtra("imgUrl", imgUrl);
                        intent.putExtra("isVideo", true);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void getWorksData() {
        JSONObject params = new JSONObject();
        try {
            params.put("authorId",authorId);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("pageNum",pageNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(worksUrl, OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AuthorWorksBean authorWorksBean = GsonUtil.GsonToBean(result.toString(),AuthorWorksBean.class);
                List<AuthorWorksBean.DataBean> dataBeans = authorWorksBean.getData();
                if(dataBeans != null)
                    worksList.addAll(dataBeans);
                    moreAuthorWorksAdapter.notifyDataSetChanged();
                    mRecyclerView.onPullComplete();
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        pageNum = 1;
        getWorksData();
    }

    @Override
    public void onLoadMore() {

    }
}
