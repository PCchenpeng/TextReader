package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.NewSearchActivity;
import com.dace.textreader.activity.SearchResultActivity;
import com.dace.textreader.bean.TestSearchBean;

import java.util.List;

public class SearchTestAdapter extends BaseAdapter {
    private Context context;
    private List<TestSearchBean.DataBean.TipListBean> mData;
    public SearchTestAdapter(Context context,List<TestSearchBean.DataBean.TipListBean> mData){
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

        final String searchText = mData.get(position).getTip();

        viewHolder.tv_content.setText(searchText);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SearchResultActivity.class);
                intent.putExtra("searchWord",searchText);
                context.startActivity(intent);
            }
        });


        return convertView;
    }

    class ViewHolder{
        TextView tv_content;
    }
}
