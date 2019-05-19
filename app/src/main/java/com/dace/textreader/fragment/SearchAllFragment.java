package com.dace.textreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.activity.ArticleDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.SearchResultActivity;
import com.dace.textreader.activity.WordDetailActivity;
import com.dace.textreader.adapter.SearchAlbumAdapter;
import com.dace.textreader.adapter.SearchArticleAdapter;
import com.dace.textreader.adapter.SearchAuthorAdapter;
import com.dace.textreader.bean.SearchResultBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.LineWrapLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchAllFragment extends Fragment implements View.OnClickListener {
    private View view;

    private String searchUrl = HttpUrlPre.SEARCHE_URL + "/search/search/full/text";
    private SearchResultBean mData;
    private LinearLayout ll_words,ll_author,ll_album,ll_article;
    private LineWrapLayout lwl_words;
    private RecyclerView rcl_author,rcl_album,rcl_article;
    private TextView tv_words_more,tv_author_more,tv_album_more,tv_article_more;
    private Context mContext;
    private SmartRefreshLayout refreshLayout;
    private SearchAuthorAdapter searchAuthorAdapter;
    private SearchAlbumAdapter searchAlbumAdapter;
    private SearchArticleAdapter searchArticleAdapter;
    private String searchWord;
    private List<SubListBean> authorListBeans = new ArrayList<>();
    private List<SubListBean> albumListBeans = new ArrayList<>();
    private List<SubListBean> articleListBeans = new ArrayList<>();
//    private SearchResultActivity searchResultActivity;
    private boolean isAuthor;
    private int pageNo = 1;
    private NestedScrollView nsl_search_all;

  public static SearchAllFragment newInstance(String searchResult, String word,boolean isAuthor) {
      SearchAllFragment f = new SearchAllFragment();
             Bundle args = new Bundle();
             args.putString("word", word);
             args.putString("searchResult",searchResult);
             args.putBoolean("isAuthor",isAuthor);
             f.setArguments(args);
             return f;
 }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_all, container, false);
        initView();
        initData();
        initEvents();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
