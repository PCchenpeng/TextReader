package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.CompetitionBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * 竖向排列的活动、比赛列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class EventsVerticalListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<CompetitionBean> mList;

    public EventsVerticalListAdapter(Context mContext, List<CompetitionBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_writing_competition_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CompetitionBean competitionBean = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(competitionBean.getTitle());
        ((ViewHolder) holder).tv_content.setText(competitionBean.getContent());
        GlideUtils.loadImageWithNoPlaceholder(mContext, competitionBean.getImage(),
                ((ViewHolder) holder).iv_image);
        if (competitionBean.getStatus() == 1) {
            ((ViewHolder) holder).tv_status.setText("进行中");
            ((ViewHolder) holder).rl_status.setSelected(true);
        } else if (competitionBean.getStatus() == 2) {
            ((ViewHolder) holder).rl_status.setSelected(false);
            ((ViewHolder) holder).tv_status.setText("评选中");
        } else if (competitionBean.getStatus() == -1) {
            ((ViewHolder) holder).rl_status.setSelected(false);
            ((ViewHolder) holder).tv_status.setText("敬请期待");
        } else {
            ((ViewHolder) holder).rl_status.setSelected(false);
            ((ViewHolder) holder).tv_status.setText("已结束");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClick != null) {
            itemClick.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen itemClick;

    public void setOnItemClickListen(OnItemClickListen itemClick) {
        this.itemClick = itemClick;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_title;
        TextView tv_content;
        RelativeLayout rl_status;
        TextView tv_status;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_writing_competition_item);
            tv_title = itemView.findViewById(R.id.tv_title_writing_competition_item);
            tv_content = itemView.findViewById(R.id.tv_content_writing_competition_item);
            tv_status = itemView.findViewById(R.id.tv_status_writing_competition_item);
            rl_status = itemView.findViewById(R.id.rl_status_writing_competition_item);
        }
    }
}
