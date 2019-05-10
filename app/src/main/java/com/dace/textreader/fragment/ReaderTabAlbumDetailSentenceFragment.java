package com.dace.textreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dace.textreader.R;
import com.dace.textreader.activity.DailySentenceActivity;
import com.dace.textreader.activity.SentenceExplainationActivity;
import com.dace.textreader.adapter.BookAdapter;
import com.dace.textreader.adapter.ReaderTabAlbumDetailListAdapter;
import com.dace.textreader.adapter.SentenceListAdapter;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.bean.SentenceBean;
import com.dace.textreader.bean.SentenceListBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReaderTabAlbumDetailSentenceFragment extends Fragment {

    private View view;
    private String url = HttpUrlPre.HTTP_URL_+"/select/sentence/list";
    private List<SentenceListBean.DataBean> mData = new ArrayList<>();
    private SentenceListAdapter sentenceListAdapter;
    private RecyclerView recyclerView;
    private String albumId = "-1";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reader_tab, container, false);

        initData();
        initView();

        return view;
    }

    private void initData() {

    }

    private void initView() {
        recyclerView = view.findViewById(R.id.rcv_tab);
        sentenceListAdapter = new SentenceListAdapter(getContext(),mData);
        LinearLayoutManager layoutManager_recommend = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager_recommend);
        recyclerView.setAdapter(sentenceListAdapter);
        sentenceListAdapter.setOnItemClickListener(new SentenceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                turnToArticleDetail(pos);
            }
        });
    }

    /**
     * 查看文章详细内容
     *
     * @param pos
     */
    private void turnToArticleDetail(int pos) {
        SentenceListBean.DataBean sentenceBean = mData.get(pos);
        Intent intent = new Intent(getContext(), SentenceExplainationActivity.class);
        intent.putExtra("articleId", sentenceBean.getArticleId());
        intent.putExtra("content", sentenceBean.getContent());
        intent.putExtra("source", sentenceBean.getSource());
        intent.putExtra("annotation", sentenceBean.getAnnotation());
        startActivity(intent);
    }


    private void loadItemData() {
        JSONObject params = new JSONObject();
        try {
            params.put("albumId",albumId);
            params.put("pageNum","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(getContext()).requestAsyn(url, OkHttpManager.TYPE_POST_JSON, params,
                new OkHttpManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        SentenceListBean sentenceListBean = GsonUtil.GsonToBean(result.toString(),SentenceListBean.class);
                        Log.d("111","readTabAlbumDetailBean  " + GsonUtil.BeanToJson(sentenceListBean));
                        mData = sentenceListBean.getData();
                        if(mData !=null)
                        sentenceListAdapter.refreshData(mData);
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
        loadItemData();
    }
}
