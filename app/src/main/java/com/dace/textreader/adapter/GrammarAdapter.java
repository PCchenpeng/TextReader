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
import com.dace.textreader.bean.GrammarBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/10/31 0031 下午 3:04.
 * Version   1.0;
 * Describe :  语法列表适配器
 * History:
 * ==============================================================================
 */
public class GrammarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<GrammarBean> mList;

    public GrammarAdapter(Context context, List<GrammarBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grammar_layout, viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        GrammarBean grammarBean = mList.get(i);
        if (grammarBean.isEditor()) {
            ((ViewHolder) viewHolder).iv_editor.setVisibility(View.VISIBLE);
            if (grammarBean.isSelected()) {
                ((ViewHolder) viewHolder).iv_editor.setImageResource(R.drawable.icon_edit_selected);
            } else {
                ((ViewHolder) viewHolder).iv_editor.setImageResource(R.drawable.icon_edit_unselected);
            }
        } else {
            ((ViewHolder) viewHolder).iv_editor.setVisibility(View.GONE);
        }
        ((ViewHolder) viewHolder).tv_grammar.setText(grammarBean.getGrammar());
        ((ViewHolder) viewHolder).tv_content.setText(grammarBean.getContent());
        String title = "《" + grammarBean.getEssayTitle() + "》";
        ((ViewHolder) viewHolder).tv_title.setText(title);
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

        ImageView iv_editor;
        TextView tv_grammar;
        TextView tv_content;
        TextView tv_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_editor = itemView.findViewById(R.id.iv_editor_grammar_item);
            tv_grammar = itemView.findViewById(R.id.tv_grammar_item);
            tv_content = itemView.findViewById(R.id.tv_content_grammar_item);
            tv_title = itemView.findViewById(R.id.tv_title_grammar_item);
        }
    }

}
