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
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.AfterReading;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * 读后感列表的适配器
 * Created by 70391 on 2017/9/28.
 */

public class AfterReadingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<AfterReading> mList;

    public AfterReadingRecyclerViewAdapter(Context context, List<AfterReading> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_after_reading_layout, parent, false);
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
            AfterReading afterReading = mList.get(position);
            for (String key : bundle.keySet()) {
                switch (key) {
                    case "isLiked":
                        if (afterReading.isLiked() == 1) {
                            ((ViewHolder) holder).iv_liker.setImageResource(R.drawable.bottom_points_selected);
                        } else {
                            ((ViewHolder) holder).iv_liker.setImageResource(R.drawable.bottom_points_unselected);
                        }
                        break;
                    case "likeNum":
                        ((ViewHolder) holder).tv_liker.setText(String.valueOf(afterReading.getLikeNum()));
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        AfterReading afterReading = mList.get(position);
        if (afterReading.isLiked() == 1) {
            ((ViewHolder) holder).iv_liker.setImageResource(R.drawable.bottom_points_selected);
        } else {
            ((ViewHolder) holder).iv_liker.setImageResource(R.drawable.bottom_points_unselected);
        }
        ((ViewHolder) holder).tv_name.setText(afterReading.getUsername());
        GlideUtils.loadUserImage(mContext,
//                HttpUrlPre.FILE_URL +
                        afterReading.getUserImg(),
                ((ViewHolder) holder).iv_head);
        ((ViewHolder) holder).tv_liker.setText(String.valueOf(afterReading.getLikeNum()));
        ((ViewHolder) holder).tv_date.setText(afterReading.getDate());
        ((ViewHolder) holder).tv_content.setText(afterReading.getFeeling());
        ((ViewHolder) holder).ll_liker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLikeItemClickListener != null) {
                    mOnLikeItemClickListener.onItemClick(holder.getAdapterPosition());
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
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    public interface OnAfterReadingItemClick {
        void onItemClick(View view);
    }

    private OnAfterReadingItemClick mOnItemClickListener;

    public void setOnItemClickListener(OnAfterReadingItemClick listener) {
        mOnItemClickListener = listener;
    }

    public interface OnAfterReadingLikeItemClick {
        void onItemClick(int position);
    }

    private OnAfterReadingLikeItemClick mOnLikeItemClickListener;

    public void setOnItemLikeClickListener(OnAfterReadingLikeItemClick listener) {
        mOnLikeItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_date;
        LinearLayout ll_liker;
        ImageView iv_liker;
        TextView tv_liker;
        TextView tv_content;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_head = itemView.findViewById(R.id.iv_head_after_reading);
            tv_name = itemView.findViewById(R.id.tv_username_after_reading);
            tv_date = itemView.findViewById(R.id.tv_date_after_reading);
            ll_liker = itemView.findViewById(R.id.ll_liker_after_reading);
            iv_liker = itemView.findViewById(R.id.iv_liker_after_reading);
            tv_liker = itemView.findViewById(R.id.tv_liker_after_reading);
            tv_content = itemView.findViewById(R.id.tv_content_after_reading);
        }
    }
}
