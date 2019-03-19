package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dace.textreader.R;
import com.dace.textreader.bean.TemplateBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/8 0008 下午 4:21.
 * Version   1.0;
 * Describe :  模板列表适配器
 * History:
 * ==============================================================================
 */
public class WritingTemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<TemplateBean> mList;

    public WritingTemplateAdapter(Context context, List<TemplateBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_writing_template_layout,
                viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        TemplateBean bean = mList.get(i);
        GlideUtils.loadSquareImage(mContext, bean.getImagePath(), ((ViewHolder) viewHolder).imageView);
        ((ViewHolder) viewHolder).relativeLayout.setSelected(bean.isSelected());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListen != null) {
            onItemClickListen.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen onItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.onItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_writing_template_item);
            relativeLayout = itemView.findViewById(R.id.rl_writing_template_item);
        }
    }

}
