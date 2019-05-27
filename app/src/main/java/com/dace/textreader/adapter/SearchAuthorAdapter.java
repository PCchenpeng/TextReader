package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dace.textreader.R;
import com.dace.textreader.activity.AuthorDetailActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.activity.UserHomepageActivity;
import com.dace.textreader.bean.FollowBean;
import com.dace.textreader.bean.SubListBean;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchAuthorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<SubListBean> mData;
    private Boolean isShowHeader;
    private final int TYPE_HEADER = 1;
    private final int TYPE_ITEM = 2;

    public SearchAuthorAdapter(List<SubListBean> data, Context context,Boolean isShowHeader){
        this.mContext = context;
        this.mData = data;
        this.isShowHeader = isShowHeader;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_search_author, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        GlideUtils.loadHomeUserImage(mContext,mData.get(i).getSource_image(),((ItemHolder)viewHolder).iv_author);
        ((ItemHolder)viewHolder).tv_author_name.setText(mData.get(i).getAuthor());
        final String authorId = mData.get(i).getIndex_id();
        ((ItemHolder)viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (mData.get(i).getSort_num().equals("2")) {//创作者
                    intent = new Intent(mContext, UserHomepageActivity.class);
                    intent.putExtra("userId", Long.parseLong(authorId));
                } else {
                    intent = new Intent(mContext, AuthorDetailActivity.class);
                    intent.putExtra("authorId", authorId);
                    intent.putExtra("author", mData.get(i).getAuthor());
                }
                mContext.startActivity(intent);
            }
        });
        ((ItemHolder)viewHolder).rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mData.get(i).getSort_num().equals("2")) {//创作者
                    followUser(((ItemHolder)viewHolder).rl_follow,((ItemHolder)viewHolder).tv_follow,((ItemHolder)viewHolder).iv_follow,i);
                } else {
                    //                if (!mData.get(i).isFollow()){
                    follow(((ItemHolder)viewHolder).rl_follow,((ItemHolder)viewHolder).tv_follow,((ItemHolder)viewHolder).iv_follow,i);
//                } else {
//                    unfollow(((ItemHolder)viewHolder).rl_follow,((ItemHolder)viewHolder).tv_follow,((ItemHolder)viewHolder).iv_follow,i);
//                }
                }
            }
        });
    }

    public void followUser(final RelativeLayout rl_follow, final TextView tv_follow, final ImageView iv_follow, final int position){
        JSONObject params = new JSONObject();
        try {
            params.put("followingId",String.valueOf(mData.get(position).getIndex_id()));
            params.put("followerId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(mContext).requestAsyn(HttpUrlPre.HTTP_URL + "/followRelation/setup", OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                FollowBean followBean = GsonUtil.GsonToBean(result.toString(),FollowBean.class);
                if (followBean.getStatus() == 600){//已经关注过
//                    rl_follow.setSelected(true);
//                    iv_follow.setVisibility(View.GONE);
//                    tv_follow.setText("已关注");
//                    mData.get(position).setFollow(true);
                    Toast.makeText(mContext,followBean.getMsg(),Toast.LENGTH_SHORT).show();
                } else if (followBean.getStatus() == 200){
                    mData.get(position).setFollow(true);
                    rl_follow.setSelected(true);
                    iv_follow.setVisibility(View.GONE);
                    tv_follow.setText("已关注");
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    public void follow(final RelativeLayout rl_follow, final TextView tv_follow, final ImageView iv_follow, final int position){
        JSONObject params = new JSONObject();
        try {
            params.put("type","2");
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("albumId",mData.get(position).getIndex_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(mContext).requestAsyn(HttpUrlPre.HTTP_URL_ + "/album/subscribe", OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                FollowBean followBean = GsonUtil.GsonToBean(result.toString(),FollowBean.class);
                if (followBean.getStatus() == 600){//已经关注过
//                    rl_follow.setSelected(true);
//                    iv_follow.setVisibility(View.GONE);
//                    tv_follow.setText("已关注");
//                    mData.get(position).setFollow(true);
                    Toast.makeText(mContext,followBean.getMsg(),Toast.LENGTH_SHORT).show();
                } else if (followBean.getStatus() == 200){
                    mData.get(position).setFollow(true);
                    rl_follow.setSelected(true);
                    iv_follow.setVisibility(View.GONE);
                    tv_follow.setText("已关注");
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    public void unfollow(final RelativeLayout rl_follow, final TextView tv_follow, final ImageView iv_follow, final int position){
        JSONObject params = new JSONObject();
        try {
            params.put("type","2");
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("albumId",mData.get(position).getIndex_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(mContext).requestAsyn(HttpUrlPre.HTTP_URL_ + "/album/unsubscribe", OkHttpManager.TYPE_POST_JSON, params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                FollowBean followBean = GsonUtil.GsonToBean(result.toString(),FollowBean.class);
                if (followBean.getStatus() == 200){
                    mData.get(position).setFollow(false);
                    rl_follow.setSelected(false);
                    iv_follow.setVisibility(View.VISIBLE);
                    tv_follow.setText("关注");
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if (isShowHeader){
            return mData == null ? 0 : mData.size()+1;
        }else {
            return mData == null ? 0 : mData.size();
        }

    }

    @Override
    public int getItemViewType(int position){
        if(isShowHeader){
            if(position == 0){
                return TYPE_HEADER;
            }else {
                return TYPE_ITEM;
            }
        }else {
            return TYPE_ITEM;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_author;
        TextView tv_author_name;
        TextView tv_follow;
        ImageView iv_follow;
        RelativeLayout rl_follow;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            iv_author = itemView.findViewById(R.id.iv_author);
            tv_author_name = itemView.findViewById(R.id.tv_author_name);
            iv_follow = itemView.findViewById(R.id.iv_follow);
            tv_follow = itemView.findViewById(R.id.tv_follow);
            rl_follow = itemView.findViewById(R.id.rl_follow);
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder {
        public HeadHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void addData(List<SubListBean> item){
        if(item != null)
        this.mData.addAll(item);
        notifyDataSetChanged();
    }

    public void refreshData(List<SubListBean> item){
        this.mData.clear();
        if(item != null)
        this.mData.addAll(item);
        notifyDataSetChanged();
    }

}
