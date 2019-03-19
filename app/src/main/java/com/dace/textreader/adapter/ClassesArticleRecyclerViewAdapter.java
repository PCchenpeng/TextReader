package com.dace.textreader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dace.textreader.R;
import com.dace.textreader.bean.Classes;
import com.dace.textreader.util.GlideRoundImage;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.List;

/**
 * 课内文章的文章列表适配器
 * Created by 70391 on 2017/7/31.
 */

public class ClassesArticleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<Classes> mList;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ClassesArticleRecyclerViewAdapter(Context mContext, List<Classes> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_classes_article_layout, parent, false);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Classes classes = mList.get(position);
        ((ViewHolder) holder).tv_title.setText(classes.getTitle());
        ((ViewHolder) holder).tv_author.setText(classes.getAuthor());
        String url = classes.getImagePath();
        if (mContext != null) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.image_placeholder_square)
                    .error(R.drawable.image_placeholder_square)
                    .centerCrop()
                    .transform(new GlideRoundImage(mContext, 4));
            Glide.with(mContext)
                    .load(url)
                    .apply(options)
                    .listener(GlidePalette.with(url)
                            .use(GlidePalette.Profile.MUTED)
                            .intoBackground(((ViewHolder) holder).tv_author, GlidePalette.Swatch.RGB)
                            .crossfade(true)
                    )
                    .into(((ViewHolder) holder).iv_bg);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_author;
        ImageView iv_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_bg = itemView.findViewById(R.id.iv_background_classes_article_item);
            tv_title = itemView.findViewById(R.id.tv_title_classes_article_item);
            tv_author = itemView.findViewById(R.id.tv_author_classes_article_item);
        }
    }
}
