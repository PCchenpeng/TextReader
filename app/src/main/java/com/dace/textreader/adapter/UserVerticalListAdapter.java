package com.dace.textreader.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/10/25 0025 上午 10:28.
 * Version   1.0;
 * Describe :  纵向用户布局列表适配器
 * History:
 * ==============================================================================
 */
public class UserVerticalListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<UserBean> mList;

    public UserVerticalListAdapter(Context context, List<UserBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_vertical_list_layout, viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            UserBean userBean = mList.get(position);
            for (String key : bundle.keySet()) {
                switch (key) {
                    case "followed":
                        if (NewMainActivity.STUDENT_ID == userBean.getUserId() || userBean.getUserId() == -1) {
                            ((ViewHolder) holder).rl_follow.setVisibility(View.GONE);
                        } else {
                            ((ViewHolder) holder).rl_follow.setVisibility(View.VISIBLE);
                            if (userBean.getFollowed() == 1) {
                                ((ViewHolder) holder).rl_follow.setSelected(true);
                                ((ViewHolder) holder).iv_follow.setVisibility(View.GONE);
                                ((ViewHolder) holder).tv_follow.setText("已关注");
                            } else {
                                ((ViewHolder) holder).rl_follow.setSelected(false);
                                ((ViewHolder) holder).iv_follow.setVisibility(View.VISIBLE);
                                ((ViewHolder) holder).tv_follow.setText("关注");
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        UserBean userBean = mList.get(i);
        GlideUtils.loadUserImage(mContext,  userBean.getUserImage(),
                ((ViewHolder) viewHolder).iv_user);
        ((ViewHolder) viewHolder).tv_user.setText(userBean.getUsername());
        ((ViewHolder) viewHolder).tv_grade.setText(userBean.getUserGrade());
        String number = userBean.getCompositionNum() + "篇作文";
        ((ViewHolder) viewHolder).tv_number.setText(number);
        if (NewMainActivity.STUDENT_ID == userBean.getUserId() || userBean.getUserId() == -1) {
            ((ViewHolder) viewHolder).rl_follow.setVisibility(View.GONE);
        } else {
            ((ViewHolder) viewHolder).rl_follow.setVisibility(View.VISIBLE);
            if (userBean.getFollowed() == 1) {
                ((ViewHolder) viewHolder).rl_follow.setSelected(true);
                ((ViewHolder) viewHolder).iv_follow.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tv_follow.setText("已关注");
            } else {
                ((ViewHolder) viewHolder).rl_follow.setSelected(false);
                ((ViewHolder) viewHolder).iv_follow.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tv_follow.setText("关注");
            }
        }
        ((ViewHolder) viewHolder).rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemFollowClickListen != null) {
                    onItemFollowClickListen.onFollowClick(i);
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

    public interface OnItemFollowClickListen {
        void onFollowClick(int position);
    }

    private OnItemFollowClickListen onItemFollowClickListen;

    public void setOnItemFollowClickListen(OnItemFollowClickListen onItemFollowClickListen) {
        this.onItemFollowClickListen = onItemFollowClickListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_user;
        TextView tv_user;
        TextView tv_grade;
        TextView tv_number;
        RelativeLayout rl_follow;
        ImageView iv_follow;
        TextView tv_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_user = itemView.findViewById(R.id.iv_user_vertical_list_item);
            tv_user = itemView.findViewById(R.id.tv_user_vertical_list_item);
            tv_grade = itemView.findViewById(R.id.tv_grade_user_vertical_list_item);
            tv_number = itemView.findViewById(R.id.tv_number_user_vertical_list_item);
            rl_follow = itemView.findViewById(R.id.rl_follow_user_vertical_list_item);
            iv_follow = itemView.findViewById(R.id.iv_follow_user_vertical_list_item);
            tv_follow = itemView.findViewById(R.id.tv_follow_user_vertical_list_item);
        }
    }

}
