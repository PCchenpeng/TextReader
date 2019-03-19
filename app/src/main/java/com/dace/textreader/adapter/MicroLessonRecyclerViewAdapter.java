package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.LessonBean;

import java.util.List;

/**
 * 微课课程列表的适配器
 * Created by 70391 on 2017/9/28.
 */

public class MicroLessonRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<LessonBean> mList;

    public MicroLessonRecyclerViewAdapter(Context context, List<LessonBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_micro_lesson_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        LessonBean lessonBean = mList.get(position);
        ((ViewHolder) holder).tv_number.setText(String.valueOf(position + 1));
        if (lessonBean.isPlaying()) {
            ((ViewHolder) holder).tv_number.setSelected(true);
        } else {
            ((ViewHolder) holder).tv_number.setSelected(false);
        }
        ((ViewHolder) holder).tv_title.setText(lessonBean.getName());
        ((ViewHolder) holder).tv_duration.setText(lessonBean.getDuration());
        ((ViewHolder) holder).tv_play_number.setText(String.valueOf(lessonBean.getPlayNum()));
        int free = lessonBean.getFree();
        if (free == 1) {
            ((ViewHolder) holder).tv_listen.setText("试听");
        } else if (free == 0) {
            ((ViewHolder) holder).tv_listen.setText("购买");
        } else if (free == 2) {
            ((ViewHolder) holder).tv_listen.setText("学习");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v);
        }
    }

    public interface OnMicroLessonItemListenClick {
        void onItemClick(View view);
    }

    private OnMicroLessonItemListenClick mItemClickListener;

    public void setOnMicroLessonItemListenClick(OnMicroLessonItemListenClick listener) {
        mItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_number;
        TextView tv_title;
        TextView tv_duration;
        TextView tv_play_number;
        TextView tv_listen;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_number = itemView.findViewById(R.id.tv_lesson_number_micro_lesson_item);
            tv_title = itemView.findViewById(R.id.tv_lesson_title_micro_lesson_item);
            tv_duration = itemView.findViewById(R.id.tv_lesson_duration_micro_lesson_item);
            tv_play_number = itemView.findViewById(R.id.tv_lesson_play_number_micro_lesson_item);
            tv_listen = itemView.findViewById(R.id.tv_lesson_listen_micro_lesson_item);
        }
    }
}
