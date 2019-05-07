package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.AuthorDetailActivity;
import com.dace.textreader.activity.KnowledgeDetailActivity;
import com.dace.textreader.activity.MySubscriptionActivity;
import com.dace.textreader.activity.ReaderTabAlbumDetailActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.bean.SubscriptionChildBean;
import com.dace.textreader.bean.SubscriptionChildBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/2/26 0026 下午 2:01.
 * Version   1.0;
 * Describe :  知识汇总子项列表适配器
 * History:
 * ==============================================================================
 */
public class SubscriptionChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    public final static int TYPE_CONTENT = 1;
    public final static int TYPE_DELETE = 2;

    private Context mContext;
    private List<SubscriptionChildBean> mList;
    private String retType;

    public SubscriptionChildAdapter(Context context, List<SubscriptionChildBean> list,String retType) {
        this.mContext = context;
        this.mList = list;
        this.retType = retType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_subscription_child_layout, viewGroup, false);
        view.setOnClickListener(this);
        view.findViewById(R.id.iv_delete).setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (retType.equals("专辑")){
            ((ViewHolder) viewHolder).tv_title.setText(mList.get(i).getTitle());
        } else if (retType.equals("书")) {
            ((ViewHolder) viewHolder).tv_title.setText(mList.get(i).getTitle());
        } else if (retType.equals("创作者")) {
            ((ViewHolder) viewHolder).tv_title.setText(mList.get(i).getAuthor());
        } else if (retType.equals("作者")){
            ((ViewHolder) viewHolder).tv_title.setText(mList.get(i).getAuthor());
        }
        if (!mList.get(i).getCover().equals(((ViewHolder) viewHolder).iv_content.getTag(R.id.iv_content))) {//加载过一次,不再加载
            GlideUtils.loadHomeUserImage(mContext, mList.get(i).getCover(), ((ViewHolder) viewHolder).iv_content);
            ((ViewHolder) viewHolder).iv_content.setTag(R.id.iv_content, mList.get(i).getCover());
        }


        if (((MySubscriptionActivity)viewHolder.itemView.getContext()).isEditting){
            ((ViewHolder) viewHolder).iv_delete.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) viewHolder).iv_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListen != null) {
            switch (v.getId()) {
                case R.id.rl_subscription:
                        mOnItemClickListen.onClick(v,TYPE_CONTENT);
                    break;
                case R.id.iv_delete:
                    mOnItemClickListen.onClick(v,TYPE_DELETE);
                    break;
            }
        }
    }

    public interface OnItemClickListen {
        void onClick(View view,int type);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        ImageView iv_content;
        ImageView iv_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_content = itemView.findViewById(R.id.iv_content);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }

}
