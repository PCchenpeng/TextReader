package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.ReaderChoiceBean;
import com.dace.textreader.bean.ReaderRecommendationBean;
import com.dace.textreader.util.GlideUtils;

import java.util.List;

public class ReadRecommendationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ReaderRecommendationBean.DataBean> itemList;
    private List<ReaderChoiceBean.DataBean.EssayListBean> choiceList;

    private final int TYPE_HEAD = 1;
    private final int TYPE_DES = 2;

    private Context context;

    public ReadRecommendationAdapter(List<ReaderRecommendationBean.DataBean> itemList,
                                     List<ReaderChoiceBean.DataBean.EssayListBean> choiceList,Context context){
        this.itemList = itemList;
        this.choiceList = choiceList;
        this.context = context;
    }

    public void addData(List<ReaderRecommendationBean.DataBean> item){
        this.itemList.addAll(item);
        notifyDataSetChanged();
    }

    public void setChoiceData(List<ReaderChoiceBean.DataBean.EssayListBean> choiceList){
        this.choiceList = choiceList;
        notifyDataSetChanged();
    }

    public void refreshData(List<ReaderRecommendationBean.DataBean> item){
        this.itemList.clear();
        this.itemList.addAll(item);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType){
            case TYPE_HEAD:
                view = LayoutInflater.from(context).inflate(
                        R.layout.item_readrecommendation_1, viewGroup, false);
                return new TopHolder(view);
            case TYPE_DES:
                view = LayoutInflater.from(context).inflate(
                        R.layout.item_readrecommendation_2, viewGroup, false);
                return new ItemHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType){
            case TYPE_HEAD:
                ChoiceAdapter choiceAdapter = new ChoiceAdapter(choiceList,context);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                ((TopHolder)viewHolder).rcl_banner.setLayoutManager(layoutManager);
                ((TopHolder)viewHolder).rcl_banner.setAdapter(choiceAdapter);
//                ((TopHolder)viewHolder).rcl_banner.setVisibility(View.GONE);
                break;
            case TYPE_DES:
                GlideUtils.loadImage(context, itemList.get(i-1).getAlbumCover(),
                        ((ItemHolder) viewHolder).iv_big_1);
                ((ItemHolder)viewHolder).tv_big_title.setText(itemList.get(i-1).getAlbumTitle());

                ((ItemHolder)viewHolder).tv_title_1.setText(itemList.get(i-1).getArticleList().get(0).getTitle());
                ((ItemHolder)viewHolder).tv_des_1.setText(itemList.get(i-1).getArticleList().get(0).getSubContent());
                GlideUtils.loadImage(context, itemList.get(i-1).getArticleList().get(0).getImage(),
                        ((ItemHolder) viewHolder).iv_small_1);
                GlideUtils.loadHomeUserImage(context, itemList.get(i-1).getArticleList().get(0).getSourceImage(),
                        ((ItemHolder) viewHolder).iv_source_1);
                ((ItemHolder)viewHolder).tv_source_1.setText(itemList.get(i-1).getArticleList().get(0).getSource());
                ((ItemHolder)viewHolder).tv_type_1.setText(itemList.get(i-1).getArticleList().get(0).getSource());


                if(itemList.get(i-1).getArticleList().size() ==1){
                        ((ItemHolder)viewHolder).ll_item_2.setVisibility(View.GONE);
                    }else if(itemList.get(i-1).getArticleList().size() ==2){
                    ((ItemHolder)viewHolder).ll_item_2.setVisibility(View.VISIBLE);
                    ((ItemHolder)viewHolder).tv_title_2.setText(itemList.get(i-1).getArticleList().get(1).getTitle());
                    ((ItemHolder)viewHolder).tv_des_2.setText(itemList.get(i-1).getArticleList().get(1).getSubContent());
                    GlideUtils.loadImage(context, itemList.get(i-1).getArticleList().get(1).getImage(),
                            ((ItemHolder) viewHolder).iv_small_2);
                    GlideUtils.loadHomeUserImage(context, itemList.get(i-1).getArticleList().get(1).getSourceImage(),
                            ((ItemHolder) viewHolder).iv_source_2);
                    ((ItemHolder)viewHolder).tv_source_2.setText(itemList.get(i-1).getArticleList().get(1).getSource());
                    ((ItemHolder)viewHolder).tv_type_2.setText(itemList.get(i-1).getArticleList().get(1).getSource());
                }


                break;
        }
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return TYPE_HEAD;
        }else {
            return TYPE_DES;
        }
    }
    

    @Override
    public int getItemCount() {
        return itemList == null ? 1 : itemList.size()+1;
    }

    class TopHolder extends RecyclerView.ViewHolder{
        RelativeLayout rl_search;
        LinearLayout ll_tradition,ll_story,ll_science,ll_excellent;
        RecyclerView rcl_banner;
         TopHolder(@NonNull View itemView) {
            super(itemView);
             rl_search =  itemView.findViewById(R.id.rl_search);
             ll_tradition = itemView.findViewById(R.id.ll_tradition);
             ll_story = itemView.findViewById(R.id.ll_story);
             ll_science = itemView.findViewById(R.id.ll_science);
             ll_excellent = itemView.findViewById(R.id.ll_excellent);
             rcl_banner = itemView.findViewById(R.id.rlv_banner);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        TextView tv_big_title,tv_title_1,tv_des_1,tv_source_1,tv_type_1,tv_title_2,tv_des_2,tv_source_2,tv_type_2;
        ImageView iv_big_1,iv_small_1,iv_small_2,iv_source_1,iv_source_2;
        LinearLayout ll_item_1;
        LinearLayout ll_item_2;
         ItemHolder(@NonNull View itemView) {
            super(itemView);
             tv_big_title =  itemView.findViewById(R.id.tv_big_title);
             tv_title_1 =  itemView.findViewById(R.id.tv_title_1);
             tv_des_1 =  itemView.findViewById(R.id.tv_des_1);
             tv_source_1 =  itemView.findViewById(R.id.tv_source_1);
             tv_type_1 =  itemView.findViewById(R.id.tv_type_1);
             tv_title_2 =  itemView.findViewById(R.id.tv_title_2);
             tv_des_2 =  itemView.findViewById(R.id.tv_des_2);
             tv_source_2 =  itemView.findViewById(R.id.tv_source_2);
             tv_type_2 =  itemView.findViewById(R.id.tv_type_2);
             iv_big_1 =  itemView.findViewById(R.id.iv_big_1);
             iv_small_1 =  itemView.findViewById(R.id.iv_small_1);
             iv_small_2 =  itemView.findViewById(R.id.iv_small_2);
             iv_source_1 =  itemView.findViewById(R.id.iv_source_1);
             iv_source_2 =  itemView.findViewById(R.id.iv_source_2);
             ll_item_1 = itemView.findViewById(R.id.ll_item_1);
             ll_item_2 = itemView.findViewById(R.id.ll_item_2);
        }
    }

    class ChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<ReaderChoiceBean.DataBean.EssayListBean> choiceList;
        private Context context;
        public ChoiceAdapter(List<ReaderChoiceBean.DataBean.EssayListBean> choiceList,Context context){
            this.choiceList = choiceList;
            this.context = context;
        }



        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.item_reader_recommend_choice, viewGroup, false);
            return new ChoiceHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            GlideUtils.loadImage(context, choiceList.get(i).getImage(),
                    ((ChoiceHolder) viewHolder).iv_img);
            ((ChoiceHolder)viewHolder).tv_type.setText(choiceList.get(i).getType());
        }

        @Override
        public int getItemCount() {
            return choiceList == null ? 0 : choiceList.size();
        }

        class ChoiceHolder extends RecyclerView.ViewHolder{
            ImageView iv_img;
            TextView tv_type;

            ChoiceHolder(@NonNull View itemView) {
                super(itemView);
                iv_img =  itemView.findViewById(R.id.iv_img);
                tv_type = itemView.findViewById(R.id.tv_type);
            }
        }
    }

}
