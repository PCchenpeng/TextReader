package com.dace.textreader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.TestSearchBean;

import java.util.List;

public class SearchTestAdapter extends BaseAdapter {
    private Context context;
    private List<TestSearchBean.DataBean> mData;
    public SearchTestAdapter(Context context,List<TestSearchBean.DataBean> mData){
        this.context = context;
        this.mData = mData;
    }
    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_search_test, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_content = convertView.findViewById(R.id.tv_cotent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String text = mData.get(position).getTip();

        viewHolder.tv_content.setText(text);


        return convertView;
    }

    class ViewHolder{
        TextView tv_content;
    }
}
