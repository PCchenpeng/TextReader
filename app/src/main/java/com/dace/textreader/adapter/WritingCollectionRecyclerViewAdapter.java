package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.WritingBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * 作文收藏列表的适配器
 * Created by 70391 on 2017/9/28.
 */

public class WritingCollectionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<WritingBean> mList;
    private Typeface typeface_mark;

    public WritingCollectionRecyclerViewAdapter(Context context, List<WritingBean> list, Typeface score) {
        this.mContext = context;
        this.mList = list;
        typeface_mark = score;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_writing_new_collection_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        WritingBean writingBean = mList.get(position);
        if (writingBean.isEditor()) {
            ((ViewHolder) holder).iv_select.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_select.setVisibility(View.GONE);
        }
        if (writingBean.isSelected()) {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ViewHolder) holder).iv_select.setImageResource(R.drawable.icon_edit_unselected);
        }
        ((ViewHolder) holder).tv_title.setText(writingBean.getTitle());
        GlideUtils.loadUserImage(mContext, HttpUrlPre.FILE_URL + writingBean.getUserImg(),
                ((ViewHolder) holder).iv_user);
        String userInfo = writingBean.getUsername() + "·" + writingBean.getUserGrade();
        ((ViewHolder) holder).tv_username.setText(userInfo);

        if (writingBean.getContent().equals("") || writingBean.getContent().equals("null")) {
            ((ViewHolder) holder).tv_content.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_score.setVisibility(View.GONE);
            ((ViewHolder) holder).iv_score.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_views.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_comment.setVisibility(View.GONE);
            ((ViewHolder) holder).ll_task.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_has_delete.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).tv_has_delete.setVisibility(View.GONE);
            ((ViewHolder) holder).tv_content.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).tv_score.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).iv_score.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).tv_views.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).tv_comment.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).ll_task.setVisibility(View.VISIBLE);

            ((ViewHolder) holder).tv_content.setText(writingBean.getContent());
            if (writingBean.getViews().equals("") || writingBean.getViews().equals("null")) {
                ((ViewHolder) holder).tv_views.setVisibility(View.GONE);
            } else {
                String views = writingBean.getViews() + "阅读";
                ((ViewHolder) holder).tv_views.setText(views);
            }
            ((ViewHolder) holder).tv_comment.setVisibility(View.GONE);
            ((ViewHolder) holder).ll_task.setVisibility(View.GONE);
//            if (writingBean.getTaskName().equals("") || writingBean.getTaskName().equals("null")) {
//                //没有参加比赛的情况
//                if (writingBean.getComment().equals("") || writingBean.getComment().equals("null")) {
//                    ((ViewHolder) holder).tv_comment.setVisibility(View.GONE);
//                } else {
//                    String comment = "评语：" + writingBean.getComment();
//                    ((ViewHolder) holder).tv_comment.setText(comment);
//                }
//                ((ViewHolder) holder).ll_task.setVisibility(View.GONE);
//            } else {
//                //参加比赛的情况
//                String taskName;
//                if (writingBean.getType() == 2) {
//                    taskName = "比赛：" + writingBean.getTaskName();
//                } else if (writingBean.getType() == 3) {
//                    taskName = "征稿：" + writingBean.getTaskName();
//                } else {
//                    taskName = "范文：" + writingBean.getTaskName();
//                }
//                ((ViewHolder) holder).tv_task.setText(taskName);
//                ((ViewHolder) holder).tv_comment.setVisibility(View.GONE);
//                ((ViewHolder) holder).ll_task.setVisibility(View.VISIBLE);
//            }
            if (writingBean.getStatus() == 1) {  //分数制
                if (writingBean.getMark() == -1) {
                    ((ViewHolder) holder).tv_score.setVisibility(View.GONE);
                    ((ViewHolder) holder).iv_score.setVisibility(View.GONE);
                } else {
                    ((ViewHolder) holder).tv_score.setTypeface(typeface_mark);
                    String score = String.valueOf(writingBean.getMark()) + "分";
                    ((ViewHolder) holder).tv_score.setText(score);
                }
            } else {  //等级制
                if (writingBean.getPrize().equals("") || writingBean.getPrize().equals("null")) {
                    ((ViewHolder) holder).tv_score.setVisibility(View.GONE);
                    ((ViewHolder) holder).iv_score.setVisibility(View.GONE);
                } else {
                    ((ViewHolder) holder).tv_score.setTypeface(typeface_mark);
                    ((ViewHolder) holder).tv_score.setText(writingBean.getPrize());
                }
            }
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

    public interface OnWritingCollectionItemClick {
        void onItemClick(View view);
    }

    private OnWritingCollectionItemClick mItemClickListener;

    public void setOnWritingCollectionItemClick(OnWritingCollectionItemClick listener) {
        mItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_select;
        TextView tv_title;
        TextView tv_content;
        ImageView iv_user;
        TextView tv_username;
        TextView tv_score;
        ImageView iv_score;
        TextView tv_comment;
        TextView tv_views;
        LinearLayout ll_task;
        TextView tv_task;
        TextView tv_has_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_select = itemView.findViewById(R.id.iv_select_new_collection_writing_item);
            tv_title = itemView.findViewById(R.id.tv_title_new_collection_writing_item);
            tv_content = itemView.findViewById(R.id.tv_content_new_collection_writing_item);
            iv_user = itemView.findViewById(R.id.iv_image_new_collection_writing_item);
            tv_username = itemView.findViewById(R.id.tv_author_new_collection_writing_item);
            tv_score = itemView.findViewById(R.id.tv_score_new_collection_writing_item);
            iv_score = itemView.findViewById(R.id.iv_score_new_collection_writing_item);
            tv_comment = itemView.findViewById(R.id.tv_comment_new_collection_writing_item);
            tv_views = itemView.findViewById(R.id.tv_views_new_collection_writing_item);
            ll_task = itemView.findViewById(R.id.ll_task_new_collection_writing_item);
            tv_task = itemView.findViewById(R.id.tv_task_new_collection_writing_item);
            tv_has_delete = itemView.findViewById(R.id.tv_has_delete_new_collection_writing_item);
        }
    }
}
