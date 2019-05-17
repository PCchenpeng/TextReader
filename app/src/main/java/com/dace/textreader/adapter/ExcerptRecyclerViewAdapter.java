package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.AuthorActivity;
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.GlossaryWordExplainActivity;
import com.dace.textreader.bean.ExcerptBean;

import java.util.List;

/**
 * 摘抄列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class ExcerptRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<ExcerptBean> mList;

    public ExcerptRecyclerViewAdapter(Context context, List<ExcerptBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_excerpt_list_layout, parent, false);
        //给布局设置点击监听
        ViewHolder holder = new ViewHolder(view);
//        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ExcerptBean excerptBean = mList.get(position);
        if (excerptBean.isEditor()) {
            ((ViewHolder) holder).iv_select.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_select.setVisibility(View.GONE);
        }
        if (excerptBean.isSelected()) {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_unselected);
        }
        final String excerpt = excerptBean.getExcerpt();
        ((ViewHolder) holder).tv_excerpt.setText(excerpt);
        final int sourceType = excerptBean.getSourceType();
        String title = excerptBean.getEssayTitle();
        if (sourceType == 0 || sourceType == 1) {
            title = "《" + title + "》";
        } else if (sourceType == 2) {
            title = "词堆：" + title;
        } else if (sourceType == 3) {
            title = "作者信息：" + title;
        }
        ((ViewHolder) holder).tv_title.setText(title);
//        ((ViewHolder) holder).tv_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sourceType == 0) {
//                    turnToComposition(String.valueOf(excerptBean.getEssayId()), excerptBean.getEssayType());
//                } else if (sourceType == 1) {
//                    turnToArticle(excerptBean.getEssayId(), excerptBean.getEssayType());
//                } else if (sourceType == 2) {
//                    turnToGlossary(excerptBean.getEssayId(), excerptBean.getEssayTitle());
//                } else if (sourceType == 3) {
//                    turnToAuthor(excerptBean.getEssayTitle());
//                }
//            }
//        });
        ((ViewHolder) holder).tv_time.setText(excerptBean.getTime());
        ((ViewHolder) holder).ll_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ViewHolder) holder).tv_excerpt.getMaxLines() == 2) {
                    ((ViewHolder) holder).tv_excerpt.setMaxLines(Integer.MAX_VALUE);
                    ((ViewHolder) holder).tv_excerpt.setText(excerpt);
                    ((ViewHolder) holder).iv_more.setImageResource(R.drawable.ic_expand_less_black_36dp);
                    ((ViewHolder) holder).tv_more.setText("收起");
                } else {
                    ((ViewHolder) holder).tv_excerpt.setMaxLines(2);
                    ((ViewHolder) holder).tv_excerpt.setText(excerpt);
                    ((ViewHolder) holder).iv_more.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    ((ViewHolder) holder).tv_more.setText("展开");
                }
            }
        });

        ((ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(excerptBean);
            }
        });
    }

    /**
     * 前往作文详情
     *
     * @param compositionId
     * @param area
     */
    private void turnToComposition(String compositionId, int area) {
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", compositionId);
        intent.putExtra("area", area);
        mContext.startActivity(intent);
    }

    /**
     * 前往文章详情
     *
     * @param essayId
     * @param essayType
     */
//    private void turnToArticle(long essayId, int essayType) {
//        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
//        intent.putExtra("id", essayId);
//        intent.putExtra("type", essayType);
//        mContext.startActivity(intent);
//    }

    /**
     * 前往生词解释
     *
     * @param word
     */
    private void turnToGlossary(long id, String word) {
        Intent intent = new Intent(mContext, GlossaryWordExplainActivity.class);
        intent.putExtra("glossaryId", id);
        intent.putExtra("words", word);
        intent.putExtra("essayTitle", "");
        intent.putExtra("glossaryTitle", word);
        mContext.startActivity(intent);
    }

    /**
     * 前往作者信息
     *
     * @param author
     */
    private void turnToAuthor(String author) {
        Intent intent = new Intent(mContext, AuthorActivity.class);
        intent.putExtra("author", author);
        intent.putExtra("id", -1);
        intent.putExtra("readId", -1);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

//    @Override
//    public void onClick(View v) {
//        if (mOnItemClickListener != null) {
//            mOnItemClickListener.onItemClick(v);
//        }
//    }

    public interface OnExcerptItemClick {
        void onItemClick(ExcerptBean itemData);
    }

    private OnExcerptItemClick mOnItemClickListener;

    public void setOnItemClickListener(OnExcerptItemClick listener) {
        mOnItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_select;
        TextView tv_excerpt;
        LinearLayout ll_more;
        ImageView iv_more;
        TextView tv_more;
        TextView tv_title;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_select = itemView.findViewById(R.id.iv_select_excerpt_item);
            tv_excerpt = itemView.findViewById(R.id.tv_content_excerpt_item);
            ll_more = itemView.findViewById(R.id.ll_more_excerpt_item);
            iv_more = itemView.findViewById(R.id.iv_more_excerpt_item);
            tv_more = itemView.findViewById(R.id.tv_more_excerpt_item);
            tv_title = itemView.findViewById(R.id.tv_title_excerpt_item);
            tv_time = itemView.findViewById(R.id.tv_time_excerpt_item);
        }
    }
}
