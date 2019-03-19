package com.dace.textreader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.fragment.ArticleNotationFragment;
import com.dace.textreader.fragment.ArticleTextFragment;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.TipsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文章详情
 */
public class NewArticleDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG_TEXT = "text";
    private static final String TAG_NOTATION = "notation";
    private static final String TAG_INTENSIVE = "intensive";
    private static final String TAG_COMMENT = "comment";

    //结束时间
    private final String endReadUrl = HttpUrlPre.HTTP_URL + "/statistics/end/update?";

    //底部布局
    private LinearLayout ll_bottom;
    private LinearLayout ll_text;
    private ImageView iv_text;
    private TextView tv_text;
    private LinearLayout ll_notation;
    private ImageView iv_notation;
    private TextView tv_notation;
    private LinearLayout ll_intensive;
    private ImageView iv_intensive;
    private TextView tv_intensive;
    private RelativeLayout ll_comment;
    private ImageView iv_comment;
    private TextView tv_comment;
    private TextView tv_comment_count;

    private FragmentManager fm;  //Fragment管理对象
    private Fragment mFragment;
    private ArticleTextFragment textFragment;
    private ArticleNotationFragment notationFragment;
    private int colorTextSel = Color.parseColor("#F29D4B");
    private int colorTextNor = Color.parseColor("#999999");

    public static long essayId = -1;
    public static int essayType = -1;
    public static String essayTitle = "";
    public static long readID = -1;  //阅读ID

    /**
     * 极光推送相关
     **/
    //消息Id
    private static final String KEY_MSGID = "msg_id";
    //该通知的下发通道
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    //通知附加字段
    private static final String KEY_EXTRAS = "n_extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_article_detail);

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.StatusBarLightMode(this, flag);

        fm = getSupportFragmentManager();

        initIntentData();
        initBottomLayout();
        initFragment();
        initView();
        initEvent();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_VIEW)) {
            String data = intent.getData().toString();
            if (!TextUtils.isEmpty(data)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String msgId = jsonObject.optString(KEY_MSGID);
                    byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
                    String extras = jsonObject.optString(KEY_EXTRAS);

                    JSONObject extrasJson = new JSONObject(extras);
                    String myValue = extrasJson.getString("params");

                    JSONObject object = new JSONObject(myValue);
                    essayId = object.optLong("productId", -1L);
                    essayType = object.optInt("areaType", -1);

                    //上报点击事件
                    JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        } else {
            essayId = getIntent().getLongExtra("id", -1L);
            essayType = getIntent().getIntExtra("type", -1);
        }
    }

    private void initView() {
        showFragment(textFragment, TAG_TEXT);
    }

    private void initFragment() {
        textFragment = new ArticleTextFragment();
        textFragment.setOnArticleNextClick(new ArticleTextFragment.OnArticleNextClick() {
            @Override
            public void onNext(long nextPage) {
                essayId = nextPage;
                tv_comment_count.setVisibility(View.INVISIBLE);
            }
        });
        textFragment.setOnArticleTextBackClick(new ArticleTextFragment.OnArticleTextBackClick() {
            @Override
            public void onClick() {
                closeActivity();
            }
        });
        textFragment.setOnArticleMediaPlay(new ArticleTextFragment.OnArticleMediaPlay() {
            @Override
            public void onPlay() {
                if (getPlayService() != null && getPlayService().isPlaying()) {
                    getPlayService().pause();
                }
            }
        });
        textFragment.setOnTipsDismiss(new ArticleTextFragment.OnTipsDismiss() {
            @Override
            public void onDismiss() {
                TipsUtil tipsUtil = new TipsUtil(NewArticleDetailActivity.this);
                tipsUtil.showTipAboveView(ll_intensive, "精读\n为你智能标注");
            }
        });
        textFragment.setOnReceivedCommentData(new ArticleTextFragment.OnReceivedCommentData() {
            @Override
            public void onReceived(int count) {
                if (count != -1 && count != 0) {
                    tv_comment_count.setVisibility(View.VISIBLE);
                    if (count > 99) {
                        tv_comment_count.setText("99+");
                    } else {
                        tv_comment_count.setText(String.valueOf(count));
                    }
                } else {
                    tv_comment_count.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (essayType == 2 || essayType == 4) {
            notationFragment = new ArticleNotationFragment();
            notationFragment.setOnArticleNotationBackClick(new ArticleNotationFragment.OnArticleNotationBackClick() {
                @Override
                public void onClick() {
                    closeActivity();
                }
            });
        }
    }

    private void initEvent() {
        ll_text.setOnClickListener(this);
        ll_notation.setOnClickListener(this);
        ll_intensive.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
    }

    /**
     * 隐藏操作栏
     */
    public void hideOperateBar() {
        ll_bottom.setVisibility(View.GONE);
    }

    /**
     * 隐藏操作栏
     */
    public void showOperateBar() {
        ll_bottom.setVisibility(View.VISIBLE);
    }

    private void initBottomLayout() {
        ll_bottom = findViewById(R.id.ll_bottom_article_fragment);
        ll_text = findViewById(R.id.ll_text_new_article_detail_bottom);
        iv_text = findViewById(R.id.iv_text_new_article_detail_bottom);
        tv_text = findViewById(R.id.tv_text_new_article_detail_bottom);
        ll_notation = findViewById(R.id.ll_notation_new_article_detail_bottom);
        iv_notation = findViewById(R.id.iv_notation_new_article_detail_bottom);
        tv_notation = findViewById(R.id.tv_notation_new_article_detail_bottom);
        ll_intensive = findViewById(R.id.ll_intensive_new_article_detail_bottom);
        iv_intensive = findViewById(R.id.iv_intensive_new_article_detail_bottom);
        tv_intensive = findViewById(R.id.tv_intensive_new_article_detail_bottom);
        ll_comment = findViewById(R.id.ll_comment_new_article_detail_bottom);
        iv_comment = findViewById(R.id.iv_comment_new_article_detail_bottom);
        tv_comment = findViewById(R.id.tv_comment_new_article_detail_bottom);
        tv_comment_count = findViewById(R.id.tv_comment_count_new_article_detail_bottom);

        if (essayType != 2 && essayType != 4) {
            ll_notation.setVisibility(View.GONE);
        }
    }

    private void initBottomView() {
        iv_text.setImageResource(R.drawable.icon_tab_read_text_nor);
        tv_text.setTextColor(colorTextNor);
        iv_notation.setImageResource(R.drawable.icon_tab_read_notation_nor);
        tv_notation.setTextColor(colorTextNor);
        iv_intensive.setImageResource(R.drawable.icon_tab_read_intensive_nor);
        tv_intensive.setTextColor(colorTextNor);
        iv_comment.setImageResource(R.drawable.icon_tab_read_comment_nor);
        tv_comment.setTextColor(colorTextNor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_text_new_article_detail_bottom:
                showFragment(textFragment, TAG_TEXT);
                break;
            case R.id.ll_notation_new_article_detail_bottom:
                showFragment(notationFragment, TAG_NOTATION);
                break;
            case R.id.ll_intensive_new_article_detail_bottom:
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    turnToIntensive();
                }
                break;
            case R.id.ll_comment_new_article_detail_bottom:
                if (!essayTitle.equals("")) {
                    turnToCommentList();
                }
                break;
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 跳转到精读页面
     */
    private void turnToIntensive() {
        Intent intent = new Intent(this, IntensiveReadingActivity.class);
        intent.putExtra("essayId", essayId);
        intent.putExtra("essayType", essayType);
        startActivity(intent);
    }

    /**
     * 跳转到评论列表
     */
    private void turnToCommentList() {
        Intent intent = new Intent(this, NewCommentListActivity.class);
        intent.putExtra("essayId", essayId);
        intent.putExtra("essayType", essayType);
        intent.putExtra("essayTitle", essayTitle);
        startActivity(intent);
    }

    /**
     * 显示Fragment
     *
     * @param tag
     */
    private void showFragment(Fragment fragment, String tag) {
        if (mFragment == null) {
            fm.beginTransaction().add(R.id.frame_article_fragment, fragment, tag).commit();
            mFragment = fragment;

            bottomTabChange(tag);
        } else if (fragment != mFragment) {
            //开启Fragment事务
            FragmentTransaction transaction = fm.beginTransaction();
            if (!fragment.isAdded()) {
                transaction.hide(mFragment).add(R.id.frame_article_fragment, fragment, tag);
            } else {
                transaction.hide(mFragment).show(fragment);
            }
            transaction.commit();
            mFragment = fragment;

            bottomTabChange(tag);
        }
    }

    /**
     * 底部TAB改变
     *
     * @param tag
     */
    private void bottomTabChange(String tag) {
        initBottomView();
        switch (tag) {
            case TAG_TEXT:
                iv_text.setImageResource(R.drawable.icon_tab_read_text_sel);
                tv_text.setTextColor(colorTextSel);
                break;
            case TAG_NOTATION:
                iv_notation.setImageResource(R.drawable.icon_tab_read_notation_sel);
                tv_notation.setTextColor(colorTextSel);
                break;
            case TAG_INTENSIVE:
                iv_intensive.setImageResource(R.drawable.icon_tab_read_intensive_sel);
                tv_intensive.setTextColor(colorTextSel);
                break;
            case TAG_COMMENT:
                iv_comment.setImageResource(R.drawable.icon_tab_read_comment_sel);
                tv_comment.setTextColor(colorTextSel);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * 关闭页面
     */
    private void closeActivity() {
        overRead(readID);
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

    /**
     * 用户阅读结束
     *
     * @param id 开始阅读是返回的阅读ID
     */
    private void overRead(long id) {
        if (NewMainActivity.STUDENT_ID != -1 && id != -1) {
            new OverRead().execute(endReadUrl + "id=" + id);
        }
    }

    /**
     * 结束阅读统计
     */
    private class OverRead extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
