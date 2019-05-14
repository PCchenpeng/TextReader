package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.dace.textreader.R;
import com.dace.textreader.audioUtils.AudioFocusManager;
import com.dace.textreader.bean.AudioArticleBean;
import com.dace.textreader.util.DataEncryption;
import com.dace.textreader.util.DataUtil;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.util.GsonUtil;
import com.dace.textreader.util.HttpUrlPre;
import com.dace.textreader.util.ImageUtils;
import com.dace.textreader.util.MyToastUtil;
import com.dace.textreader.util.ShareUtil;
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.util.okhttp.OkHttpManager;
import com.dace.textreader.view.dialog.BaseNiceDialog;
import com.dace.textreader.view.dialog.NiceDialog;
import com.dace.textreader.view.dialog.ViewConvertListener;
import com.dace.textreader.view.dialog.ViewHolder;
import com.dace.textreader.view.weight.pullrecycler.album.AlbumView;
import com.dace.textreader.view.weight.pullrecycler.album.CurlPage;
import com.dace.textreader.view.weight.pullrecycler.album.OnFlipedLastPageListener;
import com.dace.textreader.view.weight.pullrecycler.album.OnPageClickListener;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeAudioDetailActivity extends BaseActivity implements View.OnClickListener,OnPageClickListener, OnFlipedLastPageListener {

    private String essayId;
    private int pyNum;

    private AudioArticleBean mData;
    private String audioUrl;
    private int pageNum =1;
    private String url = HttpUrlPre.HTTP_URL_ + "/select/article/detail";
    private boolean isAudioComplete;

    /**
     * 播放器
     */
    private MediaPlayer mPlayer;

    /**
     * 捕获/丢弃音乐焦点处理
     */
    private AudioFocusManager mAudioFocusManager;

    private AlbumView album_view;
    private ImageView iv_back,iv_share,iv_playpause,iv_collect,iv_fullscreen;
    private TextView tv_currNum,tv_totalNum;
    private int lastIndex = -1;
    private int currentIndex = 0;
    Bitmap shadowLine;

    Bitmap[] frontBacks = new Bitmap[4];
    Bitmap[] pages = new Bitmap[2];
    private int albumSize;

    List<Bitmap> splitBitmap = new ArrayList<>();

    List<Bitmap> imageList;
    private Bitmap[] imageArray;

    private boolean isLoadingImg = false;

    private boolean hasRender = false;

    private boolean isPortrait = true;

    private Thread musicThread;

    private boolean isOnPageScroll = false;

    private String title;
    private Bitmap shareBitmap;
    private String shareImgUrl;
    private WbShareHandler shareHandler;
    private String shareContent = "";
    private String shareQQUrl;
    private String shareWXUrl;
    private String shareWBUrl;

    private boolean isDataComplete;
    private boolean isCollected;

    private String collectUrl = HttpUrlPre.HTTP_URL_ + "/insert/essay/collect";
    private String deleteCollectUrl = HttpUrlPre.HTTP_URL_ + "/delete/essay/collect" ;

    private int bitmipHeight;
    private int bitmipWidth;
    private boolean islandspaceBitmap;//是否是高度大于宽度的图片

    private ImageView iv_playpause_land,iv_fullscreen_land;
    private TextView tv_currNum_land,tv_totalNum_land;
    private RelativeLayout rl_bottom,rl_bottom_land;
    private SeekBar seekBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_audio_detail);
//        essayId = "10000004";
        essayId = getIntent().getStringExtra("id");
        pyNum = getIntent().getIntExtra("py",-1);

        initView();
