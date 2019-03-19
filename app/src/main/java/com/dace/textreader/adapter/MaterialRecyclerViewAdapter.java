package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.MaterialBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

/**
 * 素材列表的适配器
 * Created by 70391 on 2017/9/28.
 */

public class MaterialRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<MaterialBean> mList;

    public MaterialRecyclerViewAdapter(Context context, List<MaterialBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_material_list_layout, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MaterialBean materialBean = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(materialBean.getTitle());
        ((ViewHolder) holder).tv_date.setText(materialBean.getTime());
        String score = materialBean.getScore() + "PY";
        ((ViewHolder) holder).tv_score.setText(score);
        ((ViewHolder) holder).tv_type.setText(DataUtil.typeConversion(materialBean.getEssayType()));
        GlideUtils.loadSmallImage(mContext, materialBean.getImage(), ((ViewHolder) holder).iv_essay);
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

    public interface OnMaterialItemDeleteClick {
        void onItemClick(View view);
    }

    private OnMaterialItemDeleteClick mItemClickListener;

    public void setOnMaterialItemDeleteClick(OnMaterialItemDeleteClick listener) {
        mItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_type;
        TextView tv_score;
        TextView tv_date;
        ImageView iv_essay;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_material_list_item);
            tv_type = itemView.findViewById(R.id.tv_type_material_list_item);
            tv_score = itemView.findViewById(R.id.tv_score_material_list_item);
            iv_essay = itemView.findViewById(R.id.iv_essay_material_list_item);
            tv_date = itemView.findViewById(R.id.tv_time_material_list_item);
        }
    }
}
