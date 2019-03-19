package com.dace.textreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;
import com.hhl.library.OnInitSelectedPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * 炸词界面的流式标签布局适配器
 * Created by 70391 on 2017/8/4.
 */

public class WordTagAdapter<T> extends BaseAdapter implements OnInitSelectedPosition {

    private final String punctuationStr =
            "[\\\"（*%&@#$）〝〞，。？！；：·…,.?!:;、……“”‘’()【】＜＞<>》\\\\d+\\\\w+《＝ /-》]+\"";

    private final Context mContext;
    private final List<T> mDataList;

    public WordTagAdapter(Context mContext) {
        this.mContext = mContext;
        mDataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.word_tag_layout, null);

        RelativeLayout relativeLayout = view.findViewById(R.id.rl_tag_item);
        TextView textView = view.findViewById(R.id.tv_word_tag_item);

        T t = mDataList.get(position);

        if (t instanceof String) {
            textView.setText((String) t);
            if (punctuationStr.contains((String) t)) {
                textView.setPadding(DensityUtil.dip2px(mContext, 2),
                        DensityUtil.dip2px(mContext, 4),
                        DensityUtil.dip2px(mContext, 2),
                        DensityUtil.dip2px(mContext, 4));
                relativeLayout.setBackgroundResource(R.drawable.explain_word_unselected_round_rectangle_bg);
            }
        }
        return view;
    }

    public void onlyAddAll(List<T> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean isSelectedPosition(int position) {
        if (position % 2 == 0) {
            return true;
        }
        return false;
    }


}
