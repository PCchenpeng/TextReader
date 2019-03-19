package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.Article;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * 不含文章类型的文章列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
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

    public ChildRecyclerViewAdapter(Context mContext, List<Article> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void addData(List<Article> list) {
        int start = mList.size();
        int count = list.size();
        mList.addAll(list);
        notifyItemRangeChanged(start, count);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.reader_list_item_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Article article = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(article.getTitle());
        ((ViewHolder) holder).tv_content.setText(article.getContent());
        String grade = article.getPyScore() + "PY";
        ((ViewHolder) holder).tv_level.setText(grade);
        String views = String.valueOf(article.getViews()) + "人阅读";
        ((ViewHolder) holder).tv_views.setText(views);
        if (article.getStatus() == 1) {
            ((ViewHolder) holder).tv_title.setTextColor(Color.parseColor("#999999"));
            ((ViewHolder) holder).tv_content.setTextColor(Color.parseColor("#999999"));
        } else {
            ((ViewHolder) holder).tv_title.setTextColor(Color.parseColor("#333333"));
            ((ViewHolder) holder).tv_content.setTextColor(Color.parseColor("#666666"));
        }
        GlideUtils.loadSmallImage(mContext, article.getImagePath(), ((ViewHolder) holder).iv_article);
        ((ViewHolder) holder).rl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteClickListen != null) {
                    mOnDeleteClickListen.onDelete(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_content;
        TextView tv_level;
        ImageView iv_article;
        TextView tv_views;
        RelativeLayout rl_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_reader_list_item);
            tv_content = itemView.findViewById(R.id.tv_content_reader_list_item);
            tv_level = itemView.findViewById(R.id.tv_level_reader_list_item);
            tv_views = itemView.findViewById(R.id.tv_views_reader_list_item);
            iv_article = itemView.findViewById(R.id.iv_reader_list_item);
            rl_delete = itemView.findViewById(R.id.rl_delete_reader_list_item);
        }
    }

    public interface OnDeleteClickListen {
        void onDelete(int position);
    }

    private OnDeleteClickListen mOnDeleteClickListen;

    public void setOnDeleteClickListen(OnDeleteClickListen onDeleteClickListen) {
        this.mOnDeleteClickListen = onDeleteClickListen;
    }
}
