package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.SettingsNews;

import java.util.List;

/**
 * 系统通知列表适配器
 * Created by 70391 on 2017/9/28.
 */

public class SettingsNewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<SettingsNews> mList;

    public SettingsNewsRecyclerViewAdapter(Context context, List<SettingsNews> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_settings_news_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        SettingsNews settingsNews = mList.get(position);
        if (settingsNews.getType() == 2) {
            ((ViewHolder) holder).tv_title.setText("新课程上线了");
        } else if (settingsNews.getType() == 3) {
            ((ViewHolder) holder).tv_title.setText("新的活动通知");
        }
        ((ViewHolder) holder).tv_content.setText(settingsNews.getTitle());
        ((ViewHolder) holder).tv_time.setText(settingsNews.getTime());
        if (settingsNews.isViewOrNot()) {
            ((ViewHolder) holder).view_status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnSettingsNewsItemClick != null) {
            mOnSettingsNewsItemClick.onClick(v);
        }
    }

    public interface OnSettingsNewsItemClick {
        void onClick(View view);
    }

    private OnSettingsNewsItemClick mOnSettingsNewsItemClick;

    public void setOnSettingsNewsItemClickListen(OnSettingsNewsItemClick onSettingsNewsItemClickListen) {
        this.mOnSettingsNewsItemClick = onSettingsNewsItemClickListen;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        View view_status;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_settings_news_item);
            tv_content = itemView.findViewById(R.id.tv_content_settings_news_item);
            tv_time = itemView.findViewById(R.id.tv_time_settings_news_item);
            view_status = itemView.findViewById(R.id.view_status_settings_news_item);
        }
    }
}
