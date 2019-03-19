package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dace.textreader.R;
import com.dace.textreader.bean.BannerBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * 介绍图片列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class IntroductionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<BannerBean> mList;

    public IntroductionAdapter(Context mContext, List<BannerBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setList(List<BannerBean> mList) {
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_introduction_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            holder.itemView.setPadding(DensityUtil.dip2px(mContext, 15),
                    0, 0, 0);
        } else if (position == mList.size() - 1) {
            holder.itemView.setPadding(DensityUtil.dip2px(mContext, 10), 0,
                    DensityUtil.dip2px(mContext, 15), 0);
        } else {
            holder.itemView.setPadding(DensityUtil.dip2px(mContext, 10),
                    0, 0, 0);
        }

        BannerBean bannerBean = mList.get(position);
        GlideUtils.loadSmallImage(mContext, bannerBean.getImagePath(), ((ViewHolder) holder).iv_image);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClick != null) {
            itemClick.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen itemClick;

    public void setOnItemClickListen(OnItemClickListen itemClick) {
        this.itemClick = itemClick;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_introduction_item);
        }
    }
}
