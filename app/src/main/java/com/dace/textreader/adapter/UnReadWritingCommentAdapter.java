package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.bean.UnReadWritingComment;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * 作文未读消息列表的适配器
 * Created by 70391 on 2017/8/13.
 */

public class UnReadWritingCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<UnReadWritingComment> mList;

    public UnReadWritingCommentAdapter(Context context, List<UnReadWritingComment> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_unread_comment_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final UnReadWritingComment unReadComment = mList.get(position);
        GlideUtils.loadUserImage(mContext,
                HttpUrlPre.FILE_URL + unReadComment.getCommentUserImg(),
                ((ViewHolder) holder).iv_head);
        ((ViewHolder) holder).tv_reply_username.setText(unReadComment.getCommentUsername());
        ((ViewHolder) holder).tv_reply_time.setText(unReadComment.getCommentTime());
        String username = unReadComment.getReplyUsername();
        String comment = "回复" + username + "：" + unReadComment.getCommentContent();
        int length = 2 + username.length();
        SpannableStringBuilder ssb = new SpannableStringBuilder(comment);
        ForegroundColorSpan foregroundColorSpan =
                new ForegroundColorSpan(Color.parseColor("#557FDF"));
        ssb.setSpan(foregroundColorSpan, 2, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ViewHolder) holder).tv_reply_comment.setText(ssb);
        String replyComment;
        SpannableStringBuilder ssb_reply;
        if (unReadComment.getReReplyUserId() == -1) {
            replyComment = username + "：" + unReadComment.getReplyCommentContent();
            ssb_reply = new SpannableStringBuilder(replyComment);
            ssb_reply.setSpan(new ForegroundColorSpan(Color.parseColor("#557FDF")),
                    0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String reReplyUsername = unReadComment.getReReplyUsername();
            replyComment = username + "回复" + reReplyUsername + "：" + unReadComment.getReplyCommentContent();
            ssb_reply = new SpannableStringBuilder(replyComment);
            ssb_reply.setSpan(new ForegroundColorSpan(Color.parseColor("#557FDF")),
                    0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int l = username.length() + 2;
            int end = l + reReplyUsername.length();
            ssb_reply.setSpan(new ForegroundColorSpan(Color.parseColor("#557FDF")),
                    l, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        ((ViewHolder) holder).tv_user_comment.setText(ssb_reply);
        ((ViewHolder) holder).tv_essayTitle.setText(unReadComment.getTitle());
        ((ViewHolder) holder).iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToUserHomePage(unReadComment.getCommentUserId());
            }
        });
        ((ViewHolder) holder).tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
        ((ViewHolder) holder).ll_essay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnWritingItemClickListener != null) {
                    mOnWritingItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 前往用户首页
     *
     * @param userId
     */
    private void turnToUserHomePage(long userId) {
        if (userId != -1) {
            Intent intent = new Intent(mContext, UserHomepageActivity.class);
            intent.putExtra("userId", userId);
            mContext.startActivity(intent);
        }
    }

    //自定义监听事件
    public interface OnUnReadWritingCommentReplyClick {
        void onItemClick(int position);
    }

    private OnUnReadWritingCommentReplyClick mOnItemClickListener;

    public void setOnReplyClickListener(OnUnReadWritingCommentReplyClick listener) {
        mOnItemClickListener = listener;
    }

    //自定义监听事件
    public interface OnUnReadCommentWritingClickListener {
        void onItemClick(int position);
    }

    private OnUnReadCommentWritingClickListener mOnWritingItemClickListener;

    public void setOnWritingClickListener(OnUnReadCommentWritingClickListener listener) {
        mOnWritingItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_head;
        TextView tv_reply_username;
        TextView tv_reply_time;
        TextView tv_reply_comment;
        TextView tv_user_comment;
        LinearLayout ll_essay;
        TextView tv_essayTitle;
        TextView tv_reply;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_head = itemView.findViewById(R.id.iv_head_unread_comments_item);
            tv_reply_username = itemView.findViewById(R.id.tv_reply_username_unread_comments_item);
            tv_reply_time = itemView.findViewById(R.id.tv_reply_time_unread_comments_item);
            tv_reply_comment = itemView.findViewById(R.id.tv_reply_comment_unread_comments_item);
            tv_user_comment = itemView.findViewById(R.id.tv_user_comment_unread_comments_item);
            ll_essay = itemView.findViewById(R.id.ll_essay_info_unread_comments_item);
            tv_essayTitle = itemView.findViewById(R.id.tv_essay_title_unread_comments_item);
            tv_reply = itemView.findViewById(R.id.tv_reply_unread_comments_item);
        }
    }
}
