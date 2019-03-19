package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/8/17 0017 下午 1:55.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */

public class GlossaryFlexBoxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private List<String> mList;
    private Context mContext;
    private boolean isEditor;

    public GlossaryFlexBoxAdapter(List<String> list, Context context, boolean isEditor) {
        this.mList = list;
        this.mContext = context;
        this.isEditor = isEditor;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.glossary_word_item_layout, null);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tv_word.setText(mList.get(position));
        if (isEditor) {
            ((ViewHolder) holder).iv_delete.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_delete.setVisibility(View.GONE);
        }
        RelativeLayout relativeLayout = (RelativeLayout) ((ViewHolder) holder).itemView;
        ViewGroup.LayoutParams lp = relativeLayout.getLayoutParams();
        if (lp instanceof FlexboxLayout.LayoutParams) {
            FlexboxLayout.LayoutParams layoutParams =
                    (FlexboxLayout.LayoutParams) lp;
            layoutParams.setFlexGrow(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public void onClick(View v) {
        if (boxItemClick != null) {
            boxItemClick.onClick(v);
        }
    }

    public interface OnGlossaryFlexBoxItemClick {
        void onClick(View view);
    }

    private OnGlossaryFlexBoxItemClick boxItemClick;

    public void setOnGlossaryFlexBoxItemClickListen(OnGlossaryFlexBoxItemClick itemClickListen) {
        this.boxItemClick = itemClickListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_word;
        ImageView iv_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_word = itemView.findViewById(R.id.tv_glossary_word_item);
            iv_delete = itemView.findViewById(R.id.iv_glossary_word_item);
        }
    }
}
