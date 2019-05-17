package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.CollectArticleBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

/**
 * 文章收藏列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class ArticleCollectionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CollectArticleBean.DataBean> mList;

    public ArticleCollectionRecyclerViewAdapter(Context context, List<CollectArticleBean.DataBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_article_collection, parent, false);
        //给布局设置点击监听
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        CollectArticleBean.DataBean article = mList.get(position);
        if (article.isEditor()) {
            ((ViewHolder) holder).iv_select.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_select.setVisibility(View.GONE);
        }
        if (article.isSelected()) {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_unselected);
        }
        ((ViewHolder) holder).tv_title.setText(article.getTitle());
        ((ViewHolder) holder).tv_content.setText(article.getSubContent());
        String level = article.getScore() + "PY";
        ((ViewHolder) holder).tv_level.setText(level);
        ((ViewHolder) holder).tv_type.setText(article.getType());
//        String views = String.valueOf(article.get()) + "人阅读";
//        ((ViewHolder) holder).tv_views.setText(views);
        GlideUtils.loadSmallImage(mContext, article.getImage(), ((ViewHolder) holder).iv_article);
        ((ViewHolder) holder).tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewHolder) holder).swipeMenuLayout.quickClose();
                mOnDeleteArticleItemClick.onClick(position);
            }
        });
        ((ViewHolder) holder).ll_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnCollectionArticleItemClick {
        void onItemClick(int position);
    }

    private OnCollectionArticleItemClick mOnItemClickListener;

    public void setOnItemClickListener(OnCollectionArticleItemClick listener) {
        mOnItemClickListener = listener;
    }

    public interface OnDeleteArticleItemClick {
        void onClick(int position);
    }

    private OnDeleteArticleItemClick mOnDeleteArticleItemClick;

    public void setOnDeleteArticleItemClick(OnDeleteArticleItemClick onDeleteArticleItemClick) {
        this.mOnDeleteArticleItemClick = onDeleteArticleItemClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_view;
        ImageView iv_select;
        TextView tv_title;
        TextView tv_content;
        TextView tv_level;
        TextView tv_type;
        TextView tv_views;
        ImageView iv_article;
        //侧滑删除
        private SwipeMenuLayout swipeMenuLayout;
        private TextView tv_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeMenuLayout = itemView.findViewById(R.id.swip_menu_layout_article_collection_item);
            tv_delete = itemView.findViewById(R.id.tv_delete_new_collection_article_item);
            ll_view = itemView.findViewById(R.id.ll_new_collection_article_item);
            iv_select = itemView.findViewById(R.id.iv_select_new_collection_article_item);
            tv_title = itemView.findViewById(R.id.tv_title_new_collection_article_item);
            tv_content = itemView.findViewById(R.id.tv_content_new_collection_article_item);
            tv_level = itemView.findViewById(R.id.tv_level_new_collection_article_item);
            tv_type = itemView.findViewById(R.id.tv_type_new_collection_article_item);
            tv_views = itemView.findViewById(R.id.tv_views_new_collection_article_item);
            iv_article = itemView.findViewById(R.id.iv_new_collection_article_item);
        }
    }
}
