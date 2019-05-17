package com.dace.textreader.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dace.textreader.R;
import com.dace.textreader.bean.HomeRecommendationBean;
import com.dace.textreader.listen.OnUserInfoClickListen;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.adapter
 * Created by Administrator.
 * Created time 2018/10/23 0023 下午 4:41.
 * Version   1.0;
 * Describe :  首页推荐页适配器
 * History:
 * ==============================================================================
 */
public class HomeRecommendationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private static final int TYPE_WRITING = 0;
    private static final int TYPE_READ = 1;
    private static final int TYPE_LESSON = 2;
    private static final int TYPE_EVENTS = 3;

    private Context mContext;
    private List<HomeRecommendationBean> mList;
    private Typeface mTypeface;
    private int width;
    private int height;

    public HomeRecommendationAdapter(Context context, List<HomeRecommendationBean> list) {
        this.mContext = context;
        this.mList = list;

        AssetManager mgr = mContext.getAssets();
        mTypeface = Typeface.createFromAsset(mgr, "css/GB2312.ttf");

        width = DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 30);
        height = (int) (width * 0.5625);
    }

    public void addData(List<HomeRecommendationBean> list) {
        int start = mList.size();
        int count = list.size();
        mList.addAll(list);
        notifyItemRangeChanged(start, count);
    }

    public void setList(List<HomeRecommendationBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder holder = null;
        if (i == 0) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_home_recommend_writing_layout, viewGroup, false);
            view.setOnClickListener(this);
            holder = new WritingViewHolder(view);
        }
        return holder;
    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position,
