package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.CardActivity;
import com.dace.textreader.activity.CouponActivity;
import com.dace.textreader.activity.RechargeActivity;
import com.dace.textreader.bean.WalletDataBean;
import com.dace.textreader.util.GlideUtils;

public class WalletAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private WalletDataBean.DataBean mData;

    private final int TYPE_TOP = 1;
    private final int TYPE_ITEM = 2;

    public WalletAdapter(Context context, WalletDataBean.DataBean data){
        this.mContext = context;
        this.mData = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType){
            case TYPE_TOP:
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_wallet_1, viewGroup, false);
                return new TopHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_wallet_2, viewGroup, false);
                return new ItemHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        Log.e("walletAdapter","position = " + position);
        Log.e("walletAdapter","viewType = " + viewType);
        switch (viewType){
            case TYPE_TOP:
                if (mData != null && mData.getCard() != null){
                    GlideUtils.loadImage(mContext, mData.getCard().getImg(),
                            ((TopHolder) viewHolder).iv_img);
                    ((TopHolder) viewHolder).tv_title.setText(mData.getCard().getTitle());
                    ((TopHolder) viewHolder).tv_account.setText(String.valueOf(mData.getWallet().getAmount()));
                    ((TopHolder) viewHolder).tv_cardNo.setText(String.valueOf(mData.getWallet().getCardNum()));
                    ((TopHolder) viewHolder).tv_couponNo.setText(String.valueOf(mData.getWallet().getCouponNum()));

                    ((TopHolder)viewHolder).iv_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    ((TopHolder)viewHolder).tv_charge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, RechargeActivity.class));
                        }
                    });

                    ((TopHolder)viewHolder).ll_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, CardActivity.class));
                        }
                    });

                    ((TopHolder)viewHolder).ll_coupon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onCouponClick != null)
                                onCouponClick.onClick();
                        }
                    });
                }
                break;
            case TYPE_ITEM:
                GlideUtils.loadImage(mContext, mData.getList().get(position - 1).getImg(),
                        ((ItemHolder) viewHolder).iv_img);
                ((ItemHolder) viewHolder).tv_title.setText(mData.getList().get(position - 1).getTitle());
                ((ItemHolder)viewHolder).iv_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.getList().size() + 1;
    }

    public void setData(WalletDataBean.DataBean data){
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return TYPE_TOP;
        }else {
            return TYPE_ITEM;
        }
    }

    class TopHolder extends RecyclerView.ViewHolder{
        ImageView iv_img;
        TextView tv_title,tv_account,tv_cardNo,tv_couponNo,tv_charge;
        LinearLayout ll_card,ll_coupon;

        TopHolder(@NonNull View itemView) {
            super(itemView);
            iv_img =  itemView.findViewById(R.id.iv_img);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_account = itemView.findViewById(R.id.tv_account);
            tv_cardNo = itemView.findViewById(R.id.tv_cardNo);
            tv_couponNo = itemView.findViewById(R.id.tv_couponNo);
            tv_charge = itemView.findViewById(R.id.tv_charge);
            ll_card = itemView.findViewById(R.id.ll_card);
            ll_coupon = itemView.findViewById(R.id.ll_coupon);

        }
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        ImageView iv_img;
        TextView tv_title;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_img =  itemView.findViewById(R.id.iv_img);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    public interface OnCouponClick{
        void onClick();
    }

    OnCouponClick onCouponClick;

    public void setOnCouponClick(OnCouponClick onCouponClick){
        this.onCouponClick = onCouponClick;
    }
}
