package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.LessonBean;

import java.util.List;

/**
 * 播放列表适配器
 * Created by 70391 on 2018/4/2.
 */

public class PlayerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<LessonBean> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnPlayerItemClickListener {
        void onItemClick(View view);
    }

    private OnPlayerItemClickListener mOnItemClickListener;

    public void setOnPlayerItemClickListener(OnPlayerItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public PlayerRecyclerViewAdapter(Context mContext, List<LessonBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_player_list_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LessonBean lessonBean = mList.get(position);
        String number;
        int n = position + 1;
        if (n < 10) {
            number = "0" + String.valueOf(n);
        } else {
            number = String.valueOf(n);
        }
        ((ViewHolder) holder).tv_number.setText(number);
        ((ViewHolder) holder).tv_title.setText(lessonBean.getName());
        if (lessonBean.isPlaying()) {
            ((ViewHolder) holder).iv_image.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).tv_number.setTextColor(Color.parseColor("#ff9933"));
            ((ViewHolder) holder).tv_title.setTextColor(Color.parseColor("#ff9933"));
        } else {
            ((ViewHolder) holder).iv_image.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_number.setTextColor(Color.parseColor("#333333"));
            ((ViewHolder) holder).tv_title.setTextColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_image;
        TextView tv_number;
        TextView tv_title;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image_player_list_item);
            tv_number = itemView.findViewById(R.id.tv_number_player_list_item);
            tv_title = itemView.findViewById(R.id.tv_title_player_list_item);
        }
    }
}
