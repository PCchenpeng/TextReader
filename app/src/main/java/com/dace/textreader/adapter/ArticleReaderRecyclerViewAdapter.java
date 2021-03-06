package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.Article;
import com.dace.textreader.util.DataUtil;

import java.util.List;

/**
 * 课内文章的课外阅读推荐列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class ArticleReaderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<Article> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ArticleReaderRecyclerViewAdapter(Context mContext, List<Article> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_article_reader_list_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(article.getTitle());
        ((ViewHolder) holder).tv_type.setText(DataUtil.typeConversion(article.getType()));
        String views = String.valueOf(article.getViews()) + "人阅读";
        ((ViewHolder) holder).tv_views.setText(views);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_type;
        TextView tv_views;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_more_reader_list_item);
            tv_type = itemView.findViewById(R.id.tv_type_more_reader_list_item);
            tv_views = itemView.findViewById(R.id.tv_views_more_reader_list_item);
        }
    }
}
