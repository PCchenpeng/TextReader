package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dace.textreader.R;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/2 0002 下午 2:27.
 * Version   1.0;
 * Describe :  用户头像列表
 * History:
 * ==============================================================================
 */
public class UserImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<UserBean> mList;

    public UserImageAdapter(Context context, List<UserBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_image_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (i == 0) {
            viewHolder.itemView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.itemView.setVisibility(View.VISIBLE);
            UserBean userBean = mList.get(i - 1);
            GlideUtils.loadUserImage(mContext,
                     userBean.getUserImage(),
                    ((ViewHolder) viewHolder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_user_image_item);
        }
    }

}
