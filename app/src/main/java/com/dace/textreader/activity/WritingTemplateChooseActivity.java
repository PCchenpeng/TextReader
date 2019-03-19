package com.dace.textreader.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.adapter.CompetitionRecyclerViewAdapter;
import com.dace.textreader.adapter.WritingTemplateAdapter;
import com.dace.textreader.bean.CompetitionBean;
import com.dace.textreader.bean.TemplateBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.editor.RichEditor;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 写作文--模板选择
 */
public class WritingTemplateChooseActivity extends BaseActivity {

    private static final String templateUrl = HttpUrlPre.HTTP_URL + "/select/format/list";
    //保存到草稿箱
    private static final String SAVE_WRITING_DRAFT = HttpUrlPre.HTTP_URL + "/writing/save";
    //发布作文
    private static final String publishUrl = HttpUrlPre.HTTP_URL + "/release/writing";
    //作文比赛活动
    private static final String competitionUrl = HttpUrlPre.HTTP_URL + "/writing/match/query";
    //判断学生是否已提交过稿到征稿比赛中
    private static final String checkCompetitionUrl = HttpUrlPre.HTTP_URL + "/is/not/commit/match";
    //提交作文到比赛活动
    private static final String submitCompetitionUrl = HttpUrlPre.HTTP_URL + "/writing/commit/match";

    private RelativeLayout rl_back;
    private TextView tv_commit;
    private WebView webView;
    private RichEditor editor;
    private TextView tv_add;
    private RelativeLayout rl_template;
    private ImageView iv_less;
    private RecyclerView recyclerView;

    private WritingTemplateChooseActivity mContext;

    private String writingId;
    private String writingTitle;
    private String writingContent;
    private String writingCover;
    private int writingNumber;
    private int writingType;
    private int writingArea;
    private String writingTaskId;

    private List<TemplateBean> mList = new ArrayList<>();
    private WritingTemplateAdapter adapter;
    private boolean refreshing = false;

    //活动列表
    private List<CompetitionBean> competitionList = new ArrayList<>();
    private String mSelectedTaskId = "";  //当前选择的
    private int mCompetitionPageNum = 1;  //活动页码

    private int writingFormat = 1;
    private int mSelectedPosition = -1;

    private boolean isCorrection = false;  //是否是作文批改，是的话点击完成直接跳转批改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_template_choose);

        mContext = this;

