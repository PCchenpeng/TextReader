package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dace.textreader.R;
import com.dace.textreader.adapter.PlayerRecyclerViewAdapter;
import com.dace.textreader.audioUtils.OnPlayerEventListener;
import com.dace.textreader.bean.LessonBean;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DateUtil;
import com.dace.textreader.util.GlideRoundImage;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.StatusBarUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 微课播放详情
 */
public class AudioPlayerActivity extends BaseActivity implements View.OnClickListener {

    private static final String shareUrl = HttpUrlPre.HTTP_URL + "/get/share/course/";

    private RelativeLayout rl_back;
    private RelativeLayout rl_share;
    private ImageView iv_lesson;
    private TextView tv_lesson_media;
    private TextView tv_teacher_media;
    private SeekBar seekBar;
    private TextView tv_cur;
    private TextView tv_max;
    private ImageView iv_pre;
    private ImageView iv_play;
    private ImageView iv_next;
    private ImageView iv_list;
    private ImageView iv_text;

    private AudioPlayerActivity mContext;

    private RelativeLayout rl_list;
    private RecyclerView recyclerView_dialog;
    private TextView tv_cancel_dialog;
    private PlayerRecyclerViewAdapter adapter_dialog;
    private List<LessonBean> mList = new ArrayList<>();

    private String teacher_name;
    private LessonBean lessonBean;
    private Bitmap lessonBitmap;

    private boolean fromUserChange = false;
    private int mediaDuration = 0;

    private boolean fromMicroLesson = false;  //是否是从微课详情进入的
    private boolean turnMicroLesson = false;  //是否是进入微课详情
    private LinearLayoutManager layoutManager;

    private final int TYPE_SHARE_WX_FRIEND = 1;  //微信好友
    private final int TYPE_SHARE_WX_FRIENDS = 2;  //微信朋友圈
    private final int TYPE_SHARE_QQ = 3;  //qq
    private final int TYPE_SHARE_QZone = 4;  //qq空间
    private final int TYPE_SHARE_LINK = 5;  //复制链接
    private final int TYPE_SHARE_Weibo = 6;
    private int type_share = -1;  //分享类型

    private WbShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        mContext = this;

        //修改状态栏的文字颜色为黑色
        int flag = StatusBarUtil.StatusBarLightMode(mContext);
        StatusBarUtil.StatusBarLightMode(mContext, flag);

        teacher_name = NewMainActivity.lessonTeacher;
        fromMicroLesson = getIntent().getBooleanExtra("fromMicro", false);

        initView();
        initData();
        initDialogView();
        initPlayServiceListener();
        initEvents();

