package com.dace.textreader.activity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dace.textreader.GlideApp;
import com.dace.textreader.R;
import com.dace.textreader.adapter.AuthorDetailAdapter;
import com.dace.textreader.adapter.AuthorWorksAdapter;
import com.dace.textreader.bean.AuthorDetailBean;
import com.dace.textreader.bean.AuthorWorksBean;
import com.dace.textreader.util.CustomController;
import com.dace.textreader.util.GlideUtils;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.weight.pullrecycler.ExpandableTextView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AuthorDetailActivity extends BaseActivity implements View.OnClickListener{

    private String url = HttpUrlPre.HTTP_URL_ + "/select/author/detail";
    private String worksUrl = HttpUrlPre.HTTP_URL_ + "/select/author/article/list";
    private String authorId;
    private ExpandableTextView expandableTextView;
//    private ImageView iv_topimg ,iv_playvideo;
    private RelativeLayout rl_back_copy;
    private ImageView iv_author,iv_audio;
    private TextView tv_author,tv_follow;
    private RecyclerView rcl_author_detail,rcl_author_works;
    private List<AuthorDetailBean.DataBean.DescriptionListBean> detailList = new ArrayList<>();
    private List<AuthorWorksBean.DataBean> worksList = new ArrayList<>();
    private AuthorDetailAdapter authorDetailAdapter;
    private AuthorWorksAdapter authorWorksAdapter;
    private NiceVideoPlayer videoPlayer;
    private CustomController controller;
    private NestedScrollView scroll_view;
    private MediaPlayer mPlayer;
    private String audioUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        initData();
        initView();
        initEvents();
    }

    private void initData() {
        authorId = getIntent().getStringExtra("authorId");
    }

    private void initView() {
        expandableTextView = findViewById(R.id.expand_text_view);
//        iv_topimg = findViewById(R.id.iv_topimg);
//        iv_topimg = findViewById(R.id.iv_topimg);
//        iv_playvideo = findViewById(R.id.iv_playvideo);
        rl_back_copy = findViewById(R.id.rl_back_copy);
        iv_author = findViewById(R.id.iv_author);
        iv_audio = findViewById(R.id.iv_audio);
        tv_author = findViewById(R.id.tv_author);
        tv_follow = findViewById(R.id.tv_follow);
        rcl_author_detail = findViewById(R.id.rcl_author_detail);
        rcl_author_works = findViewById(R.id.rcl_author_works);

        mPlayer = new MediaPlayer();
        scroll_view = findViewById(R.id.scroll_view);
        videoPlayer = findViewById(R.id.videoplayer);
        controller = new CustomController(this);
        controller.setOnScreenChangeListener(new CustomController.OnScreenChangeListener() {
            @Override
            public void onNormal() {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        scroll_view.scrollTo(0,0);
                    }
                }, 500);

            }

            @Override
            public void onFullScreen() {

            }
        });

        authorDetailAdapter = new AuthorDetailAdapter(this,detailList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcl_author_detail.setLayoutManager(linearLayoutManager);
        rcl_author_detail.setAdapter(authorDetailAdapter);

        authorWorksAdapter = new AuthorWorksAdapter(this,worksList);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcl_author_works.setLayoutManager(linearLayoutManager1);
        rcl_author_works.setAdapter(authorWorksAdapter);

        expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
            }
        });

        getData();
        getWorksData();
    }



    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("authorId",authorId);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("width",750);
            params.put("height",420);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(url, OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AuthorDetailBean authorDetailBean = GsonUtil.GsonToBean(result.toString(),AuthorDetailBean.class);
                audioUrl = authorDetailBean.getData().getFemaleAudio();
                String text = authorDetailBean.getData().getContent();
                String textTest = text.replaceAll("\n","\n\n");
                List<AuthorDetailBean.DataBean.DescriptionListBean> descriptionListBeanList = authorDetailBean.getData().getDescriptionList();
                if(descriptionListBeanList != null)
                    detailList.addAll(descriptionListBeanList);
                authorDetailAdapter.notifyDataSetChanged();
                expandableTextView.setText(textTest);
                GlideUtils.loadUserImage(AuthorDetailActivity.this,authorDetailBean.getData().getImage(),iv_author);
                tv_author.setText(authorDetailBean.getData().getAuthor());
                if (authorDetailBean.getData().getVideo()!= null){
                    videoPlayer.setVisibility(View.VISIBLE);
                    GlideApp.with(AuthorDetailActivity.this)
                            .load(authorDetailBean.getData().getVideo().getImg())
                            .placeholder(R.drawable.img_default)
                            .into(controller.imageView());
                    controller.setTitle(authorDetailBean.getData().getVideo().getTitle());
                        String videoUrl = authorDetailBean.getData().getVideo().getLink();
                        videoUrl = videoUrl.replaceAll("https","http");
                        videoPlayer.setUp(videoUrl, null);
                        videoPlayer.setController(controller);

                }
//                    GlideUtils.loadHomeImage(AuthorDetailActivity.this,authorDetailBean.getData().getVideo().getImg(),iv_topimg);
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void getWorksData() {
        JSONObject params = new JSONObject();
        try {
            params.put("authorId",authorId);
            params.put("studentId",NewMainActivity.STUDENT_ID);
            params.put("pageNum",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpManager.getInstance(this).requestAsyn(worksUrl, OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                AuthorWorksBean authorWorksBean = GsonUtil.GsonToBean(result.toString(),AuthorWorksBean.class);
                List<AuthorWorksBean.DataBean> dataBeans = authorWorksBean.getData();
                if(dataBeans != null)
                worksList.addAll(dataBeans);
                authorWorksAdapter.notifyDataSetChanged();
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void initEvents() {
        rl_back_copy.setOnClickListener(this);
        iv_audio.setOnClickListener(this);
        tv_author.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back_copy:
                finish();
                break;
            case R.id.iv_audio:
                play(audioUrl);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    /**
     * 播放，这种是直接传音频实体类
     * 有两种，一种是播放本地播放，另一种是在线播放
     *
     * @param music music
     */
    public void play(String music) {

        try {
            mPlayer.reset();
            //把音频路径传给播放器
            mPlayer.setDataSource(music);
            //准备
            mPlayer.prepareAsync();
            //监听
            mPlayer.setOnPreparedListener(mOnPreparedListener);
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mPlayer != null) {
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 音频准备好的监听器
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        /** 当音频准备好可以播放了，则这个方法会被调用  */
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        //销毁MediaPlayer
        if(mPlayer != null){
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
