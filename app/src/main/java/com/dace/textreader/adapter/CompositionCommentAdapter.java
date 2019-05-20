package com.dace.textreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.bean.WritingCommentBean;
import com.dace.textreader.listen.OnUserInfoClickListen;
import com.dace.textreader.util.GlideCircleTransform;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * 作文评论列表适配器
 * Created by 70391 on 2017/8/13.
 */

public class CompositionCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private final int TYPE_COMMENT = 1;  //回复内容
    private final int TYPE_REPLY = 2;  //回复其他用户

    private Context mContext;
    private List<WritingCommentBean> mList;

    public CompositionCommentAdapter(Context context, List<WritingCommentBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMENT) {
            View comment_view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_comment_layout, parent, false);
            CommentViewHolder commentViewHolder = new CommentViewHolder(comment_view);
            comment_view.setOnClickListener(this);
            return commentViewHolder;
        } else if (viewType == TYPE_REPLY) {
            View reply_view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_comment_reply_layout, parent, false);
            ReplyViewHolder replyViewHolder = new ReplyViewHolder(reply_view);
            reply_view.setOnClickListener(this);
            return replyViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .transform(new GlideCircleTransform(mContext))
                .error(R.drawable.image_student);
        final WritingCommentBean comment = mList.get(position);
        if (holder instanceof CommentViewHolder) {
            GlideUtils.loadUserImage(mContext,
//                    HttpUrlPre.FILE_URL +
                            comment.getCommentUserImg(),
                    ((CommentViewHolder) holder).iv_head);
            ((CommentViewHolder) holder).tv_name.setText(comment.getCommentUsername());
            ((CommentViewHolder) holder).tv_content.setText(comment.getCommentContent());
            ((CommentViewHolder) holder).tv_time.setText(comment.getCommentTime());
            ((CommentViewHolder) holder).iv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserInfoClickListen != null) {
                        onUserInfoClickListen.onClick(comment.getCommentUserId());
                    }
                }
            });
            ((CommentViewHolder) holder).tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserInfoClickListen != null) {
                        onUserInfoClickListen.onClick(comment.getCommentUserId());
                    }
                }
            });
        } else if (holder instanceof ReplyViewHolder) {
            GlideUtils.loadUserImage(mContext,
//                    HttpUrlPre.FILE_URL +
                            comment.getCommentUserImg(),
                    ((ReplyViewHolder) holder).iv_head);
            ((ReplyViewHolder) holder).tv_name.setText(comment.getCommentUsername());
            String replyUsername = comment.getReplyUsername();
            int length = 2 + replyUsername.length();
            String content = "回复" + replyUsername + "：" + comment.getCommentContent();
            SpannableStringBuilder ssb = new SpannableStringBuilder(content);
            ForegroundColorSpan foregroundColorSpan =
                    new ForegroundColorSpan(Color.parseColor("#557FDF"));
            ssb.setSpan(foregroundColorSpan, 2, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ReplyViewHolder) holder).tv_content.setText(ssb);
            ((ReplyViewHolder) holder).tv_time.setText(comment.getCommentTime());

            String replyContent = replyUsername + "：" + comment.getReplyCommentContent();
            SpannableStringBuilder ssb_reply = new SpannableStringBuilder(replyContent);
            ssb_reply.setSpan(new ForegroundColorSpan(Color.parseColor("#557FDF")),
                    0, replyUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ReplyViewHolder) holder).tv_reply_name.setText(ssb_reply);
            ((ReplyViewHolder) holder).iv_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserInfoClickListen != null) {
                        onUserInfoClickListen.onClick(comment.getCommentUserId());
                    }
                }
            });
            ((ReplyViewHolder) holder).tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserInfoClickListen != null) {
                        onUserInfoClickListen.onClick(comment.getCommentUserId());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int replyId = mList.get(position).getReplyCommentId();
        if (replyId == -1) {
            return TYPE_COMMENT;
        } else {
            return TYPE_REPLY;
        }
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListen != null) {
            onItemClickListen.onItemClick(v);
        }
    }

    public interface OnItemClickListen {
        void onItemClick(View view);
    }

    private OnItemClickListen onItemClickListen;

    public void setOnItemClickListen(OnItemClickListen listener) {
        this.onItemClickListen = listener;
    }

    private OnUserInfoClickListen onUserInfoClickListen;

    public void setOnUserInfoClickListen(OnUserInfoClickListen onUserInfoClickListen) {
        this.onUserInfoClickListen = onUserInfoClickListen;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;

        public CommentViewHolder(View itemView) {
            super(itemView);
            iv_head = itemView.findViewById(R.id.iv_comment_user_image_item);
            tv_name = itemView.findViewById(R.id.tv_comment_user_name_item);
            tv_content = itemView.findViewById(R.id.tv_comment_content_item);
            tv_time = itemView.findViewById(R.id.tv_comment_duration_item);
        }
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        TextView tv_reply_name;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            iv_head = itemView.findViewById(R.id.iv_user_image_reply_comment_item);
            tv_name = itemView.findViewById(R.id.tv_username_reply_comment_item);
            tv_content = itemView.findViewById(R.id.tv_comment_content_reply_comment_item);
            tv_time = itemView.findViewById(R.id.tv_comment_duration_reply_comment_item);
            tv_reply_name = itemView.findViewById(R.id.tv_reply_info_reply_comment_item);
        }
    }

    public List<WritingCommentBean> getmList() {
        return mList;
    }
}
