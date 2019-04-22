package com.dace.textreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.dace.textreader.adapter.GlossaryFlexBoxAdapter;
import com.dace.textreader.adapter.SymbolAdapter;
import com.dace.textreader.bean.AutoSaveWritingBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.JsonParser;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.SpeechRecognizerUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.WaveView;
import com.dace.textreader.view.editor.RichEditor;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.sleep;

/**
 * 写作文
 */
public class WritingActivity extends BaseActivity implements View.OnClickListener {

    //查看作文内容
    private static final String GET_WRITING_DATA = HttpUrlPre.HTTP_URL + "/writing/edit/select";
    //查看比赛作文内容和时间
    private static final String GET_COMPETITION_DATA = HttpUrlPre.HTTP_URL + "/select/match/my/writing";
    //保存到草稿箱
    private static final String SAVE_WRITING_DRAFT = HttpUrlPre.HTTP_URL + "/writing/save";
    //提交作文到比赛活动
    private static final String submitCompetitionUrl = HttpUrlPre.HTTP_URL + "/writing/commit/match";
    //查看比赛时间
    private static final String competitionTimeUrl = HttpUrlPre.HTTP_URL + "/get/match/timer";

    private final static int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;  //请求音频权限
    private static final int TURN_TO_SUBMIT_REVIEW_CODE = 2;  //前往批改
    private static final int REQUEST_NOTES = 3;  //前往刻意训练
    private static final int ERROR_CORRECTION = 4;  //前往纠错
    private static final int IMAGE_TO_TEXT = 5;  //前往图片转文字

    private RelativeLayout rl_root;
    private RelativeLayout rl_top;
    private RelativeLayout rl_back;  //返回按钮
    private TextView tv_commit;

    private EditText et_title;
    private TextView tv_time;
    private TextView tv_word_count;
    private TextView tv_countdown;

    private RelativeLayout rl_summary;
    private RelativeLayout rl_error;

    // 语音听写对象
    private SpeechRecognizerUtil speechRecognizerUtil;

    private WritingActivity mContext;

    private boolean isSpeeching = false;

    private String id = "";
    private int type;
    private int area;  //作文来源
    private String time;
    private int word_count = 0;
    private String title = "";
    private String content = "";
    private String taskId = "";  //比赛、活动ID
    private int status = 0;  //比赛状态 0表示已结束、1表示征稿中、2表示评选中
    private boolean isFromCompetitionH5 = false;  //是否来自比赛H5
    private boolean isCompetitionWriting = false;  //是否提交过比赛作文
    private String competitionEndTime = "";//比赛结束时间
    private String competitionCountdownTime = "";  //倒计时

    private boolean editHasFocus = true;  //哪个输入框获取了焦点,true表示title，false表示content

    /**
     * 2018-10-18改版新增内容
     */
    private static final String coverUrl = HttpUrlPre.HTTP_URL + "/upload/composition/cover";
    private static final String imageUrl = HttpUrlPre.HTTP_URL + "/upload/composition/image";

    private RelativeLayout rl_cover;
    private ImageView iv_cover;
    private ImageView iv_delete_cover;
    private ImageView iv_change_cover;
    private ImageView iv_cover_placeholder;
    private TextView tv_cover_placeholder;

    private LinearLayout ll_practice;
    private RelativeLayout rl_delete_practice;
    private RelativeLayout rl_more_practice;
    private ImageView iv_more_practice;
    private TextView tv_practice;
    private RecyclerView recyclerView_practice;

    private RichEditor richEditor;
    private TextView tv_hint;

    private LinearLayout ll_keyboard_operate;
    private RelativeLayout rl_bottom_keyboard;
    private RelativeLayout rl_bottom_symbol;
    private ImageView iv_bottom_symbol;
    private RelativeLayout rl_bottom_voice;
    private RelativeLayout rl_bottom_image;
    private ImageView iv_bottom_image;
    private RelativeLayout rl_bottom_hide;

    private LinearLayout ll_simple_operate;
    private RelativeLayout rl_simple_symbol;
    private ImageView iv_simple_symbol;
    private RelativeLayout rl_simple_image;
    private ImageView iv_simple_image;
    private RelativeLayout rl_simple_voice;
    private WaveView waveView_simple;
    private ImageView iv_simple_voice;

    private FrameLayout frameLayout_operate;

    private boolean isFirst = true;  //是否是第一次进入

    private GlossaryFlexBoxAdapter adapter;
    private List<String> mList = new ArrayList<>();
    private String practice = "";
    private boolean isShowMore = true;

    private ImagePicker imagePicker;//图片选择框架
    private int chooseImageSort = 1; //选择图片的种类，0是封面，1是插入正文图片，2是选中图片转文字
    private int mIndex = -1;  //正文图片操作位置

    private String cover = "";
    private int format = 1;

    private boolean isShowOperate = false;
    private boolean isShowSymbol = false;
    private boolean isShowVoice = false;
    private boolean isShowImage = false;

    private View view_symbol;
    private RecyclerView recyclerView_symbol_view;
    private List<String> mList_symbol = new ArrayList<>();
    private SymbolAdapter adapter_symbol;

    private View view_voice;
    private RelativeLayout rl_voice_view;
    private WaveView waveView_voice_view;
    private ImageView iv_voice_view;
    private TextView tv_voice_view;

    private View view_image;
    private LinearLayout ll_text_recognition_view;
    private LinearLayout ll_insert_image_view;

    private boolean isCorrection = false;  //是否是作文批改，是的话点击完成直接跳转批改

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        id = getIntent().getStringExtra("id");
        type = getIntent().getIntExtra("type", 5);
        area = getIntent().getIntExtra("area", 5);
        taskId = getIntent().getStringExtra("taskId");
        isCorrection = getIntent().getBooleanExtra("isCorrection", false);
        isCompetitionWriting = getIntent().getBooleanExtra("isCompetitionWriting", false);
        isFromCompetitionH5 = getIntent().getBooleanExtra("isFromCompetitionH5", false);

        time = DateUtil.getTodayDateTime();

        if (type == 2 && !taskId.equals("") && !taskId.equals("null")) {
            isFromCompetitionH5 = true;
        }

        initView();
        if (isFromCompetitionH5) {
            initCompetitionWritingData();
        } else {
            initData();
            if (isCompetitionWriting && type == 2) {
                initCompetitionTimeData();
            }
        }
        initEvents();


        // 使用SpeechRecognizer对象，可根据回调消息自定义界面
        speechRecognizerUtil = SpeechRecognizerUtil.getInstance();
        speechRecognizerUtil.init(mContext, mInitListener);

        checkUserPermission();

        initOperateChildView();
        initOperateChildEvents();

