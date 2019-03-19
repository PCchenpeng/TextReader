package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SentenceBean;

import java.util.List;

/**
 * 每日一句收藏列表的适配器
 * Created by 70391 on 2017/9/28.
 */

public class SentenceCollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<SentenceBean> mList;

    public SentenceCollectionAdapter(Context context, List<SentenceBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_sentence_collection_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SentenceBean sentenceBean = mList.get(position);
        if (sentenceBean.isEditor()) {
            ((ViewHolder) holder).iv_select.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_select.setVisibility(View.GONE);
        }
        if (sentenceBean.isSelected()) {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_unselected);
        }
        ((ViewHolder) holder).tv_date.setText(sentenceBean.getDate());
        ((ViewHolder) holder).tv_content.setText(sentenceBean.getContent());
        ((ViewHolder) holder).tv_author.setText(sentenceBean.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v);
        }
    }

    public interface OnSentenceCollectionItemClick {
        void onItemClick(View view);
    }

    private OnSentenceCollectionItemClick mItemClickListener;

    public void setOnSentenceCollectionItemClick(OnSentenceCollectionItemClick listener) {
        mItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_select;
        TextView tv_date;
        TextView tv_content;
        TextView tv_author;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_select = itemView.findViewById(R.id.iv_select_new_collection_sentence_item);
            tv_date = itemView.findViewById(R.id.tv_date_sentence_collection_item);
            tv_content = itemView.findViewById(R.id.tv_content_sentence_collection_item);
            tv_author = itemView.findViewById(R.id.tv_author_sentence_collection_item);
        }
    }
}
