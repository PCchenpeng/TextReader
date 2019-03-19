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
import com.dace.textreader.bean.TeacherBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/9/17 0017 上午 9:48.
 * Version   1.0;
 * Describe :老师列表适配器
 * History:
 * ==============================================================================
 */

public class WritingWorkTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<TeacherBean> mList;
    private boolean mShowSelectedIcon;
    private boolean showHasBind = true;

    public WritingWorkTeacherAdapter(Context context, List<TeacherBean> list, boolean showSelectedIcon) {
        this.mContext = context;
        this.mList = list;
        this.mShowSelectedIcon = showSelectedIcon;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_teacher_list_layout,
                parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        TeacherBean teacherBean = mList.get(position);
        ((ViewHolder) holder).tv_name.setText(teacherBean.getTeacherName());
        ((ViewHolder) holder).tv_organization.setText(teacherBean.getOrganization());
        if (teacherBean.getRelationStatus() == 0) {
            ((ViewHolder) holder).iv_selected.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_bind.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_status.setText("等待老师确认");
        } else if (teacherBean.getRelationStatus() == -1) {
            ((ViewHolder) holder).iv_selected.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_bind.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_status.setText("绑定不通过");
        } else if (teacherBean.getRelationStatus() == -2) {
            ((ViewHolder) holder).iv_selected.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_status.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_bind.setVisibility(View.VISIBLE);
        } else if (teacherBean.getRelationStatus() == 1) {
            ((ViewHolder) holder).tv_bind.setVisibility(View.GONE);
            if (mShowSelectedIcon) {
                ((ViewHolder) holder).iv_selected.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).tv_status.setVisibility(View.GONE);
                if (teacherBean.isSelected()) {
                    ((ViewHolder) holder).iv_selected.setImageResource(R.drawable.icon_edit_selected);
                } else {
                    ((ViewHolder) holder).iv_selected.setImageResource(R.drawable.icon_edit_unselected);
                }
            } else {
                ((ViewHolder) holder).iv_selected.setVisibility(View.GONE);
                ((ViewHolder) holder).tv_status.setVisibility(View.VISIBLE);
                if (showHasBind) {
                    ((ViewHolder) holder).tv_status.setText("已绑定");
                } else {
                    ((ViewHolder) holder).tv_status.setText("解除绑定");
                }
            }
        }
        ((ViewHolder) holder).tv_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemBindClickListen != null) {
                    onItemBindClickListen.onClick(position);
                }
            }
        });
        ((ViewHolder) holder).tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemUnBindClickListen != null) {
                    onItemUnBindClickListen.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClick != null) {
            mOnItemClick.onClick(v);
        }
    }

    /**
     * 设置是否显示已绑定字样
     *
     * @param showHasBind
     */
    public void setShowHasBind(boolean showHasBind) {
        this.showHasBind = showHasBind;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_organization;
        TextView tv_status;
        ImageView iv_selected;
        TextView tv_bind;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name_teacher_list_item);
            tv_organization = itemView.findViewById(R.id.tv_organization_teacher_list_item);
            tv_status = itemView.findViewById(R.id.tv_status_teacher_list_item);
            iv_selected = itemView.findViewById(R.id.iv_selected_teacher_list_item);
            tv_bind = itemView.findViewById(R.id.tv_bind_teacher_list_item);
        }
    }

    public interface OnTeacherListItemClickListen {
        void onClick(View view);
    }

    private OnTeacherListItemClickListen mOnItemClick;

    public void setOnItemClick(OnTeacherListItemClickListen onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    public interface OnTeacherItemBindClickListen {
        void onClick(int position);
    }

    private OnTeacherItemBindClickListen onItemBindClickListen;

    public void setOnTeacherItemBindClick(OnTeacherItemBindClickListen onItemBindClickListen) {
        this.onItemBindClickListen = onItemBindClickListen;
    }

    public interface OnTeacherItemUnBindClickListen {
        void onClick(int position);
    }

    private OnTeacherItemUnBindClickListen onItemUnBindClickListen;

    public void setOnTeacherItemUnBindClick(OnTeacherItemUnBindClickListen onItemUnBindClickListen) {
        this.onItemUnBindClickListen = onItemUnBindClickListen;
    }

}
