package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewArticleDetailActivity;
import com.dace.textreader.bean.Notes;

import java.util.List;

/**
 * 笔记列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<Notes> mList;
    private boolean showTitle;

    public NotesRecyclerViewAdapter(Context context, List<Notes> list, boolean isShowTitle) {
        this.mContext = context;
        this.mList = list;
        this.showTitle = isShowTitle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_notes_layout, parent, false);
        //给布局设置点击监听
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Notes notes = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(notes.getTitle());
        if (showTitle) {
            ((ViewHolder) holder).tv_title.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).tv_title.setVisibility(View.GONE);
        }
        if (notes.isEditor()) {
            ((ViewHolder) holder).iv_select.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_select.setVisibility(View.GONE);
        }
        if (notes.isSelected()) {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_unselected);
        }
        ((ViewHolder) holder).tv_note.setText(notes.getNote());
        ((ViewHolder) holder).tv_content.setText(notes.getContent());
        ((ViewHolder) holder).tv_time.setText(notes.getTime());
        ((ViewHolder) holder).tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (showTitle) {
                    turnToArticle(mList.get(pos).getEssayId(), mList.get(pos).getEssayType());
                }
            }
        });
        ((ViewHolder) holder).iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemShareClickListener != null) {
                    mOnItemShareClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
        ((ViewHolder) holder).iv_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ViewHolder) holder).tv_content.getMaxLines() == 2) {
                    ((ViewHolder) holder).tv_content.setMaxLines(Integer.MAX_VALUE);
                    ((ViewHolder) holder).tv_note.setMaxLines(Integer.MAX_VALUE);
                    ((ViewHolder) holder).iv_expand.setImageResource(R.drawable.ic_expand_less_black_36dp);
                } else {
                    ((ViewHolder) holder).tv_content.setMaxLines(2);
                    ((ViewHolder) holder).tv_note.setMaxLines(2);
                    ((ViewHolder) holder).iv_expand.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
            }
        });
    }

    //跳转到文章页面
    private void turnToArticle(long id, int type) {
        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    public interface OnNotesItemClick {
        void onItemClick(View view);
    }

    private OnNotesItemClick mOnItemClickListener;

    public void setOnItemClickListener(OnNotesItemClick listener) {
        mOnItemClickListener = listener;
    }

    public interface OnNotesShareItemClick {
        void onItemClick(int position);
    }

    private OnNotesShareItemClick mOnItemShareClickListener;

    public void setOnItemShareClickListener(OnNotesShareItemClick listener) {
        mOnItemShareClickListener = listener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_select;
        TextView tv_note;
        RelativeLayout ll_title;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        ImageView iv_share;
        ImageView iv_expand;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_select = itemView.findViewById(R.id.iv_notes_select_item);
            tv_note = itemView.findViewById(R.id.tv_notes_item);
            ll_title = itemView.findViewById(R.id.ll_notes_title_item);
            tv_title = itemView.findViewById(R.id.tv_notes_title_item);
            tv_content = itemView.findViewById(R.id.tv_notes_content_item);
            tv_time = itemView.findViewById(R.id.tv_notes_time_item);
            iv_share = itemView.findViewById(R.id.iv_notes_share_item);
            iv_expand = itemView.findViewById(R.id.iv_expand_content_item);
        }
    }
}