//        album_view.setZOrderMediaOverlay(true);
        mPlayer = new MediaPlayer();

        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
        shareHandler.setProgressColor(Color.parseColor("#ff9933"));
        loadData(essayId);
    }


    @Override

    public void onConfigurationChanged (Configuration newConfig){

        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_home_audio_detail);

        //注意，这里删除了init()，否则又初始化了，状态就丢失

        initView();
        hasRender =false;
        tv_currNum.setText(String.valueOf(currentIndex+1));
        initAlbumView(currentIndex);

        if(isCollected){
            if(isPortrait){
                if(isPortrait){
                    iv_collect.setImageResource(R.drawable.picbook_icon_collect_selected);
                }else {
                    iv_collect.setImageResource(R.drawable.icon_bg_collect_select);
                }
            }
        }

    }


    private void initView() {
        album_view = findViewById(R.id.album_view);
        if(isPortrait){
            album_view.setZOrderOnTop(true);
        }else {
            album_view.setZOrderOnTop(true);
            album_view.setZOrderMediaOverlay(true);
        }
        iv_back = findViewById(R.id.iv_back);
        iv_share = findViewById(R.id.iv_share);
        iv_playpause = findViewById(R.id.iv_playpause);
        iv_collect = findViewById(R.id.iv_collect);
        iv_fullscreen = findViewById(R.id.iv_fullscreen);
        tv_currNum = findViewById(R.id.tv_currNum);
        tv_totalNum = findViewById(R.id.tv_totalNum);
        rl_bottom = findViewById(R.id.rl_bottom);

        iv_back.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_playpause.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        if (getLastCustomNonConfigurationInstance() != null) {
            lastIndex = (Integer) getLastCustomNonConfigurationInstance();
        }
        if(isPortrait){
            iv_playpause_land = findViewById(R.id.iv_playpause_land);
            iv_fullscreen_land = findViewById(R.id.iv_fullscreen_land);
            rl_bottom_land = findViewById(R.id.rl_bottom_land);
            tv_currNum_land = findViewById(R.id.tv_currNum_land);
            tv_totalNum_land = findViewById(R.id.tv_totalNum_land);
            seekBar = findViewById(R.id.seek_bar);
            iv_playpause_land.setOnClickListener(this);
            iv_fullscreen_land.setOnClickListener(this);
            seekBar.getThumb().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);//设置滑块颜色、样式

            seekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);//设置进度条颜色、样式

        }
    }

    private void loadData(String essayId) {
        JSONObject params = new JSONObject();

        try {
            params.put("studentId", NewMainActivity.STUDENT_ID);
            params.put("gradeId", NewMainActivity.GRADE_ID);
            params.put("width", 420);
            params.put("height", 750);
            params.put("essayId",essayId);
            params.put("isShare",0);
            params.put("py",pyNum);
            params.put("sign",DataEncryption.encode(String.valueOf(System.currentTimeMillis()),"Z25pYW5l"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(url,OkHttpManager.TYPE_POST_JSON,params, new OkHttpManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                analyzeRecommendData(result.toString());
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                if(isPortrait){
                    this.finish();
                }else {
                    currentIndex = album_view.getCurrentIndex();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isPortrait = true;
                }

                break;
            case R.id.iv_share:
                if(isDataComplete){
                    shareNote();
                }else {
                    MyToastUtil.showToast(this,"请等待数据加载完成之后再试");
                }

                break;
            case R.id.iv_collect:
                if(isDataComplete){
                    if(isCollected)
                        deleteCollect();
                    else
                        collectedArticle();
                }else {
                    showTips("请等待页面加载完成");
                }
                break;
            case R.id.iv_fullscreen:
                currentIndex = album_view.getCurrentIndex();
                if(isPortrait){
                    if(islandspaceBitmap){
                        int width = DensityUtil.getScreenWidth(this);

                        int height = (int)((float)bitmipHeight/bitmipWidth * width);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) album_view.getLayoutParams();
                        layoutParams.width = width;
                        layoutParams.height = height;
                        album_view.setLayoutParams(layoutParams);
                        rl_bottom.setVisibility(View.GONE);
                        rl_bottom_land.setVisibility(View.VISIBLE);
                    }else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        isPortrait = false;
                    }

                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isPortrait = true;
                }

                break;
            case R.id.iv_playpause_land:
            case R.id.iv_playpause:
                if(isPortrait){
//                    if(mPlayer.get){}
//                    iv_playpause.setImageResource(R.drawable.picbook_btn_pause);
                    if(mPlayer.isPlaying()){
                        mPlayer.pause();
                        iv_playpause.setImageResource(R.drawable.picbook_btn_play);
                        iv_playpause_land.setImageResource(R.drawable.video_icon_play);
                    }else {
                        mPlayer.start();
                        iv_playpause.setImageResource(R.drawable.picbook_btn_pause);
                        iv_playpause_land.setImageResource(R.drawable.video_icon_pause);
                    }
                }else {
                    if(mPlayer.isPlaying()){
                        mPlayer.pause();
                        iv_playpause.setImageResource(R.drawable.video_icon_play);
                    }else {
                        mPlayer.start();
                        iv_playpause.setImageResource(R.drawable.video_icon_pause);
                    }
                }
                break;
            case R.id.iv_fullscreen_land:
                rl_bottom_land.setVisibility(View.GONE);
                rl_bottom.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onFlippedLastPage() {

    }

    @Override
    public void onClickPage(int mCurrentIndex, boolean isFront) {

    }

    @Override
    public boolean validEnableClick(int mCurrentIndex, boolean isFront) {
        return false;
    }

    private class SizeChangedObserver implements AlbumView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
//            album_view.setMargins(.1f, .05f, .1f, .2f);
        }

    }

    private class PageProvider implements AlbumView.PageProvider {


        @Override
        public Bitmap[] getFrontBitmap() {

            Bitmap front = getTexture(frontBacks[0], true, true, false);
            Bitmap back = getTexture(frontBacks[1], false, true, false);

            Bitmap[] covers = new Bitmap[2];
            covers[0] = front;
            covers[1] = back;

            return covers;
        }

        @Override
        public Bitmap[] getBackBitmap() {
            Bitmap front = getTexture(frontBacks[2], true, false, false);
            Bitmap back = getTexture(frontBacks[3], false, false, false);

            Bitmap[] covers = new Bitmap[2];
            covers[0] = front;
            covers[1] = back;

            return covers;
        }

        @Override
        public int getPageCount() {
            return albumSize / 2 - 1;    //pageSize/2  因为是正反面 - 2  减去封面和尾页
        }

        private Bitmap getTexture(Bitmap bitmap, boolean isFront, boolean isFirst, boolean isPage) {
            // Bitmap original size.
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            // Bitmap size expanded to next power of two. This is done due to
            // the requirement on many devices, texture width and height should
            // be power of two.
            int newW = getNextHighestPO2(w);
            int newH = getNextHighestPO2(h);

            // TODO: Is there another way to create a bigger Bitmap and copy
            // original Bitmap to it more efficiently? Immutable bitmap anyone?
            Bitmap bitmapTex = Bitmap.createBitmap(newW, newH, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bitmapTex);
            c.drawBitmap(bitmap, 0, 0, null);


            float texX = (float) w / newW;
            float texY = (float) h / newH;

            float[] rect = new float[4];
            rect[0] = 0;
            rect[1] = 0;
            rect[2] = texX;
            rect[3] = texY;

            if (!isPage) {
                if (isFirst) {
                    if (isFront) {
                        album_view.setFirstFrontRect(rect);
                    } else {
                        album_view.setFirstBackRect(rect);
                    }
                } else {
                    if (isFront) {
                        album_view.setLastFrontRect(rect);
                    } else {
                        album_view.setLastBackRect(rect);
                    }
                }
            }

            return bitmapTex;
        }

        private int getNextHighestPO2(int n) {
            n -= 1;
            n = n | (n >> 1);
            n = n | (n >> 2);
            n = n | (n >> 4);
            n = n | (n >> 8);
            n = n | (n >> 16);
            n = n | (n >> 32);
            return n + 1;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {
            isOnPageScroll = true;
            Bitmap front = splitBitmap.get(index*2 +1);
            Bitmap back = splitBitmap.get(index*2+2);
            switch (index) {
                default: {
                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setTexture(back, CurlPage.SIDE_BACK);
                    break;
                }
            }
        }

    }

    /**
     * 分析推荐数据
     *
     * @param s
     */
    private void analyzeRecommendData(String s) {
        mData = GsonUtil.GsonToBean(s,AudioArticleBean.class);
        audioUrl = mData.getData().getEssay().getAudio();
        title = mData.getData().getEssay().getTitle();
        isCollected = mData.getData().getCollectOrNot() == 1;
        if(isCollected){
            iv_collect.setImageResource(R.drawable.picbook_icon_collect_selected);
            if(isPortrait){
                if(isPortrait){

                }else {
                    iv_collect.setImageResource(R.drawable.icon_bg_collect_select);
                }
            }
        }

        play(audioUrl);
        SpliterImg(mData);
        AudioArticleBean.DataBean.ShareListBean shareListBean =  mData.getData().getShareList();
        if(shareListBean != null){
            shareQQUrl = shareListBean.getQq().getLink();
            shareWXUrl = shareListBean.getWx().getLink();
            shareWBUrl = shareListBean.getWeibo().getLink();
            shareImgUrl = shareListBean.getWx().getImage();
            prepareBitmap(shareImgUrl);
            isDataComplete = true;
        }

    }

    private void SpliterImg(final AudioArticleBean mData) {
        int size = mData.getData().getEssay().getContentList().size();
        imageArray = new Bitmap[size];
//        imageList = new ArrayList<>();
        String lastUrl = mData.getData().getEssay().getContentList().get(size-1).getPic();
        new DownloadImgTask().execute(lastUrl,String.valueOf(size-1),String.valueOf(size));
        String firstUrl = mData.getData().getEssay().getContentList().get(0).getPic();
        new DownloadImgTask().execute(firstUrl,String.valueOf(0),String.valueOf(size));
        String secondUrl = mData.getData().getEssay().getContentList().get(1).getPic();
        new DownloadImgTask().execute(secondUrl,String.valueOf(1),String.valueOf(size));
        for (int i = 2; i < size-1;i++){
            String url = mData.getData().getEssay().getContentList().get(i).getPic();
                new DownloadImgTask().execute(url,String.valueOf(i),String.valueOf(size));
        }
    }

    private void initAlbumView(int currentIndex) {
        splitBitmap = new ArrayList<>();

        for (int i = 0;i < imageArray.length;i++){
            if(imageArray[i] == null){
                List<Bitmap>   splitPages = split(BitmapFactory.decodeResource(getResources(), R.drawable.picbook_placeholder),2,1);
                splitBitmap.add(splitPages.get(0));
                splitBitmap.add(splitPages.get(1));
            }else {
                List<Bitmap>   splitPages = split(imageArray[i],2,1);
                splitBitmap.add(splitPages.get(0));
                splitBitmap.add(splitPages.get(1));
            }
        }

        albumSize = splitBitmap.size();

        if(isPortrait){
            tv_totalNum.setText("/"+String.valueOf(albumSize/2));
            tv_totalNum_land.setText(String.valueOf(albumSize/2));
            seekBar.setMax(albumSize/2-1);
        }else {
            tv_totalNum.setText(String.valueOf(albumSize/2));
        }


        frontBacks[0] = splitBitmap.get(0);
        frontBacks[1] = splitBitmap.get(0);
        frontBacks[2] = splitBitmap.get(splitBitmap.size()-1);
        frontBacks[3] = splitBitmap.get(splitBitmap.size()-1);

        if(!hasRender){
            hasRender = true;
            album_view.setSizeChangedObserver(new SizeChangedObserver());
            album_view.setOnPageClickListener(this);
            album_view.setOnFlipedLastPageListener(this);
            album_view.setPageProvider(new PageProvider(), false); //只传一个参数默认是软翻
            album_view.setOnPageEndListener(new AlbumView.PageEndListener() {
                @Override
                public void onPageEnd(int currentIndex) {
                    isOnPageScroll = false;
                    tv_currNum.setText(String.valueOf(currentIndex+1));
                    if (isPortrait){
                        seekBar.setProgress(currentIndex);
                        tv_currNum_land.setText(String.valueOf(currentIndex+1));
                    }
                    if(currentIndex == 0){

                        if(isAudioComplete){
                            isAudioComplete = false;
                            play(audioUrl);
                        }else {
                            mPlayer.seekTo(0);
                        }

                    }else {
                        mPlayer.seekTo(mData.getData().getEssay().getContentList().get(currentIndex-1).getSecond()*1000);
                        if(isAudioComplete){
                            isAudioComplete = false;
                            mPlayer.start();
                            if(isPortrait){
                                iv_playpause.setImageResource(R.drawable.picbook_btn_pause);
                                iv_playpause_land.setImageResource(R.drawable.video_icon_pause);
                            }else {
                                iv_playpause.setImageResource(R.drawable.video_icon_pause);
                            }

                        }
                    }

                }

            });

            album_view.setOnClickListener(new AlbumView.OnClickListener() {
                @Override
                public void onClick() {
                    if(isPortrait){
                        if(rl_bottom_land.getVisibility() == View.VISIBLE){
                            rl_bottom_land.setVisibility(View.GONE);
                        }else {
                            rl_bottom_land.setVisibility(View.VISIBLE);
                        }
                    }else {
                        if(rl_bottom.getVisibility() == View.VISIBLE){
                            rl_bottom.setVisibility(View.GONE);
                        }else {
                            rl_bottom.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            if (lastIndex != -1 && lastIndex != 0) {
                album_view.setCurrentIndex(lastIndex);
            } else {
                album_view.setCurrentIndex(currentIndex);
            }
            album_view.requestRender();
        }

    }

    private class DownloadImgTask extends AsyncTask<String,Void,ImageBean> {


        @Override
        protected ImageBean doInBackground(String[] strings) {

            FutureTarget<Bitmap> futureTarget =
                    Glide.with(HomeAudioDetailActivity.this)
                            .asBitmap()
                            .load(strings[0])
                            .submit();
            Bitmap bitmap = null;
            try {
                bitmap = futureTarget.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            Glide.with(HomeAudioDetailActivity.this).clear(futureTarget);
            ImageBean imageBean = new ImageBean(bitmap,Integer.valueOf(strings[1]),Integer.valueOf(strings[2]));

            return imageBean;
        }

        @Override
        protected void onPostExecute(ImageBean imageBean) {
            super.onPostExecute(imageBean);
//            imageList.add(bitmap);
//            imageList.add(imageBean.index,imageBean.bitmap);
            imageArray[imageBean.index] = imageBean.bitmap;

//            if(imageList.size() == imageBean.size && imageList.get(0)!= null){
//                initAlbumView(imageList);
//            }

            if(imageArray[imageBean.size -1] != null && imageArray[0] != null && imageArray[1] != null){
                bitmipWidth = imageArray[imageBean.size -1].getWidth();
                bitmipHeight = imageArray[imageBean.size -1].getHeight();
                if(bitmipHeight > bitmipWidth){
                    islandspaceBitmap = true;
                }else {
                    islandspaceBitmap = false;
                }
                Log.d("bitmapsize", "width: " + bitmipWidth); //400px
                Log.d("bitmapsize", "height: " + bitmipHeight); //400px
                initAlbumView(0);
            }
        }
    }


    public static List<Bitmap> split(Bitmap bitmap, int xPiece, int yPiece) {

        List<Bitmap> pieces = new ArrayList<>(xPiece * yPiece);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / xPiece;
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                int xValue = j * pieceWidth;
                int yValue = i * height;
                Bitmap bit = Bitmap.createBitmap(bitmap, xValue, yValue,
                        width/2, height);
                pieces.add(bit);
            }
        }

        return pieces;
    }


    /**
     * 播放，这种是直接传音频实体类
     * 有两种，一种是播放本地播放，另一种是在线播放
     *
     * @param music music
     */
    public void play(final String music) {

//        mPlayingMusic = music;
//        createMediaPlayer();
        try {
            mPlayer.reset();
            //把音频路径传给播放器
            mPlayer.setDataSource(DataEncryption.audioEncode(music));
            //准备
            mPlayer.prepareAsync();
            //设置状态为准备中
            mPlayer.setOnPreparedListener(mOnPreparedListener);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isAudioComplete = true;
                    hasRender = false;
                    initAlbumView(0);
//                    AlbumView.PointerPosition pointerPosition = album_view.getmPointerPos();
//                    pointerPosition.mPos.x =  0.22222221f;
//                    pointerPosition.mPos.y =  0.21111113f;
//                    pointerPosition.mPressure = 0.8f;
//                    album_view.updateCurlPos(pointerPosition);
//                    album_view.smoothPage(-20,false);
//                    iv_playpause.setImageResource(R.drawable.picbook_btn_play);
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
//            if (isPreparing()) {
            mPlayer.start();
            musicThread =new Thread(new MuiscThread());
            // 启动线程
            musicThread.start();
            if(isPortrait){
                iv_playpause.setImageResource(R.drawable.picbook_btn_pause);
                iv_playpause_land.setImageResource(R.drawable.video_icon_pause);
            }else {
                iv_playpause.setImageResource(R.drawable.video_icon_pause);
            }

//            }
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

        //放弃音频焦点
        if(mAudioFocusManager != null)
        mAudioFocusManager.abandonAudioFocus();
    }


    class ImageBean{
        Bitmap bitmap;
        int index;
        int size;
        ImageBean(Bitmap bitmap,int index,int size){
            this.bitmap = bitmap;
            this.index = index;
            this.size = size;
        }
    }


    //建立一个子线程实现Runnable接口
    class MuiscThread implements Runnable {

        @Override
        //实现run方法
        public void run() {
            //判断音乐的状态，在不停止与不暂停的情况下向总线程发出信息
            while (mPlayer != null ) {

                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发出的信息
                if(mPlayer != null)
                handler.sendEmptyMessage(mPlayer.getCurrentPosition());
            }

        }

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            for(int i=0;i<mData.getData().getEssay().getContentList().size()-1;i++){
                if(msg.what/100 == mData.getData().getEssay().getContentList().get(i).getSecond()*10){

                    if((i+1) == album_view.getCurrentIndex()){
                        return;
                    }else {
                        Log.e("HomeAudioDetailActivity","i="+String.valueOf(i));
                        Log.e("HomeAudioDetailActivity","currentindex="+String.valueOf(album_view.getCurrentIndex()));
                        album_view.setCurrentIndex(i+1);
                        tv_currNum.setText(String.valueOf(i+2));
                        if (isPortrait){
                            seekBar.setProgress(i+1);
                        }
                    }


                }
            }
            // 将SeekBar位置设置到当前播放位置
//            seekBar.setProgress(msg.what);
            //获得音乐的当前播放时间
//            currentime.setText(formatime(msg.what));
        }
    };

    private void collectedArticle() {
        JSONObject params = new JSONObject();
        try {
            params.put("essayId",essayId);
            params.put("gradeId",NewMainActivity.GRADE_ID);
            params.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(collectUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {

                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.getString("status").equals("200")){
                        showTips("收藏成功");
                        isCollected = true;
                        if(isPortrait){
                            iv_collect.setImageResource(R.drawable.picbook_icon_collect_selected);
                        }else {
                            iv_collect.setImageResource(R.drawable.icon_bg_collect_select);
                        }
                    }else  if (jsonObject.getString("status").equals("400")){
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }

    private void deleteCollect() {
        JSONObject params = new JSONObject();
        String essayids  = "["+essayId+"]";
        try {
            params.put("essayIds",essayids);
            params.put("studentId",NewMainActivity.STUDENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpManager.getInstance(this).requestAsyn(deleteCollectUrl,OkHttpManager.TYPE_POST_JSON,params,new OkHttpManager.ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {

                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.getString("status").equals("200")){
                        showTips("删除收藏成功");
                        isCollected = false;
                        if(isPortrait){
                            iv_collect.setImageResource(R.drawable.picbook_icon_collect);
                        }else {
                            iv_collect.setImageResource(R.drawable.icon_bg_collect_default);
                        }
                    }else  if (jsonObject.getString("status").equals("400")){
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        });
    }


    /**
     * 分享笔记
     */
    private void shareNote() {
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
                                shareArticleToWX(true,shareWXUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weixinpyq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareArticleToWX(false,shareWXUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_weibo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WbSdk.isWbInstall(HomeAudioDetailActivity.this)) {
                                    shareToWeibo(shareWBUrl);
                                } else {
                                    showTips("请先安装微博");
                                }
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qq, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQQ(shareQQUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_qzone, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareToQZone(shareQQUrl);
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.share_to_link, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
     * 分享到QQ空间
     */
    private void shareToQZone(String url) {
        ShareUtil.shareToQZone(this, url, title, shareContent, shareImgUrl);
    }

    /**
     * 分享笔记到微信
     *
     * @param friend true为分享到好友，false为分享到朋友圈
     */
    private void shareArticleToWX(boolean friend, String url) {

        Bitmap thumb = shareBitmap;
        if(thumb == null)
            thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        ShareUtil.shareToWx(this, url, title, shareContent,
                ImageUtils.bmpToByteArrayCopy(thumb, false), friend);
    }

    /**
     * 分享到QQ好友
     */
    private void shareToQQ(String url) {
        ShareUtil.shareToQQ(this, url, title, shareContent, shareImgUrl);
    }

    /**
     * 分享到微博
     *
     * @param url
     */
    private void shareToWeibo(String url) {

        if (shareBitmap == null) {
            shareBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
        }

        ShareUtil.shareToWeibo(shareHandler, url, NewMainActivity.lessonTitle,
                NewMainActivity.lessonContent, shareBitmap);

    }

    /**
     * 准备Bitmap
     */
    private void prepareBitmap(final String shareImgUrl) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                shareBitmap = ImageUtils.GetNetworkBitmap(shareImgUrl);
                if (shareBitmap == null) {
                    shareBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                }
            }
        }.start();
    }


}
