package com.dace.textreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dace.textreader.R;
import com.hhl.library.OnInitSelectedPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索界面流式标签布局的适配器
 * Created by 70391 on 2017/8/4.
 */

public class TagAdapter<T> extends BaseAdapter implements OnInitSelectedPosition {

    private final Context mContext;
    private final List<T> mDataList;

    public TagAdapter(Context mContext) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tag_layout, null);

        TextView textView = view.findViewById(R.id.tv_tag_item);

        T t = mDataList.get(position);

        if (t instanceof String) {
            textView.setText((String) t);
        }
        return view;
    }

    public void onlyAddAll(List<T> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<T> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }


    @Override
    public boolean isSelectedPosition(int position) {
        if (position % 2 == 0) {
            return true;
        }
        return false;
    }

}