        initData();
        initView();
        initTemplateData();
        initEvents();
    }

    private void initTemplateData() {
        if (refreshing) {
            showTip("正在获取模板数据");
        } else {
            new GetData(mContext).execute(templateUrl);
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCorrection) {
                    commitToTeacher();
                } else {
                    clickCommit();
                }
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() == 0) {
                    showTip("正在获取模板数据");
                    initTemplateData();
                }
                rl_template.setVisibility(View.VISIBLE);
            }
        });
        iv_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_template.setVisibility(View.GONE);
            }
        });
        adapter.setOnItemClickListen(new WritingTemplateAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView.getChildAdapterPosition(view);
                if (pos != mSelectedPosition) {
                    mList.get(mSelectedPosition).setSelected(false);
                    adapter.notifyItemChanged(mSelectedPosition);
                    mList.get(pos).setSelected(true);
                    adapter.notifyItemChanged(pos);
                    mSelectedPosition = pos;
                    tv_add.setText("更换模板");
                    writingFormat = mList.get(mSelectedPosition).getFormat();
                    loadWebView();
                }
            }
        });
    }

    /**
     * 点击了完成按钮
     */
    private void clickCommit() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_submit_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        ImageView iv_close = holder.getView(R.id.iv_close_writing_dialog);
                        LinearLayout ll_draft = holder.getView(R.id.ll_writing_save_draft_dialog);
                        LinearLayout ll_public = holder.getView(R.id.ll_writing_public_dialog);
                        LinearLayout ll_correction = holder.getView(R.id.ll_writing_submit_dialog);
                        LinearLayout ll_work = holder.getView(R.id.ll_writing_work_dialog);
                        LinearLayout ll_events = holder.getView(R.id.ll_writing_events_dialog);
                        ll_draft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveWritingToDraft();
                                dialog.dismiss();
                            }
                        });
                        ll_public.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPublicWritingDialog();
                                dialog.dismiss();
                            }
                        });
                        ll_correction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commitToTeacher();
                                dialog.dismiss();
                            }
                        });
                        ll_work.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitToWork();
                                dialog.dismiss();
                            }
                        });
                        ll_events.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (competitionList.size() == 0) {
                                    showTip("暂无活动");
                                } else {
                                    showCompetitionDialog();
                                }
                                dialog.dismiss();
                            }
                        });
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 保存作文内容到草稿箱
     */
    private void saveWritingToDraft() {
        showTip("正在保存作文内容到草稿箱...");
        new SaveToDraft(mContext).execute(SAVE_WRITING_DRAFT,
                writingContent, writingTitle, String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(writingNumber), writingCover, String.valueOf(writingFormat));
    }

    private int isPublic = 1;  //作文发布是否公开

    /**
     * 显示隐私作文私密设置
     */
    private void showPublicWritingDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_public_setting_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        SwitchButton switchButton = holder.getView(R.id.switch_writing_public_setting_dialog);
                        switchButton.setChecked(true);
                        isPublic = 1;
                        TextView tv_sure = holder.getView(R.id.tv_sure_writing_public_setting_dialog);
                        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    isPublic = 1;
                                } else {
                                    isPublic = 0;
                                }
                            }
                        });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                publicDraft();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 发布作文
     */
    private void publicDraft() {
        showTip("正在发布作文...");
        new PublicDraft(this)
                .execute(publishUrl, writingId, writingTitle, writingContent, String.valueOf(5),
                        writingTaskId, String.valueOf(writingNumber), String.valueOf(isPublic));
    }

    /**
     * 提交给老师批改
     */
    private void commitToTeacher() {
        Intent intent = new Intent(mContext, SubmitReviewActivity.class);
        intent.putExtra("type", "writing");
        intent.putExtra("isEditor", true);
        intent.putExtra("id", writingId);
        intent.putExtra("title", writingTitle);
        intent.putExtra("count", writingNumber);
        intent.putExtra("writing_match", writingTaskId);
        intent.putExtra("writing_content", writingContent);
        intent.putExtra("writing_cover", writingCover);
        intent.putExtra("writing_format", writingFormat);
        startActivityForResult(intent, 0);
    }

    /**
     * 提交作业
     */
    private void submitToWork() {
        Intent intent = new Intent(mContext, WritingWorkActivity.class);
        intent.putExtra("isSubmit", true);
        intent.putExtra("isEditor", true);
        intent.putExtra("writingId", writingId);
        intent.putExtra("writingTitle", writingTitle);
        intent.putExtra("writingContent", writingContent);
        intent.putExtra("writingCover", writingCover);
        intent.putExtra("writingFormat", writingFormat);
        intent.putExtra("writingCount", writingNumber);
        intent.putExtra("writingArea", "5");
        intent.putExtra("writingType", writingType);
        startActivityForResult(intent, 0);
    }

    /**
     * 显示活动、比赛列表
     */
    private void showCompetitionDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_submit_competition_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final RecyclerView recyclerView = holder.getView(R.id.recycler_view_submit_competition_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_submit_competition_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_submit_competition_dialog);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                                LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        final CompetitionRecyclerViewAdapter adapter =
                                new CompetitionRecyclerViewAdapter(mContext, competitionList);
                        recyclerView.setAdapter(adapter);
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                if (competitionList.size() != 0) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                        adapter.setOnItemClickListener(
                                new CompetitionRecyclerViewAdapter.OnCompetitionItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int pos = recyclerView.getChildAdapterPosition(view);
                                        int status = competitionList.get(pos).getStatus();
                                        if (pos != getCompetitionSelectedPos() && status == 1) {
                                            updateCompetitionList();
                                            competitionList.get(pos).setSelected(true);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos = getCompetitionSelectedPos();
                                if (pos == -1) {
                                    showTip("请选择活动项目");
                                } else {
                                    String id = competitionList.get(pos).getId();
                                    checkCompetition(id);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 检查用户在这个活动中有没有投稿
     */
    private void checkCompetition(String taskId) {
        mSelectedTaskId = taskId;
        new CheckCompetition(this).execute(checkCompetitionUrl, taskId);
    }

    /**
     * 获取当前选中的活动、比赛
     *
     * @return
     */
    private int getCompetitionSelectedPos() {
        int i = -1;
        for (int j = 0; j < competitionList.size(); j++) {
            if (competitionList.get(j).isSelected()) {
                i = j;
                break;
            }
        }
        return i;
    }

    /**
     * 更新活动、比赛列表
     */
    private void updateCompetitionList() {
        for (int i = 0; i < competitionList.size(); i++) {
            competitionList.get(i).setSelected(false);
        }
    }

    private void initData() {

        writingId = getIntent().getStringExtra("id");
        writingTitle = getIntent().getStringExtra("title");
        writingContent = getIntent().getStringExtra("content");
        writingCover = getIntent().getStringExtra("cover");
        writingNumber = getIntent().getIntExtra("count", 0);
        writingType = getIntent().getIntExtra("type", 5);
        writingArea = getIntent().getIntExtra("area", 5);
        writingTaskId = getIntent().getStringExtra("taskId");
        writingFormat = getIntent().getIntExtra("format", 1);
        isCorrection = getIntent().getBooleanExtra("isCorrection", false);

        initCompetitionData();
    }

    /**
     * 获取活动、比赛数据
     */
    private void initCompetitionData() {
        new GetCompetitionData(this).execute(competitionUrl);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_writing_template_choose);
        tv_commit = findViewById(R.id.tv_commit_writing_template_choose);

        webView = findViewById(R.id.web_view_writing_template_choose);
        editor = findViewById(R.id.tv_content_writing_template_choose);

        tv_add = findViewById(R.id.tv_add_writing_template_choose);
        rl_template = findViewById(R.id.rl_add_writing_template_choose);
        iv_less = findViewById(R.id.iv_less_writing_template_choose);
        recyclerView = findViewById(R.id.recycler_view_writing_template_choose);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WritingTemplateAdapter(mContext, mList);
        recyclerView.setAdapter(adapter);

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        rl_template.setVisibility(View.GONE);
        updateUi();
    }

    private void updateUi() {

        loadWebView();

        editor.setNoImageOperate();
        editor.setContent(writingContent);
//        try {
//            JSONArray array = new JSONArray(writingContent);
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject object = array.getJSONObject(i);
//                String type = object.getString("type");
//                String s = object.getString("content");
//                if (type.equals("image")) {
//                    editor.insertImage(s);
//                } else {
//                    editor.appendText(s);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            editor.appendText(writingContent);
//        }
        editor.setNoEditor();
    }

    private void loadWebView() {
        String path;
        if (writingCover.equals("")) {
            path = "file:///android_asset/html/compositionStyle.html?" +
                    "format=" + writingFormat +
                    "&article=" + writingTitle +
                    "&saveTime=" + DateUtil.getTime() +
                    "&username=" + NewMainActivity.USERNAME;
        } else {
            path = "file:///android_asset/html/compositionStyle.html?" +
                    "format=" + writingFormat +
                    "&article=" + writingTitle +
                    "&saveTime=" + DateUtil.getTime() +
                    "&username=" + NewMainActivity.USERNAME +
                    "&cover=" + writingCover;
        }
        webView.loadUrl(path);
    }

    private void initWebSettings() {
        WebSettings webSettings = webView.getSettings();
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //允许js代码
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        //禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        //禁用文字缩放
        webSettings.setTextZoom(100);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置不缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                boolean isSubmit = data.getBooleanExtra("submit", false);
                if (isSubmit) {
                    Intent intent = new Intent();
                    intent.putExtra("submit", true);
                    setResult(0, intent);
                    finish();
                }
            }
        }
    }

    /**
     * 分析模板数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    TemplateBean bean = new TemplateBean();
                    bean.setFormat(object.optInt("format", 1));
                    bean.setImagePath(object.getString("url"));
                    if (writingFormat == bean.getFormat()) {
                        bean.setSelected(true);
                        mSelectedPosition = i;
                    } else {
                        bean.setSelected(false);
                    }
                    mList.add(bean);
                }

                if (mSelectedPosition == -1 && mList.size() != 0) {
                    mList.get(0).setSelected(true);
                    mSelectedPosition = 0;
                }

                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析活动、比赛数据
     *
     * @param s
     */
    private void analyzeCompetitionData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (jsonArray.length() == 0) {
                    noCompetition();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        CompetitionBean competitionBean = new CompetitionBean();
                        competitionBean.setId(object.getString("id"));
                        competitionBean.setTitle(object.getString("title"));
                        competitionBean.setContent(object.getString("description"));
                        competitionBean.setStatus(object.optInt("status", -1));
                        competitionBean.setImage(object.getString("image"));
                        if (competitionBean.getId().equals(writingTaskId)) {
                            competitionBean.setSelected(true);
                            competitionList.add(0, competitionBean);
                        } else {
                            competitionBean.setSelected(false);
                            competitionList.add(competitionBean);
                        }
                    }
                    mCompetitionPageNum = mCompetitionPageNum + 1;
                    initCompetitionData();
                }
            } else {
                noCompetition();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noCompetition();
        }
    }

    /**
     * 暂无活动
     */
    private void noCompetition() {

    }

    /**
     * 分析调用了保存到草稿箱接口后返回的信息
     *
     * @param s
     */
    private void analyzeSaveToDraftData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                String id = object.getString("id");
                saveSuccess(id);
            } else {
                saveFailed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            saveFailed();
        }
    }

    /**
     * 保存到草稿箱失败
     */
    private void saveFailed() {
        showTip("保存失败，请稍后重试！");
    }

    /**
     * 保存到草稿箱成功
     */
    private void saveSuccess(String id) {
        Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", writingTitle);
        intent.putExtra("content", writingContent);
        intent.putExtra("cover", writingCover);
        intent.putExtra("count", writingNumber);
        intent.putExtra("format", writingFormat);
        intent.putExtra("area", 5);
        intent.putExtra("index", 0);
        startActivityForResult(intent, 0);
    }

    /**
     * 发布作文数据返回处理
     *
     * @param s
     */
    private void publicDraftData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                writingId = object.getString("id");
                publicDraftSuccess();
            } else if (300 == jsonObject.optInt("status", -1)) {
                String msg = jsonObject.getString("msg");
                showTip(msg);
            } else {
                publicDraftError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            publicDraftError();
        }
    }

    /**
     * 发布成功
     */
    private void publicDraftSuccess() {
        Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
        intent.putExtra("id", writingId);
        intent.putExtra("title", writingTitle);
        intent.putExtra("content", writingContent);
        intent.putExtra("cover", writingCover);
        intent.putExtra("count", writingNumber);
        intent.putExtra("format", writingFormat);
        intent.putExtra("area", 5);
        intent.putExtra("index", 1);
        startActivityForResult(intent, 0);
    }

    /**
     * 发布作文失败
     */
    private void publicDraftError() {
        showTip("发布失败");
    }

    private void analyzeCheckCompetition(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (400 == jsonObject.optInt("status", -1)) {
                showNeedCoverDialog();
            } else if (700 == jsonObject.optInt("status", -1)) {
                showTip("活动已失效");
            } else {
                submitToCompetition(mSelectedTaskId);
            }
        } catch (JSONException e) {
            submitToCompetition(mSelectedTaskId);
        }
    }

    /**
     * 显示是否覆盖原稿
     */
    private void showNeedCoverDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("是否确定覆盖原文");
                        tv_content.setText("提交“活动”操作将覆盖原文\n在截稿期限前您可再次编辑您的作文");
                        tv_left.setText("确定");
                        tv_right.setText("取消");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitToCompetition(mSelectedTaskId);
                                dialog.dismiss();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setMargin(40)
                .show(getSupportFragmentManager());
    }

    /**
     * 提交到活动
     */
    private void submitToCompetition(String taskId) {
        new SubmitCompetition(mContext).execute(submitCompetitionUrl, taskId,
                String.valueOf(writingNumber));
    }

    /**
     * 分析提交到活动、比赛的数据
     *
     * @param s
     */
    private void analyzeSubmitCompetition(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
                intent.putExtra("id", writingId);
                intent.putExtra("title", writingTitle);
                intent.putExtra("content", writingContent);
                intent.putExtra("cover", writingCover);
                intent.putExtra("count", writingNumber);
                intent.putExtra("format", writingFormat);
                intent.putExtra("area", 5);
                intent.putExtra("index", 4);
                startActivityForResult(intent, 0);
            } else if (700 == jsonObject.optInt("status", -1)) {
                showTip("活动已结束");
                initCompetitionData();
            } else {
                errorSubmitCompetition();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorSubmitCompetition();
        }
    }

    /**
     * 提交到活动、比赛失败
     */
    private void errorSubmitCompetition() {
        showTip("参加活动失败，请稍后再试");
    }

    /**
     * 弹出吐丝
     *
     * @param tips
     */
    private void showTip(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取模板数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Void, String, WritingTemplateChooseActivity> {

        protected GetData(WritingTemplateChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingTemplateChooseActivity activity, String[] strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingTemplateChooseActivity activity, String s) {
            if (s != null) {
                activity.analyzeData(s);
            }
            activity.refreshing = false;
        }
    }

    /**
     * 获取比赛、活动数据
     */
    private static class GetCompetitionData
            extends WeakAsyncTask<String, Void, String, WritingTemplateChooseActivity> {

        protected GetCompetitionData(WritingTemplateChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingTemplateChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("pageNum", activity.mCompetitionPageNum);
                object.put("pageSize", 10);
                object.put("type", 3);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingTemplateChooseActivity activity, String s) {
            if (s == null) {
                activity.noCompetition();
            } else {
                activity.analyzeCompetitionData(s);
            }
        }
    }

    /**
     * 保存到草稿箱
     */
    private static class SaveToDraft
            extends WeakAsyncTask<String, Void, String, WritingTemplateChooseActivity> {

        protected SaveToDraft(WritingTemplateChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingTemplateChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", strings[1]);
                jsonObject.put("article", strings[2]);
                jsonObject.put("studentId", strings[3]);
                jsonObject.put("wordsNum", Integer.valueOf(strings[4]));
                if (activity.writingArea != 2 && activity.writingType != 2) {
                    if (!activity.writingTaskId.equals("") && !activity.writingTaskId.equals("null")) {
                        jsonObject.put("taskId", activity.writingTaskId);
                    }
                }
                if (activity.writingArea == 5 && activity.writingType == 5
                        && !activity.writingId.equals("")) {
                    jsonObject.put("id", activity.writingId);
                }
                if (!strings[5].equals("") && !strings[5].equals("null")) {
                    jsonObject.put("cover", strings[5]);
                }
                jsonObject.put("format", strings[6]);
                RequestBody body = RequestBody.create(DataUtil.JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingTemplateChooseActivity activity, String s) {
            if (s == null) {
                activity.saveFailed();
            } else {
                activity.analyzeSaveToDraftData(s);
            }
        }
    }

    /**
     * 发布作文
     */
    private static class PublicDraft
            extends WeakAsyncTask<String, Void, String, WritingTemplateChooseActivity> {

        protected PublicDraft(WritingTemplateChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingTemplateChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (activity.writingArea == 5 && !strings[1].equals("")
                        && !strings[1].equals("null")) {
                    object.put("id", strings[1]);
                }
                object.put("article", strings[2]);
                object.put("content", strings[3]);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("type", Integer.valueOf(strings[4]));
                if (activity.writingArea == 5 && !strings[6].equals("")
                        && !strings[5].equals("null")) {
                    object.put("taskId", strings[5]);
                }
                object.put("wordsNum", Integer.valueOf(strings[6]));
                object.put("isPublic", Integer.valueOf(strings[7]));
                if (!activity.writingCover.equals("") && !activity.writingCover.equals("null")) {
                    object.put("cover", activity.writingCover);
                }
                object.put("format", activity.writingFormat);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingTemplateChooseActivity activity, String s) {
            if (s == null) {
                activity.publicDraftError();
            } else {
                activity.publicDraftData(s);
            }
        }
    }

    /**
     * 检查比赛、活动是否已投稿
     */
    private static class CheckCompetition
            extends WeakAsyncTask<String, Void, String, WritingTemplateChooseActivity> {

        protected CheckCompetition(WritingTemplateChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingTemplateChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", NewMainActivity.STUDENT_ID);
                object.put("matchId", strings[1]);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingTemplateChooseActivity activity, String s) {
            if (s == null) {
                activity.submitToCompetition(activity.mSelectedTaskId);
            } else {
                activity.analyzeCheckCompetition(s);
            }
        }
    }

    /**
     * 提交到比赛、活动
     */
    private static class SubmitCompetition
            extends WeakAsyncTask<String, Void, String, WritingTemplateChooseActivity> {

        protected SubmitCompetition(WritingTemplateChooseActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingTemplateChooseActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (activity.writingArea == 5 && !activity.writingId.equals("")
                        && !activity.writingId.equals("null")) {
                    object.put("id", activity.writingId);
                }
                object.put("content", activity.writingContent);
                object.put("article", activity.writingTitle);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                if (!activity.writingCover.equals("") && !activity.writingCover.equals("null")) {
                    object.put("cover", activity.writingCover);
                }
                object.put("type", 5);
                object.put("matchId", strings[1]);
                object.put("wordsNum", Integer.valueOf(strings[2]));
                object.put("format", activity.writingFormat);
                RequestBody body = RequestBody.create(DataUtil.JSON, object.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingTemplateChooseActivity activity, String s) {
            if (s == null) {
                activity.errorSubmitCompetition();
            } else {
                activity.analyzeSubmitCompetition(s);
            }
        }
    }

}