//                                 @NonNull List<Object> payloads) {
//        if (payloads.isEmpty()) {
//            onBindViewHolder(viewHolder, position);
//        } else {
//            Bundle payload = (Bundle) payloads.get(0);
//            if (viewHolder instanceof WritingViewHolder) {
//                HomeRecommendationBean bean = mList.get(position);
//                for (String key : payload.keySet()) {
//                    switch (key) {
//                        case "key_title":
//                            ((WritingViewHolder) viewHolder).tv_cover_title.setText(bean.getTitle());
//                            ((WritingViewHolder) viewHolder).tv_title.setText(bean.getTitle());
//                            break;
//                        case "key_content":
//                            ((WritingViewHolder) viewHolder).tv_content.setText(bean.getContent());
//                            break;
//                        case "key_image":
//                            if (!bean.getImage().equals("") && !bean.getImage().equals("null")) {
//                                ViewGroup.LayoutParams layoutParams =
//                                        ((WritingViewHolder) viewHolder).rl_cover.getLayoutParams();
//                                layoutParams.width = width;
//                                layoutParams.height = height;
//                                GlideUtils.loadImage(mContext, bean.getImage(),
//                                        ((WritingViewHolder) viewHolder).iv_cover);
//                                ((WritingViewHolder) viewHolder).tv_cover_title.setText(bean.getTitle());
//                                ((WritingViewHolder) viewHolder).rl_cover.setVisibility(View.VISIBLE);
//                                ((WritingViewHolder) viewHolder).tv_title.setVisibility(View.GONE);
//                            } else {
//                                ((WritingViewHolder) viewHolder).tv_title.setText(bean.getTitle());
//                                ((WritingViewHolder) viewHolder).tv_title.setVisibility(View.VISIBLE);
//                                ((WritingViewHolder) viewHolder).rl_cover.setVisibility(View.GONE);
//                            }
//                            break;
//                        case "key_score":
//                            updateScoreUi(viewHolder, bean);
//                            break;
//                        case "key_prize":
//                            updateScoreUi(viewHolder, bean);
//                            break;
//                        case "key_avg_score":
//                            updateScoreUi(viewHolder, bean);
//                            break;
//                        case "key_views":
//                            String views = bean.getViews() + "阅读";
//                            ((WritingViewHolder) viewHolder).tv_views.setText(views);
//                            break;
//                        case "key_user_name":
//                            ((WritingViewHolder) viewHolder).tv_user.setText(bean.getUserName());
//                            break;
//                        case "key_user_image":
//                            GlideUtils.loadUserImage(mContext,
//                                    HttpUrlPre.FILE_URL + bean.getUserImage(),
//                                    ((WritingViewHolder) viewHolder).iv_user);
//                            break;
//                        case "key_user_grade":
//                            ((WritingViewHolder) viewHolder).tv_grade.setText(bean.getUserGrade());
//                            break;
//                    }
//                }
//            }
//        }
//    }

    /**
     * 更新分数UI
     *
     * @param viewHolder
     * @param bean
     */
    private void updateScoreUi(RecyclerView.ViewHolder viewHolder, HomeRecommendationBean bean) {
        if (!bean.getCompositionScore().equals("")
                && !bean.getCompositionScore().equals("null")
                && !bean.getCompositionScore().equals("0")) {
            ((WritingViewHolder) viewHolder).tv_score.setTypeface(mTypeface);
            String score = bean.getCompositionScore() + "分";
            ((WritingViewHolder) viewHolder).tv_score.setText(score);
            ((WritingViewHolder) viewHolder).rl_score.setVisibility(View.VISIBLE);
            ((WritingViewHolder) viewHolder).ll_user_score.setVisibility(View.GONE);
        } else if (!bean.getCompositionPrize().equals("")
                && !bean.getCompositionPrize().equals("null")) {
            ((WritingViewHolder) viewHolder).tv_score.setTypeface(mTypeface);
            ((WritingViewHolder) viewHolder).tv_score.setText(bean.getCompositionPrize());
            ((WritingViewHolder) viewHolder).rl_score.setVisibility(View.VISIBLE);
            ((WritingViewHolder) viewHolder).ll_user_score.setVisibility(View.GONE);
        } else if (!bean.getCompositionAvgScore().equals("")
                && !bean.getCompositionAvgScore().equals("null")
                && !bean.getCompositionAvgScore().equals("0")) {
            ((WritingViewHolder) viewHolder).tv_user_score.setText(bean.getCompositionAvgScore());
            ((WritingViewHolder) viewHolder).ll_user_score.setVisibility(View.VISIBLE);
            ((WritingViewHolder) viewHolder).rl_score.setVisibility(View.GONE);
        } else {
            ((WritingViewHolder) viewHolder).rl_score.setVisibility(View.GONE);
            ((WritingViewHolder) viewHolder).ll_user_score.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof WritingViewHolder) {
            final HomeRecommendationBean bean = mList.get(i);
            ((WritingViewHolder) viewHolder).rl_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserInfoClickListen != null) {
                        onUserInfoClickListen.onClick(bean.getUserId());
                    }
                }
            });
            GlideUtils.loadUserImage(mContext,
//                    HttpUrlPre.FILE_URL +
                            bean.getUserImage(),
                    ((WritingViewHolder) viewHolder).iv_user);
            ((WritingViewHolder) viewHolder).tv_user.setText(bean.getUserName());
            ((WritingViewHolder) viewHolder).tv_grade.setText(bean.getUserGrade());
            //分数显示
            updateScoreUi(viewHolder, bean);
            if (!bean.getImage().equals("") && !bean.getImage().equals("null")) {
                ViewGroup.LayoutParams layoutParams =
                        ((WritingViewHolder) viewHolder).rl_cover.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                GlideUtils.loadImage(mContext, bean.getImage(), ((WritingViewHolder) viewHolder).iv_cover);
//                Glide.with(mContext).load("").crossFade(1000).into(iv_cover);
                ((WritingViewHolder) viewHolder).tv_cover_title.setText(bean.getTitle());
                ((WritingViewHolder) viewHolder).rl_cover.setVisibility(View.VISIBLE);
                ((WritingViewHolder) viewHolder).tv_title.setVisibility(View.GONE);
            } else {
                ((WritingViewHolder) viewHolder).tv_title.setText(bean.getTitle());
                ((WritingViewHolder) viewHolder).tv_title.setVisibility(View.VISIBLE);
                ((WritingViewHolder) viewHolder).rl_cover.setVisibility(View.GONE);
            }
            ((WritingViewHolder) viewHolder).tv_content.setText(bean.getContent());
            ((WritingViewHolder) viewHolder).tv_date.setText(bean.getDate());
            String views = bean.getViews() + "阅读";
            ((WritingViewHolder) viewHolder).tv_views.setText(views);
            if (bean.getUserName().equals("") || bean.getUserImage().equals("")) {
                ((WritingViewHolder) viewHolder).ll_top.setVisibility(View.GONE);
            } else {
                ((WritingViewHolder) viewHolder).ll_top.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListen != null) {
            mOnItemClickListen.onClick(v);
        }
    }

    public interface OnItemClickListen {
        void onClick(View view);
    }

    private OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.mOnItemClickListen = onItemClickListen;
    }

    private OnUserInfoClickListen onUserInfoClickListen;

    public void setOnUserInfoClickListen(OnUserInfoClickListen onUserInfoClickListen) {
        this.onUserInfoClickListen = onUserInfoClickListen;
    }

    class WritingViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_top;
        RelativeLayout rl_user;
        ImageView iv_user;
        TextView tv_user;
        TextView tv_grade;
        RelativeLayout rl_score;
        TextView tv_score;
        LinearLayout ll_user_score;
        TextView tv_user_score;
        RelativeLayout rl_cover;
        ImageView iv_cover;
        TextView tv_cover_title;
        TextView tv_title;
        TextView tv_content;
        TextView tv_date;
        TextView tv_views;

        public WritingViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_top = itemView.findViewById(R.id.ll_top_home_recommend_writing_item);
            rl_user = itemView.findViewById(R.id.rl_user_home_recommend_writing_item);
            iv_user = itemView.findViewById(R.id.iv_user_home_recommend_writing_item);
            tv_user = itemView.findViewById(R.id.tv_user_home_recommend_writing_item);
            tv_grade = itemView.findViewById(R.id.tv_grade_home_recommend_writing_item);
            rl_score = itemView.findViewById(R.id.rl_writing_score_home_recommend_writing_item);
            tv_score = itemView.findViewById(R.id.tv_writing_score_home_recommend_writing_item);
            ll_user_score = itemView.findViewById(R.id.ll_user_score_home_recommend_writing_item);
            tv_user_score = itemView.findViewById(R.id.tv_user_score_home_recommend_writing_item);
            rl_cover = itemView.findViewById(R.id.rl_cover_home_recommend_writing_item);
            iv_cover = itemView.findViewById(R.id.iv_cover_home_recommend_writing_item);
            tv_cover_title = itemView.findViewById(R.id.tv_title_white_home_recommend_writing_item);
            tv_title = itemView.findViewById(R.id.tv_title_black_home_recommend_writing_item);
            tv_content = itemView.findViewById(R.id.tv_content_home_recommend_writing_item);
            tv_date = itemView.findViewById(R.id.tv_date_home_recommend_writing_item);
            tv_views = itemView.findViewById(R.id.tv_views_home_recommend_writing_item);
        }
    }
}
