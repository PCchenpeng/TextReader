package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.WritingNewsBean;

import java.util.List;

/**
 * 作文通知列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class WritingNewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<WritingNewsBean> mList;

    public WritingNewsRecyclerViewAdapter(Context context, List<WritingNewsBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_writing_news_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        WritingNewsBean writingBean = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(writingBean.getTitle());
        if (writingBean.getStatus() == 1) {
            ((ViewHolder) holder).tv_status.setText("作文已经批改完成");
        } else if (writingBean.getStatus() == 2) {
            if (writingBean.getType() == 1) {
                ((ViewHolder) holder).tv_status.setText("抱歉，老师批改已超时，相关费用已退还");
            } else if (writingBean.getType() == 6) {
                ((ViewHolder) holder).tv_status.setText("作文批改完成啦");
            }
        }
        if (writingBean.getIsViewed() == 1) {
            ((ViewHolder) holder).tv_viewed.setVisibility(View.INVISIBLE);
        } else {
            ((ViewHolder) holder).tv_viewed.setVisibility(View.VISIBLE);
        }
        ((ViewHolder) holder).tv_time.setText(writingBean.getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnWritingNewsItemClick != null) {
            mOnWritingNewsItemClick.onClick(v);
        }
    }

    public interface OnWritingNewsItemClick {
        void onClick(View view);
    }

    private OnWritingNewsItemClick mOnWritingNewsItemClick;

    public void setOnWritingNewsItemClickListen(OnWritingNewsItemClick onWritingNewsItemClickListen) {
        this.mOnWritingNewsItemClick = onWritingNewsItemClickListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_viewed;
        TextView tv_title;
        TextView tv_status;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_viewed = itemView.findViewById(R.id.tv_viewed_writing_news_item);
            tv_title = itemView.findViewById(R.id.tv_title_writing_news_item);
            tv_status = itemView.findViewById(R.id.tv_status_writing_news_item);
            tv_time = itemView.findViewById(R.id.tv_time_writing_news_item);
        }
    }
}
