package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.adapter.CompositionCommentAdapter;
import com.dace.textreader.adapter.UserImageAdapter;
import com.dace.textreader.bean.UserBean;
import com.dace.textreader.bean.WritingCommentBean;
import com.dace.textreader.listen.OnUserInfoClickListen;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.Utils;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.ScoreView;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作文详情
 * h5+原生实现，内容部分为h5，剩下的为原生实现
 */
public class CompositionDetailActivity extends BaseActivity {

    private static final String url = HttpUrlPre.HTTP_URL + "/app/writing/select/new";

    private static final String collectUrl = HttpUrlPre.HTTP_URL + "/writing/collect";
    private static final String unCollectUrl = HttpUrlPre.HTTP_URL + "/writing/collection/delete";
    private static final String likeUrl = HttpUrlPre.HTTP_URL + "/writing/like";
    private static final String unLikeUrl = HttpUrlPre.HTTP_URL + "/writing/like/delete";

    private static final String commentListUrl = HttpUrlPre.HTTP_URL + "/composition/commentList?";
    private static final String commentUrl = HttpUrlPre.HTTP_URL + "/composition/comment";
    private static final String deleteUrl = HttpUrlPre.HTTP_URL + "/composition/comment/delete";

    private static final String userFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/setup";
    private static final String userUnFollowUrl = HttpUrlPre.HTTP_URL + "/followRelation/cancel";

    private static final String scoreUrl = HttpUrlPre.HTTP_URL + "/score/composition";

    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/get/share/writing/";

    private FrameLayout frameLayout;
    private RelativeLayout rl_back;
    private RelativeLayout rl_setting;
    private RelativeLayout rl_collection;
    private ImageView iv_collection;
    private RelativeLayout rl_more;

    private NestedScrollView scrollView;

    private WebView webView;

    private RelativeLayout rl_user;
    private ImageView iv_user;
    private TextView tv_user_name;
    private TextView tv_user_grade;
    private RelativeLayout rl_follow;
    private ImageView iv_follow;
    private TextView tv_follow;
    private TextView tv_user_description;

    private LinearLayout ll_score;
    private TextView tv_user_score;
    private LinearLayout ll_user_score;
    private RecyclerView recyclerView_user_score;
    private TextView tv_user_number;
    private TextView tv_score;

    private RelativeLayout rl_teacher;
    private TextView tv_teacher;
    private TextView tv_teacher_score;
    private TextView tv_teacher_suggest;
    private TextView tv_teacher_excellent;
    private TextView tv_teacher_insufficient;
    private TextView tv_teacher_comment;

    private LinearLayout ll_model;
    private TextView tv_model;

    private LinearLayout ll_events;
    private ImageView iv_events;
    private TextView tv_events;

    private LinearLayout ll_comment;
    private TextView tv_comment_number;
    private RecyclerView recyclerView_comment;
    private TextView tv_no_comment;

    private RelativeLayout rl_bottom;
    private TextView tv_bottom_comment;
    private RelativeLayout rl_bottom_comment;
    private TextView tv_bottom_comment_number;
    private RelativeLayout rl_bottom_points;
    private ImageView iv_bottom_points;
    private TextView tv_bottom_points;
    private RelativeLayout rl_bottom_share;

    private CompositionDetailActivity mContext;

    private String compositionId;
    private int compositionArea;
    private int compositionType;
    private int compositionFormat;
    private String compositionTitle;
    private String shareContent;
    private String orderNum = "";

    private boolean isLiked;
    private boolean isCollected;

    private long userId;
    private String userImage;
    private String userName;
    private String userGrade;
    private String userDescription;
    private int isFollowed;  //是否关注

    private int userScore;
    private int userNumber;
    private List<UserBean> mList_user = new ArrayList<>();
    private UserImageAdapter adapter_user;
    private int isScore;  //是否打分

    private String teacherScore = "";
    private String teacherComment = "";
    private String teacherSuggest;
    private String teacherExcellent;
    private String teacherInsufficient;

    private String compositionModel;

    private String eventsId;
    private int eventsStatus;
    private String eventsImage;
    private String eventsContent;

    private int likeNum;

    private CompositionCommentAdapter adapter;
    private List<WritingCommentBean> mList = new ArrayList<>();
    private int mMaxSize = 0;
    private LinearLayoutManager layoutManager;
    private String curTime;
    private int pageNum = 1;
    private boolean isLoading = false;
    private boolean isEnd = false;
    private int deletePosition = -1;  //要删除的评论的ID

    //是否正在执行相关操作
    private boolean isFollowing = false;
    private boolean isCollecting = false;
    private boolean isClickLike = false;

    private int type_share = -1;  //分享类型
    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_WeiBo = 6;  //微博

    /**
     * 极光推送相关
     **/
    //消息Id
    private static final String KEY_MSGID = "msg_id";
    //该通知的下发通道
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    //通知附加字段
    private static final String KEY_EXTRAS = "n_extras";

    private SharedPreferences sharedPreferences;
    private String[] textShowSize = new String[]{"15", "16", "18", "20", "22"};
    private int textSizePosition = 1;
    private String[] textLineSpace = new String[]{"2.4", "2.2", "2"};  //行间距
    private int textLineSpacePosition = 1;
    private int screenLight = 100;  //屏幕亮度，0~255
    private String[] background = new String[]{"FFFBE9", "FFFFFF", "EDEDF8", "DCEBCE", "EEDFC6"};  //背景色
    private int backgroundPosition = 1;

