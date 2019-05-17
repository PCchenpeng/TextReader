package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.dace.textreader.activity.ReaderTabAlbumDetailActivity;
import com.dace.textreader.bean.ReaderTabAlbumItemBean;
import com.dace.textreader.bean.ReaderTabAlbumTopBean;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;

import java.util.List;

public class ReaderTabAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<ReaderTabAlbumTopBean.DataBean> topData ;
    private List<ReaderTabAlbumItemBean.DataBean> itemData;

    private final int TYPE_TOP = 1;
    private final int TYPE_ITEM = 2;

    private int currentindex = 0;

    public ReaderTabAlbumAdapter(Context context,List<ReaderTabAlbumTopBean.DataBean> topData,List<ReaderTabAlbumItemBean.DataBean> itemData){
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
                        R.layout.item_reader_rocommend_tab_album_top, viewGroup, false);
                return new TopHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(
                        R.layout.item_reader_rocommend_tab_album, viewGroup, false);
                return new ItemHolder(view);
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        int viewType = getItemViewType(i);
        switch (viewType){
            case TYPE_TOP:
                LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false);
                TopAdapter topAdapter1 = new TopAdapter(context,topData);

                ((TopHolder)viewHolder).rcv_favourite.setLayoutManager(layoutManager);
                SnapHelper snapHelper= new GravityPagerSnapHelper(Gravity.START);
                snapHelper.attachToRecyclerView(((TopHolder)viewHolder).rcv_favourite);
                ((TopHolder)viewHolder).rcv_favourite.setAdapter(topAdapter1);
                if (topData != null && topData.size() != 0){
                    ((TopHolder)viewHolder).tv_favorite.setVisibility(View.VISIBLE);
                } else {
                    ((TopHolder)viewHolder).tv_favorite.setVisibility(View.INVISIBLE);
                }

//                ((TopHolder)viewHolder).rcv_favourite.setOnFlingListener(new RecyclerView.OnFlingListener() {
//                    @Override
//                    public boolean onFling(int i, int i1) {
//
////                        Log.e("fling","i="+String.valueOf(i));
////                        Log.e("fling","i1="+String.valueOf(i1));
//
//                        return false;
//                    }
//                });
//                ((TopHolder)viewHolder).rcv_favourite.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                            int childCount = recyclerView.getChildCount();
//                            int itemCount = recyclerView.getLayoutManager().getItemCount();
//                            int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                            if(currentindex == firstVisibleItem){
//                                Log.e("fling","没划走");
//                            }else {
//                                currentindex = firstVisibleItem;
//                                Log.e("fling","childCount="+String.valueOf(childCount));
//                                Log.e("fling","itemCount="+String.valueOf(itemCount));
//                                Log.e("fling","firstVisibleItem="+String.valueOf(firstVisibleItem));
//                            }
//
//                        }
//                    }
//                });

                break;
            case TYPE_ITEM:
                ((ItemHolder)viewHolder).tv_title.setText(itemData.get(i-1).getTitle());
                ((ItemHolder)viewHolder).tv_sub.setText(itemData.get(i-1).getSubIntroduction());

//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ItemHolder) viewHolder).iv_img.getLayoutParams();
//                params.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 25f);
//                ((ItemHolder) viewHolder).iv_img.setLayoutParams(params);
                GlideUtils.loadHomeImage(context, itemData.get(i-1).getCover(),
                        ((ItemHolder) viewHolder).iv_img);

                ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ReaderTabAlbumDetailActivity.class);
                        intent.putExtra("format",itemData.get(i-1).getFormat());
                        intent.putExtra("sentenceNum",String.valueOf(itemData.get(i-1).getSentenceNum()));
                        intent.putExtra("albumId",String.valueOf(itemData.get(i-1).getAlbumId()));
                        context.startActivity(intent);
                    }
                });
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



    public void setTopData(List<ReaderTabAlbumTopBean.DataBean> topData) {
        this.topData = topData;
        notifyDataSetChanged();
    }

    public void refreshData(List<ReaderTabAlbumItemBean.DataBean> itemata) {
//        this.itemData.clear();
//        this.itemData.addAll(itemata);
        this.itemData = itemata;
        notifyDataSetChanged();
    }

    public void addData(List<ReaderTabAlbumItemBean.DataBean> itemata) {
        this.itemData.addAll(itemata);
        notifyDataSetChanged();
    }



    class TopHolder extends  RecyclerView.ViewHolder{
        RecyclerView rcv_favourite;
        TextView tv_favorite;
        public TopHolder(@NonNull View itemView) {

            super(itemView);
            rcv_favourite = itemView.findViewById(R.id.rcv_favourite);
            tv_favorite = itemView.findViewById(R.id.tv_favorite);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_img;
        TextView tv_title,tv_sub;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_img =  itemView.findViewById(R.id.iv_img);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_sub = itemView.findViewById(R.id.tv_sub);
        }
    }



    class TopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ReaderTabAlbumTopBean.DataBean> data;
        Context context;
        TopAdapter(Context context,List<ReaderTabAlbumTopBean.DataBean> data){
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =  LayoutInflater.from(context).inflate(
                    R.layout.item_reader_recommend_tab_top_select_item2, viewGroup, false);
            return new TopAdapter.ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            ((TopAdapter.ItemHolder)viewHolder).tv_title.setText(data.get(i).getTitle());
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((TopAdapter.ItemHolder) viewHolder).iv_img.getLayoutParams();
            params.width = DensityUtil.getScreenWidth(context) - DensityUtil.dip2px(context, 25f);
            ((TopAdapter.ItemHolder) viewHolder).iv_img.setLayoutParams(params);
            GlideUtils.loadImage(context, data.get(i).getCover(),
                    ((TopAdapter.ItemHolder) viewHolder).iv_img);

            ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReaderTabAlbumDetailActivity.class);
                    intent.putExtra("format",data.get(i).getFormat());
                    intent.putExtra("sentenceNum",data.get(i).getSentenceNum());
                    intent.putExtra("albumId",data.get(i).getAlbumId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0:data.size();
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
