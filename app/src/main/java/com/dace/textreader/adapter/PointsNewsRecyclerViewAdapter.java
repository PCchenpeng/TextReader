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
import com.dace.textreader.bean.PointsNews;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * 收到的赞的列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class PointsNewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<PointsNews> mList;

    public PointsNewsRecyclerViewAdapter(Context context, List<PointsNews> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_points_news_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final PointsNews pointsNews = mList.get(position);
        GlideUtils.loadUserImage(mContext,
//                HttpUrlPre.FILE_URL +
                        pointsNews.getUserImg(),
                ((ViewHolder) holder).iv_user);
        ((ViewHolder) holder).tv_user.setText(pointsNews.getUsername());
        ((ViewHolder) holder).tv_duration.setText(pointsNews.getDuration());
        if (pointsNews.getContentType().equals("0")) {
            ((ViewHolder) holder).tv_tips.setText("赞了你的读后感");
        } else if (pointsNews.getContentType().equals("1")) {
            ((ViewHolder) holder).tv_tips.setText("赞了你的作文");
        }
        ((ViewHolder) holder).tv_title.setText(pointsNews.getTitle());
        ((ViewHolder) holder).iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToUserHomePage(pointsNews.getUserId());
            }
        });
        ((ViewHolder) holder).tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPointsNewsItemClick != null) {
                    mOnPointsNewsItemClick.onClick(holder.getAdapterPosition());
                }
            }
        });
    }

    /**
     * 前往用户主页
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

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public interface OnPointsNewsItemClick {
        void onClick(int position);
    }

    private OnPointsNewsItemClick mOnPointsNewsItemClick;

    public void setOnPointsNewsItemClick(OnPointsNewsItemClick onPointsNewsItemClick) {
        this.mOnPointsNewsItemClick = onPointsNewsItemClick;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user;
        TextView tv_user;
        TextView tv_duration;
        TextView tv_tips;
        TextView tv_title;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_user = itemView.findViewById(R.id.iv_user_image_points_news_item);
            tv_user = itemView.findViewById(R.id.tv_user_name_points_news_item);
            tv_duration = itemView.findViewById(R.id.tv_duration_points_news_item);
            tv_tips = itemView.findViewById(R.id.tv_tips_points_news_item);
            tv_title = itemView.findViewById(R.id.tv_title_points_news_item);
        }
    }
}
