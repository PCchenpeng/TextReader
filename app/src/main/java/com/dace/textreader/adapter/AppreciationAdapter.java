package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.AppreciationBean;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.view.weight.pullrecycler.ExpandableTextView;

import java.util.List;

public class AppreciationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AppreciationBean.DataBean.MyselfBean> mData;

    public AppreciationAdapter(Context context , List<AppreciationBean.DataBean.MyselfBean> data){
        mContext = context;
        mData = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_appreciation, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (mData.get(i).isEditor()) {
            ((ItemHolder) viewHolder).iv_select.setVisibility(View.VISIBLE);
        } else {
            ((ItemHolder) viewHolder).iv_select.setVisibility(View.GONE);
        }
        if (mData.get(i).isSelected()) {
            ((ItemHolder) viewHolder).iv_select.setImageResource(R.drawable.icon_edit_selected);
        } else {
            ((ItemHolder) viewHolder).iv_select.setImageResource(R.drawable.icon_edit_unselected);
        }

        ((ItemHolder)viewHolder).tv_user.setText(mData.get(i).getUsername());
        ((ItemHolder)viewHolder).expTv1.setText(mData.get(i).getNote());
        ((ItemHolder)viewHolder).tv_time.setText(DateUtil.timedate(String.valueOf(mData.get(i).getTime())));
        GlideUtils.loadUserImage(mContext,mData.get(i).getUserImg(),((ItemHolder)viewHolder).iv_user);
        final AppreciationBean.DataBean.MyselfBean myselfBean = mData.get(i);
        ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick != null)
                    onItemClick.onClick(v,myselfBean);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void refreshData(List<AppreciationBean.DataBean.MyselfBean> data){
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<AppreciationBean.DataBean.MyselfBean> data){
        if(data != null)
        mData.addAll(data);
        notifyDataSetChanged();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView tv_user,tv_time;
        ImageView iv_user,iv_select;
        private ExpandableTextView expTv1;

        public ItemHolder(View view) {
            super(view);
            tv_user = view.findViewById(R.id.tv_user);
            tv_time = view.findViewById(R.id.tv_time);
            iv_user = view.findViewById(R.id.iv_user);
            iv_select = view.findViewById(R.id.iv_select);
            expTv1 = view.findViewById(R.id.expand_text_view);
        }
    }

    public interface OnItemClick{
        void onClick(View view,AppreciationBean.DataBean.MyselfBean myselfBean);
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    private OnItemClick onItemClick;
}
