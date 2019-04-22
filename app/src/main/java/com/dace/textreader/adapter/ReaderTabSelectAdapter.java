package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.ReaderTabSelectItemBean;
import com.dace.textreader.bean.ReaderTabSelectTopBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;

import java.util.List;

public class ReaderTabSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<ReaderTabSelectTopBean.DataBean> topData ;
    private List<ReaderTabSelectItemBean.DataBean> itemData;

    private final int TYPE_TOP = 10001;
    private final int TYPE_ITEM = 10002;

    public ReaderTabSelectAdapter(Context context,List<ReaderTabSelectTopBean.DataBean> topData,List<ReaderTabSelectItemBean.DataBean> itemData){
        this.context = context;
        this.topData = topData;
        this.itemData = itemData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i){
            case TYPE_TOP:
                 view = LayoutInflater.from(context).inflate(
                        R.layout.item_reader_recommend_tab_select_top, viewGroup, false);
                return new TopHolder(view);
            case TYPE_ITEM:
                 view = LayoutInflater.from(context).inflate(
                        R.layout.item_reader_recommend_tab_select, viewGroup, false);
                return new ItemHolder(view);
                default:
                    return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType){
            case TYPE_TOP:
                LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                TopAdapter1 topAdapter1 = new TopAdapter1(context,topData);
                TopAdapter2 topAdapter2 = new TopAdapter2(context,topData);

                ((TopHolder)viewHolder).rcv_update.setLayoutManager(layoutManager);
                SnapHelper snapHelper= new GravityPagerSnapHelper(Gravity.START);
                snapHelper.attachToRecyclerView(((TopHolder)viewHolder).rcv_update);
                ((TopHolder)viewHolder).rcv_update.setAdapter(topAdapter1);


                LinearLayoutManager layoutManager1 = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                ((TopHolder)viewHolder).rcv_favourite.setLayoutManager(layoutManager1);
                SnapHelper snapHelper1= new GravityPagerSnapHelper(Gravity.START);
                snapHelper1.attachToRecyclerView(((TopHolder)viewHolder).rcv_favourite);
                ((TopHolder)viewHolder).rcv_favourite.setAdapter(topAdapter2);

                break;
            case TYPE_ITEM:
                ((ItemHolder)viewHolder).tv_title.setText(itemData.get(i-1).getTitle());
                ((ItemHolder)viewHolder).tv_subContent.setText(itemData.get(i-1).getSubContent());
                ((ItemHolder)viewHolder).tv_source.setText(itemData.get(i-1).getSource());
                ((ItemHolder)viewHolder).tv_type.setText("#"+ itemData.get(i-1).getType()+"#");

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ItemHolder) viewHolder).iv_img.getLayoutParams();
                params.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 25f);
                ((ItemHolder) viewHolder).iv_img.setLayoutParams(params);
                GlideUtils.loadImage(context, itemData.get(i-1).getImage(),
                        ((ItemHolder) viewHolder).iv_img);
                GlideUtils.loadHomeUserImage(context, itemData.get(i-1).getSourceImage(),
                        ((ItemHolder) viewHolder).iv_source);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return itemData == null ? 1 : itemData.size() + 1;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return TYPE_TOP;
        }else {
            return TYPE_ITEM;
        }
    }



    public void setTopData(List<ReaderTabSelectTopBean.DataBean> topData) {
        this.topData = topData;
        notifyDataSetChanged();
    }

    public void refreshData(List<ReaderTabSelectItemBean.DataBean> itemata) {
        this.itemData.clear();
        this.itemData.addAll(itemata);
        notifyDataSetChanged();
    }

    public void addData(List<ReaderTabSelectItemBean.DataBean> itemata) {
        this.itemData.addAll(itemata);
        notifyDataSetChanged();
    }



    class TopHolder extends  RecyclerView.ViewHolder{
        RecyclerView rcv_favourite;
        RecyclerView rcv_update;
        public TopHolder(@NonNull View itemView) {

            super(itemView);
            rcv_favourite = itemView.findViewById(R.id.rcv_favourite);
            rcv_update = itemView.findViewById(R.id.rcv_update);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_img,iv_source;
        TextView tv_title,tv_subContent,tv_source,tv_type;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_img =  itemView.findViewById(R.id.iv_img);
            iv_source = itemView.findViewById(R.id.iv_source);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subContent = itemView.findViewById(R.id.tv_subContent);
            tv_source = itemView.findViewById(R.id.tv_source);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }


    class TopAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<ReaderTabSelectTopBean.DataBean> data;
        Context context;
        TopAdapter1(Context context,List<ReaderTabSelectTopBean.DataBean> data){
            this.context = context;
            this.data = data;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(context).inflate(
                    R.layout.item_reader_recommend_tab_select_top_item1, viewGroup, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((ItemHolder)viewHolder).tv_title.setText(data.get(0).getArticleList().get(i).getTitle());
            ((ItemHolder)viewHolder).tv_subContent.setText(data.get(0).getArticleList().get(i).getSubContent());
            ((ItemHolder)viewHolder).tv_source.setText(data.get(0).getArticleList().get(i).getSource());
            ((ItemHolder)viewHolder).tv_type.setText("#"+data.get(0).getArticleList().get(i).getType()+"#");

            LinearLayout.LayoutParams params0 = (LinearLayout.LayoutParams) ((ItemHolder) viewHolder).ll_content.getLayoutParams();
            params0.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 25f);
            ((ItemHolder) viewHolder).ll_content.setLayoutParams(params0);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ItemHolder) viewHolder).iv_img.getLayoutParams();
            params.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 25f);
            ((ItemHolder) viewHolder).iv_img.setLayoutParams(params);
            GlideUtils.loadImage(context, data.get(0).getArticleList().get(i).getImage(),
                    ((ItemHolder) viewHolder).iv_img);
            GlideUtils.loadHomeUserImage(context, data.get(0).getArticleList().get(i).getSourceImage(),
                    ((ItemHolder) viewHolder).iv_source);

        }

        @Override
        public int getItemCount() {
            return data.size() == 0 ? 0 :data.get(0).getArticleList().size();
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            ImageView iv_img,iv_source;
            TextView tv_title,tv_subContent,tv_source,tv_type;
            LinearLayout ll_content;

            ItemHolder(@NonNull View itemView) {
                super(itemView);
                ll_content = itemView.findViewById(R.id.ll_content);
                iv_img =  itemView.findViewById(R.id.iv_img);
                iv_source = itemView.findViewById(R.id.iv_source);
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_subContent = itemView.findViewById(R.id.tv_subContent);
                tv_source = itemView.findViewById(R.id.tv_source);
                tv_type = itemView.findViewById(R.id.tv_type);
            }
        }
    }


    class TopAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ReaderTabSelectTopBean.DataBean> data;
        Context context;
        TopAdapter2(Context context,List<ReaderTabSelectTopBean.DataBean> data){
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(context).inflate(
                    R.layout.item_reader_recommend_tab_top_select_item2, viewGroup, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((ItemHolder)viewHolder).tv_title.setText(data.get(1).getArticleList().get(i).getTitle());
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ItemHolder) viewHolder).iv_img.getLayoutParams();
            params.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 25f);
            ((ItemHolder) viewHolder).iv_img.setLayoutParams(params);
            GlideUtils.loadImage(context, data.get(1).getArticleList().get(i).getImage(),
                    ((ItemHolder) viewHolder).iv_img);
        }

        @Override
        public int getItemCount() {
            return data.size() == 0 ? 0:data.get(1).getArticleList().size();
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            ImageView iv_img;
            TextView tv_title;

            ItemHolder(@NonNull View itemView) {
                super(itemView);
                iv_img =  itemView.findViewById(R.id.iv_img);
                tv_title = itemView.findViewById(R.id.tv_title);
            }
        }
    }

}
