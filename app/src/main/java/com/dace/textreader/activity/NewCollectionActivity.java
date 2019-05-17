package com.dace.textreader.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.ArticleCollectionFragment;
import com.dace.textreader.fragment.SentenceCollectionFragment;
import com.dace.textreader.fragment.WritingCollectionFragment;
import com.dace.textreader.listen.OnCollectionEditorListen;
import com.dace.textreader.listen.OnListDataOperateListen;
import com.dace.textreader.util.StatusBarUtil;

/**
 * 收藏
 */
public class NewCollectionActivity extends BaseActivity implements View.OnClickListener {

    private static final String ARTICLE_FRAGMENT_TAG = "article";
    private static final String WRITING_FRAGMENT_TAG = "writing";
    private static final String SENTENCE_FRAGMENT_TAG = "sentence";

    private RelativeLayout rl_back;
    private TextView tv_editor;
    private RelativeLayout rl_editor;
    private LinearLayout ll_select_all;
    private ImageView iv_select_all;
    private TextView tv_delete;
    private TextView tv_article;
    private View view_article;
    private TextView tv_writing;
    private View view_writing;
    private TextView tv_sentence;
    private View view_sentence;

    private NewCollectionActivity mContext;

    private FragmentManager fm;  //Fragment管理对象
    private Fragment mFragment;
    private ArticleCollectionFragment articleFragment;
    private WritingCollectionFragment writingFragment;
    private SentenceCollectionFragment sentenceFragment;
    private String mTag;

    //文字未选中颜色
    private int color_unselected = Color.parseColor("#999999");
    //文字选中颜色
    private int color_selected = Color.parseColor("#4D72FF");

    private boolean isEditor = false;  //是否是编辑状态
    private boolean isSelectAll = false;  //是否是全选
    private boolean hasSelected = false;

    private OnCollectionEditorListen mListen;
    private int[] status = new int[]{1, 1, 1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_collection);

        mContext = this;

        fm = getSupportFragmentManager();

        initView();
        initEvents();
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        tv_article.setOnClickListener(this);
        tv_writing.setOnClickListener(this);
        tv_sentence.setOnClickListener(this);

        tv_editor.setOnClickListener(this);
        ll_select_all.setOnClickListener(this);
        tv_delete.setOnClickListener(this);

