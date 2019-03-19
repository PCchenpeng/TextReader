package com.dace.textreader.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.util.DensityUtil;
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
 * Describe :  横向用户布局列表适配器
 * History:
 * ==============================================================================
 */
public class UserHorizontalListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private static final int TYPE_NORMAL = 0;  //正常数据
    private static final int TYPE_MORE = 1;  //更多数据

    private Context mContext;
    private List<UserBean> mList;

    public UserHorizontalListAdapter(Context context, List<UserBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == TYPE_MORE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_user_horizontal_list_more_layout,
                    viewGroup, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_user_horizontal_list_layout,
                    viewGroup, false);
        }
        view.setOnClickListener(this);

        RecyclerView.ViewHolder holder;
        if (i == TYPE_MORE) {
            holder = new ViewHolderMore(view);
        } else {
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (holder instanceof ViewHolder) {
                Bundle bundle = (Bundle) payloads.get(0);
                UserBean userBean = mList.get(position);
                for (String key : bundle.keySet()) {
                    switch (key) {
                        case "followed":
                            if (NewMainActivity.STUDENT_ID == userBean.getUserId()
                                    || userBean.getUserId() == -1) {
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
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (i == 0) {
            viewHolder.itemView.setPadding(DensityUtil.dip2px(mContext, 15),
                    0, 0, 0);
        } else {
            viewHolder.itemView.setPadding(0, 0, 0, 0);
        }
        if (viewHolder instanceof ViewHolder) {
            UserBean userBean = mList.get(i);
            GlideUtils.loadUserImage(mContext,
                    HttpUrlPre.FILE_URL + userBean.getUserImage(),
                    ((ViewHolder) viewHolder).iv_user);
            ((ViewHolder) viewHolder).tv_user.setText(userBean.getUsername());
            String number = userBean.getCompositionNum() + "篇作文";
            ((ViewHolder) viewHolder).tv_number.setText(number);
            if (userBean.getFollowed() == 1) {
                ((ViewHolder) viewHolder).rl_follow.setSelected(true);
                ((ViewHolder) viewHolder).iv_follow.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tv_follow.setText("已关注");
            } else {
                ((ViewHolder) viewHolder).rl_follow.setSelected(false);
                ((ViewHolder) viewHolder).iv_follow.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tv_follow.setText("关注");
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
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 0;
        } else {
            return mList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return TYPE_MORE;
        } else {
            return TYPE_NORMAL;
        }
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

        LinearLayout linearLayout;
        ImageView iv_user;
        TextView tv_user;
        TextView tv_number;
        RelativeLayout rl_follow;
        ImageView iv_follow;
        TextView tv_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ll_user_horizontal_list_item);
            iv_user = itemView.findViewById(R.id.iv_user_horizontal_list_item);
            tv_user = itemView.findViewById(R.id.tv_user_horizontal_list_item);
            tv_number = itemView.findViewById(R.id.tv_composition_number_user_horizontal_list_item);
            rl_follow = itemView.findViewById(R.id.rl_follow_user_horizontal_list_item);
            iv_follow = itemView.findViewById(R.id.iv_follow_user_horizontal_list_item);
            tv_follow = itemView.findViewById(R.id.tv_follow_user_horizontal_list_item);
        }
    }

    /**
     * 查看更多
     */
    class ViewHolderMore extends RecyclerView.ViewHolder {

        public ViewHolderMore(@NonNull View itemView) {
            super(itemView);
        }
    }

}