        autoSaveTime();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOperateChildEvents() {
        adapter_symbol.setOnItemClickListen(new SymbolAdapter.OnItemClickListen() {
            @Override
            public void onClick(View view) {
                int pos = recyclerView_symbol_view.getChildAdapterPosition(view);
                if (pos != -1 && pos < mList_symbol.size()) {
                    String text = mList_symbol.get(pos);
                    if (editHasFocus) {
                        int index = et_title.getSelectionStart();
                        Editable editable = et_title.getEditableText();
                        if (index < 0 || index >= editable.length()) {
                            editable.append(text);
                        } else {
                            editable.insert(index, text);
                        }
                        if (text.equals("“”") || text.equals("‘’")
                                || text.equals("《》") || text.equals("（）")) {
                            if (index + 1 <= 15) {
                                et_title.setSelection(index + 1);
                            }
                        }
                    } else {
                        richEditor.appendSymbolText(text);
                    }
                }
            }
        });
        rl_voice_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                        REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                            } else {
                                startSpeech();
                            }
                        } else {
                            startSpeech();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopSpeech();
                        break;
                }
                return true;
            }
        });
        ll_text_recognition_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showTip("功能暂未开放，敬请期待~");
                chooseImage(2, -1);
                hideOperate();
            }
        });
        ll_insert_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(1, -1);
                hideOperate();
            }
        });
    }

    /**
     * 初始化操作栏子View
     */
    private void initOperateChildView() {
        mList_symbol.add("，");
        mList_symbol.add("。");
        mList_symbol.add("！");
        mList_symbol.add("？");
        mList_symbol.add("、");
        mList_symbol.add("：");
        mList_symbol.add("“”");
        mList_symbol.add("‘’");
        mList_symbol.add("；");
        mList_symbol.add("……");
        mList_symbol.add("《》");
        mList_symbol.add("（）");
        mList_symbol.add("——");
        mList_symbol.add("~");
        mList_symbol.add("·");

        view_symbol = LayoutInflater.from(mContext).inflate(R.layout.writing_operate_symbol_layout,
                frameLayout_operate, false);
        recyclerView_symbol_view = view_symbol.findViewById(R.id.recycler_view_writing_operate_symbol);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        recyclerView_symbol_view.setLayoutManager(layoutManager);
        adapter_symbol = new SymbolAdapter(mContext, mList_symbol);
        recyclerView_symbol_view.setAdapter(adapter_symbol);
        //添加自定义分割线
        DividerItemDecoration divider_v = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        divider_v.setDrawable(getResources().getDrawable(R.drawable.shape_symbol_divider));
        recyclerView_symbol_view.addItemDecoration(divider_v);
        DividerItemDecoration divider_h = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        divider_h.setDrawable(getResources().getDrawable(R.drawable.shape_symbol_h_divider));
        recyclerView_symbol_view.addItemDecoration(divider_h);

        view_voice = LayoutInflater.from(mContext)
                .inflate(R.layout.writing_operate_voice_input_layout,
                        frameLayout_operate, false);
        rl_voice_view = view_voice.findViewById(R.id.rl_voice_writing_operate);
        waveView_voice_view = view_voice.findViewById(R.id.wave_view_voice_writing_operate);
        iv_voice_view = view_voice.findViewById(R.id.iv_voice_writing_operate);
        tv_voice_view = view_voice.findViewById(R.id.tv_voice_writing_operate);

        waveView_voice_view.setStyle(Paint.Style.FILL);
        waveView_voice_view.setColor(Color.parseColor("#ff9933"));

        view_image = LayoutInflater.from(mContext)
                .inflate(R.layout.writing_operate_image_layout,
                        frameLayout_operate, false);
        ll_text_recognition_view = view_image.findViewById(R.id.ll_text_recognition_writing_operate);
        ll_insert_image_view = view_image.findViewById(R.id.ll_insert_image_writing_operate);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirst) {
            isFirst = false;
            showOperateTips();
        }
    }

    /**
     * 显示操作提示
     */
    private void showOperateTips() {
        TipsUtil tipsUtil = new TipsUtil(mContext);
        tipsUtil.showTipAboveView(rl_simple_voice, "语音写作\n按住说话写作文~");
    }

    /**
     * 初始化限时比赛作文内容
     */
    private void initCompetitionWritingData() {
        new GetCompetitionWritingData(mContext).execute(GET_COMPETITION_DATA, taskId);
    }

    /**
     * 初始化比赛时间
     */
    private void initCompetitionTimeData() {
        new GetCompetitionTimeData(mContext).execute(competitionTimeUrl, taskId);
    }

    private void initData() {
        if (id.equals("")) {
            LitePal.findAllAsync(AutoSaveWritingBean.class).listen(
                    new FindMultiCallback<AutoSaveWritingBean>() {
                        @Override
                        public void onFinish(List<AutoSaveWritingBean> list) {
                            if (list.size() != 0) {
                                AutoSaveWritingBean bean = list.get(0);
                                showAutoSaveContent(bean);
                            }
                        }
                    });
        } else {
            new GetWritingData(mContext).execute(GET_WRITING_DATA, id, String.valueOf(area));
        }
    }

    /**
     * 显示自动保存内容
     *
     * @param bean
     */
    private void showAutoSaveContent(final AutoSaveWritingBean bean) {
        LitePal.deleteAll(AutoSaveWritingBean.class);
        title = bean.getTitle();
        content = bean.getContent();
        cover = bean.getCover();
        word_count = bean.getCount();
        et_title.append(title);
        updateCoverUi();
        updateContent();
    }

    /**
     * 自动保存
     */
    private void autoSave() {
        LitePal.deleteAll(AutoSaveWritingBean.class);
        String title = et_title.getText().toString();
        String text = richEditor.getContent();
        if (text.trim().length() != 0) {
            AutoSaveWritingBean bean = new AutoSaveWritingBean();
            bean.setTitle(title);
            bean.setContent(text);
            bean.setCover(cover);
            bean.setCount(word_count);
            bean.save();
        }
        autoSaveTime();
    }

    private Thread autoSaveThread;
    private boolean isResume = false;

    private Runnable autoSaveRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                sleep(60000);
                if (isResume) {
                    mHandler.sendEmptyMessage(0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 自动保存线程
     */
    private void autoSaveTime() {
        autoSaveThread = new Thread(autoSaveRunnable);
        autoSaveThread.start();
    }

    /**
     * 修改自动保存状态
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            autoSave();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    /**
     * 检查用户权限
     */
    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION_CODE);
            }
        }
    }

    private void initEvents() {
        imagePicker = new ImagePicker();

        rl_back.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        rl_summary.setOnClickListener(this);
        rl_error.setOnClickListener(this);

        rl_delete_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePractice();
            }
        });
        rl_more_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMorePractice();
            }
        });
        adapter.setOnGlossaryFlexBoxItemClickListen(
                new GlossaryFlexBoxAdapter.OnGlossaryFlexBoxItemClick() {
                    @Override
                    public void onClick(View view) {
                        int pos = recyclerView_practice.getChildAdapterPosition(view);
                        String word = mList.get(pos);
                        long essayId = -1;
                        String title = mList.get(pos);
                        turnToWordExplain(essayId, word, title);
                    }
                });

        rl_root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //比较Activity根布局与当前布局的大小
                        int heightDiff = rl_root.getRootView().getHeight() - rl_root.getHeight();

                        if (heightDiff > 200 || isShowOperate) {
                            showKeyboardOperate(true);
                        } else {
                            //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                            showKeyboardOperate(false);
                        }
                    }
                });

        rl_simple_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                        REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                            } else {
                                startSpeech();
                            }
                        } else {
                            startSpeech();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isSpeeching) {
                            stopSpeech();
                        }
                        break;
                }
                return true;
            }
        });
        et_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowOperate = false;
                hideOperate();
            }
        });
        et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editHasFocus = hasFocus;
                isShowOperate = false;
                isShowVoice = false;
                isShowSymbol = false;
                iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                frameLayout_operate.setVisibility(View.GONE);
                frameLayout_operate.removeAllViews();
            }
        });
        et_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    richEditor.getLastFocusEdit().requestFocus();
                    return true;
                }
                return false;
            }
        });
        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (word_count != 0 && s.toString().trim().length() != 0) {
                    tv_commit.setTextColor(Color.parseColor("#ff9933"));
                }
            }
        });
        rl_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(0, -1);
                hideOperate();
            }
        });
        iv_delete_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCover();
            }
        });
        iv_change_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(0, -1);
            }
        });
        richEditor.setOnTouchListen(new RichEditor.OnTouchListen() {
            @Override
            public void onTouch() {
                isShowOperate = false;
                hideOperate();
            }
        });
        richEditor.setOnImageDeleteClick(new RichEditor.OnImageDeleteClick() {
            @Override
            public void onImageDelete(int index) {
                deleteImage(index);
            }
        });
        richEditor.setOnImageChangeClick(new RichEditor.OnImageChangeClick() {
            @Override
            public void onImageChange(int index) {
                chooseImage(1, index);
                hideOperate();
            }
        });
        richEditor.setOnTextChangeListen(new RichEditor.OnTextChangeListen() {
            @Override
            public void onTextChange(int size) {
                word_count = size;
                if (size == 0) {
                    tv_hint.setVisibility(View.VISIBLE);
                } else {
                    if (tv_hint.getVisibility() == View.VISIBLE) {
                        tv_hint.setVisibility(View.GONE);
                    }
                }
                String count_tips = "字数：" + String.valueOf(word_count);
                tv_word_count.setText(count_tips);
                if (word_count != 0 && et_title.getText().toString().trim().length() != 0) {
                    tv_commit.setTextColor(Color.parseColor("#ff9933"));
                } else {
                    tv_commit.setTextColor(Color.parseColor("#999999"));
                }

                if (word_count > 8000) {
                    showTip("请尽量不要输入超过8000个字，避免发生未知错误~");
                }
            }
        });
        rl_simple_symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSymbol();
            }
        });
        rl_simple_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                chooseImage(false, -1);