//        searchResultActivity = (SearchResultActivity) getActivity();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.mContext = context;
//    }

    private void initData() {
      if(getArguments() != null){
          isAuthor = getArguments().getBoolean("isAuthor");
          if(isAuthor){
              tv_article_more.setVisibility(View.GONE);
              tv_album_more.setVisibility(View.GONE);
              tv_author_more.setVisibility(View.GONE);
              tv_words_more.setVisibility(View.GONE);
              refreshLayout.setEnableLoadMore(true);
          }else {
              tv_article_more.setVisibility(View.VISIBLE);
              tv_album_more.setVisibility(View.VISIBLE);
              tv_author_more.setVisibility(View.VISIBLE);
              tv_words_more.setVisibility(View.VISIBLE);
              refreshLayout.setEnableLoadMore(false);
          }
          searchWord = getArguments().getString("word");
          String searchResult = getArguments().getString("searchResult");
          if(searchWord != null && searchResult !=null && !searchWord.equals("") && !searchResult.equals("")){
              SearchResultBean searchResultBean = GsonUtil.GsonToBean(searchResult,SearchResultBean.class);
              setData(searchResultBean);
          }
      }
    }

    private void initView() {
        ll_words = view.findViewById(R.id.ll_words);
        ll_author = view.findViewById(R.id.ll_author);
        ll_album = view.findViewById(R.id.ll_album);
        ll_article = view.findViewById(R.id.ll_article);
        lwl_words = view.findViewById(R.id.lwl_words);
        rcl_author = view.findViewById(R.id.rcl_author);
        rcl_album = view.findViewById(R.id.rcl_album);
        rcl_article = view.findViewById(R.id.rcl_article);
        tv_words_more = view.findViewById(R.id.tv_words_more);
        tv_author_more = view.findViewById(R.id.tv_author_more);
        tv_album_more = view.findViewById(R.id.tv_album_more);
        tv_article_more = view.findViewById(R.id.tv_article_more);
        nsl_search_all = view.findViewById(R.id.nsl_search_all);
        rcl_author.setNestedScrollingEnabled(false);
        rcl_album.setNestedScrollingEnabled(false);
        rcl_article.setNestedScrollingEnabled(false);
        refreshLayout = view.findViewById(R.id.smart);
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);

        searchAuthorAdapter = new SearchAuthorAdapter(authorListBeans,mContext,false);
        LinearLayoutManager layoutManager_user = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rcl_author.setLayoutManager(layoutManager_user);
        rcl_author.setAdapter(searchAuthorAdapter);

        searchAlbumAdapter = new SearchAlbumAdapter(albumListBeans,mContext);
        LinearLayoutManager layoutManager_album = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rcl_album.setLayoutManager(layoutManager_album);
        rcl_album.setAdapter(searchAlbumAdapter);

        searchArticleAdapter = new SearchArticleAdapter(articleListBeans,mContext);
        LinearLayoutManager layoutManager_article = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        rcl_article.setLayoutManager(layoutManager_article);
        rcl_article.setAdapter(searchArticleAdapter);
    }

    private void initEvents() {
        tv_words_more.setOnClickListener(this);
        tv_author_more.setOnClickListener(this);
        tv_album_more.setOnClickListener(this);
        tv_article_more.setOnClickListener(this);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMoreData();
            }
        });
    }

    private void getMoreData() {
        pageNo ++;
        JSONObject params = new JSONObject();
        try {
            params.put("query",searchWord);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("width",750);
            params.put("height",420);
            params.put("pageNum",pageNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(mContext).requestAsyn(searchUrl, OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                SearchResultBean searchResultBean = GsonUtil.GsonToBean(result.toString(),SearchResultBean.class);
                if(searchResultBean != null){
                    if(searchResultBean.getStatus() == 200){
                        for(int i=0;i<searchResultBean.getData().getRet_array().size();i++ ){
                            int type = searchResultBean.getData().getRet_array().get(i).getType();
                             if(type == 5){
                                 List<SubListBean> subListBean = searchResultBean.getData().getRet_array().get(i).getSubList();
                                 searchArticleAdapter.addData(subListBean);
                            }
                        }

                    }else if(searchResultBean.getStatus() == 400){

                    }
                }
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                refreshLayout.finishLoadMore();
            }
        });
    }

    public void setData(SearchResultBean searchResultBean){
        this.mData = searchResultBean;
        ll_words.setVisibility(View.GONE);
        ll_author.setVisibility(View.GONE);
        ll_album.setVisibility(View.GONE);
        ll_article.setVisibility(View.GONE);
        for(int i=0;i<mData.getData().getRet_array().size();i++ ){
            int type = mData.getData().getRet_array().get(i).getType();
            if(type == 2){
                {
                    ll_words.setVisibility(View.VISIBLE);
                    lwl_words.removeAllViews();
                    for (int j = 0;j <mData.getData().getRet_array().get(i).getSubList().size();j++){
                        View child = View.inflate(mContext,R.layout.item_search_hot,null);
                        TextView textView = child.findViewById(R.id.tv_num);
                        final String hotWord = mData.getData().getRet_array().get(i).getSubList().get(j).getTitle();
                        final String url = mData.getData().getRet_array().get(i).getSubList().get(j).getSource();
                        textView.setText(hotWord);
                        lwl_words.addView(child);
                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                WordDetailActivity
                                Intent intent = new Intent(getActivity(),WordDetailActivity.class);
                                intent.putExtra("url",url);
                                intent.putExtra("essayId","-1");
                                intent.putExtra("sourceType","2");
                                intent.putExtra("title",hotWord);
                                intent.putExtra("word",hotWord);
                                startActivity(intent);

                            }
                        });
                    }
                    nsl_search_all.scrollTo(0,0);
                }
            }else if(type == 3){
                ll_author.setVisibility(View.VISIBLE);
                searchAuthorAdapter.refreshData(mData.getData().getRet_array().get(i).getSubList());
            }else if(type == 4){
                ll_album.setVisibility(View.VISIBLE);
                searchAlbumAdapter.refreshData(mData.getData().getRet_array().get(i).getSubList());
            }else if(type == 5){
                ll_article.setVisibility(View.VISIBLE);
                searchArticleAdapter.refreshData(mData.getData().getRet_array().get(i).getSubList());
            }
        }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.tv_words_more:
              if(onMoreClick != null)
                  onMoreClick.onClick(1);
              break;
          case R.id.tv_author_more:
              if(onMoreClick != null)
                  onMoreClick.onClick(2);
              break;
          case R.id.tv_album_more:
              if(onMoreClick != null)
                  onMoreClick.onClick(3);
              break;
          case R.id.tv_article_more:
              if(onMoreClick != null)
                  onMoreClick.onClick(4);
              break;
      }
    }

    public interface OnMoreClick{
      //1 字词更多 2 作者更多 3 专辑更多 4 文章更多
      void onClick(int type);
    }

    private OnMoreClick onMoreClick;

  public void setOnMoreClick(OnMoreClick onMoreClick){
      this.onMoreClick = onMoreClick;
  }
}
