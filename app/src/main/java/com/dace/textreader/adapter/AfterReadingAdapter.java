package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.AfterReadingBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/10/31 0031 下午 3:04.
 * Version   1.0;
 * Describe :  用户个人读后感列表适配器
 * History:
 * ==============================================================================
 */
public class AfterReadingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<AfterReadingBean> mList;

    public AfterReadingAdapter(Context context, List<AfterReadingBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_after_reading_layout,
                viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        AfterReadingBean bean = mList.get(i);
        if (bean.isEditor()) {
            ((ViewHolder) viewHolder).iv_editor.setVisibility(View.VISIBLE);
            if (bean.isSelected()) {
                ((ViewHolder) viewHolder).iv_editor.setImageResource(R.drawable.icon_edit_selected);
            } else {
                ((ViewHolder) viewHolder).iv_editor.setImageResource(R.drawable.icon_edit_unselected);
            }
        } else {
            ((ViewHolder) viewHolder).iv_editor.setVisibility(View.GONE);
        }
        ((ViewHolder) viewHolder).tv_title.setText(bean.getEssayTitle());
        ((ViewHolder) viewHolder).tv_content.setText(bean.getContent());
        ((ViewHolder) viewHolder).tv_date.setText(bean.getDate());
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

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_editor;
        TextView tv_title;
        TextView tv_content;
        TextView tv_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_editor = itemView.findViewById(R.id.iv_editor_user_after_reading_item);
            tv_title = itemView.findViewById(R.id.tv_title_user_after_reading_item);
            tv_content = itemView.findViewById(R.id.tv_content_user_after_reading_item);
            tv_date = itemView.findViewById(R.id.tv_date_user_after_reading_item);
        }
    }

}
