package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.MicroLesson;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/10/31 0031 上午 9:16.
 * Version   1.0;
 * Describe :  微课适配器
 * History:
 * ==============================================================================
 */
public class MicroLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<MicroLesson> mList;

    public MicroLessonAdapter(Context context, List<MicroLesson> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_micro_lesson, viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MicroLesson lesson = mList.get(i);
        GlideUtils.loadSquareImage(mContext, lesson.getLessonImage(), ((ViewHolder) viewHolder).iv_lesson);
        ((ViewHolder) viewHolder).tv_lesson.setText(lesson.getLessonName());
        String teacherInfo = "授课：" + lesson.getTeacherName();
        ((ViewHolder) viewHolder).tv_teacher.setText(teacherInfo);
        String numberInfo = lesson.getLessonNum() + "节";
        ((ViewHolder) viewHolder).tv_number.setText(numberInfo);
        ((ViewHolder) viewHolder).tv_play_number.setText(String.valueOf(lesson.getPlayNum()));
        String priceInfo = DataUtil.double2String(lesson.getLessonPrice()) + "派豆";
        ((ViewHolder) viewHolder).tv_price.setText(priceInfo);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_lesson;
        TextView tv_lesson;
        TextView tv_teacher;
        TextView tv_number;
        TextView tv_play_number;
        TextView tv_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_lesson = itemView.findViewById(R.id.iv_micro_lesson_item);
            tv_lesson = itemView.findViewById(R.id.tv_name_micro_lesson_item);
            tv_teacher = itemView.findViewById(R.id.tv_teacher_micro_lesson_item);
            tv_number = itemView.findViewById(R.id.tv_number_micro_lesson_item);
            tv_play_number = itemView.findViewById(R.id.tv_play_num_micro_lesson_item);
            tv_price = itemView.findViewById(R.id.tv_price_micro_lesson_item);
        }
    }

}