//                hideOperate();
                showImage();
            }
        });
        rl_bottom_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                hideOperate();
            }
        });
        rl_bottom_symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSymbol();
            }
        });
        rl_bottom_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoice();
            }
        });
        rl_bottom_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                chooseImage(false, -1);
//                hideOperate();
                showImage();
            }
        });
        rl_bottom_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(false);
                hideOperate();
            }
        });
    }

    /**
     * 前往词语解释
     *
     * @param essayId
     * @param word
     * @param title
     */
    private void turnToWordExplain(long essayId, String word, String title) {
        Intent intent = new Intent(mContext, GlossaryWordExplainActivity.class);
        intent.putExtra("glossaryId", essayId);
        intent.putExtra("words", word);
        intent.putExtra("essayTitle", "");
        intent.putExtra("glossaryTitle", title);
        startActivity(intent);
    }

    /**
     * 展开或收起刻意练习
     */
    private void showMorePractice() {
        if (isShowMore) {
            if (tv_practice.getVisibility() == View.VISIBLE) {
                tv_practice.setVisibility(View.GONE);
            }
            if (recyclerView_practice.getVisibility() == View.VISIBLE) {
                recyclerView_practice.setVisibility(View.GONE);
            }
            iv_more_practice.setImageResource(R.drawable.ic_expand_more_black_24dp);
            isShowMore = false;
        } else {
            if (isGlossaryPractice) {
                recyclerView_practice.setVisibility(View.VISIBLE);
            } else {
                tv_practice.setVisibility(View.VISIBLE);
            }
            iv_more_practice.setImageResource(R.drawable.ic_expand_less_black_36dp);
            isShowMore = true;
        }
    }

    /**
     * 删除刻意练习
     */
    private void deletePractice() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);

                        tv_title.setText("是否退出刻意训练");
                        tv_content.setText("退出后将不再展示相关内容");
                        tv_left.setText("取消");
                        tv_right.setText("确定");

                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ll_practice.setVisibility(View.GONE);
                                practice = "";
                                mList.clear();
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                    }
                })
                .setMargin(64)
                .setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    /**
     * 显示图片操作
     */
    private void showImage() {
        if (isShowOperate) {
            if (isShowImage) {
                frameLayout_operate.setVisibility(View.GONE);
                iv_simple_image.setImageResource(R.drawable.icon_write_bottom_image);
                iv_bottom_image.setImageResource(R.drawable.icon_write_bottom_image);
                frameLayout_operate.removeAllViews();
                isShowImage = false;
                isShowOperate = false;
                showKeyboardOperate(false);
            } else {
                if (isShowVoice || isShowSymbol) {
                    if (isShowSymbol) {
                        iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                        iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                    }
                    frameLayout_operate.removeAllViews();
                }
                frameLayout_operate.addView(view_image);
                iv_simple_image.setImageResource(R.drawable.icon_write_bottom_image_selected);
                iv_bottom_image.setImageResource(R.drawable.icon_write_bottom_image_selected);
                isShowImage = true;
                isShowSymbol = false;
                isShowVoice = false;
                editRequestFocus();
            }
        } else {
            isShowOperate = true;
            iv_simple_image.setImageResource(R.drawable.icon_write_bottom_image_selected);
            iv_bottom_image.setImageResource(R.drawable.icon_write_bottom_image_selected);
            frameLayout_operate.addView(view_image);
            frameLayout_operate.setVisibility(View.VISIBLE);
            isShowImage = true;
            isShowSymbol = false;
            isShowVoice = false;
            editRequestFocus();
        }
        showKeyboard(false);
    }

    /**
     * 显示语音输入
     */
    private void showVoice() {
        if (isShowVoice) {
            frameLayout_operate.setVisibility(View.GONE);
            frameLayout_operate.removeAllViews();
            isShowOperate = false;
            isShowVoice = false;
            showKeyboardOperate(false);
        } else {
            isShowOperate = true;
            if (isShowSymbol) {
                iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                frameLayout_operate.removeAllViews();
                isShowSymbol = false;
            } else if (isShowImage) {
                iv_simple_image.setImageResource(R.drawable.icon_write_bottom_image);
                iv_bottom_image.setImageResource(R.drawable.icon_write_bottom_image);
                frameLayout_operate.removeAllViews();
                isShowImage = false;
            } else {
                showKeyboard(false);
            }
            frameLayout_operate.addView(view_voice);
            frameLayout_operate.setVisibility(View.VISIBLE);
            isShowVoice = true;
            editRequestFocus();
        }
    }

    /**
     * 显示符号列表
     */
    private void showSymbol() {
        if (isShowOperate) {
            if (isShowSymbol) {
                frameLayout_operate.setVisibility(View.GONE);
                iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
                frameLayout_operate.removeAllViews();
                isShowSymbol = false;
                isShowOperate = false;
                showKeyboardOperate(false);
            } else {
                if (isShowImage) {
                    iv_simple_image.setImageResource(R.drawable.icon_write_bottom_image);
                    iv_bottom_image.setImageResource(R.drawable.icon_write_bottom_image);
                    frameLayout_operate.removeAllViews();
                    isShowImage = false;
                }
                if (isShowVoice) {
                    frameLayout_operate.removeAllViews();
                    isShowVoice = false;
                }
                frameLayout_operate.addView(view_symbol);
                iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_selected);
                iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_selected);
                isShowSymbol = true;
                editRequestFocus();
            }
        } else {
            isShowOperate = true;
            iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_selected);
            iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_selected);
            frameLayout_operate.addView(view_symbol);
            frameLayout_operate.setVisibility(View.VISIBLE);
            isShowSymbol = true;
            editRequestFocus();
        }
        showKeyboard(false);
    }

    /**
     * 输入框获取焦点
     */
    private void editRequestFocus() {
        if (!editHasFocus) {
            richEditor.getLastFocusEdit().setFocusable(true);
            richEditor.getLastFocusEdit().setFocusableInTouchMode(true);
            richEditor.getLastFocusEdit().requestFocus();
        }
    }

    /**
     * 显示软键盘
     */
    private void showKeyboard(boolean show) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (show) {
                if (editHasFocus) {
                    imm.showSoftInput(et_title, InputMethodManager.SHOW_FORCED);
                } else {
                    imm.showSoftInput(richEditor.getLastFocusEdit(), InputMethodManager.SHOW_FORCED);
                }
            } else {
                if (editHasFocus) {
                    imm.hideSoftInputFromWindow(et_title.getWindowToken(), 0);
                } else {
                    imm.hideSoftInputFromWindow(richEditor.getWindowToken(), 0);
                }
            }
        }
    }

    /**
     * 显示或隐藏软键盘操作栏
     */
    private void showKeyboardOperate(boolean isKeyboardShow) {
        if (isKeyboardShow) {  //软件盘显示
            showOperate();
        } else {  //软键盘隐藏
            hideOperate();
        }
    }

    /**
     * 显示操作栏
     */
    private void showOperate() {
        ll_keyboard_operate.setVisibility(View.VISIBLE);
        ll_simple_operate.setVisibility(View.GONE);
        rl_simple_voice.setVisibility(View.GONE);
    }

    /**
     * 隐藏操作栏
     */
    private void hideOperate() {
        isShowOperate = false;
        isShowVoice = false;
        isShowSymbol = false;
        isShowImage = false;
        iv_simple_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
        iv_bottom_symbol.setImageResource(R.drawable.icon_write_bottom_symbol_normal);
        iv_simple_image.setImageResource(R.drawable.icon_write_bottom_image);
        iv_bottom_image.setImageResource(R.drawable.icon_write_bottom_image);
        ll_simple_operate.setVisibility(View.VISIBLE);
        ll_keyboard_operate.setVisibility(View.GONE);
        rl_simple_voice.setVisibility(View.VISIBLE);
        frameLayout_operate.setVisibility(View.GONE);
        frameLayout_operate.removeAllViews();
    }

    /**
     * 删除封面图
     */
    private void deleteCover() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_yes_not_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_yes_not_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_yes_not_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_yes_not_dialog);
                        tv_title.setText("确定要删除封面图吗？");
                        tv_left.setText("确定");
                        tv_right.setText("取消");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iv_cover.setVisibility(View.GONE);
                                cover = "";
                                iv_cover_placeholder.setVisibility(View.VISIBLE);
                                tv_cover_placeholder.setVisibility(View.VISIBLE);
                                iv_delete_cover.setVisibility(View.GONE);
                                iv_change_cover.setVisibility(View.GONE);
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
                .setMargin(64)
                .setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    /**
     * 删除图片
     *
     * @param index
     */
    private void deleteImage(int index) {
        mIndex = index;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_yes_not_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_yes_not_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_yes_not_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_yes_not_dialog);
                        tv_title.setText("确定要删除这张图片吗？");
                        tv_left.setText("确定");
                        tv_right.setText("取消");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                richEditor.removeImageViewAtIndex(mIndex);
                                dialog.dismiss();
                                mIndex = -1;
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                mIndex = -1;
                            }
                        });
                    }
                })
                .setMargin(64)
                .setShowBottom(false)
                .show(getSupportFragmentManager());
    }

    /**
     * 选择图片
     */
    private void chooseImage(int sort, int index) {
        chooseImageSort = sort;
        mIndex = index;
        // 设置标题
        imagePicker.setTitle("选择图片");
        // 设置是否裁剪图片
        imagePicker.setCropImage(true);
        imagePicker.startChooser(mContext, imagePickerCallback);
    }

    /**
     * 图片选择器回调
     */
    private ImagePicker.Callback imagePickerCallback = new ImagePicker.Callback() {
        @Override
        public void onPickImage(Uri imageUri) {

        }

        @Override
        public void onCropImage(Uri imageUri) {
            super.onCropImage(imageUri);
            if (chooseImageSort == 0) {
                uploadCover(imageUri);
            } else if (chooseImageSort == 2) {
                uploadTextImage(imageUri);
            } else {
                uploadImage(imageUri);
            }
        }

        @Override
        public void cropConfig(CropImage.ActivityBuilder builder) {
            super.cropConfig(builder);
            if (chooseImageSort == 0) {
                builder.setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setBorderLineThickness(1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(16, 9);
            } else if (chooseImageSort == 2) {
                builder.setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setBorderLineThickness(1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setMinCropWindowSize(DensityUtil.dip2px(mContext, 50),
                                DensityUtil.dip2px(mContext, 50))
                        .setFixAspectRatio(false);
            } else {
                builder.setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setBorderLineThickness(1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setMinCropWindowSize(DensityUtil.getScreenWidth(mContext),
                                DensityUtil.dip2px(mContext, 150))
                        .setFixAspectRatio(false);
            }
        }

        @Override
        public void onPermissionDenied(int requestCode, String[] permissions, int[] grantResults) {
            super.onPermissionDenied(requestCode, permissions, grantResults);
            showTip("没有选择图片或拍照的权限");
        }
    };

    /**
     * 图片转文字
     *
     * @param imageUri
     */
    private void uploadTextImage(Uri imageUri) {
        String filePath = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        Intent intent = new Intent(mContext, ImageToTextActivity.class);
        intent.putExtra("imagePath", filePath);
        startActivityForResult(intent, IMAGE_TO_TEXT);
    }

    /**
     * 上传封面
     */
    private void uploadCover(Uri imageUri) {
        String filePath = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        showTip("正在上传封面图，请稍后...");
        new UploadCover(mContext).execute(coverUrl, filePath);
    }

    /**
     * 上传图片
     */
    private void uploadImage(Uri imageUri) {
        String filePath = ImageUtils.getImageAbsolutePath(mContext, imageUri);
        showTip("正在上传图片，请稍后...");
        new UploadImage(mContext).execute(imageUrl, filePath);
    }

    private void initView() {
        rl_root = findViewById(R.id.rl_root_writing);
        rl_top = findViewById(R.id.rl_top_writing);
        rl_back = findViewById(R.id.rl_back_writing);
        tv_commit = findViewById(R.id.tv_commit_writing);

        et_title = findViewById(R.id.et_title_writing);
        tv_time = findViewById(R.id.tv_time_writing);
        tv_word_count = findViewById(R.id.tv_word_count_writing);
        tv_countdown = findViewById(R.id.tv_countdown_writing);
        rl_summary = findViewById(R.id.rl_summary_writing);
        rl_error = findViewById(R.id.rl_error_writing);

        tv_time.setText(time);
        InputFilter inputFilter = new InputFilter() {
            Pattern emoji = Pattern.compile(
                    "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                Matcher matcher = emoji.matcher(source);
                if (!matcher.find()) {
                    return null;
                } else {
                    showTip("作文不支持输入表情");
                    return "";
                }
            }
        };

        et_title.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(15)});

        rl_cover = findViewById(R.id.rl_cover_writing);
        iv_cover = findViewById(R.id.iv_cover_writing);
        iv_delete_cover = findViewById(R.id.iv_delete_cover_writing);
        iv_change_cover = findViewById(R.id.iv_change_cover_writing);
        iv_cover_placeholder = findViewById(R.id.iv_cover_placeholder_writing);
        tv_cover_placeholder = findViewById(R.id.tv_cover_placeholder_writing);

        ll_practice = findViewById(R.id.ll_practice_writing);
        rl_delete_practice = findViewById(R.id.rl_delete_practice_writing);
        rl_more_practice = findViewById(R.id.rl_more_practice_writing);
        iv_more_practice = findViewById(R.id.iv_more_practice_writing);
        tv_practice = findViewById(R.id.tv_practice_writing);
        recyclerView_practice = findViewById(R.id.recycler_view_practice_writing);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mContext, FlexDirection.ROW,
                FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        recyclerView_practice.setLayoutManager(layoutManager);
        adapter = new GlossaryFlexBoxAdapter(mList, mContext, false);
        recyclerView_practice.setAdapter(adapter);

        richEditor = findViewById(R.id.richEditor_writing);
        tv_hint = findViewById(R.id.tv_hint_writing);

        ll_keyboard_operate = findViewById(R.id.ll_bottom_operate_keyboard_writing);
        rl_bottom_keyboard = findViewById(R.id.rl_keyboard_writing);
        rl_bottom_symbol = findViewById(R.id.rl_symbol_writing);
        iv_bottom_symbol = findViewById(R.id.iv_symbol_writing);
        rl_bottom_voice = findViewById(R.id.rl_voice_writing);
        rl_bottom_image = findViewById(R.id.rl_image_writing);
        iv_bottom_image = findViewById(R.id.iv_image_writing);
        rl_bottom_hide = findViewById(R.id.rl_hide_writing);

        ll_simple_operate = findViewById(R.id.ll_simple_operate_writing);
        rl_simple_symbol = findViewById(R.id.rl_symbol_simple_writing);
        iv_simple_symbol = findViewById(R.id.iv_symbol_simple_writing);
        rl_simple_image = findViewById(R.id.rl_image_simple_writing);
        iv_simple_image = findViewById(R.id.iv_image_simple_writing);
        rl_simple_voice = findViewById(R.id.rl_voice_input_writing);
        waveView_simple = findViewById(R.id.wave_view_input_writing);
        iv_simple_voice = findViewById(R.id.iv_voice_input_writing);

        frameLayout_operate = findViewById(R.id.frame_operate_writing);

        GlideUtils.loadImageWithNoOptions(mContext, R.drawable.image_write_cover_bg,
                iv_cover_placeholder);

        ll_keyboard_operate.setVisibility(View.GONE);
        frameLayout_operate.setVisibility(View.GONE);

        waveView_simple.setStyle(Paint.Style.FILL);
        waveView_simple.setColor(Color.parseColor("#ff9933"));

        initPracticeData(getIntent());

        //有的手机默认不给输入框默认焦点
        //手动给标题输入框请求焦点
        et_title.requestFocus();
    }

    private boolean isGlossaryPractice = false;  //是否是生词的刻意练习

    /**
     * 初始化刻意练习数据
     */
    private void initPracticeData(Intent intent) {

        isGlossaryPractice = false;

        //-1表示没有，0表示已有的情况下没有更换内容，1表示字符串，2表示生词列表
        int type = intent.getIntExtra("practiceType", -1);

        if (type == -1) {
            ll_practice.setVisibility(View.GONE);
        } else if (type == 1) {
            practice = intent.getStringExtra("practice");
            tv_practice.setText(practice);
            tv_practice.setVisibility(View.VISIBLE);
            recyclerView_practice.setVisibility(View.GONE);
            mList.clear();
            adapter.notifyDataSetChanged();
            ll_practice.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            String str = intent.getStringExtra("practice");
            updateGlossaryData(str);
            recyclerView_practice.setVisibility(View.VISIBLE);
            tv_practice.setVisibility(View.GONE);
            practice = "";
            ll_practice.setVisibility(View.VISIBLE);
        }

        isShowMore = true;
        iv_more_practice.setImageResource(R.drawable.ic_expand_less_black_36dp);

    }

    /**
     * 更新生词刻意练习
     *
     * @param str
     */
    private void updateGlossaryData(String str) {
        try {
            JSONArray array = new JSONArray(str);
            mList.clear();
            for (int i = 0; i < array.length(); i++) {
                mList.add(array.getString(i));
            }
            adapter.notifyDataSetChanged();
            isGlossaryPractice = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_writing:
                if (type == 2) {
                    showCompetitionCancelDialog();
                } else {
                    closeMakeSure();
                }
                break;
            case R.id.tv_commit_writing:
                if (type == 2) {
                    title = et_title.getText().toString();
                    content = richEditor.getContent();
                    if (title.trim().length() == 0) {
                        showTip("标题不能为空");
                    } else if (word_count == 0) {
                        if (content.trim().length() == 0) {
                            showTip("内容不能为空");
                        } else {
                            showTip("内容不能全部为图片");
                        }
                    } else {
                        showCommitCompetitionDialog();
                    }
                } else {
                    clickCommit();
                }
                break;
            case R.id.rl_summary_writing:
                if (NewMainActivity.STUDENT_ID == -1) {
                    turnToLogin();
                } else {
                    turnToMaterialDetail();
                }
                break;
            case R.id.rl_error_writing:
                errorCorrection();
                break;
        }
    }

    /**
     * 前往纠错
     */
    private void errorCorrection() {
        title = et_title.getText().toString();
        content = richEditor.getContent();
        if (title.trim().length() == 0) {
            showTip("作文标题不能为空");
        } else if (content.length() == 0) {
            showTip("作文内容不能为空");
        } else if (word_count == 0) {
            showTip("作文内容不能全为图片");
        } else if (word_count < 15) {
            showTip("文章内容小于15个字，无法纠错");
        } else {
            Intent intent = new Intent(mContext, ErrorCorrectionActivity.class);
            intent.putExtra("cover", cover);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("wordNum", word_count);
            startActivityForResult(intent, ERROR_CORRECTION);
        }
    }

    /**
     * 前往登录
     */
    private void turnToLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    /**
     * 显示比赛退出提示
     */
    private void showCompetitionCancelDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_title_content_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                        tv_title.setText("是否保存为草稿");
                        tv_content.setText("比赛作文有限时要求，草稿箱的作文不可以参加比赛！");
                        tv_left.setText("放弃");
                        tv_right.setText("存稿");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LitePal.deleteAll(AutoSaveWritingBean.class);
                                finish();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                keepEditor();
                                saveWritingToDraft();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(true)
                .setMargin(40)
                .show(getSupportFragmentManager());
    }

    /**
     * 显示是否确认提交比赛提示
     */
    private void showCommitCompetitionDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_writing_yes_not_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_yes_not_dialog);
                        TextView tv_left = holder.getView(R.id.tv_left_yes_not_dialog);
                        TextView tv_right = holder.getView(R.id.tv_right_yes_not_dialog);
                        tv_title.setText("是否确定提交");
                        tv_left.setText("返回编辑");
                        tv_right.setText("确定");
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                submitToCompetition(taskId);
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setMargin(40)
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    /**
     * 查看素材
     */
    private void turnToMaterialDetail() {
        Intent intent = new Intent(mContext, NotesSummaryActivity.class);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(intent, REQUEST_NOTES, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivityForResult(intent, REQUEST_NOTES);
        }
    }

    /**
     * 点击了完成按钮
     */
    private void clickCommit() {
        title = et_title.getText().toString();
        content = richEditor.getContent();
        //如果标题或内容都为空，提示用户
        if (title.trim().length() == 0) {
            showTip("标题不能为空");
        } else if (word_count == 0) {
            if (content.trim().length() == 0) {
                showTip("内容不能为空");
            } else {
                showTip("内容不能全部为图片");
            }
        } else {
            if (NewMainActivity.STUDENT_ID == -1) {
                turnToLogin();
            } else {
                turnToTemplateChoose();
            }
        }
    }

    /**
     * 前往选择模板
     */
    private void turnToTemplateChoose() {
        Intent intent = new Intent(mContext, WritingTemplateChooseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("cover", cover);
        intent.putExtra("count", word_count);
        intent.putExtra("type", type);
        intent.putExtra("area", area);
        intent.putExtra("taskId", taskId);
        intent.putExtra("format", format);
        intent.putExtra("isCorrection", isCorrection);
        startActivityForResult(intent, TURN_TO_SUBMIT_REVIEW_CODE);
    }

    /**
     * 提交到活动
     */
    private void submitToCompetition(String taskId) {
        new SubmitCompetition(mContext).execute(submitCompetitionUrl, taskId,
                String.valueOf(word_count));
    }

    /**
     * 是否确认关闭
     */
    private void closeMakeSure() {
        title = et_title.getText().toString();
        content = richEditor.getContent();
        //如果标题和内容都为空，直接退出写作界面
        if (title.trim().length() == 0 && content.trim().length() == 0) {
            finish();
        } else {
            NiceDialog.init()
                    .setLayoutId(R.layout.dialog_writing_cancel)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                            TextView textView = holder.getView(R.id.tv_cancel_tips_writing_dialog);
                            Button btn_save_draft = holder.getView(R.id.btn_save_writing_draft_dialog);
                            Button btn_give_up = holder.getView(R.id.btn_give_up_writing_draft_dialog);
                            if (!id.equals("")) {
                                textView.setText("是否存稿");
                            }
                            btn_save_draft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveWritingToDraft();
                                    dialog.dismiss();
                                }
                            });
                            btn_give_up.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LitePal.deleteAll(AutoSaveWritingBean.class);
                                    DataUtil.isDraftNeedRefresh = false;
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    })
                    .setMargin(40)
                    .setShowBottom(false)
                    .show(getSupportFragmentManager());
        }
    }

    /**
     * 保存作文内容到草稿箱
     */
    private void saveWritingToDraft() {
        content = richEditor.getContent();
        title = et_title.getText().toString();
        if (content.trim().length() == 0 || title.trim().length() == 0) {
            if (word_count == 0) {
                showTip("内容不能全是图片");
            } else {
                showTip("标题和内容不能为空");
            }
        } else {
            if (NewMainActivity.STUDENT_ID == -1) {
                turnToLogin();
            } else {
                showTip("正在保存作文内容到草稿箱...");
                new SaveToDraft(mContext).execute(SAVE_WRITING_DRAFT,
                        content, title, String.valueOf(NewMainActivity.STUDENT_ID),
                        String.valueOf(word_count), cover, String.valueOf(format));
            }
        }
    }

    /**
     * 开始听写
     */
    private void startSpeech() {
        isSpeeching = true;

        waveView_simple.start();
        iv_simple_voice.setImageResource(R.drawable.icon_write_bottom_voice_pause);

        if (frameLayout_operate.getVisibility() == View.VISIBLE) {
            waveView_voice_view.start();
            iv_voice_view.setImageResource(R.drawable.icon_write_bottom_voice_pause);
            tv_voice_view.setText("语音识别中");
        }

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        if (!editHasFocus) {
            speechRecognizerUtil.setParams("1");
        }

        int ret = speechRecognizerUtil.startVoice(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败");
        } else {
            showTip("正在听写");
        }
    }

    /**
     * 停止听写
     */
    private void stopSpeech() {
        speechRecognizerUtil.stopVoice();

        isSpeeching = false;
        waveView_simple.stop();
        iv_simple_voice.setImageResource(R.drawable.icon_write_bottom_voice_input);

        if (frameLayout_operate.getVisibility() == View.VISIBLE) {
            waveView_voice_view.stop();
            iv_voice_view.setImageResource(R.drawable.icon_write_bottom_voice_input);
            tv_voice_view.setText("语音输入");
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败");
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
//            if (error.getErrorCode() == 14002) {
//                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
//            } else
            if (error.getErrorCode() == 10118) {
                showTip("您好像没有说话哦~");
                checkUserPermission();
            } else if (error.getErrorCode() == 20001) {
                showTip("请检查网络是否连接~");
            }
            stopSpeech();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            stopSpeech();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            showResult(results.getResultString());
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 显示听写结果
     *
     * @param resultString
     */
    private void showResult(String resultString) {
        String text = JsonParser.parseIatResult(resultString);
        Editable editable;
        if (editHasFocus) {
            int index = et_title.getSelectionStart();
            editable = et_title.getEditableText();
            if (text.contains("。")) {
                text = text.replace("。", "");
            }
            if (text.contains("？")) {
                text = text.replace("？", "");
            }
            if (text.contains("！")) {
                text = text.replace("！", "");
            }
            if (index < 0 || index >= editable.length()) {
                editable.append(text);
            } else {
                editable.insert(index, text);
            }
        } else {
            richEditor.appendText(text);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_CODE) {
            if (!verifyPermissions(grantResults)) {
                showTip("录音权限被拒绝");
            }
        } else {
            imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TURN_TO_SUBMIT_REVIEW_CODE) {
            if (data != null) {
                boolean isSubmit = data.getBooleanExtra("submit", false);
                if (isSubmit) {
                    LitePal.deleteAll(AutoSaveWritingBean.class);
                    Intent intent = new Intent();
                    intent.putExtra("submit", true);
                    setResult(0, intent);
                    finish();
                }
            }
        } else if (requestCode == REQUEST_NOTES) {
            if (data != null) {
                initPracticeData(data);
            }
        } else if (requestCode == ERROR_CORRECTION) {
            if (data != null) {
                content = data.getStringExtra("content");
                richEditor.clearData(mContext);
                updateContent();
            }
        } else if (requestCode == IMAGE_TO_TEXT) {
            if (data != null) {
                String text = data.getStringExtra("text");
                richEditor.appendText(text);
            }
        } else {
            imagePicker.onActivityResult(this, requestCode, resultCode, data);
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
     * 弹出吐丝
     *
     * @param tips
     */
    private void showTip(String tips) {
        MyToastUtil.showToast(mContext, tips);
    }

    @Override
    public void onBackPressed() {
        if (type == 2) {
            showCompetitionCancelDialog();
        } else {
            closeMakeSure();
        }
    }

    /**
     * 分析获取作文内容的数据
     *
     * @param s
     */
    private void analyzeWritingData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                title = object.getString("article");
                content = object.getString("content");
                if (object.getString("cover").equals("")
                        || object.getString("cover").equals("null")) {
                    cover = "";
                } else {
                    cover = object.getString("cover");
                }
                format = object.optInt("format", 1);
                String task = "";
                if (area == 5 || area == 2) {
                    task = object.getString("taskId");
                }
                status = object.optInt("status", -1);
                et_title.append(title);
                updateCoverUi();
                updateContent();
                if (!taskId.equals("")) {
                    if (status == 1) {
                        taskId = task;
                    } else {
                        taskId = "";
                    }
                }
                if (type != 5) {
                    type = object.optInt("type", 5);
                }
                if (area != 5 && type != 2) {
                    taskId = "";
                    type = 5;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据
     */
    private void updateContent() {
        richEditor.setContent(content);
//        try {
//            JSONArray array = new JSONArray(content);
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject object = array.getJSONObject(i);
//                String type = object.getString("type");
//                String s = object.getString("content");
//                if (type.equals("image")) {
//                    richEditor.insertImage(s);
//                } else {
//                    richEditor.appendText(s);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            richEditor.appendText(content);
//        }
    }

    /**
     * 分析获取作文内容的数据
     *
     * @param s
     */
    private void analyzeWritingCompetitionData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                id = object.getString("compositionId");
                title = object.getString("article");
                content = object.getString("content");
                cover = object.getString("cover");
                taskId = object.getString("taskId");
                type = object.optInt("type", 5);
                status = object.optInt("status", -1);
                competitionEndTime = object.getString("stopTime");
                et_title.append(title);
                updateContent();
                updateCoverUi();
                if (type == 2) {
                    showCountdownView();
                }
            } else if (400 == jsonObject.optInt("status", -1) && type == 2) {
                //未开始写,无定时器
                initCompetitionTimeData();
            } else if (600 == jsonObject.optInt("status", -1) && type == 2) {
                //时间用完
                showCompetitionFinishedDialog("比赛时间已到", "您不能继续编辑作文内容", false);
            } else if (700 == jsonObject.optInt("status", -1) && type == 2) {
                //比赛结束
                showCompetitionFinishedDialog("比赛时间已到", "您不能继续编辑作文内容", false);
            } else if (800 == jsonObject.optInt("status", -1) && type == 2) {
                //定时器已开启，但内容未写
                JSONObject json = jsonObject.getJSONObject("data");
                JSONObject object = json.getJSONObject("matchTimer");
                taskId = object.getString("taskId");
                competitionEndTime = object.getString("stopTime");
                showCountdownView();
            } else {
                errorWritingCompetitionData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorWritingCompetitionData();
        }
    }

    /**
     * 显示比赛结束或无剩余写作时间对话框
     */
    private void showCompetitionFinishedDialog(final String title, final String content, final boolean keepEditor) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_single_choose_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_single_choose_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_single_choose_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_single_choose_dialog);
                        tv_title.setText(title);
                        tv_content.setText(content);
                        tv_sure.setText("知道了");
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (keepEditor) {
                                    keepEditor();
                                    dialog.dismiss();
                                } else {
                                    finish();
                                }
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(40)
                .show(getSupportFragmentManager());
    }

    /**
     * 转为普通作文继续编辑
     */
    private void keepEditor() {
        type = 5;
        taskId = "";
        id = "";
        area = 5;
        isCompetitionWriting = false;
        isFromCompetitionH5 = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timer_seconds != null) {
            timer_seconds.cancel();
            timer_seconds = null;
        }
        tv_countdown.setVisibility(View.GONE);
    }

    /**
     * 获取比赛作文内容失败
     */
    private void errorWritingCompetitionData() {

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
        LitePal.deleteAll(AutoSaveWritingBean.class);
        showTip("保存成功！");
        Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("cover", cover);
        intent.putExtra("count", word_count);
        intent.putExtra("area", 5);
        intent.putExtra("index", 0);
        startActivity(intent);
        finish();
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
                LitePal.deleteAll(AutoSaveWritingBean.class);
                Intent intent = new Intent(mContext, WritingOperateResultActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("cover", cover);
                intent.putExtra("count", word_count);
                intent.putExtra("area", 5);
                intent.putExtra("index", 4);
                startActivityForResult(intent, 0);
                finish();
            } else if (700 == jsonObject.optInt("status", -1)) {
                competitionHasFinished();
            } else {
                errorSubmitCompetition();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errorSubmitCompetition();
        }
    }

    /**
     * 活动已结束
     */
    private void competitionHasFinished() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timer_seconds != null) {
            timer_seconds.cancel();
            timer_seconds = null;
        }
        if (type == 2) {
            showCompetitionFinishDialog();
        }
    }

    /**
     * 显示比赛结束对话框
     */
    private void showCompetitionFinishDialog() {
        title = et_title.getText().toString();
        content = richEditor.getContent();
        if (content.trim().isEmpty()) {  //没有填写内容，无法参加比赛
            showCompetitionFinishedDialog("比赛时间已到", "您的正文为空，不能参加此次活动，您可作为日常写作继续编辑。", true);
        } else if (title.trim().isEmpty()) {  //标题未写，弹出添加标题的对话框
            showAddWritingTitleDialog();
        } else {  //一切ok，提交比赛
            NiceDialog.init()
                    .setLayoutId(R.layout.dialog_title_content_choose_layout)
                    .setConvertListener(new ViewConvertListener() {
                        @Override
                        protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                            TextView tv_title = holder.getView(R.id.tv_title_choose_dialog);
                            TextView tv_content = holder.getView(R.id.tv_content_choose_dialog);
                            TextView tv_left = holder.getView(R.id.tv_left_choose_dialog);
                            TextView tv_right = holder.getView(R.id.tv_right_choose_dialog);
                            tv_title.setText("比赛时间已到");
                            tv_content.setText("是否提交");
                            tv_left.setText("存稿");
                            tv_right.setText("提交");
                            tv_left.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    type = 5;
                                    taskId = "";
                                    saveWritingToDraft();
                                }
                            });
                            tv_right.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    submitToCompetition(taskId);
                                }
                            });
                        }
                    })
                    .setShowBottom(false)
                    .setOutCancel(false)
                    .setMargin(40)
                    .show(getSupportFragmentManager());
        }
    }

    /**
     * 显示添加作文标题对话框
     */
    private void showAddWritingTitleDialog() {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_add_writing_title_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        TextView tv_title = holder.getView(R.id.tv_title_add_writing_title_dialog);
                        TextView tv_content = holder.getView(R.id.tv_content_add_writing_title_dialog);
                        final EditText et_writing = holder.getView(R.id.et_title_add_writing_title_dialog);
                        TextView tv_sure = holder.getView(R.id.tv_sure_add_writing_title_dialog);
                        tv_title.setText("标题不能为空！");
                        tv_content.setText("请输入标题内容");
                        et_writing.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                        tv_sure.setText("确定");
                        tv_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (et_writing.getText().toString().trim().length() == 0) {
                                    showTip("标题不能为空");
                                } else {
                                    title = et_writing.getText().toString();
                                    et_title.setText(title);
                                    submitToCompetition(taskId);
                                }
                            }
                        });
                    }
                })
                .setShowBottom(false)
                .setOutCancel(false)
                .setMargin(40)
                .show(getSupportFragmentManager());
    }

    /**
     * 提交到活动、比赛失败
     */
    private void errorSubmitCompetition() {
        showTip("参加活动失败，请稍后再试");
    }

    /**
     * 无网络连接
     */
    private void noConnection() {
        showTip("获取数据失败，请连接网络后重试！");
    }

    /**
     * 获取作文内容
     */
    private static class GetWritingData
            extends WeakAsyncTask<String, Void, String, WritingActivity> {

        protected GetWritingData(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", strings[1]);
                jsonObject.put("studentId", NewMainActivity.STUDENT_ID);
                jsonObject.put("area", Integer.valueOf(strings[2]));
                jsonObject.put("isNew", true);
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
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.noConnection();
            } else {
                activity.analyzeWritingData(s);
            }
        }
    }

    /**
     * 获取限时比赛作文内容
     */
    private static class GetCompetitionWritingData
            extends WeakAsyncTask<String, Void, String, WritingActivity> {

        protected GetCompetitionWritingData(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("taskId", strings[1]);
                jsonObject.put("studentId", NewMainActivity.STUDENT_ID);
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
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.errorWritingCompetitionData();
            } else {
                activity.analyzeWritingCompetitionData(s);
            }
        }
    }

    /**
     * 保存到草稿箱
     */
    private static class SaveToDraft
            extends WeakAsyncTask<String, Void, String, WritingActivity> {

        protected SaveToDraft(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("content", strings[1]);
                jsonObject.put("article", strings[2]);
                jsonObject.put("studentId", strings[3]);
                jsonObject.put("wordsNum", Integer.valueOf(strings[4]));
                if (activity.area != 2 && activity.type != 2) {
                    if (!activity.taskId.equals("") && !activity.taskId.equals("null")) {
                        jsonObject.put("taskId", activity.taskId);
                    }
                }
                if (activity.area == 5 && activity.type == 5 && !activity.id.equals("")) {
                    jsonObject.put("id", activity.id);
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
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.saveFailed();
            } else {
                activity.analyzeSaveToDraftData(s);
            }
        }
    }

    /**
     * 提交到比赛、活动
     */
    private static class SubmitCompetition
            extends WeakAsyncTask<String, Void, String, WritingActivity> {

        protected SubmitCompetition(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                if (activity.area == 5 && !activity.id.equals("") && !activity.id.equals("null")) {
                    object.put("id", activity.id);
                }
                object.put("content", activity.content);
                object.put("article", activity.title);
                object.put("studentId", NewMainActivity.STUDENT_ID);
                if (!activity.cover.equals("") && !activity.cover.equals("null")) {
                    object.put("cover", activity.cover);
                }
                object.put("type", 5);
                object.put("matchId", strings[1]);
                object.put("wordsNum", Integer.valueOf(strings[2]));
                object.put("format", 1);
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
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.errorSubmitCompetition();
            } else {
                activity.analyzeSubmitCompetition(s);
            }
        }
    }

    /**
     * 获取比赛时间
     */
    private static class GetCompetitionTimeData
            extends WeakAsyncTask<String, Void, String, WritingActivity> {

        protected GetCompetitionTimeData(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("studentId", NewMainActivity.STUDENT_ID);
                jsonObject.put("taskId", Integer.valueOf(strings[1]));
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
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.keepEditor();
            } else {
                activity.analyzeCompetitionTimeData(s);
            }
        }
    }

    /**
     * 获取比赛时间数据
     */
    private void analyzeCompetitionTimeData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (200 == jsonObject.optInt("status", -1) ||
                    400 == jsonObject.optInt("status", -1)) {
                JSONObject object = jsonObject.getJSONObject("data");
                taskId = object.getString("taskId");
                competitionEndTime = object.getString("stopTime");
                showCountdownView();
            } else {
                keepEditor();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            keepEditor();
        }
    }

    /**
     * 显示倒计时
     */
    private void showCountdownView() {
        competitionCountdownTime = DateUtil.getCountDownTime(competitionEndTime);
        String text = "还剩" + competitionCountdownTime + "分钟";
        tv_countdown.setText(text);
        tv_countdown.setVisibility(View.VISIBLE);
        timer.start();
    }

    /**
     * 比赛倒计时
     */
    private CountDownTimer timer = new CountDownTimer(72000000, 60000) {

        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = Long.valueOf(competitionEndTime) - System.currentTimeMillis();
            if (seconds / 1000 <= 60) {
                secondsCountdown();
            } else {
                competitionCountdownTime = DateUtil.getCountDownTime(competitionEndTime);
                String text = "还剩" + competitionCountdownTime + "分钟";
                tv_countdown.setText(text);
                if (competitionCountdownTime.equals("20")) {
                    showCountdownTip();
                } else if (competitionCountdownTime.equals("0")) {
                    competitionHasFinished();
                }
            }
        }

        @Override
        public void onFinish() {
            competitionHasFinished();
        }
    };

    /**
     * 秒倒计时
     */
    private void secondsCountdown() {
        if (timer != null) {
            timer.cancel();
        }
        tv_countdown.setVisibility(View.VISIBLE);
        long seconds = Long.valueOf(competitionEndTime) - System.currentTimeMillis();
        String text = "还剩" + String.valueOf(seconds / 1000) + "秒";
        tv_countdown.setText(text);
        timer_seconds.start();
    }

    /**
     * 比赛倒计时
     */
    private CountDownTimer timer_seconds = new CountDownTimer(100000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            long seconds = Long.valueOf(competitionEndTime) - System.currentTimeMillis();
            String text = "还剩" + String.valueOf(seconds / 1000) + "秒";
            tv_countdown.setText(text);
            if (seconds / 1000 == 0) {
                competitionHasFinished();
            }
        }

        @Override
        public void onFinish() {
            competitionHasFinished();
        }
    };

    /**
     * 显示倒计时还剩20分钟提示
     */
    private void showCountdownTip() {
        TipsUtil tipsUtil = new TipsUtil(mContext);
        tipsUtil.showWritingCountdownTips(rl_top, "倒计还剩20分钟");
    }

    /**
     * 上传封面图
     */
    private static class UploadCover
            extends WeakAsyncTask<String, Integer, String, WritingActivity> {

        protected UploadCover(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                String imagePath = strings[1];
                File image = new File(imagePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("formData", strings[1],
                                RequestBody.create(MediaType.parse("image/*"), image))
                        .addFormDataPart("path", strings[1])
                        .addFormDataPart("fileType", "image")
                        .addFormDataPart("studentId", String.valueOf(NewMainActivity.STUDENT_ID))
                        .build();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.showTip("上传封面图失败，请检查您的网络状态是否连接");
            } else {
                activity.analyzeCoverData(s);
            }
        }
    }

    /**
     * 分析上传封面图数据
     *
     * @param s
     */
    private void analyzeCoverData(String s) {
        if (s.contains("http")) {
            cover = s;
            updateCoverUi();
        } else {
            showTip(s);
        }
    }

    /**
     * 更新封面图ui
     */
    private void updateCoverUi() {
        if (cover.equals("")) {
            return;
        }
        if (!isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .transform(new GlideRoundImage(mContext, 8));
            Glide.with(mContext)
                    .asBitmap()
                    .load(cover)
                    .apply(options)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            iv_cover.setImageResource(R.drawable.image_write_cover_bg);
                            iv_cover.setVisibility(View.VISIBLE);
                            iv_delete_cover.setVisibility(View.VISIBLE);
                            iv_change_cover.setVisibility(View.VISIBLE);
                            iv_cover_placeholder.setVisibility(View.GONE);
                            tv_cover_placeholder.setVisibility(View.GONE);
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
                            iv_cover.setImageBitmap(resource);
                            iv_cover.setVisibility(View.VISIBLE);
                            iv_delete_cover.setVisibility(View.VISIBLE);
                            iv_change_cover.setVisibility(View.VISIBLE);
                            iv_cover_placeholder.setVisibility(View.GONE);
                            tv_cover_placeholder.setVisibility(View.GONE);
                        }
                    });
        }
    }

    /**
     * 上传图片
     */
    private static class UploadImage
            extends WeakAsyncTask<String, Integer, String, WritingActivity> {

        protected UploadImage(WritingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(WritingActivity activity, String[] strings) {
            try {
                String imagePath = strings[1];
                File image = new File(imagePath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("formData", strings[1],
                                RequestBody.create(MediaType.parse("image/*"), image))
                        .addFormDataPart("path", strings[1])
                        .addFormDataPart("fileType", "image")
                        .addFormDataPart("studentId", String.valueOf(NewMainActivity.STUDENT_ID))
                        .build();
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WritingActivity activity, String s) {
            if (s == null) {
                activity.showTip("上传图片失败，请检查您的网络状态是否连接");
            } else {
                activity.analyzeImageData(s);
            }
        }
    }

    /**
     * 分析上传图片数据
     *
     * @param s
     */
    private void analyzeImageData(String s) {
        if (s.contains("http")) {
            if (mIndex == -1) {
                richEditor.insertImage(s);
            } else {
                richEditor.setImageResource(mIndex, s);
            }
        } else {
            String tips = s + "，请稍后重试~";
            showTip(tips);
        }
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizerUtil != null) {
            speechRecognizerUtil.release();
        }
        isResume = false;
        if (autoSaveThread != null && !autoSaveThread.isInterrupted()) {
            autoSaveThread.interrupt();
            autoSaveThread = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timer_seconds != null) {
            timer_seconds.cancel();
            timer_seconds = null;
        }
        super.onDestroy();
    }
}
