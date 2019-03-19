package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.GlossaryWordExplainActivity;
import com.dace.textreader.bean.ErrorCorrectionChooseBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/11/16 0016 下午 5:36.
 * Version   1.0;
 * Describe :  纠错选择列表适配器
 * History:
 * ==============================================================================
 */
public class ErrorCorrectionChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<ErrorCorrectionChooseBean> mList;

    public ErrorCorrectionChooseAdapter(Context context, List<ErrorCorrectionChooseBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_error_correction_choose_item
                , viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ErrorCorrectionChooseBean bean = mList.get(i);
        if (bean.isSelected()) {
            ((ViewHolder) viewHolder).imageView.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ViewHolder) viewHolder).imageView.setImageResource(R.drawable.icon_edit_unselected);
        }
        ((ViewHolder) viewHolder).textView.setText(bean.getText());
        ((ViewHolder) viewHolder).tv_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GlossaryWordExplainActivity.class);
                intent.putExtra("words", bean.getText());
                intent.putExtra("essayTitle", "");
                intent.putExtra("glossaryTitle", bean.getText());
                intent.putExtra("glossaryId", -1);
                mContext.startActivity(intent);
            }
        });
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

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView tv_explain;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_error_correction_choose_item);
            tv_explain = itemView.findViewById(R.id.tv_explain_error_correction_choose_item);
            imageView = itemView.findViewById(R.id.iv_error_correction_choose_item);
        }
    }

}
