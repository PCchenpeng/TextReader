package com.dace.textreader.activity;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.R;
import com.dace.textreader.bean.WordBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.TipsUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.RadiusBackgroundColorSpan;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.helper.SelectableTextHelper;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import me.biubiubiu.justifytext.library.JustifyTextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 精读界面
 */
public class IntensiveReadingActivity extends BaseActivity {

    private final String iosUrl = HttpUrlPre.HTTP_URL + "/ios/perusal/mode?";
    private final String addAfterReadingUrl = HttpUrlPre.HTTP_URL + "/personal/feeling/insert?";
    private final String afterReadingUrl = HttpUrlPre.HTTP_URL + "/essay/personal/feeling?";
    private final String updateAfterReadingUrl = HttpUrlPre.HTTP_URL + "/personal/feeling/update?";
    private final String insertGlossaryUrl = HttpUrlPre.HTTP_URL + "/personal/word/insert?";
    private final String addUnderLineUrl = HttpUrlPre.HTTP_URL + "/personal/line/insert?";
    private final String deleteUnderLineUrl = HttpUrlPre.HTTP_URL + "/personal/line/delete?";
    private final String wordExplainUrl = HttpUrlPre.HTTP_URL + "/xiandaiwen?word=";
    //古文词语解释
    private final String poetryWordUrl = HttpUrlPre.HTTP_URL + "/guwen/innotation?";
    private final String noteUrl = HttpUrlPre.HTTP_URL + "/personal/note/one?noteId=";
    private final String addGrammarUrl = HttpUrlPre.HTTP_URL + "/personal/grammar/insert";
    private final String grammarUrl = HttpUrlPre.HTTP_URL + "/personal/grammar/select?grammarId=";
    private final String deleteGlossaryUrl = HttpUrlPre.HTTP_URL + "/personal/word/line/delete?";
    private final String deleteGrammarUrl = HttpUrlPre.HTTP_URL + "/personal/grammar/delete?";
    private final String updateGrammarUrl = HttpUrlPre.HTTP_URL + "/personal/grammar/update";
    private final String deleteNoteUrl = HttpUrlPre.HTTP_URL + "/personal/note/essay/delete";
    private final String shareNoteUrl = HttpUrlPre.HTTP_URL + "/get/note/url";

    private NestedScrollView scrollView;
    private LinearLayout ll_loading;
    private ImageView iv_loading_top;
    private TextView tv_loading_content;
    private ImageView iv_loading_bottom;
    private ImageView iv_close;

    private FrameLayout frameLayout;
    private LinearLayout ll_appbar;
    private ImageView iv_exit;
    private TextView tv_tips;
    private TextView tv_title;
    private TextView tv_content;
    private RelativeLayout rl_content_underline;
    private RelativeLayout rl_content_word_bg;
    private RelativeLayout rl_note_icon;

    private LinearLayout ll_write;
    private LinearLayout ll_after_reading;
    private JustifyTextView tv_after_reading;

    private WebView webView;  //词语解释的加载控件

    private long essayId = -1;  //文章ID
    private int essayType = -1;  //文章类型
    private String essayTitle = "";  //文章标题
    private String essayContent = "";  //文章内容
    private String afterReadingId = "";  //读后感ID
    private String afterReadingContent = "";  //读后感内容
    private int isPriviate = 1;  //读后感是否私密
    private String wordC = "";
    private String LineC = "";

    private List<WordBean> wordList = new ArrayList<>();
    private List<WordBean> lineList = new ArrayList<>();
    private String grammar = "";  //语法
    private String note = "";  //笔记

    private float downX;  //内容按下的X坐标
    private float downY;  //内容按下的Y坐标
    private float movedY;  //Y方向上移动的距离
    private int mTextWidth;  //内容的宽度
    private int mTextHeight = 0;  //标题的高度

    private boolean isShowActionMode = false;  //是否显示长按菜单
    private boolean isOtherClick = false;  //是否有其他内容被点击了

    private int position = -1;  //选中内容在List中所处的位置
    private int startNote = 0;  //选中内容的起始位置
    private int endNote = 0;  //选中内容的终止位置
    private int startGrammar = 0;  //选中内容语法的起始位置
    private int endGrammar = 0;  //选中内容语法的终止位置

    private String selectedContent = "";  //选中部分的内容

    private int offsetX = 0;  //PopupWindow的显示位置的X坐标
    private int offsetY = 0;  //PopupWindow的显示位置的X坐标
    private String gid = "";  //选中内容的语法ID
    private String nid = "";  //选中内容的笔记ID

    private boolean isAllContent = false;  //是否是获取语法和笔记的所有内容
    private boolean isGrammarOk = false;  //语法内容是否获取完成
    private boolean isNoteOk = false;  //笔记内容是否获取完成

    private boolean isUpdateAfterReading = false;  //是否是修改读后感

    private int[] images_top = new int[]{R.drawable.images_top_one, R.drawable.images_top_two,
            R.drawable.images_top_three, R.drawable.images_top_four};
    private int[] images_bottom = new int[]{R.drawable.images_bottom_one, R.drawable.images_bottom_two,
            R.drawable.images_bottom_three, R.drawable.images_bottom_four};
    private String[] loading_contents = new String[]{"学会字斟句酌\n才能了然于胸\n继而举一反三",
            "博览群书\n精读好书\n阅览宜广\n贵在精专",
            "精读好文\n给大脑一点空间思考",
            "精读美文\n化为己用",
            "泛读画皮难画骨\n精读教你学画虎",
            "学须领悟精到\n不宜囫囵吞枣",
            "好读书不如会读书\n精读，让你读好书",
            "孔子韦编三绝\n美文精读几遍",
            "食须细嚼才知味\n书宜精读方了然",
            "真理越辩越明\n文章越读越精",
            "读书百遍\n其义自见"};

    private boolean isFirstLoading = true;  //是否是第一次加载
    private boolean isStartToSearch = false;  //是否正在执行查找语法笔记内容

    private boolean isFirstStart;  //是否是第一次进入
    private SharedPreferences firstSharedPreferences;

    private boolean hasIntoNote = false;  //是否进入了笔记页面，用来刷新视图

    private int popupViewWidth;  //弹窗的宽度

    //辅助文字选择
    private SelectableTextHelper mSelectableTextHelper;
    private int start = 0;  //词语开始位置
    private int length = 0;  //词语长度
    private int wordsPosition = -1;  //词语位置

