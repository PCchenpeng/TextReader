package com.dace.textreader.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.bean.CouponBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideRoundImage;

import java.util.List;

/**
 * 优惠券列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<CouponBean> mList;
    private boolean available;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnCouponItemClickListener {
        void onItemClick(View view);
    }

    private OnCouponItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnCouponItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public CouponAdapter(Context mContext, List<CouponBean> mList, boolean available) {
        this.mContext = mContext;
        this.mList = mList;
        this.available = available;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_coupon_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            CouponBean couponBean = mList.get(position);
            for (String key : bundle.keySet()) {
                switch (key) {
                    case "key_selected":
                        if (available) {
                            ((ViewHolder) holder).tv_use.setVisibility(View.GONE);
                            if (couponBean.isSelected()) {
                                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_select);
                            } else {
                                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_default);
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RecyclerView.LayoutParams layoutParams =
                (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (position == mList.size() - 1) {
            layoutParams.setMargins(DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 20),
                    DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 20));
        } else {
            layoutParams.setMargins(DensityUtil.dip2px(mContext, 15),
                    DensityUtil.dip2px(mContext, 20),
                    DensityUtil.dip2px(mContext, 15),
                    0);
        }
        holder.itemView.setLayoutParams(layoutParams);

        CouponBean couponBean = mList.get(position);
        if (couponBean.getStatus() == 1) {
            if (mContext != null) {
                RequestOptions options = new RequestOptions()
                        .transform(new GlideRoundImage(mContext, 8))
                        .centerCrop();
                Glide.with(mContext)
                        .load(R.drawable.image_coupon_item_bg_valid)
                        .apply(options)
                        .into(((ViewHolder) holder).iv_bg);
            }
        } else {
            if (mContext != null) {
                Glide.with(mContext)
                        .load(R.drawable.image_coupon_item_bg_null)
                        .into(((ViewHolder) holder).iv_bg);
            }
            ((ViewHolder) holder).iv_use.setVisibility(View.GONE);
            if (couponBean.getStatus() == 2) {
                ((ViewHolder) holder).tv_use.setText("已使用");
            } else if (couponBean.getStatus() == 3) {
                ((ViewHolder) holder).tv_use.setText("已失效");
            }
        }
        ((ViewHolder) holder).tv_title.setText(couponBean.getTitle());
        ((ViewHolder) holder).tv_content.setText(couponBean.getContent());
        String time = "有效期至" + couponBean.getStopTime();
        ((ViewHolder) holder).tv_time.setText(time);

        String tip;
        if (couponBean.getType() == 1) {
            tip = "满" + DataUtil.double2String(couponBean.getConsumption()) + "减" +
                    DataUtil.double2String(couponBean.getSubtract());
        } else if (couponBean.getType() == 2) {
            tip = DataUtil.double2String(couponBean.getDiscount()) + "折";
        } else if (couponBean.getType() == 4) {
            tip = DataUtil.double2String(couponBean.getSubtract()) + "抵用";
        } else {
            tip = "免费";
        }
        ((ViewHolder) holder).tv_tip.setText(tip);

        if (available) {
            ((ViewHolder) holder).tv_use.setVisibility(View.GONE);
            if (couponBean.isSelected()) {
                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_select);
            } else {
                ((ViewHolder) holder).iv_use.setImageResource(R.drawable.icon_coupon_default);
            }
        } else {
            if (couponBean.getCategory() == -1) {
                ((ViewHolder) holder).ll_use.setVisibility(View.GONE);
            }
        }

        if (couponBean.getIsActivated() == 0) {
            ((ViewHolder) holder).iv_activated.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).iv_activated.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_bg;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        TextView tv_tip;
        LinearLayout ll_use;
        TextView tv_use;
        ImageView iv_use;
        ImageView iv_activated;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_bg = itemView.findViewById(R.id.iv_coupon_item);
            tv_title = itemView.findViewById(R.id.tv_title_coupon_item);
            tv_content = itemView.findViewById(R.id.tv_content_coupon_item);
            tv_time = itemView.findViewById(R.id.tv_time_coupon_item);
            tv_tip = itemView.findViewById(R.id.tv_tip_coupon_item);
            ll_use = itemView.findViewById(R.id.ll_use_coupon_item);
            tv_use = itemView.findViewById(R.id.tv_use_coupon_item);
            iv_use = itemView.findViewById(R.id.iv_use_coupon_item);
            iv_activated = itemView.findViewById(R.id.iv_is_activated_coupon_item);
        }
    }
}
