package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.bean.WeekRankUser;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * 阅读排行榜的列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class RankRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<WeekRankUser> mList;

    public RankRecyclerViewAdapter(Context mContext, List<WeekRankUser> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.rank_total_item_layout, parent, false);
        TotalViewHolder holder = new TotalViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final WeekRankUser total = mList.get(position);
        if (position == 0) {
            ((TotalViewHolder) holder).iv_rank.setVisibility(View.VISIBLE);
            ((TotalViewHolder) holder).tv_rank.setVisibility(View.GONE);
            ((TotalViewHolder) holder).iv_rank.setImageResource(R.drawable.icon_week_rank_one);
        } else if (position == 1) {
            ((TotalViewHolder) holder).iv_rank.setVisibility(View.VISIBLE);
            ((TotalViewHolder) holder).tv_rank.setVisibility(View.GONE);
            ((TotalViewHolder) holder).iv_rank.setImageResource(R.drawable.icon_week_rank_two);
        } else if (position == 2) {
            ((TotalViewHolder) holder).iv_rank.setVisibility(View.VISIBLE);
            ((TotalViewHolder) holder).tv_rank.setVisibility(View.GONE);
            ((TotalViewHolder) holder).iv_rank.setImageResource(R.drawable.icon_week_rank_three);
        } else {
            ((TotalViewHolder) holder).iv_rank.setVisibility(View.GONE);
            ((TotalViewHolder) holder).tv_rank.setVisibility(View.VISIBLE);
            ((TotalViewHolder) holder).tv_rank.setText(String.valueOf(position + 1));
        }
        GlideUtils.loadUserImage(mContext, HttpUrlPre.FILE_URL + total.getImage(),
                ((TotalViewHolder) holder).iv_head);
        ((TotalViewHolder) holder).tv_name.setText(total.getName());
        ((TotalViewHolder) holder).tv_time.setText(String.valueOf(total.getDuration()));
        ((TotalViewHolder) holder).iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToUserHomePage(total.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 前往用户首页
     *
     * @param userId
     */
    private void turnToUserHomePage(long userId) {
        if (userId != -1) {
            Intent intent = new Intent(mContext, UserHomepageActivity.class);
            intent.putExtra("userId", userId);
            mContext.startActivity(intent);
        }
    }

    static class TotalViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_rank;
        TextView tv_rank;
        ImageView iv_head;
        TextView tv_name;
        TextView tv_time;

        public TotalViewHolder(View itemView) {
            super(itemView);
            iv_rank = itemView.findViewById(R.id.iv_rank_item);
            tv_rank = itemView.findViewById(R.id.tv_rank_item);
            iv_head = itemView.findViewById(R.id.iv_head_rank_item);
            tv_name = itemView.findViewById(R.id.tv_username_rank_item);
            tv_time = itemView.findViewById(R.id.tv_time_rank_item);
        }
    }
}