        articleFragment.setHasArticleItemSelectedListen(new ArticleCollectionFragment.HasArticleItemSelected() {
            @Override
            public void hasItemSelected(boolean hasSelected) {
                updateDeleteButtonBg(hasSelected);
            }
        });
        articleFragment.setOnListDataOperateListen(new OnListDataOperateListen() {
            @Override
            public void onRefresh(boolean refresh) {

            }

            @Override
            public void onLoadResult(boolean success) {
                if (success) {
                    tv_editor.setVisibility(View.VISIBLE);
                    status[0] = 1;
                } else {
                    tv_editor.setVisibility(View.GONE);
                    status[0] = 0;
                }
            }

            @Override
            public void onEditor(boolean editor) {

            }
        });
    }

    /**
     * 更新删除按钮的背景
     */
    private void updateDeleteButtonBg(boolean hasSelected) {
        this.hasSelected = hasSelected;
        if (hasSelected) {
            tv_delete.setBackgroundResource(R.drawable.shape_text_orange);
        } else {
            tv_delete.setBackgroundResource(R.drawable.shape_text_gray);
            iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
            isSelectAll = false;
        }
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_new_collection);
        tv_editor = findViewById(R.id.tv_editor_new_collection);
        rl_editor = findViewById(R.id.rl_editor_new_collection);
        ll_select_all = findViewById(R.id.ll_select_all_new_collection_bottom);
        iv_select_all = findViewById(R.id.iv_select_all_new_collection_bottom);
        tv_delete = findViewById(R.id.tv_delete_new_collection_bottom);
        tv_article = findViewById(R.id.tv_article_collection_top);
        view_article = findViewById(R.id.view_article_collection_top);
        tv_writing = findViewById(R.id.tv_writing_collection_top);
        view_writing = findViewById(R.id.view_writing_collection_top);
        tv_sentence = findViewById(R.id.tv_sentence_collection_top);
        view_sentence = findViewById(R.id.view_sentence_collection_top);

        //Fragment的初始化
        articleFragment = new ArticleCollectionFragment();
        fm.beginTransaction().add(R.id.frame_collection, articleFragment, ARTICLE_FRAGMENT_TAG).commit();
        mFragment = articleFragment;
        mTag = ARTICLE_FRAGMENT_TAG;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_new_collection:
                finish();
                break;
            case R.id.tv_editor_new_collection:
                if (mListen != null) {
                    if (isEditor) {
                        mListen.OnEditorCancel(mTag);
                    } else {
                        mListen.OnEditorOpen(mTag);
                    }
                }
                if (isEditor) {
                    tv_editor.setText("编辑");
                    isEditor = false;
                } else {
                    tv_editor.setText("取消");
                    isEditor = true;
                }
                showEditorView(isEditor);
                break;
            case R.id.ll_select_all_new_collection_bottom:
                if (isSelectAll) {
                    isSelectAll = false;
                    iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
                    mListen.OnSelectAll(mTag, isSelectAll);
                } else {
                    isSelectAll = true;
                    iv_select_all.setImageResource(R.drawable.icon_edit_selected);
                    mListen.OnSelectAll(mTag, isSelectAll);
                }
                break;
            case R.id.tv_delete_new_collection_bottom:
                if (hasSelected) {
                    mListen.OnDeleteData(mTag);
                    tv_editor.setText("编辑");
                    isEditor = false;
                    isSelectAll = false;
                    showEditorView(false);
                }
                break;
            case R.id.tv_article_collection_top:
                if (!isEditor) {
                    showFragment(articleFragment, ARTICLE_FRAGMENT_TAG);
                }
                break;
            case R.id.tv_writing_collection_top:
                if (!isEditor) {
                    if (writingFragment == null) {
                        writingFragment = new WritingCollectionFragment();
                        writingFragment.setHasWritingItemSelectedListen(new WritingCollectionFragment.HasWritingItemSelected() {
                            @Override
                            public void hasItemSelected(boolean hasSelected) {
                                updateDeleteButtonBg(hasSelected);
                            }
                        });
                        writingFragment.setOnListDataOperateListen(new OnListDataOperateListen() {
                            @Override
                            public void onRefresh(boolean refresh) {

                            }

                            @Override
                            public void onLoadResult(boolean success) {
                                if (success) {
                                    status[1] = 1;
                                    tv_editor.setVisibility(View.VISIBLE);
                                } else {
                                    status[1] = 0;
                                    tv_editor.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onEditor(boolean editor) {

                            }
                        });
                    }
                    showFragment(writingFragment, WRITING_FRAGMENT_TAG);
                }
                break;
            case R.id.tv_sentence_collection_top:
                if (!isEditor) {
                    if (sentenceFragment == null) {
                        sentenceFragment = new SentenceCollectionFragment();
                        sentenceFragment.setHasSentenceItemSelectedListen(new SentenceCollectionFragment.HasSentenceItemSelected() {
                            @Override
                            public void hasSelected(boolean hasSelected) {
                                updateDeleteButtonBg(hasSelected);
                            }
                        });
                        sentenceFragment.setOnListDataOperateListen(new OnListDataOperateListen() {
                            @Override
                            public void onRefresh(boolean refresh) {

                            }

                            @Override
                            public void onLoadResult(boolean success) {
                                if (success) {
                                    status[2] = 1;
                                    tv_editor.setVisibility(View.VISIBLE);
                                } else {
                                    status[2] = 0;
                                    tv_editor.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onEditor(boolean editor) {

                            }
                        });
                    }
                    showFragment(sentenceFragment, SENTENCE_FRAGMENT_TAG);
                }
                break;
        }
    }

    /**
     * 显示或隐藏编辑视图
     *
     * @param isEditor
     */
    private void showEditorView(boolean isEditor) {
        isSelectAll = false;
        iv_select_all.setImageResource(R.drawable.icon_edit_unselected);
        tv_delete.setBackgroundResource(R.drawable.shape_text_gray);
        if (isEditor) {
            rl_editor.setVisibility(View.VISIBLE);
        } else {
            rl_editor.setVisibility(View.GONE);
        }
    }

    /**
     * 显示视图
     *
     * @param fragment
     * @param tag
     */
    private void showFragment(Fragment fragment, String tag) {
        if (mFragment != fragment) {
            //开启Fragment事物
            FragmentTransaction transaction = fm.beginTransaction();
            if (!fragment.isAdded()) {
                transaction.hide(mFragment).add(R.id.frame_collection, fragment, tag);
            } else {
                transaction.hide(mFragment).show(fragment);
            }
            transaction.commit();
            mFragment = fragment;
            mTag = tag;
            if (fragment == articleFragment) {
                tv_article.setTextColor(color_selected);
                view_article.setVisibility(View.VISIBLE);
                tv_writing.setTextColor(color_unselected);
                view_writing.setVisibility(View.GONE);
                tv_sentence.setTextColor(color_unselected);
                view_sentence.setVisibility(View.GONE);
                if (status[0] == 1) {
                    tv_editor.setVisibility(View.VISIBLE);
                } else if (status[0] == 0) {
                    tv_editor.setVisibility(View.GONE);
                }
            } else if (fragment == writingFragment) {
                tv_article.setTextColor(color_unselected);
                view_article.setVisibility(View.GONE);
                tv_writing.setTextColor(color_selected);
                view_writing.setVisibility(View.VISIBLE);
                tv_sentence.setTextColor(color_unselected);
                view_sentence.setVisibility(View.GONE);
                if (status[1] == 1) {
                    tv_editor.setVisibility(View.VISIBLE);
                } else if (status[1] == 0) {
                    tv_editor.setVisibility(View.GONE);
                }
            } else {
                tv_article.setTextColor(color_unselected);
                view_article.setVisibility(View.GONE);
                tv_writing.setTextColor(color_unselected);
                view_writing.setVisibility(View.GONE);
                tv_sentence.setTextColor(color_selected);
                view_sentence.setVisibility(View.VISIBLE);
                if (status[2] == 1) {
                    tv_editor.setVisibility(View.VISIBLE);
                } else if (status[2] == 0) {
                    tv_editor.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 设置编辑监听
     *
     * @param listen
     */
    public void setOnEditorClickListen(OnCollectionEditorListen listen) {
        this.mListen = listen;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
