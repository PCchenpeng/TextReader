package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.LevelFragmentBean;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;
import com.dace.textreader.bean.SentenceListBean;

import java.util.List;

/**
 * 等级选择列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class SentenceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<SentenceListBean.DataBean> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    private OnItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public SentenceListAdapter(Context mContext, List<SentenceListBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_sentence_list_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SentenceListBean.DataBean dataBean = mList.get(position);
        ((ViewHolder) holder).tv_content_sentence_collection_item.setText(dataBean.getContent());
        ((ViewHolder) holder).tv_author_sentence_collection_item.setText(dataBean.getSource());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshData(List<SentenceListBean.DataBean> mList) {
//        this.itemData.clear();
//        this.itemData.addAll(itemata);
        this.mList = mList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_content_sentence_collection_item;
        TextView tv_author_sentence_collection_item;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_content_sentence_collection_item = itemView.findViewById(R.id.tv_content_sentence_collection_item);
            tv_author_sentence_collection_item = itemView.findViewById(R.id.tv_author_sentence_collection_item);
        }
    }
}
