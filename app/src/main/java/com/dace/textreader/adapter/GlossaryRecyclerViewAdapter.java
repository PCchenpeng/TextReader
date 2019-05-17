package com.dace.textreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.activity.AuthorActivity;
import com.dace.textreader.activity.CompositionDetailActivity;
import com.dace.textreader.activity.GlossaryWordExplainActivity;
import com.dace.textreader.activity.NewMainActivity;
import com.dace.textreader.bean.GlossaryBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 生词本列表适配器
 * Created by 70391 on 2017/10/24.
 */

public class GlossaryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String deleteUrl = HttpUrlPre.HTTP_URL + "/personal/word/delete/new";

    private Context mContext;
    private List<GlossaryBean> mList;
    private boolean editor = false;

    public GlossaryRecyclerViewAdapter(Context mContext, List<GlossaryBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_glossary_layout, parent, false);
//        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final GlossaryBean glossaryBean = mList.get(position);

        if (glossaryBean.getList().size() == 0) {
            return;
        }

        if (glossaryBean.isChoose()) {
            ((ViewHolder) holder).iv_choose.setVisibility(View.VISIBLE);
            if (glossaryBean.isSelected()) {
                ((ViewHolder) holder).iv_choose.setImageResource(R.drawable.icon_edit_selected);
            } else {
                ((ViewHolder) holder).iv_choose.setImageResource(R.drawable.icon_edit_unselected);
            }
        } else {
            ((ViewHolder) holder).iv_choose.setVisibility(View.GONE);
        }

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext, FlexDirection.ROW,
                FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        ((ViewHolder) holder).recyclerView.setLayoutManager(layoutManager);
        final GlossaryFlexBoxAdapter adapter = new GlossaryFlexBoxAdapter(glossaryBean.getList(), mContext, editor);
        ((ViewHolder) holder).recyclerView.setAdapter(adapter);
        adapter.setOnGlossaryFlexBoxItemClickListen(new GlossaryFlexBoxAdapter.OnGlossaryFlexBoxItemClick() {
            @Override
            public void onClick(View view) {
                int pos = ((ViewHolder) holder).recyclerView.getChildAdapterPosition(view);
                String word = glossaryBean.getList().get(pos);
                long essayId = glossaryBean.getEssayId();
                if (editor) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(word);
                    new DeleteData().execute(deleteUrl, jsonArray.toString(),
                            String.valueOf(essayId), String.valueOf(glossaryBean.getSourceType()),
                            glossaryBean.getId());
                    glossaryBean.getList().remove(pos);
                    adapter.notifyDataSetChanged();
                    if (glossaryBean.getList().size() == 0) {
                        mList.remove(position);
                        notifyDataSetChanged();
                    }
                } else {
                    turnToWordExplain(essayId, word);
                }
            }
        });

        final int sourceType = glossaryBean.getSourceType();
        String title = glossaryBean.getTitle();
        if (sourceType == 0 || sourceType == 1) {
            title = "《" + title + "》";
        } else if (sourceType == 2) {
            title = "词堆：" + title;
        } else if (sourceType == 3) {
            title = "作者信息：" + title;
        }
        ((ViewHolder) holder).tv_title.setText(title);
//        ((ViewHolder) holder).tv_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sourceType == 0) {
//                    turnToComposition(String.valueOf(glossaryBean.getEssayId()), glossaryBean.getType());
//                } else if (sourceType == 1) {
//                    turnToArticle(glossaryBean.getEssayId(), glossaryBean.getType());
//                } else if (sourceType == 2) {
//                    turnToWordExplain(glossaryBean.getEssayId(), glossaryBean.getTitle());
//                } else if (sourceType == 3) {
//                    turnToAuthor(glossaryBean.getTitle());
//                }
//            }
//        });

        ((ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListen != null)
                    onItemClickListen.onClick(glossaryBean);
            }
        });

    }

    /**
     * 前往作文详情
     *
     * @param compositionId
     * @param area
     */
    private void turnToComposition(String compositionId, int area) {
        Intent intent = new Intent(mContext, CompositionDetailActivity.class);
        intent.putExtra("writingId", compositionId);
        intent.putExtra("area", area);
        mContext.startActivity(intent);
    }

    /**
     * 前往文章详情
     *
     * @param essayId
     * @param essayType
     */
//    private void turnToArticle(long essayId, int essayType) {
//        Intent intent = new Intent(mContext, NewArticleDetailActivity.class);
//        intent.putExtra("id", essayId);
//        intent.putExtra("type", essayType);
//        mContext.startActivity(intent);
//    }

    /**
     * 跳转到词语解释
     */
    private void turnToWordExplain(long essayId, String words) {
        Intent intent = new Intent(mContext, GlossaryWordExplainActivity.class);
        intent.putExtra("glossaryId", essayId);
        intent.putExtra("words", words);
        intent.putExtra("essayTitle", "");
        intent.putExtra("glossaryTitle", words);
        mContext.startActivity(intent);
    }

    /**
     * 前往作者信息
     *
     * @param author
     */
    private void turnToAuthor(String author) {
        Intent intent = new Intent(mContext, AuthorActivity.class);
        intent.putExtra("author", author);
        intent.putExtra("id", -1);
        intent.putExtra("readId", -1);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
        notifyDataSetChanged();
    }

//    @Override
//    public void onClick(View v) {
//        if (onItemClickListen != null) {
//            onItemClickListen.onClick(v);
//        }
//    }

    public interface OnItemClickListen {
        void onClick(GlossaryBean glossaryBean);
    }

    private OnItemClickListen onItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        this.onItemClickListen = onItemClickListen;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_choose;
        TextView tv_title;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_choose = itemView.findViewById(R.id.iv_choose_glossary_item);
            tv_title = itemView.findViewById(R.id.tv_essay_title_glossary_item);
            recyclerView = itemView.findViewById(R.id.recycler_view_glossary_item);
        }
    }

    //删除数据
    private class DeleteData extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            //获取数据之前
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("words", params[1]);
                json.put("essayId", params[2]);
                json.put("status", 0);
                json.put("sourceType", params[3]);
                json.put("wId", params[4]);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //获取数据之后
        }
    }

}