    private Bitmap bitmap;
    private String imageUrl = "";
    private String compositionUrl = "";
    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.webkit.WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_composition_detail);

        mContext = this;

        initIntentData();
        initSharedPreferencesData();
        initView();
        initEvents();
        showLoadingView(true);
        initData();

        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));

    }

    private void initSharedPreferencesData() {
        sharedPreferences = getSharedPreferences("composition_setting", Context.MODE_PRIVATE);
        textSizePosition = sharedPreferences.getInt("textSize", 1);
        textLineSpacePosition = sharedPreferences.getInt("textLineSpace", 1);
        screenLight = sharedPreferences.getInt("screenLight", -1);
        backgroundPosition = sharedPreferences.getInt("background", 1);

        if (screenLight == -1) {
            screenLight = Utils.getSystemBrightness(mContext);
        } else {
            Utils.saveScreenBrightness(mContext, screenLight);
        }
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
                    compositionId = object.getString("productId");
                    compositionArea = object.optInt("area", 0);
                    orderNum = "";

                    //上报点击事件
                    JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        compositionId = getIntent().getStringExtra("writingId");
        compositionArea = getIntent().getIntExtra("area", 0);
        if (compositionArea == 1) {
            orderNum = getIntent().getStringExtra("orderNum");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        curTime = DateUtil.getTodayDateTime();
    }

    private void updateOperateView() {
        if (NewMainActivity.STUDENT_ID != -1 && NewMainActivity.STUDENT_ID == userId) {
            rl_more.setVisibility(View.VISIBLE);
        } else {
            rl_more.setVisibility(View.GONE);
        }
    }

    private void initEvents() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });
        rl_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compositionArea != 0) {
                    showShareDialog();
                } else {
                    if (NewMainActivity.STUDENT_ID == -1) {
                        turnToLogin();
                    } else {
                        if (isCollecting) {
                            showTips("另一个操作正在执行，请稍后...");
                        } else {
                            isCollecting = true;
                            if (isCollected) {
                                unCollect();
                            } else {
                                collect();
                            }
                        }
                    }
                }
            }
        });
        rl_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOperateDialog();
            }
        });
        rl_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToUserHomepage();
            }
        });
        rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    if (isFollowing) {
                        showTips("另一个操作正在执行，请稍后...");
                    } else {
                        isFollowing = true;
                        if (isFollowed == 1) {
                            unFollowUser();
                        } else {
                            followUser();
                        }
                    }
                }
            }
        });
        tv_teacher_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTeacherUi(0);
            }
        });
        tv_teacher_excellent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTeacherUi(1);
            }
        });
        tv_teacher_insufficient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTeacherUi(2);
            }
        });
        tv_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    showScoreDialog();
                }
            }
        });
        ll_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventsStatus == 0) {
                    turnToEventsDetail();
                } else {
                    turnToEventsH5();
                }
            }
        });
        recyclerView_comment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!isLoading && !isEnd) {
                    if (mList.size() != 0) {
                        getMoreCommentData(newState);
                    }
                }
            }
        });
        adapter.setOnUserInfoClickListen(new OnUserInfoClickListen() {
            @Override
            public void onClick(long userId) {
                if (userId != -1) {
                    Intent intent = new Intent(mContext, UserHomepageActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
        tv_bottom_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyDialog(-1, -1);
            }
        });
        rl_bottom_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToComment();
            }
        });
        rl_bottom_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    if (isClickLike) {
                        showTips("另一个操作正在执行，请稍后...");
                    } else {
                        isClickLike = true;
                        if (isLiked) {
                            unLike();
                        } else {
                            like();
                        }
                    }
                }
            }
        });
        rl_bottom_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });
        adapter.setOnItemClickListen(new CompositionCommentAdapter.OnItemClickListen() {
            @Override
            public void onItemClick(View view) {
                int pos = recyclerView_comment.getChildAdapterPosition(view);
                showCommentOptions(pos);
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 更新老师评语
     *
     * @param i
     */
    private void updateTeacherUi(int i) {
        if (i == 0) {
            teacherComment = teacherSuggest;
            tv_teacher_suggest.setSelected(true);
            tv_teacher_excellent.setSelected(false);
            tv_teacher_insufficient.setSelected(false);
        } else if (i == 1) {
            teacherComment = teacherExcellent;
            tv_teacher_suggest.setSelected(false);
            tv_teacher_excellent.setSelected(true);
            tv_teacher_insufficient.setSelected(false);
        } else if (i == 2) {
            teacherComment = teacherInsufficient;
            tv_teacher_suggest.setSelected(false);
            tv_teacher_excellent.setSelected(false);
            tv_teacher_insufficient.setSelected(true);
        }
        tv_teacher_comment.setText(teacherComment);
    }

    /**
     * 前往用户主页
     */
    private void turnToUserHomepage() {
        if (userId != -1) {
            Intent intent = new Intent(mContext, UserHomepageActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        }
    }

    /**
     * 显示设置对话框
     */
    private void showSettingsDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_composition_setting_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        final RelativeLayout rl_size_min = holder.getView(R.id.rl_font_minify_composition_setting_dialog);
                        final ImageView iv_size_min = holder.getView(R.id.iv_font_minify_composition_setting_dialog);
                        final TextView tv_size = holder.getView(R.id.tv_font_size_composition_setting_dialog);
                        final RelativeLayout rl_size_max = holder.getView(R.id.rl_font_enlarge_composition_setting_dialog);
                        final ImageView iv_size_max = holder.getView(R.id.iv_font_enlarge_composition_setting_dialog);
                        final RelativeLayout rl_row_space_max = holder.getView(R.id.rl_row_spacing_max_composition_setting_dialog);
                        final RelativeLayout rl_row_space_normal = holder.getView(R.id.rl_row_spacing_normal_composition_setting_dialog);
                        final RelativeLayout rl_row_space_min = holder.getView(R.id.rl_row_spacing_min_composition_setting_dialog);
                        SeekBar seekBar = holder.getView(R.id.seek_bar_light_composition_setting_dialog);
                        final RelativeLayout rl_bg_one = holder.getView(R.id.rl_bg_one_composition_setting_dialog);
                        final RelativeLayout rl_bg_two = holder.getView(R.id.rl_bg_two_composition_setting_dialog);
                        final RelativeLayout rl_bg_three = holder.getView(R.id.rl_bg_three_composition_setting_dialog);
                        final RelativeLayout rl_bg_four = holder.getView(R.id.rl_bg_four_composition_setting_dialog);
                        final RelativeLayout rl_bg_five = holder.getView(R.id.rl_bg_five_composition_setting_dialog);

                        if (textSizePosition == 0) {
                            iv_size_min.setImageAlpha(80);
                        } else {
                            iv_size_min.setImageAlpha(255);
                        }
                        if (textSizePosition == 4) {
                            iv_size_max.setImageAlpha(80);
                        } else {
                            iv_size_max.setImageAlpha(255);
                        }
                        tv_size.setText(textShowSize[textSizePosition]);
                        rl_row_space_max.setSelected(false);
                        rl_row_space_normal.setSelected(false);
                        rl_row_space_min.setSelected(false);
                        if (textLineSpacePosition == 0) {
                            rl_row_space_max.setSelected(true);
                        } else if (textLineSpacePosition == 1) {
                            rl_row_space_normal.setSelected(true);
                        } else {
                            rl_row_space_min.setSelected(true);
                        }
                        seekBar.setProgress(screenLight);
                        rl_bg_one.setSelected(false);
                        rl_bg_two.setSelected(false);
                        rl_bg_three.setSelected(false);
                        rl_bg_four.setSelected(false);
                        rl_bg_five.setSelected(false);
                        if (backgroundPosition == 0) {
                            rl_bg_one.setSelected(true);
                        } else if (backgroundPosition == 1) {
                            rl_bg_two.setSelected(true);
                        } else if (backgroundPosition == 2) {
                            rl_bg_three.setSelected(true);
                        } else if (backgroundPosition == 3) {
                            rl_bg_four.setSelected(true);
                        } else {
                            rl_bg_five.setSelected(true);
                        }

                        rl_size_min.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textSizePosition != 0) {
                                    textSizePosition = textSizePosition - 1;
                                    tv_size.setText(textShowSize[textSizePosition]);
                                    changeWebStyle();
                                }
                                if (textSizePosition == 0) {
                                    iv_size_min.setImageAlpha(80);
                                } else {
                                    iv_size_min.setImageAlpha(255);
                                }
                                if (textSizePosition == 4) {
                                    iv_size_max.setImageAlpha(80);
                                } else {
                                    iv_size_max.setImageAlpha(255);
                                }
                            }
                        });
                        rl_size_max.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (textSizePosition != 4) {
                                    textSizePosition = textSizePosition + 1;
                                    tv_size.setText(textShowSize[textSizePosition]);
                                    changeWebStyle();
                                }
                                if (textSizePosition == 0) {
                                    iv_size_min.setImageAlpha(80);
                                } else {
                                    iv_size_min.setImageAlpha(255);
                                }
                                if (textSizePosition == 4) {
                                    iv_size_max.setImageAlpha(80);
                                } else {
                                    iv_size_max.setImageAlpha(255);
                                }
                            }
                        });
                        rl_row_space_max.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_row_space_max.isSelected()) {
                                    textLineSpacePosition = 0;
                                    rl_row_space_max.setSelected(true);
                                    rl_row_space_normal.setSelected(false);
                                    rl_row_space_min.setSelected(false);
                                    changeWebStyle();
                                }
                            }
                        });
                        rl_row_space_normal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_row_space_normal.isSelected()) {
                                    textLineSpacePosition = 1;
                                    rl_row_space_normal.setSelected(true);
                                    rl_row_space_max.setSelected(false);
                                    rl_row_space_min.setSelected(false);
                                    changeWebStyle();
                                }
                            }
                        });
                        rl_row_space_min.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_row_space_min.isSelected()) {
                                    textLineSpacePosition = 2;
                                    rl_row_space_max.setSelected(false);
                                    rl_row_space_normal.setSelected(false);
                                    rl_row_space_min.setSelected(true);
                                    changeWebStyle();
                                }
                            }
                        });
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                Utils.saveScreenBrightness(mContext, progress);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                screenLight = seekBar.getProgress();
                            }
                        });
                        rl_bg_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_one.isSelected()) {
                                    backgroundPosition = 0;
                                    rl_bg_one.setSelected(true);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(false);
                                    changeWebStyle();
                                }
                            }
                        });
                        rl_bg_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_two.isSelected()) {
                                    backgroundPosition = 1;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(true);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(false);
                                    changeWebStyle();
                                }
                            }
                        });
                        rl_bg_three.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_three.isSelected()) {
                                    backgroundPosition = 2;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(true);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(false);
                                    changeWebStyle();
                                }
                            }
                        });
                        rl_bg_four.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_four.isSelected()) {
                                    backgroundPosition = 3;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(true);
                                    rl_bg_five.setSelected(false);
                                    changeWebStyle();
                                }
                            }
                        });
                        rl_bg_five.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!rl_bg_five.isSelected()) {
                                    backgroundPosition = 4;
                                    rl_bg_one.setSelected(false);
                                    rl_bg_two.setSelected(false);
                                    rl_bg_three.setSelected(false);
                                    rl_bg_four.setSelected(false);
                                    rl_bg_five.setSelected(true);
                                    changeWebStyle();
                                }
                            }
                        });
                    }
                })
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 改变内容样式
     */
    private void changeWebStyle() {
        try {
            JSONObject object = new JSONObject();
            object.put("fontSize", textShowSize[textSizePosition]);
            object.put("lineHeight", textLineSpace[textLineSpacePosition]);
            object.put("bgColor", background[backgroundPosition]);
            if (webView != null) {
                webView.loadUrl("javascript:changeTxtDetail(" + object.toString() + ")");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            initCompositionData();
        }
    }

    /**
     * 显示操作对话框
     */
    private void showOperateDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_excellent_composition_operate_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_order = holder.getView(R.id.tv_order_excellent_operate_dialog);
                        TextView tv_editor = holder.getView(R.id.tv_editor_excellent_operate_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_excellent_operate_dialog);
                        if (orderNum.equals("")) {
                            tv_order.setVisibility(View.GONE);
                        }
                        tv_order.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                turnToOrderDetail();
                                dialog.dismiss();
                            }
                        });
                        tv_editor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                turnToWriting();
                                dialog.dismiss();
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
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
     * 前往订单详情
     */
    private void turnToOrderDetail() {
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
        intent.putExtra("orderNum", orderNum);
        startActivity(intent);
    }

    /**
     * 前往写作界面重新编辑
     */
    private void turnToWriting() {
        Intent intent = new Intent(mContext, WritingActivity.class);
        intent.putExtra("id", compositionId);
        intent.putExtra("area", compositionArea);
        intent.putExtra("type", 5);
        intent.putExtra("taskId", "");
        startActivity(intent);
    }

    /**
     * 显示打分对话框
     */
    private void showScoreDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_user_score_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_user_score_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_user_score_dialog);
                        final ScoreView scoreView = holder.getView(R.id.score_user_score_dialog);
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int value = scoreView.getCurValue();
                                userScoreComposition(value);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 打分
     *
     * @param value
     */
    private void userScoreComposition(int value) {
        new ScoreComposition(mContext).execute(scoreUrl, String.valueOf(NewMainActivity.STUDENT_ID),
                compositionId, String.valueOf(value));
    }

    /**
     * 滑动到评论列表
     */
    private void scrollToComment() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, ll_comment.getTop());
            }
        });
    }

    /**
     * 点赞
     */
    private void like() {
        new LikeOrNot(mContext).execute(likeUrl, String.valueOf(NewMainActivity.STUDENT_ID),
                compositionId, String.valueOf(compositionArea));
    }

    /**
     * 取消点赞
     */
    private void unLike() {
        new LikeOrNot(mContext).execute(unLikeUrl, String.valueOf(NewMainActivity.STUDENT_ID),
                compositionId, String.valueOf(compositionArea));
    }

    /**
     * 收藏
     */
    private void collect() {
        new CollectComposition(mContext).execute(collectUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), compositionId, compositionTitle);
    }

    /**
     * 取消收藏
     */
    private void unCollect() {
        JSONArray array = new JSONArray();
        array.put(compositionId);
        new UnCollectComposition(mContext).execute(unCollectUrl,
                String.valueOf(NewMainActivity.STUDENT_ID), array.toString());
    }

    /**
     * 关注用户
     */
    private void followUser() {
        new FollowUser(mContext).execute(userFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 取消关注用户
     */
    private void unFollowUser() {
        new FollowUser(mContext).execute(userUnFollowUrl,
                String.valueOf(userId), String.valueOf(NewMainActivity.STUDENT_ID));
    }

    /**
     * 前往查看活动详情
     */
    private void turnToEventsDetail() {
        Intent intent = new Intent(mContext, WritingEventDetailsActivity.class);
        intent.putExtra("taskId", eventsId);
        startActivity(intent);
    }

    /**
     * 前往活动H5
     */
    private void turnToEventsH5() {
        Intent intent = new Intent(mContext, EventsActivity.class);
        intent.putExtra("pageName", eventsId);
        startActivity(intent);
    }

    private void initData() {
        new GetData(mContext).execute(url, String.valueOf(NewMainActivity.STUDENT_ID),
                compositionId, String.valueOf(compositionArea));
    }

    /**
     * 初始化作文数据
     */
    private void initCompositionData() {

        if (webView != null) {
            webView.loadUrl("file:///android_asset/html/composition.html?" +
                    "composId=" + compositionId +
                    "&studentId=" + NewMainActivity.STUDENT_ID +
                    "&area=" + compositionArea +
                    "&format=" + compositionFormat +
                    "&fontSize=" + textShowSize[textSizePosition] +
                    "&lineHeight=" + textLineSpace[textLineSpacePosition] +
                    "&bgColor=" + background[backgroundPosition]);
        }

    }

    /**
     * 显示加载等待视图
     */
    private void showLoadingView(boolean show) {
        if (isDestroyed()) {
            return;
        }
        if (show) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.loading_h5_layout, null);
            ImageView iv_loading = view.findViewById(R.id.iv_h5_loading);
            GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_placeholder_h5, iv_loading);
            frameLayout.removeAllViews();
            frameLayout.addView(view);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
            frameLayout.removeAllViews();
        }
    }

    private void initCommentData() {
        isLoading = true;
        pageNum = 1;
        isEnd = false;
        mList.clear();
        mMaxSize = 0;
        adapter.notifyDataSetChanged();
        new GetCommentData(this).execute(commentListUrl +
                "compositionId=" + compositionId + "&pageNum=" + pageNum + "&pageSize=10");
    }

    /**
     * 获取更多数据
     */
    private void getMoreCommentData(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
            pageNum++;
            isLoading = true;
            new GetCommentData(this).execute(commentListUrl +
                    "compositionId=" + compositionId + "&pageNum=" + pageNum + "&pageSize=10");
        }
    }

    private void initView() {
        frameLayout = findViewById(R.id.frame_composition_detail);

        rl_back = findViewById(R.id.rl_back_composition_detail);
        rl_setting = findViewById(R.id.rl_setting_composition_detail);
        rl_collection = findViewById(R.id.rl_collection_composition_detail);
        iv_collection = findViewById(R.id.iv_collection_composition_detail);
        rl_more = findViewById(R.id.rl_more_composition_detail);

        rl_setting.setVisibility(View.GONE);
        rl_collection.setVisibility(View.GONE);
        rl_more.setVisibility(View.GONE);

        rl_bottom = findViewById(R.id.rl_bottom_composition_detail);
        tv_bottom_comment = findViewById(R.id.tv_comment_composition_detail);
        rl_bottom_comment = findViewById(R.id.rl_bottom_comment_composition_detail);
        tv_bottom_comment_number = findViewById(R.id.tv_bottom_comment_composition_detail);
        rl_bottom_points = findViewById(R.id.rl_bottom_points_composition_detail);
        iv_bottom_points = findViewById(R.id.iv_bottom_points_composition_detail);
        tv_bottom_points = findViewById(R.id.tv_bottom_points_composition_detail);
        rl_bottom_share = findViewById(R.id.rl_bottom_share_composition_detail);

        scrollView = findViewById(R.id.nested_scroll_view_composition_detail);
        webView = findViewById(R.id.web_view_composition_detail);
        rl_user = findViewById(R.id.rl_user_composition_detail);
        iv_user = findViewById(R.id.iv_user_composition_detail);
        tv_user_name = findViewById(R.id.tv_username_composition_detail);
        tv_user_grade = findViewById(R.id.tv_user_grade_composition_detail);
        rl_follow = findViewById(R.id.rl_follow_composition_detail);
        iv_follow = findViewById(R.id.iv_follow_composition_detail);
        tv_follow = findViewById(R.id.tv_follow_composition_detail);
        tv_user_description = findViewById(R.id.tv_user_description_composition_detail);

        ll_score = findViewById(R.id.ll_score_composition_detail);
        tv_user_score = findViewById(R.id.tv_user_score_composition_detail);
        ll_user_score = findViewById(R.id.ll_user_score_composition_detail);
        recyclerView_user_score = findViewById(R.id.recycler_view_user_image_composition_detail);
        tv_user_number = findViewById(R.id.tv_user_number_composition_detail);
        tv_score = findViewById(R.id.tv_score_composition_detail);
        rl_teacher = findViewById(R.id.rl_teacher_score_composition_detail);
        tv_teacher = findViewById(R.id.tv_teacher_composition_detail);
        tv_teacher_score = findViewById(R.id.tv_teacher_score_composition_detail);
        tv_teacher_suggest = findViewById(R.id.tv_teacher_suggest_composition_detail);
        tv_teacher_excellent = findViewById(R.id.tv_teacher_excellent_composition_detail);
        tv_teacher_insufficient = findViewById(R.id.tv_teacher_insufficient_composition_detail);
        tv_teacher_comment = findViewById(R.id.tv_teacher_comment_composition_detail);
        ll_model = findViewById(R.id.ll_model_composition_detail);
        tv_model = findViewById(R.id.tv_model_composition_detail);
        ll_events = findViewById(R.id.ll_events_composition_detail);
        iv_events = findViewById(R.id.iv_events_composition_detail);
        tv_events = findViewById(R.id.tv_events_composition_detail);
        ll_comment = findViewById(R.id.ll_comment_composition_detail);
        tv_comment_number = findViewById(R.id.tv_comment_number_composition_detail);
        recyclerView_comment = findViewById(R.id.recycler_view_comment_composition_detail);
        tv_no_comment = findViewById(R.id.tv_no_comment_composition_detail);

        //得到AssetManager
        AssetManager mgr = mContext.getAssets();
        //根据路径得到Typeface
        Typeface score = Typeface.createFromAsset(mgr, "css/GB2312.ttf");
        tv_teacher_score.setTypeface(score);

        LinearLayoutManager layoutManager_user_score = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_user_score.setLayoutManager(layoutManager_user_score);
        adapter_user = new UserImageAdapter(mContext, mList_user);
        recyclerView_user_score.setAdapter(adapter_user);

        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        recyclerView_comment.setLayoutManager(layoutManager);
        adapter = new CompositionCommentAdapter(mContext, mList);
        recyclerView_comment.setAdapter(adapter);
        recyclerView_comment.setNestedScrollingEnabled(false);

        if (compositionArea != 0) {
            rl_bottom.setVisibility(View.GONE);
            iv_collection.setImageResource(R.drawable.bottom_share);
        }

        initWebSettings();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("pythe") && url.contains("composition") && url.contains("param=")) {
                    String message = url.split("param=")[1];
                    String str1 = message.substring(0, message.indexOf(";"));
                    String str2 = "";
                    try {
                        str2 = URLDecoder.decode(message.substring(message.indexOf(";") + 1), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    showTeacherCommentDialog(str1, str2);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    postRemoveLoading();
                }
            }
        });
    }

    /**
     * 延迟移除loading
     */
    private void postRemoveLoading() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1500);
                    mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLoadingView(false);
        }
    };

    /**
     * 显示老师评语对话框
     *
     * @param str1 1表示优秀，0表示瑕疵，-1表示删除，-2表示修改，-3表示需要插入
     * @param str2
     */
    private void showTeacherCommentDialog(final String str1, final String str2) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_teacher_comment_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        ImageView imageView = holder.getView(R.id.iv_teacher_comment_dialog);
                        TextView textView = holder.getView(R.id.tv_teacher_comment_dialog);
                        if (str1.equals("1")) {
                            imageView.setImageResource(R.drawable.write_composition_detail_mark_good);
                        } else if (str1.equals("0")) {
                            imageView.setImageResource(R.drawable.write_composition_detail_mark_modify);
                        } else if (str1.equals("-1")) {
                            imageView.setImageResource(R.drawable.write_composition_detail_mark_delete);
                        } else if (str1.equals("-2")) {
                            imageView.setImageResource(R.drawable.write_composition_detail_mark_bad);
                        } else if (str1.equals("-3")) {
                            imageView.setImageResource(R.drawable.write_composition_detail_mark_insert);
                        }
                        textView.setText(str2);
                        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
                    }
                })
                .setMargin(48)
                .setShowBottom(false)
                .show(getSupportFragmentManager());
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
        //设置跨域访问
        webSettings.setAllowFileAccessFromFileURLs(true);
        //禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        //禁用文字缩放
        webSettings.setTextZoom(100);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
    }

    /**
     * 显示评论操作对话框
     *
     * @param pos
     */
    private void showCommentOptions(final int pos) {
        WritingCommentBean comment = mList.get(pos);
        final long userId = comment.getCommentUserId();
        final long commentId = comment.getCommentId();
        final String commentContent = comment.getCommentContent();
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_comment_item_click_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_reply = holder.getView(R.id.tv_reply_comment_dialog);
                        TextView tv_copy = holder.getView(R.id.tv_copy_comment_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_comment_dialog);
                        if (userId == NewMainActivity.STUDENT_ID) {
                            tv_reply.setText("删除");
                        }
                        tv_reply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (userId == NewMainActivity.STUDENT_ID) {
                                    deleteComment(pos, commentId);
                                } else {
                                    replyDialog(userId, commentId);
                                }
                                dialog.dismiss();
                            }
                        });
                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                copyContent(commentContent);
                                dialog.dismiss();
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
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
     * 前往登录
     */
    private void turnToLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            initData();
        } else {
            shareHandler.doResultIntent(data, null);
        }
    }

    /**
     * 显示分享对话框
     */
    private void showShareDialog() {
        type_share = -1;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_share_pic_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.share_cancel_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_wx_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWriting(TYPE_SHARE_WX_FRIEND);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_wxs_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWriting(TYPE_SHARE_WX_FRIENDS);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_picture_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(mContext,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                0);
                                    } else {
                                        showPicShare();
                                    }
                                } else {
                                    showPicShare();
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_weibo_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    shareWriting(TYPE_SHARE_WeiBo);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_qq_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWriting(TYPE_SHARE_QQ);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_qzone_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWriting(TYPE_SHARE_QZone);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.ll_copy_share_pic_dialog, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWriting(TYPE_SHARE_LINK);
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setDimAmount(0.3f)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 生成图片分享
     */
    private void showPicShare() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_share_composition_pic_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        final WebView frameLayout = holder.getView(R.id.frame_share_composition_pic_dialog);
                        final RelativeLayout rl_share_bottom = holder.getView(R.id.rl_bottom_share_composition_pic_dialog);
                        LinearLayout ll_wx_dialog = holder.getView(R.id.ll_wx_composition_share_dialog);
                        LinearLayout ll_wxs_dialog = holder.getView(R.id.ll_wxs_composition_share_dialog);
                        LinearLayout ll_download_dialog = holder.getView(R.id.ll_download_composition_share_dialog);
                        LinearLayout ll_weibo_dialog = holder.getView(R.id.ll_weibo_composition_share_dialog);
                        LinearLayout ll_qq_dialog = holder.getView(R.id.ll_qq_composition_share_dialog);
                        LinearLayout ll_qz_dialog = holder.getView(R.id.ll_qzone_composition_share_dialog);
                        LinearLayout ll_copy_dialog = holder.getView(R.id.ll_copy_composition_share_dialog);
                        TextView tv_cancel = holder.getView(R.id.tv_cancel_composition_share_dialog);

                        frameLayout.setHorizontalScrollBarEnabled(false);//水平不显示
                        frameLayout.setVerticalScrollBarEnabled(false); //垂直不显示
                        WebSettings webSettings = frameLayout.getSettings();
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

                        frameLayout.loadUrl("file:///android_asset/html/composition.html?" +
                                "composId=" + compositionId +
                                "&studentId=" + NewMainActivity.STUDENT_ID +
                                "&area=" + compositionArea +
                                "&format=" + compositionFormat +
                                "&fontSize=" + textShowSize[textSizePosition] +
                                "&lineHeight=" + textLineSpace[textLineSpacePosition] +
                                "&bgColor=" + background[backgroundPosition]);

                        ll_wx_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getWebViewBitmap(mContext, frameLayout),
                                                ImageUtils.getViewGroupBitmap(rl_share_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                shareImageToWX(true, imageUrl);
                            }
                        });
                        ll_wxs_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bitmap == null) {
                                    bitmap = ImageUtils.createWaterMaskImage(mContext,
                                            ImageUtils.getWebViewBitmap(mContext, frameLayout),
                                            ImageUtils.getViewGroupBitmap(rl_share_bottom));
                                }
                                shareImageToWX(false, imageUrl);
                            }
                        });
                        ll_download_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getWebViewBitmap(mContext, frameLayout),
                                                ImageUtils.getViewGroupBitmap(rl_share_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                if (imageUrl.equals("")) {
                                    showTips("保存图片失败");
                                } else {
                                    showTips("图片保存在" + imageUrl);
                                }
                            }
                        });
                        ll_weibo_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (bitmap == null) {
                                    bitmap = ImageUtils.createWaterMaskImage(mContext,
                                            ImageUtils.getWebViewBitmap(mContext, frameLayout),
                                            ImageUtils.getViewGroupBitmap(rl_share_bottom));
                                }
                                if (WbSdk.isWbInstall(mContext)) {
                                    ShareUtil.shareImageToWeibo(shareHandler, bitmap);
                                } else {
                                    showTips("请先安装微博");
                                }
                            }
                        });
                        ll_qq_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getWebViewBitmap(mContext, frameLayout),
                                                ImageUtils.getViewGroupBitmap(rl_share_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                shareImageToQQ(imageUrl, false);
                            }
                        });
                        ll_qz_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (imageUrl.equals("")) {
                                    if (bitmap == null) {
                                        bitmap = ImageUtils.createWaterMaskImage(mContext,
                                                ImageUtils.getWebViewBitmap(mContext, frameLayout),
                                                ImageUtils.getViewGroupBitmap(rl_share_bottom));
                                    }
                                    imageUrl = ImageUtils.downloadBitmap(bitmap);
                                }
                                shareImageToQQ(imageUrl, true);
                            }
                        });
                        ll_copy_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareWriting(TYPE_SHARE_LINK);
                            }
                        });
                        tv_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .show(getSupportFragmentManager());
    }

    /**
     * @param imageUrl 本地图片路径
     */
    private void shareImageToQQ(String imageUrl, boolean isFriends) {
        ShareUtil.shareImageToQQ(this, imageUrl, !isFriends);
    }

    /**
     * 分享图片到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareImageToWX(boolean friend, String imageUrl) {
        ShareUtil.shareImageToWX(mContext, imageUrl, friend);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (verifyPermissions(grantResults)) {
                showPicShare();
            } else {
                showTips("没有存储权限，无法生成图片");
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 分享作文
     */
    private void shareWriting(int type) {
        this.type_share = type;
        if (compositionUrl.equals("")) {
            MyToastUtil.showToast(mContext, "正在准备分享内容...");
            new GetShareHtml(this).execute(shareUrl, compositionId,
                    String.valueOf(compositionArea), String.valueOf(compositionFormat));
        } else {
            share(compositionUrl);
        }
    }

    /**
     * 评论
     *
     * @param userId
     * @param commentId
     */
    private void replyDialog(final long userId, final long commentId) {
        if (NewMainActivity.STUDENT_ID == -1) {
            turnToLogin();
        } else {
            NiceDialog.init()
                    .setLayoutId(R.layout.write_comment_dialog_layout)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            final EditText editText = holder.getView(R.id.edit_input);
                            final TextView textView = holder.getView(R.id.tv_content_number);
                            final TextView tv_commit = holder.getView(R.id.tv_dialog_comment_commit);
                            TextView tv_cancel = holder.getView(R.id.tv_dialog_comment_cancel);
                            textView.setText(String.valueOf(100));
                            editText.setHint("请输入评论");
                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                            editText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    int left = 100 - editText.getText().toString().length();
                                    textView.setText(String.valueOf(left));
                                    if (left == 100) {
                                        tv_commit.setSelected(false);
                                    } else {
                                        tv_commit.setSelected(true);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager imm = (InputMethodManager)
                                            getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(editText, 0);
                                }
                            });
                            tv_commit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String str = editText.getText().toString().trim();
                                    if (TextUtils.isEmpty(str)) {
                                        MyToastUtil.showToast(mContext, "请输入内容");
                                    } else {
                                        new CommitCommentData(mContext).execute(commentUrl,
                                                editText.getText().toString(),
                                                compositionId, String.valueOf(userId),
                                                String.valueOf(commentId), compositionTitle);
                                        InputMethodManager imm = (InputMethodManager)
                                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm.isActive()) {
                                            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                                        }
                                        dialog.dismiss();
                                    }
                                }
                            });
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
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
    }

    /**
     * 删除自己的评论
     */
    private void deleteComment(int pos, long commentId) {
        this.deletePosition = pos;
        new DeleteCommentData(mContext).execute(deleteUrl, String.valueOf(commentId));
    }

    /**
     * 复制内容到截切板
     *
     * @param str 要复制的内容
     */
    private void copyContent(String str) {
        DataUtil.copyContent(mContext, str);
    }

    /**
     * 分析作文数据
     *
     * @param s
     */
    private void analyzeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                isLiked = object.optBoolean("like", false);
                isCollected = object.optBoolean("collect", false);
                shareContent = object.getString("subContent");

                isFollowed = object.optInt("followed", 0);
                isScore = object.optInt("rated", 0);

                JSONArray array = object.getJSONArray("raterImages");
                if (array.length() != 0) {
                    mList_user.clear();
                    for (int i = 0; i < array.length(); i++) {
                        UserBean userBean = new UserBean();
                        userBean.setUserImage(array.getString(i));
                        mList_user.add(userBean);
                    }
                }

                JSONObject writing = object.getJSONObject("writing");
                compositionFormat = writing.optInt("format", 1);
                compositionTitle = writing.getString("article");
                compositionType = writing.optInt("type", 0);

                userId = writing.optLong("studentId", -1);
                userImage = writing.getString("userimg");
                userName = writing.getString("username");
                userGrade = DataUtil.gradeCode2Chinese(writing.optInt("gradeid", 110));
                userDescription = writing.getString("description");

                userScore = writing.optInt("avgScore", 0);
                userNumber = writing.optInt("quantity", 0);

                if (writing.optInt("status", 1) == 0) {  //分数显示等奖制
                    if (writing.getString("prize").equals("") ||
                            writing.getString("prize").equals("null")) {
                        teacherScore = "";
                    } else {
                        teacherScore = writing.getString("prize");
                    }
                } else {  //分数显示打分数字
                    if (writing.getString("mark").equals("") ||
                            writing.getString("mark").equals("null")) {
                        teacherScore = "";
                    } else {
                        teacherScore = writing.optInt("mark", 0) + "分";
                    }
                }

                String comment = writing.getString("comment");
                if (comment.equals("") || comment.equals("null")) {
                    teacherComment = "";
                } else {
                    JSONObject teacherCommentObject = new JSONObject(comment);
                    teacherSuggest = teacherCommentObject.getString("suggestion");
                    teacherExcellent = teacherCommentObject.getString("spot");
                    teacherInsufficient = teacherCommentObject.getString("deficiency");
                    if (teacherSuggest.equals("") || teacherSuggest.equals("null")) {
                        teacherSuggest = "暂无内容";
                    }
                    if (teacherExcellent.equals("") || teacherExcellent.equals("null")) {
                        teacherExcellent = "暂无内容";
                    }
                    if (teacherInsufficient.equals("") || teacherInsufficient.equals("null")) {
                        teacherInsufficient = "暂无内容";
                    }
                    teacherComment = teacherSuggest;
                }


                if (compositionType == 4) {
                    compositionModel = writing.getString("matchName");
                }

                likeNum = writing.optInt("likeNum", 0);

                if (!object.getString("match").equals("")
                        && !object.getString("match").equals("null")) {
                    JSONObject events = object.getJSONObject("match");
                    eventsId = events.getString("id");
                    eventsStatus = events.optInt("status", 1);
                    eventsImage = events.getString("image");
                    eventsContent = events.getString("title");
                } else {
                    eventsId = "";
                }

                updateUi();
                initCompositionData();
                if (compositionArea == 0) {
                    initCommentData();
                } else {
                    ll_comment.setVisibility(View.GONE);
                }
            } else {
                noConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noConnect();
        }
    }

    /**
     * 更新ui
     */
    private void updateUi() {
        if (isDestroyed()) {
            return;
        }
        GlideUtils.loadUserImage(mContext, HttpUrlPre.FILE_URL + userImage, iv_user);
        tv_user_name.setText(userName);
        tv_user_grade.setText(userGrade);
        if (userDescription.equals("") || userDescription.equals("null")) {
            tv_user_description.setText("该用户暂无介绍");
        } else {
            tv_user_description.setText(userDescription);
        }

        updateOperateView();

        updateFollowUi();

        updateUserScoreUi();

        if (teacherScore.equals("") || teacherScore.equals("null")
                || teacherComment.equals("")) {
            rl_teacher.setVisibility(View.GONE);
        } else {
            tv_teacher_score.setText(teacherScore);
            rl_teacher.setVisibility(View.VISIBLE);
            updateTeacherUi(0);
        }

        if (compositionType == 1) {
            tv_teacher.setText("老师评语");
        } else {
            tv_teacher.setText("评选评语");
        }

        if (compositionType == 4) {
            tv_model.setText(compositionModel);
            ll_model.setVisibility(View.VISIBLE);
        } else {
            ll_model.setVisibility(View.GONE);
        }

        if (eventsId.equals("") || eventsId.equals("null")) {
            ll_events.setVisibility(View.GONE);
        } else {
            if (!isDestroyed()) {
                RequestOptions options_events = new RequestOptions()
                        .placeholder(R.drawable.image_placeholder_rectangle)
                        .error(R.drawable.image_placeholder_rectangle)
                        .transform(new GlideRoundImage(mContext));
                Glide.with(mContext)
                        .asBitmap()
                        .load(eventsImage)
                        .apply(options_events)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                iv_events.setImageResource(R.drawable.image_placeholder_rectangle);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                iv_events.setImageBitmap(resource);
                            }
                        });
            }
            tv_events.setText(eventsContent);
            ll_events.setVisibility(View.VISIBLE);
        }

        updateLikeNumUi();

        if (compositionArea == 0) {
            if (isCollected) {
                iv_collection.setImageResource(R.drawable.bottom_collection_selected);
            } else {
                iv_collection.setImageResource(R.drawable.bottom_collection_unselected);
            }
        }

        rl_setting.setVisibility(View.VISIBLE);
        rl_collection.setVisibility(View.VISIBLE);
    }

    /**
     * 更新点赞ui
     */
    private void updateLikeNumUi() {
        if (likeNum > 10000) {
            String info = likeNum / 10000 + "w+";
            tv_bottom_points.setText(info);
        } else if (likeNum == 0) {
            tv_bottom_points.setText("");
        } else {
            tv_bottom_points.setText(String.valueOf(likeNum));
        }
        if (isLiked) {
            iv_bottom_points.setImageResource(R.drawable.bottom_points_selected);
        } else {
            iv_bottom_points.setImageResource(R.drawable.bottom_points_unselected);
        }
    }

    /**
     * 更新关注ui
     */
    private void updateFollowUi() {
        if (userId == NewMainActivity.STUDENT_ID && userId != -1) {
            rl_follow.setVisibility(View.GONE);
        } else {
            rl_follow.setVisibility(View.VISIBLE);
            if (isFollowed == 1) {
                rl_follow.setSelected(true);
                iv_follow.setVisibility(View.GONE);
                tv_follow.setText("已关注");
            } else {
                rl_follow.setSelected(false);
                iv_follow.setVisibility(View.VISIBLE);
                tv_follow.setText("关注");
            }
        }
    }

    /**
     * 更新用户评分UI
     */
    private void updateUserScoreUi() {
        if (compositionArea != 0) {
            ll_score.setVisibility(View.GONE);
            return;
        }
        if (userScore == 0) {
            tv_user_score.setTextColor(Color.parseColor("#333333"));
            tv_user_score.setTextSize(14);
            tv_user_score.setText("暂无评分");
        } else {
            tv_user_score.setTextColor(Color.parseColor("#ff9933"));
            tv_user_score.setTextSize(40);
            tv_user_score.setText(String.valueOf(userScore));
        }
        if (isScore == 1) {
            tv_score.setVisibility(View.GONE);
        } else {
            tv_score.setVisibility(View.VISIBLE);
        }
        if (mList_user.size() == 0) {
            ll_user_score.setVisibility(View.GONE);
        } else {
            adapter_user.notifyDataSetChanged();
            String info = userNumber + "名派友参与打分";
            tv_user_number.setText(info);
            ll_user_score.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取作文内容失败
     */
    private void noConnect() {
        if (mContext == null) {
            return;
        }
        rl_setting.setVisibility(View.GONE);
        rl_collection.setVisibility(View.GONE);
        rl_more.setVisibility(View.GONE);
        View errorView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_loading_error_layout, null);
        TextView tv_reload = errorView.findViewById(R.id.tv_reload_list_loading_error);
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                initData();
            }
        });
        frameLayout.removeAllViews();
        frameLayout.addView(errorView);
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 分析评论数据
     *
     * @param s 获取得到的数据
     */
    private void analyzeCommentListData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.optInt("status", -1) == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                mMaxSize = data.optInt("size", 0);
                updateCommentNumber();
                JSONArray jsonArray = data.getJSONArray("comment");
                if (jsonArray.length() == 0) {
                    emptyCommentData();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        WritingCommentBean comment = new WritingCommentBean();
                        comment.setId(json.getString("compositionId"));
                        comment.setType(json.optInt("compositionType", -1));
                        comment.setCommentId(json.optLong("originalCommentId", -1));
                        String commentTime = json.getString("originalCommentTime");
                        if (commentTime.equals("null")) {
                            commentTime = "2018-01-01 00:00";
                        } else {
                            commentTime = DateUtil.time2YMD(commentTime);
                        }
                        comment.setCommentTime(DateUtil.getTimeDiff_(commentTime, curTime));
                        comment.setCommentContent(json.getString("originalCommentContent"));
                        if (json.getString("originalCommenterId").equals("null")) {
                            comment.setCommentUserId(-1);
                        } else {
                            comment.setCommentUserId(json.optLong("originalCommenterId", -1));
                        }
                        comment.setCommentUsername(json.getString("commenterName"));
                        comment.setCommentUserImg(json.getString("commenterImg"));
                        if (json.getString("replyCommentId").equals("null")) {
                            comment.setReplyCommentId(-1);
                        } else {
                            comment.setReplyCommentId(json.optInt("replyCommentId", -1));
                        }
                        String replyTime = json.getString("replyCommentTime");
                        if (replyTime.equals("null")) {
                            replyTime = "2018-01-01 00:00";
                        } else {
                            replyTime = DateUtil.time2YMD(replyTime);
                        }
                        comment.setReplyCommentTime(DateUtil.getTimeDiff_(replyTime, curTime));
                        comment.setReplyCommentContent(json.getString("replyCommentContent"));
                        if (json.getString("replyCommenterId").equals("null")) {
                            comment.setReplyUserId(-1);
                        } else {
                            comment.setReplyUserId(json.optLong("replyCommenterId", -1));
                        }
                        comment.setReplyUsername(json.getString("replyCommenterName"));
                        comment.setReplyUserImg(json.getString("replyCommenterImg"));
                        mList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                    if (tv_no_comment.getVisibility() == View.VISIBLE) {
                        tv_no_comment.setVisibility(View.GONE);
                    }
                }
            } else if (400 == jsonObject.optInt("status", -1)) {
                emptyCommentData();
            } else {
                errorCommentConnect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorCommentConnect();
        }
    }

    /**
     * 更新评论数量
     */
    private void updateCommentNumber() {
        String info = mMaxSize + "条评论";
        tv_comment_number.setText(info);
        if (mMaxSize > 10000) {
            String str = likeNum / 10000 + "w+";
            tv_bottom_points.setText(str);
        } else if (mMaxSize == 0) {
            tv_bottom_points.setText("");
        } else {
            tv_bottom_comment_number.setText(String.valueOf(mMaxSize));
        }
    }

    /**
     * 获取数据为空
     */
    private void emptyCommentData() {
        isEnd = true;
        if (mList.size() == 0) {
            tv_no_comment.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取数据失败
     */
    private void errorCommentConnect() {
        if (mList.size() == 0) {
            tv_no_comment.setVisibility(View.VISIBLE);
        } else {
            showTips("获取数据失败，请稍后重试");
        }
    }

    /**
     * 分析收藏或取消收藏的数据
     *
     * @param s
     */
    private void analyzeCollectData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status")) {
                if (isCollected) {
                    isCollected = false;
                    iv_collection.setImageResource(R.drawable.bottom_collection_unselected);
                } else {
                    isCollected = true;
                    iv_collection.setImageResource(R.drawable.bottom_collection_selected);
                }
            } else {
                errorCollect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorCollect();
        }
    }

    /**
     * 收藏或取消收藏失败
     */
    private void errorCollect() {
        if (isCollected) {
            showTips("取消收藏失败，请稍后再试~");
        } else {
            showTips("收藏失败，请稍后再试~");
        }
    }

    /**
     * 分析关注用户数据
     *
     * @param s
     */
    private void analyzeFollowUserData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (isFollowed == 1) {
                    isFollowed = 0;
                } else {
                    isFollowed = 1;
                }
                updateFollowUi();
            } else {
                errorFollowUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorFollowUser();
        }
    }

    /**
     * 关注用户失败
     */
    private void errorFollowUser() {
        if (isFollowed == 1) {
            showTips("取消关注失败，请稍后重试~");
        } else {
            showTips("关注失败，请稍后重试~");
        }
    }

    /**
     * 分析点赞或取消点赞数据
     *
     * @param s
     */
    private void analyzeLikeOrNotData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status")) {
                if (isLiked) {
                    isLiked = false;
                    likeNum = likeNum - 1;
                } else {
                    isLiked = true;
                    likeNum = likeNum + 1;
                }
                updateLikeNumUi();
            } else {
                errorCollect();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorCollect();
        }
    }

    /**
     * 点赞或取消点赞失败
     */
    private void errorLikeOrNot() {
        if (isLiked) {
            showTips("取消点赞失败，请稍后重试~");
        } else {
            showTips("点赞失败，请稍后重试~");
        }
    }

    /**
     * 分析评论返回的数据
     *
     * @param s
     */
    private void analyzeCommentData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject json = jsonObject.getJSONObject("data");
                WritingCommentBean comment = new WritingCommentBean();
                comment.setId(json.getString("compositionId"));
                comment.setType(json.optInt("compositionType", -1));
                if (json.getString("originalCommentId").equals("null")) {
                    comment.setCommentId(-1);
                } else {
                    comment.setCommentId(json.optLong("originalCommentId", -1));
                }
                String commentTime = json.getString("originalCommentTime");
                if (commentTime.equals("null")) {
                    commentTime = "2018-01-01 00:00";
                } else {
                    commentTime = DateUtil.time2YMD(commentTime);
                }
                comment.setCommentTime(DateUtil.getTimeDiff_(commentTime, curTime));
                comment.setCommentContent(json.getString("originalCommentContent"));
                if (json.getString("originalCommenterId").equals("null")) {
                    comment.setCommentUserId(-1);
                } else {
                    comment.setCommentUserId(json.optLong("originalCommenterId", -1));
                }
                comment.setCommentUsername(json.getString("commenterName"));
                comment.setCommentUserImg(json.getString("commenterImg"));
                if (json.getString("replyCommentId").equals("null")) {
                    comment.setReplyCommentId(-1);
                } else {
                    comment.setReplyCommentId(json.optInt("replyCommentId", -1));
                }
                String replyTime = json.getString("replyCommentTime");
                if (replyTime.equals("null")) {
                    replyTime = "2018-01-01 00:00";
                } else {
                    replyTime = DateUtil.time2YMD(replyTime);
                }
                comment.setReplyCommentTime(DateUtil.getTimeDiff_(replyTime, curTime));
                comment.setReplyCommentContent(json.getString("replyCommentContent"));
                if (json.getString("replyCommenterId").equals("null")) {
                    comment.setReplyUserId(-1);
                } else {
                    comment.setReplyUserId(json.optLong("replyCommenterId", -1));
                }
                comment.setReplyUsername(json.getString("replyCommenterName"));
                comment.setReplyUserImg(json.getString("replyCommenterImg"));
                mList.add(0, comment);
                adapter.notifyDataSetChanged();
                if (tv_no_comment.getVisibility() == View.VISIBLE) {
                    tv_no_comment.setVisibility(View.GONE);
                }
                mMaxSize = mMaxSize + 1;
                updateCommentNumber();
            } else {
                errorComment();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorComment();
        }
    }

    /**
     * 评论失败
     */
    private void errorComment() {
        showTips("评论失败");
    }

    /**
     * 分析删除评论的返回数据
     *
     * @param s
     */
    private void analyzeDeleteCommentData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                if (deletePosition != -1) {
                    mList.remove(deletePosition);
                    adapter.notifyDataSetChanged();
                    deletePosition = -1;
                    if (mList.size() == 0) {
                        emptyCommentData();
                    }
                    mMaxSize = mMaxSize - 1;
                    updateCommentNumber();
                }
            } else {
                errorDeleteComment();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorDeleteComment();
        }
    }

    /**
     * 删除评论失败
     */
    private void errorDeleteComment() {
        showTips("删除评论失败");
    }

    /**
     * 分析打分数据
     *
     * @param s
     */
    private void analyzeScoreCompositionData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                userNumber = object.optInt("quantity", 0);
                userScore = object.optInt("avgScore", 0);
                UserBean userBean = new UserBean();
                userBean.setUserImage(NewMainActivity.USERIMG);
                if (mList_user.size() < 6) {
                    mList_user.add(userBean);
                } else {
                    mList_user.add(0, userBean);
                    int max = mList_user.size();
                    mList_user.remove(max - 1);
                }
                isScore = 1;
                updateUserScoreUi();
            } else {
                errorScoreComposition();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorScoreComposition();
        }
    }

    /**
     * 打分出错
     */
    private void errorScoreComposition() {
        showTips("打分失败，请稍后再试~");
    }

    /**
     * 分析分享链接数据
     *
     * @param s
     */
    private void analyzeShareData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                compositionUrl = jsonObject.getString("data");
                share(compositionUrl);
            } else {
                errorShare();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorShare();
        }
    }

    /**
     * 分享
     *
     * @param url
     */
    private void share(String url) {
        switch (type_share) {
            case TYPE_SHARE_WX_FRIEND:
                shareArticleToWX(true, url);
                break;
            case TYPE_SHARE_WX_FRIENDS:
                shareArticleToWX(false, url);
                break;
            case TYPE_SHARE_WeiBo:
                shareToWeiBo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url);
                break;
            case TYPE_SHARE_LINK:
                DataUtil.copyContent(mContext, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeiBo(String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        ShareUtil.shareToWeibo(shareHandler, url, compositionTitle, shareContent, thumb);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, compositionTitle, shareContent,
                HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, compositionTitle, shareContent,
                HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(mContext, url, compositionTitle, shareContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        MyToastUtil.showToast(mContext, "分享失败，请稍后重试");
    }


    /**
     * 显示提示信息
     *
     * @param tips
     */
    private void showTips(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    /**
     * 获取作文详情
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, CompositionDetailActivity> {

        protected GetData(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", strings[1]);
                json.put("id", strings[2]);
                json.put("area", strings[3]);
                json.put("isNew", 1);
                RequestBody requestBody = RequestBody.create(DataUtil.JSON, json.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.noConnect();
            } else {
                activity.analyzeData(s);
            }
        }
    }

    /**
     * 获取评论数据
     */
    private static class GetCommentData
            extends WeakAsyncTask<String, Integer, String, CompositionDetailActivity> {

        protected GetCommentData(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            activity.isLoading = false;
            if (s == null) {
                activity.errorCommentConnect();
            } else {
                activity.analyzeCommentListData(s);
            }
        }
    }

    /**
     * 提交评论数据
     */
    private static class CommitCommentData
            extends WeakAsyncTask<String, Integer, String, CompositionDetailActivity> {

        protected CommitCommentData(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("commentContent", params[1]);
                json.put("commentUserId", NewMainActivity.STUDENT_ID);
                json.put("replyCompositionId", params[2]);
                json.put("replyUserId", params[3]);
                json.put("replyCommentId", params[4]);
                json.put("replyCompositionTitle", params[5]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorComment();
            } else {
                activity.analyzeCommentData(s);
            }
        }
    }


    /**
     * 收藏作文
     */
    private static class CollectComposition
            extends WeakAsyncTask<String, Void, String, CompositionDetailActivity> {

        protected CollectComposition(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("writingId", strings[2]);
                object.put("article", strings[3]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorCollect();
            } else {
                activity.analyzeCollectData(s);
            }
            activity.isCollecting = false;
        }
    }

    /**
     * 取消收藏作文
     */
    private static class UnCollectComposition
            extends WeakAsyncTask<String, Void, String, CompositionDetailActivity> {

        protected UnCollectComposition(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("writingId", strings[2]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorCollect();
            } else {
                activity.analyzeCollectData(s);
            }
            activity.isCollecting = false;
        }
    }

    /**
     * 关注用户
     */
    private static class FollowUser
            extends WeakAsyncTask<String, Void, String, CompositionDetailActivity> {

        protected FollowUser(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("followingId", strings[1]);
                object.put("followerId", strings[2]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorFollowUser();
            } else {
                activity.analyzeFollowUserData(s);
            }
            activity.isFollowing = false;
        }
    }

    /**
     * 点赞或取消点赞
     */
    private static class LikeOrNot
            extends WeakAsyncTask<String, Void, String, CompositionDetailActivity> {

        protected LikeOrNot(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("writingId", strings[2]);
                object.put("area", strings[3]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorLikeOrNot();
            } else {
                activity.analyzeLikeOrNotData(s);
            }
            activity.isClickLike = false;
        }
    }

    /**
     * 删除自己的评论数据
     */
    private static class DeleteCommentData
            extends WeakAsyncTask<String, Integer, String, CompositionDetailActivity> {

        protected DeleteCommentData(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("commentUserId", NewMainActivity.STUDENT_ID);
                json.put("commentId", params[1]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorDeleteComment();
            } else {
                activity.analyzeDeleteCommentData(s);
            }
        }
    }

    /**
     * 打分
     */
    private static class ScoreComposition
            extends WeakAsyncTask<String, Void, String, CompositionDetailActivity> {

        protected ScoreComposition(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("compositionId", strings[2]);
                object.put("score", strings[3]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorScoreComposition();
            } else {
                activity.analyzeScoreCompositionData(s);
            }
        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, CompositionDetailActivity> {

        protected GetShareHtml(CompositionDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(CompositionDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("area", strings[2]);
                object.put("format", strings[3]);
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
        protected void onPostExecute(CompositionDetailActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("textSize", textSizePosition);
        editor.putInt("textLineSpace", textLineSpacePosition);
        editor.putInt("screenLight", screenLight);
        editor.putInt("background", backgroundPosition);
        editor.apply();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        super.onDestroy();
    }
}
