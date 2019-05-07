package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SubscriptionBean;
import com.dace.textreader.util.GlideUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/2/26 0026 下午 1:59.
 * Version   1.0;
 * Describe :  知识汇总列表适配器
 * History:
 * ==============================================================================
 */
public class MySubscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SubscriptionBean> mList;
    public List<SubscriptionChildAdapter> mListChildAdapter = new ArrayList<>();

    public MySubscriptionAdapter(Context context, List<SubscriptionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_my_subscription_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        SubscriptionBean SubscriptionBean = mList.get(i);
        ((ViewHolder) viewHolder).tv_title.setText(SubscriptionBean.getRetType());
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        ((ViewHolder) viewHolder).recyclerView.setLayoutManager(layoutManager);
        SubscriptionChildAdapter adapter = new SubscriptionChildAdapter(mContext, SubscriptionBean.getRetList(),mList.get(i).getRetType());
        adapter.setHasStableIds (true);
        ((DefaultItemAnimator) ((ViewHolder) viewHolder).recyclerView.getItemAnimator()).setSupportsChangeAnimations(false); // 取消动画效果
        ((ViewHolder) viewHolder).recyclerView.setAdapter(adapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setRemoveDuration(0);
        ((ViewHolder) viewHolder).recyclerView.setItemAnimator(defaultItemAnimator);
        mListChildAdapter.add(adapter);
        adapter.setOnItemClickListen(new SubscriptionChildAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view,int type) {
                int pos = -1;
                switch (type) {
                    case SubscriptionChildAdapter.TYPE_CONTENT:
                        pos = ((ViewHolder) viewHolder).recyclerView.getChildAdapterPosition(view);
                        break;
                    case SubscriptionChildAdapter.TYPE_DELETE:
                        pos =((ViewHolder) viewHolder).recyclerView.getChildAdapterPosition((View) view.getParent());
                        break;
                }
                if (mOnItemClickListen != null) {
                    mOnItemClickListen.onClick(i,type, pos);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListen {
        void onClick(int position,int type, int childPosition);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_knowledge_summary_item);
            recyclerView = itemView.findViewById(R.id.recycler_view_knowledge_summary_item);
        }
    }

}
