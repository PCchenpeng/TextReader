package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.WritingBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/9 0009 下午 4:52.
 * Version   1.0;
 * Describe :  我的作文列表适配器
 * History:
 * ==============================================================================
 */
public class MyCompositionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<WritingBean> mList;

    public MyCompositionAdapter(Context context, List<WritingBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_composition_layout,
                viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        WritingBean bean = mList.get(i);
        if (bean.isEditor()) {
            ((ViewHolder) viewHolder).iv_selected.setVisibility(View.VISIBLE);
            if (bean.isSelected()) {
                ((ViewHolder) viewHolder).iv_selected.setImageResource(R.drawable.icon_edit_selected);
            } else {
                ((ViewHolder) viewHolder).iv_selected.setImageResource(R.drawable.icon_edit_unselected);
            }
        } else {
            ((ViewHolder) viewHolder).iv_selected.setVisibility(View.GONE);
        }

        if (bean.getType() == 3) {
            ((ViewHolder) viewHolder).iv_head.setImageResource(R.drawable.icon_writing_papers);
        } else if (bean.getType() == 2) {
            ((ViewHolder) viewHolder).iv_head.setImageResource(R.drawable.icon_writing_status_active);
        } else {
            ((ViewHolder) viewHolder).iv_head.setImageResource(R.drawable.icon_writing_status_nor);
        }

        if (bean.getIsPublic() == 1) {
            ((ViewHolder) viewHolder).iv_public.setVisibility(View.GONE);
        } else {
            ((ViewHolder) viewHolder).iv_public.setVisibility(View.VISIBLE);
        }

        ((ViewHolder) viewHolder).tv_title.setText(bean.getTitle());

        String num = bean.getWordsNum() + "字";
        ((ViewHolder) viewHolder).tv_number.setText(num);

        ((ViewHolder) viewHolder).tv_date.setText(bean.getDate());

        if (bean.getCover().equals("") || bean.getCover().equals("null")) {
            ((ViewHolder) viewHolder).iv_cover.setVisibility(View.GONE);
        } else {
            ((ViewHolder) viewHolder).iv_cover.setVisibility(View.VISIBLE);
            GlideUtils.loadSquareImage(mContext, bean.getCover(), ((ViewHolder) viewHolder).iv_cover);
        }

        if (bean.getTaskId().equals("") || bean.getTaskId().equals("null") ||
                bean.getTaskName().equals("") || bean.getTaskName().equals("null")) {
            ((ViewHolder) viewHolder).ll_task.setVisibility(View.GONE);
        } else {
            String task;
            if (bean.getType() == 2) {
                task = "比赛：" + bean.getTaskName();
            } else {
                task = "征稿：" + bean.getTaskName();
            }
            ((ViewHolder) viewHolder).tv_task.setText(task);
            ((ViewHolder) viewHolder).ll_task.setVisibility(View.VISIBLE);
        }

        int index = bean.getIndex();  //哪个fragment，0草稿，1发布，2批改，3作业，4活动

        if (index == 4) {  //活动
            ((ViewHolder) viewHolder).tv_status.setVisibility(View.VISIBLE);
            if (bean.getStatus() == 0) {
                ((ViewHolder) viewHolder).tv_status.setText("活动结束");
                ((ViewHolder) viewHolder).tv_status.setTextColor(Color.parseColor("#999999"));
            } else if (bean.getStatus() == 1) {
                ((ViewHolder) viewHolder).tv_status.setText("征稿中");
                ((ViewHolder) viewHolder).tv_status.setTextColor(Color.parseColor("#46CA61"));
            } else if (bean.getStatus() == 2) {
                ((ViewHolder) viewHolder).tv_status.setText("评选中");
                ((ViewHolder) viewHolder).tv_status.setTextColor(Color.parseColor("#FF9933"));
            } else {
                ((ViewHolder) viewHolder).tv_status.setVisibility(View.GONE);
            }
        } else if (index == 2 || index == 3) {  //批改、作业
            ((ViewHolder) viewHolder).tv_status.setVisibility(View.VISIBLE);
            if (bean.getStatus() == 0) {
                ((ViewHolder) viewHolder).tv_status.setText("批改中");
                ((ViewHolder) viewHolder).tv_status.setTextColor(Color.parseColor("#FF9933"));
            } else if (bean.getStatus() == 2) {
                ((ViewHolder) viewHolder).tv_status.setText("批改超时已退款");
                ((ViewHolder) viewHolder).tv_status.setTextColor(Color.parseColor("#FF2F2F"));
            } else if (bean.getStatus() == 1) {
                ((ViewHolder) viewHolder).tv_status.setText("批改完成");
                ((ViewHolder) viewHolder).tv_status.setTextColor(Color.parseColor("#46CA61"));
            } else {
                ((ViewHolder) viewHolder).tv_status.setVisibility(View.GONE);
            }
        } else {  //草稿、发布
            ((ViewHolder) viewHolder).tv_status.setVisibility(View.GONE);
        }

        if (index == 0) {
            ((ViewHolder) viewHolder).tv_operate_one.setText("批改");
            ((ViewHolder) viewHolder).tv_operate_one.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_two.setText("发布");
            ((ViewHolder) viewHolder).tv_operate_two.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_three.setText("分享");
            ((ViewHolder) viewHolder).tv_operate_three.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_more.setVisibility(View.VISIBLE);
        } else if (index == 1 || index == 3) {
            ((ViewHolder) viewHolder).tv_operate_one.setText("批改");
            ((ViewHolder) viewHolder).tv_operate_one.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_two.setText("编辑");
            ((ViewHolder) viewHolder).tv_operate_two.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_three.setText("分享");
            ((ViewHolder) viewHolder).tv_operate_three.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_more.setVisibility(View.VISIBLE);
        } else if (index == 2) {
            ((ViewHolder) viewHolder).tv_operate_one.setText("编辑");
            ((ViewHolder) viewHolder).tv_operate_one.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_two.setText("发布");
            ((ViewHolder) viewHolder).tv_operate_two.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_three.setText("分享");
            ((ViewHolder) viewHolder).tv_operate_three.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).tv_operate_more.setVisibility(View.VISIBLE);
        } else if (index == 4) {
            if (bean.getStatus() == 1) {  //征稿中
                if (bean.getType() == 2) {  //比赛作文
                    ((ViewHolder) viewHolder).tv_operate_one.setText("编辑");
                    ((ViewHolder) viewHolder).tv_operate_one.setVisibility(View.VISIBLE);
                    ((ViewHolder) viewHolder).tv_operate_two.setText("分享");
                    ((ViewHolder) viewHolder).tv_operate_two.setVisibility(View.VISIBLE);
                    ((ViewHolder) viewHolder).tv_operate_three.setText("删除");
                    ((ViewHolder) viewHolder).tv_operate_three.setVisibility(View.VISIBLE);
                    ((ViewHolder) viewHolder).tv_operate_more.setVisibility(View.GONE);
                } else {
                    ((ViewHolder) viewHolder).tv_operate_one.setText("批改");
                    ((ViewHolder) viewHolder).tv_operate_one.setVisibility(View.VISIBLE);
                    ((ViewHolder) viewHolder).tv_operate_two.setText("编辑");
                    ((ViewHolder) viewHolder).tv_operate_two.setVisibility(View.VISIBLE);
                    ((ViewHolder) viewHolder).tv_operate_three.setText("分享");
                    ((ViewHolder) viewHolder).tv_operate_three.setVisibility(View.VISIBLE);
                    ((ViewHolder) viewHolder).tv_operate_more.setVisibility(View.VISIBLE);
                }
            } else {
                ((ViewHolder) viewHolder).tv_operate_one.setText("编辑");
                ((ViewHolder) viewHolder).tv_operate_one.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tv_operate_two.setText("分享");
                ((ViewHolder) viewHolder).tv_operate_two.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tv_operate_three.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tv_operate_more.setVisibility(View.GONE);
            }
        }

        ((ViewHolder) viewHolder).tv_operate_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemOperateOneClick != null) {
                    onItemOperateOneClick.onClick(i);
                }
            }
        });
        ((ViewHolder) viewHolder).tv_operate_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemOperateTwoClick != null) {
                    onItemOperateTwoClick.onClick(i);
                }
            }
        });
        ((ViewHolder) viewHolder).tv_operate_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemOperateThreeClick != null) {
                    onItemOperateThreeClick.onClick(i);
                }
            }
        });
        ((ViewHolder) viewHolder).tv_operate_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemOperateMoreClick != null) {
                    onItemOperateMoreClick.onClick(i);
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

    public interface OnItemOperateOneClick {
        void onClick(int position);
    }

    private OnItemOperateOneClick onItemOperateOneClick;

    public void setOnItemOperateOneClick(OnItemOperateOneClick onItemOperateOneClick) {
        this.onItemOperateOneClick = onItemOperateOneClick;
    }

    public interface OnItemOperateTwoClick {
        void onClick(int position);
    }

    private OnItemOperateTwoClick onItemOperateTwoClick;

    public void setOnItemOperateTwoClick(OnItemOperateTwoClick onItemOperateTwoClick) {
        this.onItemOperateTwoClick = onItemOperateTwoClick;
    }

    public interface OnItemOperateThreeClick {
        void onClick(int position);
    }

    private OnItemOperateThreeClick onItemOperateThreeClick;

    public void setOnItemOperateThreeClick(OnItemOperateThreeClick onItemOperateThreeClick) {
        this.onItemOperateThreeClick = onItemOperateThreeClick;
    }

    public interface OnItemOperateMoreClick {
        void onClick(int position);
    }

    private OnItemOperateMoreClick onItemOperateMoreClick;

    public void setOnItemOperateMoreClick(OnItemOperateMoreClick onItemOperateMoreClick) {
        this.onItemOperateMoreClick = onItemOperateMoreClick;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_selected;
        ImageView iv_head;
        ImageView iv_public;
        TextView tv_title;
        TextView tv_number;
        TextView tv_date;
        TextView tv_status;
        ImageView iv_cover;
        LinearLayout ll_task;
        TextView tv_task;
        TextView tv_operate_one;
        TextView tv_operate_two;
        TextView tv_operate_three;
        TextView tv_operate_more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_selected = itemView.findViewById(R.id.iv_selected_my_composition_item);
            iv_head = itemView.findViewById(R.id.iv_head_my_composition_item);
            iv_public = itemView.findViewById(R.id.iv_public_my_composition_item);
            tv_title = itemView.findViewById(R.id.tv_title_my_composition_item);
            tv_number = itemView.findViewById(R.id.tv_number_my_composition_item);
            tv_date = itemView.findViewById(R.id.tv_date_my_composition_item);
            tv_status = itemView.findViewById(R.id.tv_status_my_composition_item);
            iv_cover = itemView.findViewById(R.id.iv_cover_my_composition_item);
            ll_task = itemView.findViewById(R.id.ll_task_my_composition_item);
            tv_task = itemView.findViewById(R.id.tv_task_my_composition_item);
            tv_operate_one = itemView.findViewById(R.id.tv_operate_one_my_composition_item);
            tv_operate_two = itemView.findViewById(R.id.tv_operate_two_my_composition_item);
            tv_operate_three = itemView.findViewById(R.id.tv_operate_three_my_composition_item);
            tv_operate_more = itemView.findViewById(R.id.tv_operate_more_my_composition_item);

        }
    }

}