    private int type_share = -1;  //分享类型
    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;  //复制链接
    private String shareContent = "";

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intensive_reading);

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(IntensiveReadingActivity.this);
        StatusBarUtil.StatusBarLightMode(IntensiveReadingActivity.this, flag);

        essayId = getIntent().getLongExtra("essayId", -1);
        essayType = getIntent().getIntExtra("essayType", -1);
        essayTitle = getIntent().getStringExtra("essayTitle");
        essayContent = getIntent().getStringExtra("essayContent");

        firstSharedPreferences = getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        isFirstStart = firstSharedPreferences.getBoolean("intensive", true);

        initView();
        initData();
        initEvents();

        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasIntoNote) {
            initData();
            hasIntoNote = false;
        }
    }

    private void initData() {

        new GetData(this).execute(iosUrl +
                "studentId=" + NewMainActivity.STUDENT_ID +
                "&essayId=" + essayId +
                "&grade=" + NewMainActivity.GRADE);

    }

    private void initEvents() {
        iv_close.setOnClickListener(onClickListener);
        ll_write.setOnClickListener(onClickListener);
        tv_after_reading.setOnClickListener(onClickListener);
        tv_content.setOnClickListener(onClickListener);
        ll_loading.setOnClickListener(onClickListener);
        iv_exit.setOnClickListener(onClickListener);
        tv_tips.setOnClickListener(onClickListener);
        tv_title.setOnClickListener(onClickListener);

        tv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        downX = 0;
                        downY = 0;
                        downX = event.getX();
                        downY = event.getY();
                        if (android.os.Build.BRAND.equals("Xiaomi") ||
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            mSelectableTextHelper.setTouchPoint((int) downX, (int) downY);
                            if (mSelectableTextHelper.isShowOperate()) {
                                mSelectableTextHelper.resetSelectionInfo();
                                mSelectableTextHelper.hideSelectView();
                            }
                        }
                        isOtherClick = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (android.os.Build.BRAND.equals("Xiaomi") ||
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            if (event.getX() - downX > 40
                                    && !isShowActionMode
                                    && !mSelectableTextHelper.isShowOperate()) {
                                turnToNotesActivity();
                            }
                        } else {
                            if (event.getX() - downX > 40
                                    && !isShowActionMode) {
                                turnToNotesActivity();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int offset = scrollY - oldScrollY;
                movedY += offset;
                if (ll_appbar.getVisibility() == View.VISIBLE) {
                    ll_appbar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 跳转到笔记列表
     */
    private void turnToNotesActivity() {
        hasIntoNote = true;
        Intent intent = new Intent(IntensiveReadingActivity.this, NotesActivity.class);
        intent.putExtra("isAllNotes", false);
        intent.putExtra("essayId", essayId);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close_loading_intensive_reading:
                    finish();
                    break;
                case R.id.iv_close_appbar_intensive_reading:
                    finish();
                    break;
                case R.id.ll_write_after_reading:
                    isUpdateAfterReading = false;
                    writeAfterReading();
                    break;
                case R.id.tv_after_reading_content_intensive:
                    updateAfterReading();
                    break;
                case R.id.tv_intensive_reading_content:
                case R.id.tv_intensive_reading_title:
                case R.id.tv_intensive_reading_tips:
                    showAppBar();
                    break;
                case R.id.ll_loading_intensive_reading:
                    break;
            }
        }
    };

    /**
     * 显示AppBar
     */
    private void showAppBar() {
        if (android.os.Build.BRAND.equals("Xiaomi") ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!isOtherClick && !mSelectableTextHelper.isShowOperate()) {
                if (ll_appbar.getVisibility() == View.GONE) {
                    ll_appbar.setVisibility(View.VISIBLE);
                } else {
                    ll_appbar.setVisibility(View.GONE);
                }
            }
        } else {
            if (!isOtherClick) {
                if (ll_appbar.getVisibility() == View.GONE) {
                    ll_appbar.setVisibility(View.VISIBLE);
                } else {
                    ll_appbar.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 修改读后感
     */
    private void updateAfterReading() {
        isUpdateAfterReading = true;
        writeAfterReading();
    }

    /**
     * 写下读后感
     */
    private void writeAfterReading() {
        Intent intent = new Intent(this, WriteAfterReadingActivity.class);
        intent.putExtra("content", tv_after_reading.getText());
        intent.putExtra("isPriviate", isPriviate);
        startActivityForResult(intent, 1);
    }

    /**
     * 提交读后感
     *
     * @param s 读后感内容
     */
    private void commitAfterReading(final String s, int type) {
        if (isUpdateAfterReading) {
            new InsertAfterReading(IntensiveReadingActivity.this)
                    .execute(updateAfterReadingUrl +
                            "feelingId=" + afterReadingId +
                            "&feeling=" + s +
                            "&isPrivate=" + type);
        } else {
            new InsertAfterReading(IntensiveReadingActivity.this)
                    .execute(addAfterReadingUrl +
                            "studentId=" + NewMainActivity.STUDENT_ID +
                            "&essayId=" + essayId +
                            "&feeling=" + s +
                            "&isPrivate=" + type);
            ll_after_reading.setVisibility(View.VISIBLE);
            ll_write.setVisibility(View.GONE);
        }
        String after = s + "\n";
        tv_after_reading.setText(after);
    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView_intensive);

        ll_loading = findViewById(R.id.ll_loading_intensive_reading);
        iv_loading_top = findViewById(R.id.iv_loading_top_intensive);
        tv_loading_content = findViewById(R.id.tv_loading_content_intensive);
        iv_loading_bottom = findViewById(R.id.iv_loading_bottom_intensive);
        iv_close = findViewById(R.id.iv_close_loading_intensive_reading);

        frameLayout = findViewById(R.id.frame_intensive);
        ll_appbar = findViewById(R.id.ll_appbar_intensive_reading);
        iv_exit = findViewById(R.id.iv_close_appbar_intensive_reading);
        tv_tips = findViewById(R.id.tv_intensive_reading_tips);
        tv_title = findViewById(R.id.tv_intensive_reading_title);
        rl_content_word_bg = findViewById(R.id.rl_word_bg_intensive_reading);
        rl_content_underline = findViewById(R.id.rl_intensive_reading_content_underline);
        tv_content = findViewById(R.id.tv_intensive_reading_content);
        rl_note_icon = findViewById(R.id.rl_note_intensive);
        ll_write = findViewById(R.id.ll_write_after_reading);

        ll_after_reading = findViewById(R.id.ll_after_reading_intensive);
        tv_after_reading = findViewById(R.id.tv_after_reading_content_intensive);

        Random random_top = new Random();
        int loading_top = random_top.nextInt(4);
        GlideUtils.loadImageWithNoOptions(this, images_top[loading_top], iv_loading_top);
        GlideUtils.loadImageWithNoOptions(this, images_bottom[loading_top], iv_loading_bottom);

        int loading_content = random_top.nextInt(11);
        tv_loading_content.setText(loading_contents[loading_content]);

        tv_content.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                isShowActionMode = true;
                isOtherClick = true;
                //返回false则不会显示弹窗
                if (Build.BRAND.equals("Xiaomi") ||
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (mSelectableTextHelper != null) {
                        if (mSelectableTextHelper.isShowOperate()) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return true;
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menu.clear();
                menuInflater.inflate(R.menu.action_basis_menu, menu);
                if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                    try {
                        Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                        m.setAccessible(true);
                        m.invoke(menu, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_basis_item_annotate:
                        int start = tv_content.getSelectionStart();
                        int end = tv_content.getSelectionEnd();
                        int position_l = hasExist(start, end);
                        if (position_l == -1) {
                            underLine(tv_content.getSelectionStart(), tv_content.getSelectionEnd());
                        } else {
                            MyToastUtil.showToast(IntensiveReadingActivity.this, "划线已经存在");
                        }

                        mode.finish();
                        break;
                    case R.id.action_basis_item_grammar:
                        startGrammar = tv_content.getSelectionStart();
                        endGrammar = tv_content.getSelectionEnd();
                        grammar = "";
                        int position_g = hasExist(startGrammar, endGrammar);
                        if (position_g == -1) {
                            chooseGrammar(startGrammar, endGrammar);
                        } else {
                            String g = lineList.get(position_g).getGrammarId();
                            if (g.equals("") || g.equals("null")) {
                                chooseGrammar(startGrammar, endGrammar);
                            } else {
                                isAllContent = false;
                                showGrammar(g);
                            }
                        }
                        mode.finish();
                        break;
                    case R.id.action_basis_item_note:
                        startNote = tv_content.getSelectionStart();
                        endNote = tv_content.getSelectionEnd();
                        int position_n = hasExist(startNote, endNote);
                        if (position_n == -1) {
                            startToAddNote(startNote, endNote);
                        } else {
                            String n = lineList.get(position_n).getNoteId();
                            if (n.equals("") || n.equals("null")) {
                                startToAddNote(startNote, endNote);
                            } else {
                                isAllContent = false;
                                showNote(n);
                            }
                        }
                        mode.finish();
                        break;
                    case R.id.action_basis_item_copy:
                        String str = essayContent.substring(tv_content.getSelectionStart(),
                                tv_content.getSelectionEnd());
                        copyContent(str);
                        mode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isShowActionMode = false;
                isOtherClick = false;
            }
        });

        tv_content.setMovementMethod(LinkMovementMethod.getInstance());

        ViewTreeObserver vto = tv_content.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mTextHeight += tv_tips.getHeight();
            }
        });

        ViewTreeObserver vto1 = tv_content.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mTextHeight += tv_title.getHeight();
            }
        });
        ViewTreeObserver vto2 = tv_content.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mTextWidth = tv_content.getWidth();
            }
        });

    }

    /**
     * 是否存在画线内容
     *
     * @param start
     * @param end
     */
    private int hasExist(int start, int end) {
        int hasExist = -1;
        for (int i = 0; i < lineList.size(); i++) {
            WordBean wordBean = lineList.get(i);
            int s = wordBean.getStart();
            int e = s + wordBean.getLength();
            if (start == s && e == end) {
                hasExist = i;
                break;
            }
        }
        return hasExist;
    }

    /**
     * 复制内容到截切板
     *
     * @param str 要复制的内容
     */
    private void copyContent(String str) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("Label", str);
        if (cm != null) {
            cm.setPrimaryClip(cd);
        }
    }

    /**
     * 画线
     *
     * @param start 画线起始位置
     * @param end   画线终止位置
     */
    private void underLine(int start, int end) {

        int len = end - start;

        WordBean wordBean = new WordBean();
        wordBean.setStart(start);
        wordBean.setLength(len);
        wordBean.setGrammarId("");
        wordBean.setNoteId("");
        lineList.add(wordBean);
        showContent();

        new UnderLine(IntensiveReadingActivity.this)
                .execute(addUnderLineUrl + "studentId=" + NewMainActivity.STUDENT_ID +
                        "&essayId=" + essayId +
                        "&loc=" + start +
                        "&len=" + len);
    }

    /**
     * 删除句子划线
     *
     * @param start 删除划线的起始位置
     * @param end   删除划线的终止位置
     */
    private void deleteUnderLine(int start, int end) {

        int len = end - start;

        new UnderLine(IntensiveReadingActivity.this)
                .execute(deleteUnderLineUrl + "studentId=" + NewMainActivity.STUDENT_ID +
                        "&essayId=" + essayId +
                        "&loc=" + start +
                        "&len=" + len);
    }

    /**
     * 显示句子的弹窗
     */
    private void showSentencePopupWindow(final int position) {

        gid = "";
        nid = "";
        note = "";
        grammar = "";

        this.position = position;

        final int start = lineList.get(position).getStart();
        final int end = start + lineList.get(position).getLength();

        View view = LayoutInflater.from(IntensiveReadingActivity.this)
                .inflate(R.layout.popup_window_sentence_layout, null);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        final int offsetX = getOffsetX(view);
        final int offsetY = (int) (downY + mTextHeight - movedY + 40.0f);

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        popupWindow.showAtLocation(tv_content, Gravity.NO_GRAVITY,
                offsetX, offsetY);

        LinearLayout ll_popup_sentence_delete = view.findViewById(R.id.ll_popup_sentence_delete);
        LinearLayout ll_popup_sentence_grammar = view.findViewById(R.id.ll_popup_sentence_grammar);
        LinearLayout ll_popup_sentence_note = view.findViewById(R.id.ll_popup_sentence_note);
        LinearLayout ll_popup_sentence_copy = view.findViewById(R.id.ll_popup_sentence_copy);

        ll_popup_sentence_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineList.remove(position);
                showContent();
                deleteUnderLine(start, end);
                isOtherClick = false;
                popupWindow.dismiss();
            }
        });
        ll_popup_sentence_grammar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gid = lineList.get(position).getGrammarId();
                if (gid == null || gid.equals("null") || gid.equals("")) {
                    grammar = "";
                    chooseGrammar(start, end);
                } else {
                    isAllContent = false;
                    grammar = "";
                    showGrammar(gid);
                }
                isOtherClick = false;
                popupWindow.dismiss();
            }
        });
        ll_popup_sentence_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nid = lineList.get(position).getNoteId();
                if (nid == null || nid.equals("null") || nid.equals("")) {
                    startToAddNote(start, end);
                } else {
                    isAllContent = false;
                    showNote(nid);
                }
                isOtherClick = false;
                popupWindow.dismiss();
            }
        });
        ll_popup_sentence_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = essayContent.substring(start, end);
                copyContent(str);
                isOtherClick = false;
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 显示语法和笔记
     *
     * @param gid     语法ID
     * @param nid     笔记ID
     * @param offsetX 显示位置的X坐标
     * @param offsetY 显示位置的Y坐标
     */
    private void showGrammarAndNote(String gid, String nid, int offsetX, int offsetY) {
        if (!isStartToSearch) {
            isStartToSearch = true;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.gid = gid;
            this.nid = nid;
            new GetGrammarContent(this).execute(grammarUrl + gid);
            new GetNoteContent(this).execute(noteUrl + nid);
        }
    }

    /**
     * 选择语法
     *
     * @param start 选中内容在文中的起始位置
     * @param end   选中内容在文中的终止位置
     */
    private void chooseGrammar(final int start, final int end) {

        final String content = essayContent.substring(start, end);

        startGrammar = start;
        endGrammar = end;

        View view = LayoutInflater.from(IntensiveReadingActivity.this)
                .inflate(R.layout.popup_window_sentence_grammar_layout, null);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        popupWindow.showAtLocation(tv_content, Gravity.NO_GRAVITY,
                0, (int) (downY + mTextHeight - movedY + 40.0f));

        LinearLayout ll_parallelism = view.findViewById(R.id.ll_popup_window_grammar_parallelism);
        final ImageView iv_parallelism = view.findViewById(R.id.iv_popup_window_grammar_parallelism);

        LinearLayout ll_metaphor = view.findViewById(R.id.ll_popup_window_grammar_metaphor);
        final ImageView iv_metaphor = view.findViewById(R.id.iv_popup_window_grammar_metaphor);

        LinearLayout ll_personification = view.findViewById(R.id.ll_popup_window_grammar_personification);
        final ImageView iv_personification = view.findViewById(R.id.iv_popup_window_grammar_personification);

        LinearLayout ll_negative = view.findViewById(R.id.ll_popup_window_grammar_negative);
        final ImageView iv_negative = view.findViewById(R.id.iv_popup_window_grammar_negative);

        LinearLayout ll_sure = view.findViewById(R.id.ll_popup_window_grammar_sure);
        final ImageView iv_sure = view.findViewById(R.id.iv_popup_window_grammar_sure);

        LinearLayout ll_exaggeration = view.findViewById(R.id.ll_popup_window_grammar_exaggeration);
        final ImageView iv_exaggeration = view.findViewById(R.id.iv_popup_window_grammar_exaggeration);

        LinearLayout ll_doubt = view.findViewById(R.id.ll_popup_window_grammar_doubt);
        final ImageView iv_doubt = view.findViewById(R.id.iv_popup_window_grammar_doubt);

        LinearLayout ll_statement = view.findViewById(R.id.ll_popup_window_grammar_statement);
        final ImageView iv_statement = view.findViewById(R.id.iv_popup_window_grammar_statement);

        LinearLayout ll_duality = view.findViewById(R.id.ll_popup_window_grammar_duality);
        final ImageView iv_duality = view.findViewById(R.id.iv_popup_window_grammar_duality);

        LinearLayout ll_sigh = view.findViewById(R.id.ll_popup_window_grammar_sigh);
        final ImageView iv_sigh = view.findViewById(R.id.iv_popup_window_grammar_sigh);

        LinearLayout ll_asked = view.findViewById(R.id.ll_popup_window_grammar_asked);
        final ImageView iv_asked = view.findViewById(R.id.iv_popup_window_grammar_asked);

        LinearLayout ll_association = view.findViewById(R.id.ll_popup_window_grammar_association);
        final ImageView iv_association = view.findViewById(R.id.iv_popup_window_grammar_association);

        LinearLayout ll_other = view.findViewById(R.id.ll_popup_window_grammar_other);
        final ImageView iv_other = view.findViewById(R.id.iv_popup_window_grammar_other);
        TextView tv_other = view.findViewById(R.id.tv_popup_window_grammar_other);

        switch (grammar) {
            case "排比句":
                iv_parallelism.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "比喻句":
                iv_metaphor.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "拟人句":
                iv_personification.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "否定句":
                iv_negative.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "肯定句":
                iv_sure.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "夸张句":
                iv_exaggeration.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "疑问句":
                iv_doubt.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "陈述句":
                iv_statement.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "对偶句":
                iv_duality.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "反问句":
                iv_asked.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "感叹句":
                iv_sigh.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "关联句":
                iv_association.setImageResource(R.drawable.icon_grammar_selected);
                break;
            case "":
                break;
            default:
                iv_other.setImageResource(R.drawable.icon_grammar_selected);
                tv_other.setText(grammar);
                break;
        }

        ll_parallelism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_parallelism.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "排比句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "排比句");
                }
                popupWindow.dismiss();
            }
        });
        ll_metaphor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_metaphor.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "比喻句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "比喻句");
                }
                popupWindow.dismiss();
            }
        });
        ll_personification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_personification.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "拟人句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "拟人句");
                }
                popupWindow.dismiss();
            }
        });
        ll_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_negative.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "否定句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "否定句");
                }
                popupWindow.dismiss();
            }
        });
        ll_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_sure.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "肯定句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "肯定句");
                }
                popupWindow.dismiss();
            }
        });
        ll_exaggeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_exaggeration.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "夸张句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "夸张句");
                }
                popupWindow.dismiss();
            }
        });
        ll_doubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_doubt.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "疑问句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "疑问句");
                }
                popupWindow.dismiss();
            }
        });
        ll_statement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_statement.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "陈述句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "陈述句");
                }
                popupWindow.dismiss();
            }
        });
        ll_duality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_duality.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "对偶句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "对偶句");
                }
                popupWindow.dismiss();
            }
        });
        ll_sigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_sigh.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "感叹句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "感叹句");
                }
                popupWindow.dismiss();
            }
        });
        ll_asked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_asked.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "反问句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "反问句");
                }
                popupWindow.dismiss();
            }
        });
        ll_association.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underLine(start, end);
                iv_association.setImageResource(R.drawable.icon_grammar_selected);

                if (grammar.equals("")) {
                    new AddGrammar(IntensiveReadingActivity.this)
                            .execute(addGrammarUrl, content, "关联句");
                } else {
                    new UpdateGrammar(IntensiveReadingActivity.this)
                            .execute(updateGrammarUrl, "关联句");
                }
                popupWindow.dismiss();
            }
        });
        ll_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NiceDialog.init()
                        .setLayoutId(R.layout.sentence_grammar_edit_layout)
                        .setConvertListener(new ViewConvertListener() {
                            @Override
                            protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                final EditText editText = holder.getView(R.id.et_sentence_grammar_edit);
                                final TextView textView = holder.getView(R.id.tv_sentence_grammar_edit_commit);
                                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                                editText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        InputMethodManager imm = (InputMethodManager)
                                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(editText, 0);
                                    }
                                });
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String str = editText.getText().toString().replaceAll("\\s*", "");
                                        if (TextUtils.isEmpty(str)) {
                                            editText.setText("");
                                            MyToastUtil.showToast(IntensiveReadingActivity.this,
                                                    "请输入句式内容");
                                        } else {
                                            InputMethodManager imm = (InputMethodManager)
                                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                                            if (imm.isActive()) {
                                                imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                                            }
                                            underLine(start, end);
                                            iv_other.setImageResource(R.drawable.icon_grammar_selected);

                                            if (grammar.equals("")) {
                                                new AddGrammar(IntensiveReadingActivity.this)
                                                        .execute(addGrammarUrl, content,
                                                                editText.getText().toString());
                                            } else {
                                                new UpdateGrammar(IntensiveReadingActivity.this)
                                                        .execute(updateGrammarUrl,
                                                                editText.getText().toString());
                                            }
                                            editText.setText("");
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        })
                        .setShowBottom(true)
                        .show(getSupportFragmentManager());
                popupWindow.dismiss();
            }
        });

    }

    /**
     * 显示语法
     *
     * @param gid 语法ID
     */
    private void showGrammar(String gid) {
        this.gid = gid;
        new GetGrammarContent(this).execute(grammarUrl + gid);
    }

    /**
     * 显示笔记内容
     *
     * @param nid 笔记ID
     */
    private void showNote(String nid) {
        this.nid = nid;
        new GetNoteContent(this).execute(noteUrl + nid);
    }

    /**
     * 跳转到添加笔记的页面
     *
     * @param start 选中内容的起始位置
     * @param end   选中内容的终止位置
     */
    private void startToAddNote(int start, int end) {

        int len = end - start;
        Intent intent = new Intent(IntensiveReadingActivity.this, AddNoteActivity.class);
        intent.putExtra("essayId", essayId);
        intent.putExtra("content", essayContent.substring(start, end));
        intent.putExtra("loc", start);
        intent.putExtra("len", len);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            initData();
        } else if (requestCode == 1) {
            if (data != null) {
                String s = data.getStringExtra("content");
                int type = data.getIntExtra("type", 1);
                commitAfterReading(s, type);
            }
        }
    }


    /**
     * 显示词语的弹窗
     */
    private void showWordsPopupWindow(int position) {

        wordsPosition = position;
        start = wordList.get(position).getStart();
        length = wordList.get(position).getLength();
        int end = start + length;

        showWordsExplanation(essayContent.substring(start, end));
        isOtherClick = false;
    }

    /**
     * 显示词语意思
     *
     * @param word 词语
     */
    private void showWordsExplanation(String word) {
        showLoadingView();

        if (essayType == 2 || essayType == 4) {
            new WordExplain(this)
                    .execute(poetryWordUrl + "word=" + word +
                            "&title=" + essayTitle);
        } else {
            new WordExplain(this).execute(wordExplainUrl + word);
        }
    }

    private void showLoadingView() {
        if (isDestroyed()) {
            return;
        }
        frameLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.view_loading, null);
        ImageView iv_loading = view.findViewById(R.id.iv_loading_content);
        GlideUtils.loadGIFImageWithNoOptions(this, R.drawable.image_loading, iv_loading);
        frameLayout.removeAllViews();
        frameLayout.addView(view);
    }

    /**
     * 获取视图的X偏移量
     *
     * @param view
     * @return
     */
    private int getOffsetX(final View view) {
        int x = 0;
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                popupViewWidth = view.getMeasuredWidth();
            }
        });
        if (downX > popupViewWidth / 2
                && mTextWidth - downX > popupViewWidth / 2) {  //左右都可以放下View这个视图
            x = (int) downX - popupViewWidth / 2;
        }
        if (downX == popupViewWidth / 2) {  //左边的位置刚好可以放下View这个视图
            x = 30;
        }
        if (mTextWidth - downX == popupViewWidth / 2) {  //右边刚好可以放下View这个视图
            x = (int) (downX - popupViewWidth / 2 - 30);
        }
        if (downX < popupViewWidth / 2) {  //左边的位置放不下View这个视图
            x = 30;
        }
        if (mTextWidth - downX < popupViewWidth / 2) {  //右边放不下View这个视图
            x = mTextWidth - popupViewWidth / 2 - 30;
        }
        return x;
    }

    /**
     * 等待用户
     */
    private void waitUser() {
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    updateUI();
                    break;
            }
        }
    };

    /**
     * 更新UI
     */
    private void updateUI() {

        tv_title.setText(essayTitle);

        wordList.clear();
        lineList.clear();

        try {
            if (wordC != null && !wordC.equals("null")) {
                JSONObject wordJsonObject = new JSONObject(wordC);
                Iterator<String> keys = wordJsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    WordBean word = new WordBean();
                    word.setStart(Integer.valueOf(key));
                    word.setLength(wordJsonObject.optInt(key, 0));
                    wordList.add(word);
                }
            }

            if (LineC != null && !LineC.equals("null")) {
                JSONObject lineJsonObject = new JSONObject(LineC);
                Iterator<String> titles = lineJsonObject.keys();
                while (titles.hasNext()) {
                    String key = titles.next();
                    WordBean wordBean = new WordBean();
                    wordBean.setStart(Integer.valueOf(key.split("_")[0]));
                    wordBean.setLength(Integer.valueOf(key.split("_")[1]));

                    JSONObject id = lineJsonObject.getJSONObject(key);
                    wordBean.setGrammarId(id.getString("gid"));
                    wordBean.setNoteId(id.getString("nid"));
                    lineList.add(wordBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        showContent();

    }

    /**
     * 显示内容
     */
    private void showContent() {
        if (mSelectableTextHelper != null) {
            mSelectableTextHelper = null;
        }

        final SpannableStringBuilder ssb = new SpannableStringBuilder(essayContent);

        int radiusColor = getResources().getColor(R.color.colorRadiusBackground);

        int length = essayContent.length();

        for (int i = 0; i < wordList.size(); i++) {
            final WordBean wordBean = wordList.get(i);
            final int position = i;
            int start = wordBean.getStart();
            int end = start + wordBean.getLength();

            if (end > start && start >= 0 && end <= length) {
                //圆角背景
                RadiusBackgroundColorSpan backgroundColorSpan =
                        new RadiusBackgroundColorSpan(radiusColor, 8);
                ssb.setSpan(backgroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //点击事件
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        isOtherClick = true;
                        showWordsPopupWindow(position);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
//                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#666666"));
                        ds.setUnderlineText(false);
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        for (int j = 0; j < lineList.size(); j++) {
            final WordBean wordBean = lineList.get(j);
            final int position = j;
            final int start = wordBean.getStart();
            final int end = start + wordBean.getLength();

            if (end > start) {
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        isOtherClick = true;
                        selectedContent = tv_content.getText().toString().substring(start, end);
                        showSentencePopupWindow(position);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#666666"));
                        ds.setUnderlineText(false);
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }
        tv_content.setText(ssb);

        if (android.os.Build.BRAND.equals("Xiaomi") ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {  //锤子手机的话就不显示文字选择辅助类
            mSelectableTextHelper = new SelectableTextHelper.Builder(tv_content)
                    .setSelectedColor(getResources().getColor(R.color.selected_blue))
                    .setCursorHandleSizeInDp(20)
                    .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
                    .build();
            mSelectableTextHelper.setOnUnderLineClickListener(new SelectableTextHelper.OnUnderLineClick() {
                @Override
                public void onUnderLineClick(int start, int end) {
                    int position_l = hasExist(start, end);
                    if (position_l == -1) {
                        underLine(start, end);
                    } else {
                        MyToastUtil.showToast(IntensiveReadingActivity.this, "划线已经存在");
                    }
                }
            });
            mSelectableTextHelper.setOnGrammarClickListener(new SelectableTextHelper.OnGrammarClick() {
                @Override
                public void onGrammarClick(int start, int end) {
                    startGrammar = start;
                    endGrammar = end;
                    grammar = "";
                    int position_g = hasExist(startGrammar, endGrammar);
                    if (position_g == -1) {
                        chooseGrammar(startGrammar, endGrammar);
                    } else {
                        String g = lineList.get(position_g).getGrammarId();
                        if (g.equals("") || g.equals("null")) {
                            chooseGrammar(startGrammar, endGrammar);
                        } else {
                            isAllContent = false;
                            showGrammar(g);
                        }
                    }
                }
            });
            mSelectableTextHelper.setOnNoteClickListener(new SelectableTextHelper.OnNoteClick() {
                @Override
                public void onNoteClick(int start, int end) {
                    startNote = start;
                    endNote = end;
                    int position_n = hasExist(startNote, endNote);
                    if (position_n == -1) {
                        startToAddNote(startNote, endNote);
                    } else {
                        String n = lineList.get(position_n).getNoteId();
                        if (n.equals("") || n.equals("null")) {
                            startToAddNote(startNote, endNote);
                        } else {
                            isAllContent = false;
                            showNote(n);
                        }
                    }
                }
            });
        }

        showUnderLine();
        showNoteIcon();
//        showWordBg();

        if (ll_loading.getVisibility() == View.VISIBLE) {
            ll_loading.setVisibility(View.GONE);
            if (isFirstStart) {
                TipsUtil tipsUtil = new TipsUtil(this);
                tipsUtil.showTipWebView(ll_loading, "长按选择正文\n有惊喜");
                tipsUtil.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        TipsUtil tipsUtil = new TipsUtil(IntensiveReadingActivity.this);
                        tipsUtil.showTipToLeftView(ll_loading, "左滑\n查看笔记");
                    }
                });
                SharedPreferences.Editor editor = firstSharedPreferences.edit();
                editor.putBoolean("intensive", false);
                editor.apply();
            }
        }
    }

    /**
     * 显示词语背景
     */
    private void showWordBg() {
        rl_content_word_bg.removeAllViews();
        int radiusColor = getResources().getColor(R.color.colorRadiusBackground);

        for (int i = 0; i < wordList.size(); i++) {
            final WordBean wordBean = wordList.get(i);
            Layout layout = tv_content.getLayout();

            int start = wordBean.getStart();
            int end = start + wordBean.getLength();

            int line_start = layout.getLineForOffset(start);
            int line_end = layout.getLineForOffset(end);

            if (line_end == line_start) {  //只有一行
                Rect rect = new Rect();
                layout.getLineBounds(line_start, rect);
                int length = (int) (layout.getSecondaryHorizontal(end) - layout.getPrimaryHorizontal(start));
                View view = new View(IntensiveReadingActivity.this);
                view.setBackgroundColor(radiusColor);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        length + 4, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                rl_content_word_bg.addView(view, params);
            } else {
                for (int j = 0; j <= line_end - line_start; j++) {
                    int line = line_start + j;
                    Rect rect = new Rect();
                    layout.getLineBounds(line, rect);
                    View view = new View(IntensiveReadingActivity.this);
                    view.setBackgroundColor(radiusColor);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            length - 20, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    rl_content_word_bg.addView(view, params);
                }
            }
        }
    }

    /**
     * 显示下划线
     */
    private void showUnderLine() {
        int underLineColor = Color.parseColor("#ff9933");
        rl_content_underline.removeAllViews();

        for (int i = 0; i < lineList.size(); i++) {

            final WordBean wordBean = lineList.get(i);

            Layout layout = tv_content.getLayout();

            final int start = wordBean.getStart();
            final int end = start + wordBean.getLength();

            int line_start = layout.getLineForOffset(start);
            int line_end = layout.getLineForOffset(end);

            //最后一行的位置
            int end_line = layout.getLineForOffset(tv_content.getText().toString().length());

            if (line_end == line_start) {  //只有一行

                Rect rect = new Rect();
                layout.getLineBounds(line_start, rect);
                int offset;

                if (android.os.Build.BRAND.equals("SMARTISAN")) {
                    offset = rect.bottom - 30;
                } else {
                    if (line_end == end_line) {
                        offset = rect.bottom;
                    } else {
                        offset = rect.bottom - 30;
                    }
                }

                int length = (int) (layout.getSecondaryHorizontal(end) - layout.getPrimaryHorizontal(start));
                View view = new View(IntensiveReadingActivity.this);
                view.setBackgroundColor(underLineColor);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        length - 20, DensityUtil.dip2px(this, 1));
                params.setMargins((int) layout.getPrimaryHorizontal(start) + 10,
                        offset, 0, 0);
                rl_content_underline.addView(view, params);
            } else {
                for (int j = 0; j <= line_end - line_start; j++) {
                    int line = line_start + j;
                    Rect rect = new Rect();
                    layout.getLineBounds(line, rect);
                    int offset;
                    int length;
                    int offset_left;
                    if (j == 0) {
                        offset = rect.bottom - 30;
                        offset_left = (int) layout.getPrimaryHorizontal(start);
                        length = (int) (rect.right - layout.getPrimaryHorizontal(start));
                    } else if (j == line_end - line_start) {
                        offset_left = 0;
                        if (android.os.Build.BRAND.equals("SMARTISAN")) {
                            offset = rect.bottom - 30;
                        } else {
                            if (line_end == end_line) {
                                offset = rect.bottom;
                            } else {
                                offset = rect.bottom - 30;
                            }
                        }
                        length = (int) (layout.getSecondaryHorizontal(end) - rect.left);
                    } else {
                        offset_left = 0;
                        offset = rect.bottom - 30;
                        length = rect.right - rect.left;
                    }

                    View view = new View(IntensiveReadingActivity.this);
                    view.setBackgroundColor(underLineColor);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            length - 20, DensityUtil.dip2px(this, 1));
                    params.setMargins(offset_left + 10, offset, 0, 0);
                    rl_content_underline.addView(view, params);
                }
            }
        }
    }

    /**
     * 显示划线部分的Icon
     */
    private void showNoteIcon() {

        rl_note_icon.removeAllViews();

        for (int j = 0; j < lineList.size(); j++) {

            final WordBean wordBean = lineList.get(j);
            if ((!wordBean.getNoteId().equals("")
                    && !wordBean.getNoteId().equals("null"))
                    || (!wordBean.getGrammarId().equals("")
                    && !wordBean.getGrammarId().equals("null"))) {

                Layout layout = tv_content.getLayout();
                Rect bound = new Rect();

                final int pos = j;
                final int start = wordBean.getStart();
                final int end = start + wordBean.getLength();

                int line = layout.getLineForOffset(start);
                layout.getLineBounds(line, bound);

                final int y = bound.top;
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        DensityUtil.dip2px(this, 24),
                        DensityUtil.dip2px(this, 24));
                ImageView imageView = new ImageView(IntensiveReadingActivity.this);
                imageView.setImageResource(R.drawable.words_note);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAllContent = true;
                        position = pos;

                        int offset = (int) (v.getY() + mTextHeight - movedY + 60.0f);

                        selectedContent = tv_content.getText().toString().substring(start, end);
                        String gid = lineList.get(pos).getGrammarId();
                        String nid = lineList.get(pos).getNoteId();
                        showGrammarAndNote(gid, nid, offsetX, offset);
                    }
                });
                params.setMargins(0, y, 0, 0);
                rl_note_icon.addView(imageView, params);
            }
        }
    }

    /**
     * 显示读后感
     */
    private void showAfterReadingContent() {
        if (afterReadingContent != null
                && !afterReadingContent.equals("")
                && !afterReadingContent.equals("null")) {
            String after = afterReadingContent + "\n";
            tv_after_reading.setText(after);
        } else {
            ll_after_reading.setVisibility(View.GONE);
            ll_write.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示笔记和语法的内容
     */
    private void showAllContent() {
        shareContent = "";
        isStartToSearch = false;
        View view = LayoutInflater.from(IntensiveReadingActivity.this)
                .inflate(R.layout.popup_window_all_content_layout, null);

        LinearLayout ll_grammar = view.findViewById(R.id.ll_grammar_all_content_popup_window);
        TextView tv_grammar = view.findViewById(R.id.tv_grammar_all_content_popup_window);
        LinearLayout ll_note = view.findViewById(R.id.ll_note_all_content_popup_window);
        final TextView tv_note = view.findViewById(R.id.tv_note_all_content_popup_window);
        ImageView iv_delete = view.findViewById(R.id.iv_delete_all_content_popup_window);
        LinearLayout ll_share = view.findViewById(R.id.ll_share_add_content_popup_window);

        if (grammar.equals("")) {
            ll_grammar.setVisibility(View.GONE);
        } else {
            tv_grammar.setText(grammar);
            grammar = "";
        }

        if (note.equals("")) {
            ll_note.setVisibility(View.GONE);
            ll_share.setVisibility(View.GONE);
        } else {
            tv_note.setText(note);
            shareContent = note;
            note = "";
        }

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        popupWindow.showAtLocation(tv_content, Gravity.NO_GRAVITY, 0, offsetY);

        ll_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        IntensiveReadingActivity.this, AddNoteActivity.class);
                intent.putExtra("nid", nid);
                intent.putExtra("note", tv_note.getText().toString());
                intent.putExtra("content", selectedContent);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gid.equals("")) {
                    new UnderLine(IntensiveReadingActivity.this)
                            .execute(deleteGrammarUrl +
                                    "studentId=" + NewMainActivity.STUDENT_ID +
                                    "&essayId=" + essayId +
                                    "&grammarId=" + gid);
                }
                if (!nid.equals("")) {
                    JSONArray json = new JSONArray();
                    json.put(nid);
                    new DeleteNoteData(IntensiveReadingActivity.this)
                            .execute(deleteNoteUrl, json.toString());
                }
                int start = lineList.get(position).getStart();
                int end = start + lineList.get(position).getLength();
                deleteUnderLine(start, end);
                if (position != -1) {
                    lineList.remove(position);
                    position = -1;
                }
                showContent();
                popupWindow.dismiss();
            }
        });
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNote(nid);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 分享笔记
     */
    private void shareNote(final String noteId) {
        type_share = -1;
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_share_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.share_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_wechat, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIEND, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(IntensiveReadingActivity.this)) {
                                    getShareHtml(TYPE_SHARE_Weibo, noteId);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone, noteId);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK, noteId);
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
     * 获取分享链接
     */
    private void getShareHtml(int type_share, String noteId) {
        this.type_share = type_share;
        showTips("正在准备分享内容...");
        new GetShareHtml(this).execute(shareNoteUrl, noteId);
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, essayTitle, essayContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 分享笔记到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(this, url, essayTitle, shareContent,
                ImageUtils.bmpToByteArray(thumb, true), friend);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, essayTitle, essayContent, HttpUrlPre.SHARE_APP_ICON);
    }

    /**
     * 显示语法内容
     */
    private void showGrammarContent() {
        chooseGrammar(0, 1);
    }

    /**
     * 获取到笔记内容后去往修改笔记
     */
    private void showNoteContent() {
        Intent intent = new Intent(IntensiveReadingActivity.this, AddNoteActivity.class);
        intent.putExtra("nid", nid);
        intent.putExtra("note", note);
        intent.putExtra("content", selectedContent);
        startActivity(intent);
    }

    /**
     * 暂无词语解释
     */
    private void noContent() {
        frameLayout.removeAllViews();
        frameLayout.setVisibility(View.GONE);
        showTips("抱歉，暂无该词语解释，后续将为您添加");
    }

    /**
     * 词语解释
     *
     * @param content
     */
    private void showExplain(String content) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_window_word_explain_layout, null);
        webView = view.findViewById(R.id.web_view_popup_window_word_explain);
        webView.loadDataWithBaseURL("about:blank", getHtmlData(content),
                "text/html", "utf-8", null);
        ImageView iv_close = view.findViewById(R.id.iv_close_word_explain);
        LinearLayout ll_add = view.findViewById(R.id.ll_add_word_explain);
        LinearLayout ll_delete = view.findViewById(R.id.ll_delete_word_explain);

        frameLayout.removeAllViews();
        frameLayout.addView(view);

        View one = view.findViewById(R.id.view_one);
        View two = view.findViewById(R.id.view_two);
        View three = view.findViewById(R.id.view_three);
        View four = view.findViewById(R.id.view_four);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
            }
        });
        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UnderLine(IntensiveReadingActivity.this)
                        .execute(insertGlossaryUrl +
                                "word=" + essayContent.substring(start, start + length) +
                                "&studentId=" + NewMainActivity.STUDENT_ID +
                                "&essayId=" + essayId +
                                "&title=" + essayTitle +
                                "&sourceType=1" +
                                "&type=" + essayType);
                frameLayout.setVisibility(View.GONE);
            }
        });
        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordList.remove(wordsPosition);
                new UnderLine(IntensiveReadingActivity.this)
                        .execute(deleteGlossaryUrl +
                                "studentId=" + NewMainActivity.STUDENT_ID +
                                "&essayId=" + essayId +
                                "&loc=" + start +
                                "&len=" + length);
                showContent();
                frameLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取完整的Html源码
     *
     * @param bodyHtml 内容主体
     * @return
     */
    private String getHtmlData(String bodyHtml) {
        String head = "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"Generator\" content=\"EditPlus®\">\n" +
                "  <meta name=\"Author\" content=\"\">\n" +
                "  <meta name=\"Keywords\" content=\"\">\n" +
                "  <meta name=\"Description\" content=\"\">\n" +
                "  <title>Document</title>";
        String style = "<style> body{background:#444444; text-align:justify;text-justify:distribute;} " +
                "ul{margin-left:-15px; line-height: 1.2;} " +
                "li{line-height: 1.2;} " +
                "dd{margin-left:20px;}</style> " +
                "<div  style=\"color:#FFFFFF;font-family:华文细黑;font-size:1.0rem;\">";
        String body = "</head>\n" +
                " <body>";
        String over = "</body>\n" +
                "</html>";
        return head + style + body + bodyHtml + over;
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
                String url = jsonObject.getString("data");
                share(url);
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
            case TYPE_SHARE_Weibo:
                shareToWeibo(url);
                break;
            case TYPE_SHARE_QQ:
                shareToQQ(url);
                break;
            case TYPE_SHARE_QZone:
                shareToQZone(url);
                break;
            case TYPE_SHARE_LINK:
                DataUtil.copyContent(this, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWeibo(shareHandler, url, essayTitle, shareContent, thumb);
    }

    /**
     * 分享失败
     */
    private void errorShare() {
        showTips("分享失败，请稍后重试");
    }

    /**
     * 获取精读数据
     */
    private static class GetData
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected GetData(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        activity.wordC = data.getString("cross");
                        activity.LineC = data.getString("lineC");
                        activity.essayTitle = data.getString("title");
                        activity.essayContent = data.getString("content");
                        activity.afterReadingId = data.getString("feelingid");
                        if (activity.afterReadingId != null
                                && !activity.afterReadingId.equals("")
                                && !activity.afterReadingId.equals("null")) {
                            activity.ll_write.setVisibility(View.GONE);
                            new GetAfterReadingInfo(activity).execute(activity.afterReadingUrl +
                                    "feelingId=" + activity.afterReadingId +
                                    "&studentId=" + NewMainActivity.STUDENT_ID);
                        } else {
                            activity.ll_after_reading.setVisibility(View.GONE);
                        }
                        if (activity.isFirstLoading) {
                            activity.isFirstLoading = false;
                            activity.waitUser();
                        } else {
                            activity.updateUI();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取读后感数据
     */
    private static class GetAfterReadingInfo
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected GetAfterReadingInfo(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            //获取数据之后
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                //获取数据之后
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        JSONObject json = jsonObject.getJSONObject("data");
                        activity.afterReadingContent = json.getString("feeling");
                        activity.isPriviate = json.optInt("ispriviate", 1);
                        activity.showAfterReadingContent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加读后感
     */
    private static class InsertAfterReading
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected InsertAfterReading(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            //获取数据之后
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    if (200 == json.optInt("status", -1)) {
                        if (!activity.isUpdateAfterReading) {
                            JSONObject data = json.getJSONObject("data");
                            activity.afterReadingId = data.getString("id");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 划线操作
     */
    private static class UnderLine
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected UnderLine(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            //获取数据之后
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                activity.analyzeAddExcerptData(s);
            }
        }
    }

    /**
     * 分析添加到生词本数据
     */
    private void analyzeAddExcerptData(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (200 == json.optInt("status", -1)) {
                showTips("添加成功");
            } else if (400 == json.optInt("status", -1)) {
                if (s.contains("该词已在生词本中")) {
                    showTips("该词已在生词本中");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 词语解释
     */
    private static class WordExplain
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected WordExplain(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            //获取数据之后
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                //获取数据之后
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        JSONObject json = jsonObject.getJSONObject("data");
                        String content = json.getString("annotation");
                        activity.showExplain(content);
                    } else if (400 == jsonObject.optInt("status", -1)) {
                        activity.noContent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    activity.noContent();
                }
            }
        }
    }

    /**
     * 获取笔记内容
     */
    private static class GetNoteContent
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected GetNoteContent(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute(IntensiveReadingActivity activity) {
            activity.note = "";
            activity.isNoteOk = false;
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        activity.note = data.getString("note");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            activity.isNoteOk = true;
            if (activity.isAllContent) {
                if (activity.isGrammarOk) {
                    activity.showAllContent();
                }
            } else {
                activity.showNoteContent();
            }
        }
    }

    /**
     * 添加语法
     */
    private static class AddGrammar
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected AddGrammar(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                int len = activity.endGrammar - activity.startGrammar;
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("essayId", activity.essayId);
                json.put("content", params[1]);
                json.put("grammar", params[2]);
                json.put("loc", activity.startGrammar);
                json.put("len", len);
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            activity.position = -1;
            activity.startGrammar = 0;
            activity.endGrammar = 0;
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        MyToastUtil.showToast(activity, "更新成功");
                        activity.initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 修改语法
     */
    private static class UpdateGrammar
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected UpdateGrammar(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("grammarId", activity.gid);
                json.put("grammar", params[1]);
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            activity.position = -1;
            activity.startGrammar = 0;
            activity.endGrammar = 0;
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        MyToastUtil.showToast(activity, "更新成功");
                        activity.initData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取语法内容
     */
    private static class GetGrammarContent
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected GetGrammarContent(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute(IntensiveReadingActivity activity) {
            super.onPreExecute(activity);
            activity.grammar = "";
            activity.isGrammarOk = false;
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            if (s == null) {
                MyToastUtil.showToast(activity, "请求超时");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.optInt("status", -1)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        activity.grammar = data.getString("grammar");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            activity.isGrammarOk = true;
            if (activity.isAllContent) {
                if (activity.isNoteOk) {
                    activity.showAllContent();
                }
            } else {
                activity.showGrammarContent();
            }
        }
    }

    /**
     * 删除笔记数据
     */
    private static class DeleteNoteData
            extends WeakAsyncTask<String, Integer, String, IntensiveReadingActivity> {

        protected DeleteNoteData(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] params) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("studentId", NewMainActivity.STUDENT_ID);
                json.put("essayId", activity.essayId);
                json.put("status", 0);
                json.put("notes", params[1]);
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {

        }
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, IntensiveReadingActivity> {

        protected GetShareHtml(IntensiveReadingActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(IntensiveReadingActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("noteId", strings[1]);
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
        protected void onPostExecute(IntensiveReadingActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
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

        if (mSelectableTextHelper != null) {
            mSelectableTextHelper.destroy();
        }

        super.onDestroy();
    }
}
