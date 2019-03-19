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
import com.dace.textreader.bean.LiveShowLessonBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2019/1/25 0025 下午 4:32.
 * Version   1.0;
 * Describe :  直播课列表适配器
 * History:
 * ==============================================================================
 */
public class LiveShowLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<LiveShowLessonBean> mList;
    private boolean activated;

    public LiveShowLessonAdapter(Context context, List<LiveShowLessonBean> list, boolean activated) {
        this.mContext = context;
        this.mList = list;
        this.activated = activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_show_lesson_layout,
                viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            LiveShowLessonBean bean = mList.get(position);
            if (bean.isSelected()) {
                ((ViewHolder) holder).iv_selected.setImageResource(R.drawable.icon_edit_selected);
            } else {
                ((ViewHolder) holder).iv_selected.setImageResource(R.drawable.icon_edit_unselected);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        LiveShowLessonBean bean = mList.get(i);
        if (activated) {
            ((ViewHolder) viewHolder).iv_selected.setVisibility(View.VISIBLE);
            if (bean.isSelected()) {
                ((ViewHolder) viewHolder).iv_selected.setImageResource(R.drawable.icon_edit_selected);
            } else {
                ((ViewHolder) viewHolder).iv_selected.setImageResource(R.drawable.icon_edit_unselected);
            }
        } else {
            ((ViewHolder) viewHolder).iv_selected.setVisibility(View.GONE);
        }
        GlideUtils.loadSmallImage(mContext, bean.getImage(), ((ViewHolder) viewHolder).imageView);
        ((ViewHolder) viewHolder).tv_title.setText(bean.getTitle());
        ((ViewHolder) viewHolder).tv_content.setText(bean.getContent());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListen != null) {
            mOnItemClickListen.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_selected;
        ImageView imageView;
        TextView tv_title;
        TextView tv_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_selected = itemView.findViewById(R.id.iv_selected_live_show_lesson_item);
            imageView = itemView.findViewById(R.id.iv_live_show_lesson_item);
            tv_title = itemView.findViewById(R.id.tv_title_live_show_lesson_item);
            tv_content = itemView.findViewById(R.id.tv_content_live_show_lesson_item);
        }
    }

}
