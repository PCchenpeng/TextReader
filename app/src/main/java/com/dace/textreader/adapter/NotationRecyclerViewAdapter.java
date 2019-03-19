package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dace.textreader.R;
import com.dace.textreader.bean.NotationBean;

import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;

/**
 * 注释列表适配器
 * Created by 70391 on 2018/4/2.
 */

public class NotationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NotationBean> mList;

    public NotationRecyclerViewAdapter(Context mContext, List<NotationBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_notation_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        NotationBean notationBean = mList.get(position);
        String original = notationBean.getOriginal() + "\n";
        ((ViewHolder) holder).tv_original.setText(original);
        String notation = notationBean.getNotation() + "\n";
        ((ViewHolder) holder).tv_notation.setText(notation);
        if (notationBean.isExpand()) {
            ((ViewHolder) holder).tv_notation.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).iv_notation.setImageResource(R.drawable.ic_expand_less_black_36dp);
        } else {
            ((ViewHolder) holder).tv_notation.setVisibility(View.GONE);
            ((ViewHolder) holder).iv_notation.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }
        ((ViewHolder) holder).ll_notation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ViewHolder) holder).tv_notation.getVisibility() == View.VISIBLE) {
                    ((ViewHolder) holder).tv_notation.setVisibility(View.GONE);
                    ((ViewHolder) holder).iv_notation.setImageResource(R.drawable.ic_expand_more_black_24dp);
                } else {
                    ((ViewHolder) holder).tv_notation.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).iv_notation.setImageResource(R.drawable.ic_expand_less_black_36dp);
                }
            }
        });
        ((ViewHolder) holder).tv_notation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ViewHolder) holder).tv_notation.getVisibility() == View.VISIBLE) {
                    ((ViewHolder) holder).tv_notation.setVisibility(View.GONE);
                    ((ViewHolder) holder).iv_notation.setImageResource(R.drawable.ic_expand_more_black_24dp);
                } else {
                    ((ViewHolder) holder).tv_notation.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).iv_notation.setImageResource(R.drawable.ic_expand_less_black_36dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        JustifyTextView tv_original;
        JustifyTextView tv_notation;
        LinearLayout ll_notation;
        ImageView iv_notation;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_original = itemView.findViewById(R.id.tv_original_notation_item);
            tv_notation = itemView.findViewById(R.id.tv_notation_item);
            ll_notation = itemView.findViewById(R.id.ll_notation_item);
            iv_notation = itemView.findViewById(R.id.iv_notation_item);
        }
    }
}
