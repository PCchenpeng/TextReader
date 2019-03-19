package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.CompetitionBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * 活动、比赛列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class CompetitionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<CompetitionBean> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnCompetitionItemClickListener {
        void onItemClick(View view);
    }

    private OnCompetitionItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnCompetitionItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CompetitionRecyclerViewAdapter(Context mContext, List<CompetitionBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_competition_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CompetitionBean competitionBean = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(competitionBean.getTitle());
        ((ViewHolder) holder).tv_content.setText(competitionBean.getContent());
        GlideUtils.loadSmallImage(mContext, competitionBean.getImage(),
                ((ViewHolder) holder).iv_image);
        if (competitionBean.isSelected()) {
            ((ViewHolder) holder).iv_status.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_status.setVisibility(View.GONE);
        }
        if (competitionBean.getStatus() == 1) {
            ((ViewHolder) holder).tv_status.setVisibility(View.GONE);
        } else {
            ((ViewHolder) holder).tv_status.setVisibility(View.VISIBLE);
            if (competitionBean.getStatus() == 2) {
                ((ViewHolder) holder).tv_status.setText("活动评选中");
            } else {
                ((ViewHolder) holder).tv_status.setText("活动已结束");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        ImageView iv_status;
        TextView tv_title;
        TextView tv_content;
        TextView tv_status;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_competition_item);
            iv_status = itemView.findViewById(R.id.iv_status_competition_item);
            tv_title = itemView.findViewById(R.id.tv_title_competition_item);
            tv_content = itemView.findViewById(R.id.tv_content_competition_item);
            tv_status = itemView.findViewById(R.id.tv_status_competition_item);
        }
    }
}
