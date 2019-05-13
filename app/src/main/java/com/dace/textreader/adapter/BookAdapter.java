package com.dace.textreader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.KnowledgeBean;
import com.dace.textreader.bean.ReadTabAlbumDetailBean;

import java.util.List;


public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ReadTabAlbumDetailBean.DataBean.BookBean> mList;

    public BookAdapter(Context context, List<ReadTabAlbumDetailBean.DataBean.BookBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_book_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        ReadTabAlbumDetailBean.DataBean.BookBean bookBean = mList.get(i);
        ((ViewHolder) viewHolder).tv_title.setText(bookBean.getLevel1());
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        ((ViewHolder) viewHolder).recyclerView.setLayoutManager(layoutManager);
        BookChildAdapter adapter = new BookChildAdapter(mContext, bookBean.getArticleList());
        ((ViewHolder) viewHolder).recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListen(new BookChildAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = ((ViewHolder) viewHolder).recyclerView.getChildAdapterPosition(view);
                if (mOnItemClickListen != null) {
                    mOnItemClickListen.onClick(i, pos);
                }
            }
        });
    }

    public void refreshData(List<ReadTabAlbumDetailBean.DataBean.BookBean> mList) {
//        this.itemData.clear();
//        this.itemData.addAll(itemata);
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListen {
        void onClick(int position, int childPosition);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_knowledge_summary_item);
            recyclerView = itemView.findViewById(R.id.recycler_view_knowledge_summary_item);
        }
    }

    public List<ReadTabAlbumDetailBean.DataBean.BookBean> getmList() {
        return mList;
    }
}