        shareHandler = new WbShareHandler(mContext);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));

    }

    private void initDialogView() {
        rl_list = findViewById(R.id.rl_player_list_layout);
        recyclerView_dialog = findViewById(R.id.recycler_view_player_list);
        layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        adapter_dialog = new PlayerRecyclerViewAdapter(mContext, mList);
        recyclerView_dialog.setLayoutManager(layoutManager);
        recyclerView_dialog.setAdapter(adapter_dialog);

        tv_cancel_dialog = findViewById(R.id.tv_cancel_player_list);

        adapter_dialog.setOnPlayerItemClickListener(new PlayerRecyclerViewAdapter.OnPlayerItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (getPlayService() != null) {
                    int pos = recyclerView_dialog.getChildAdapterPosition(view);
                    getPlayService().play(pos);
                    if (mList.get(pos).getFree() != 0) {
                        updateList(pos);
                    }
                }
            }
        });
    }

    /**
     * 更新列表
     *
     * @param pos
     */
    private void updateList(int pos) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setPlaying(false);
        }
        if (pos > -1 && pos < mList.size() - 1) {
            mList.get(pos).setPlaying(true);
        }
        adapter_dialog.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPlayService() != null) {
            getPlayService().hideFloatView();
            if (getPlayService().isPlaying()) {
                iv_play.setImageResource(R.drawable.icon_media_player_pause);
            } else {
                iv_play.setImageResource(R.drawable.icon_media_player_start);
            }
            if (getPlayService().isPausing()) {
                int[] time = getPlayService().getCurrentPosition();
                tv_cur.setText(DateUtil.formatterTime(time[0]));
                mediaDuration = time[1];
                int p = 100 * time[0] / mediaDuration;
                seekBar.setProgress(p);
            }
        }
    }

    private void initData() {
        if (getPlayService() != null) {
            lessonBean = getPlayService().getPlayingMusic();
            mList = getPlayService().getMusicList();
        }
        updateUi();
    }

    private void updateUi() {
        iv_play.setImageResource(R.drawable.icon_media_player_pause);
        if (!isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .transform(new GlideRoundImage(mContext, 8))
                    .placeholder(R.drawable.image_placeholder_square)
                    .error(R.drawable.image_micro_lesson_default);
            Glide.with(mContext)
                    .load(lessonBean.getImage())
                    .apply(options)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource,
                                                    @Nullable Transition<? super Drawable> transition) {
                            iv_lesson.setImageDrawable(resource);
                        }
                    });
        }
        tv_lesson_media.setText(lessonBean.getName());
        tv_teacher_media.setText(teacher_name);
        tv_cur.setText("00:00");
        tv_max.setText(lessonBean.getDuration());
    }

    private void initEvents() {
        rl_back.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        iv_lesson.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_list.setOnClickListener(this);
        iv_text.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
                } else {
                    this.progress = -1;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                fromUserChange = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fromUserChange = false;
                // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
                if (progress != -1) {
                    int pro = progress * mediaDuration / seekBar.getMax();
                    if (getPlayService() != null) {
                        getPlayService().seekTo(pro);
                    }
                }
            }
        });

        rl_list.setOnClickListener(this);
        tv_cancel_dialog.setOnClickListener(this);
    }

    private void initView() {
        rl_back = findViewById(R.id.rl_back_audio_player);
        rl_share = findViewById(R.id.rl_share_audio_player);
        iv_lesson = findViewById(R.id.iv_micro_lesson_image_audio_player);
        tv_lesson_media = findViewById(R.id.tv_title_audio_player);
        tv_teacher_media = findViewById(R.id.tv_micro_teacher_audio_player);
        seekBar = findViewById(R.id.seek_bar_media_audio_player);
        tv_cur = findViewById(R.id.tv_cur_media_time_audio_player);
        tv_max = findViewById(R.id.tv_max_media_time_audio_player);
        iv_pre = findViewById(R.id.iv_pre_audio_player);
        iv_play = findViewById(R.id.iv_player_audio_player);
        iv_next = findViewById(R.id.iv_next_audio_player);
        iv_list = findViewById(R.id.iv_list_audio_player);
        iv_text = findViewById(R.id.iv_text_audio_player);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back_audio_player:
                finish();
                break;
            case R.id.rl_share_audio_player:
                showShareDialog();
                break;
            case R.id.iv_micro_lesson_image_audio_player:
                if (fromMicroLesson) {
                    finish();
                } else {
                    turnToMicroLesson();
                }
                break;
            case R.id.iv_pre_audio_player:
                if (getPlayService() != null) {
                    getPlayService().prev();
                }
                break;
            case R.id.iv_player_audio_player:
                if (getPlayService() != null) {
                    getPlayService().playPause();
                }
                break;
            case R.id.iv_next_audio_player:
                if (getPlayService() != null) {
                    getPlayService().next();
                }
                break;
            case R.id.iv_list_audio_player:
                showAudioList(true);
                break;
            case R.id.rl_player_list_layout:
                showAudioList(false);
                break;
            case R.id.tv_cancel_player_list:
                showAudioList(false);
                break;
            case R.id.iv_text_audio_player:
                turnToLessonText();
                break;
        }
    }

    /**
     * 前往文稿界面
     */
    private void turnToLessonText() {
        if (lessonBean.getStatus() == 0) {
            MyToastUtil.showToast(mContext, "该节课程暂无文稿");
        } else {
            Intent intent = new Intent(mContext, LessonTextActivity.class);
            intent.putExtra("lessonId", lessonBean.getId());
            startActivity(intent);
        }
    }

    /**
     * 显示播放列表
     */
    private void showAudioList(boolean showOrNot) {
        if (showOrNot) {
            rl_list.setVisibility(View.VISIBLE);
        } else {
            rl_list.setVisibility(View.GONE);
        }
    }

    /**
     * 前往微课界面
     */
    private void turnToMicroLesson() {
        turnMicroLesson = true;
        Intent intent = new Intent(mContext, MicroLessonActivity.class);
        intent.putExtra("id", NewMainActivity.lessonId);
        startActivity(intent);
        finish();
    }

    /**
     * 显示分享对话框
     */
    private void showShareDialog() {
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
                                getShareHtml(TYPE_SHARE_WX_FRIEND);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_WX_FRIENDS);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(mContext)) {
                                    getShareHtml(TYPE_SHARE_Weibo);
                                } else {
                                    MyToastUtil.showToast(mContext, "请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QQ);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_QZone);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getShareHtml(TYPE_SHARE_LINK);
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
     * 准备Bitmap
     */
    private void prepareBitmap() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = ImageUtils.GetNetworkBitmap(lessonBean.getImage());
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_micro_lesson_default);
                }
                lessonBitmap = Bitmap.createScaledBitmap(bitmap, 108, 108, true);
                bitmap.recycle();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new GetShareHtml(mContext)
                    .execute(shareUrl, String.valueOf(NewMainActivity.lessonId));
        }
    };

    /**
     * 获取分享链接
     *
     * @param type_share
     */
    private void getShareHtml(int type_share) {
        MyToastUtil.showToast(mContext, "正在准备分享内容...");
        this.type_share = type_share;
        if (type_share == TYPE_SHARE_WX_FRIEND || type_share == TYPE_SHARE_WX_FRIENDS) {
            prepareBitmap();
        } else {
            new GetShareHtml(this)
                    .execute(shareUrl, String.valueOf(NewMainActivity.lessonId));
        }
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, NewMainActivity.lessonTitle,
                NewMainActivity.lessonContent, lessonBean.getImage());
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, NewMainActivity.lessonTitle,
                NewMainActivity.lessonContent, lessonBean.getImage());
    }

    /**
     * 分享文章到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {
        if (lessonBitmap == null) {
            lessonBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image_micro_lesson_default);
        }

        ShareUtil.shareToWx(mContext, url, NewMainActivity.lessonTitle, NewMainActivity.lessonContent,
                ImageUtils.bmpToByteArray(lessonBitmap, true), friend);
    }

    /**
     * 初始化服务播放音频播放进度监听器
     * 这个是要是通过监听即时更新主页面的底部控制器视图
     * 同时还要同步播放详情页面mPlayFragment的视图
     */
    public void initPlayServiceListener() {
        if (getPlayService() == null) {
            return;
        }
        getPlayService().setOnPlayEventListener(new OnPlayerEventListener() {
            /**
             * 切换歌曲
             * 主要是切换歌曲的时候需要及时刷新界面信息
             */
            @Override
            public void onChange(int position, LessonBean music) {
                lessonBean = music;
                updateUi();
                updateList(position);
            }

            /**
             * 继续播放
             * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
             */
            @Override
            public void onPlayerStart() {
                iv_play.setImageResource(R.drawable.icon_media_player_pause);
            }

            /**
             * 暂停播放
             * 主要是切换歌曲的时候需要及时刷新界面信息，比如播放暂停按钮
             */
            @Override
            public void onPlayerPause() {
                iv_play.setImageResource(R.drawable.icon_media_player_start);
            }

            /**
             * 更新进度
             * 主要是播放音乐或者拖动进度条时，需要更新进度
             */
            @Override
            public void onUpdateProgress(int progress, int duration) {
                tv_cur.setText(DateUtil.formatterTime(progress));
                if (!fromUserChange) {
                    mediaDuration = duration;
                    int p = 100 * progress / duration;
                    seekBar.setProgress(p);
                }
            }

            @Override
            public void onBufferingUpdate(int percent) {
                int per = percent * 100;
                seekBar.setSecondaryProgress(per);
            }

        });
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
                DataUtil.copyContent(mContext, url);
                break;
        }
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        if (lessonBitmap == null) {
            lessonBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image_micro_lesson_default);
        }

        ShareUtil.shareToWeibo(shareHandler, url, NewMainActivity.lessonTitle,
                NewMainActivity.lessonContent, lessonBitmap);

    }

    /**
     * 分享失败
     */
    private void errorShare() {
        MyToastUtil.showToast(mContext, "分享失败，请稍后重试");
    }

    /**
     * 获取分享的链接
     */
    private static class GetShareHtml
            extends WeakAsyncTask<String, Void, String, AudioPlayerActivity> {

        protected GetShareHtml(AudioPlayerActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(AudioPlayerActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("id", strings[1]);
                object.put("name", "share_course");
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
        protected void onPostExecute(AudioPlayerActivity activity, String s) {
            if (s == null) {
                activity.errorShare();
            } else {
                activity.analyzeShareData(s);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (rl_list.getVisibility() == View.VISIBLE) {
            showAudioList(false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (getPlayService() != null) {
            if (getPlayService().isPausing() || getPlayService().isDefault()) {
                getPlayService().hideFloatView();
            } else {
                if (fromMicroLesson || turnMicroLesson) {
                    getPlayService().hideFloatView();
                } else {
                    getPlayService().showFloatView();
                }
            }
        }
        if (lessonBitmap != null) {
            lessonBitmap.recycle();
            lessonBitmap = null;
        }
        super.onDestroy();
    }
}
