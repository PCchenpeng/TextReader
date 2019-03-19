package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.MyselfNewsBean;

import java.util.List;

/**
 * 我的消息列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class MyselfNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<MyselfNewsBean> mList;

    public MyselfNewsAdapter(Context context, List<MyselfNewsBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_myself_news_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyselfNewsBean myselfNewsBean = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(myselfNewsBean.getTitle());
        int type = myselfNewsBean.getType();
        if (type == 0) {
            if (myselfNewsBean.getStatus() == 1) {
                ((ViewHolder) holder).tv_status.setText("绑定成功");
            } else if (myselfNewsBean.getStatus() == -1) {
                ((ViewHolder) holder).tv_status.setText("绑定失败");
            }
        } else if (type == 1) {
            ((ViewHolder) holder).tv_status.setText("月卡奖励");
        } else if (type == 2) {
            ((ViewHolder) holder).tv_status.setText("优惠券奖励");
        } else if (type == 3) {
            ((ViewHolder) holder).tv_status.setText("派豆奖励");
        }
        ((ViewHolder) holder).tv_time.setText(myselfNewsBean.getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnMyselfNewsItemClick != null) {
            mOnMyselfNewsItemClick.onClick(v);
        }
    }

    public interface OnMyselfNewsItemClick {
        void onClick(View view);
    }

    private OnMyselfNewsItemClick mOnMyselfNewsItemClick;

    public void setOnItemClickListen(OnMyselfNewsItemClick onItemClickListen) {
        this.mOnMyselfNewsItemClick = onItemClickListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_status;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_myself_news_item);
            tv_status = itemView.findViewById(R.id.tv_status_myself_news_item);
            tv_time = itemView.findViewById(R.id.tv_time_myself_news_item);
        }
    }
}
