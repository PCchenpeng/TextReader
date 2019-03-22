package com.dace.textreader.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.dace.textreader.util.WeakAsyncTask;
import com.dace.textreader.view.weight.pullrecycler.album.AlbumView;
import com.dace.textreader.view.weight.pullrecycler.album.CurlPage;
import com.dace.textreader.view.weight.pullrecycler.album.OnFlipedLastPageListener;
import com.dace.textreader.view.weight.pullrecycler.album.OnPageClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeAudioDetailActivity extends BaseActivity implements View.OnClickListener,OnPageClickListener, OnFlipedLastPageListener {

    private String essayId;

    private AudioArticleBean mData;
    private int pageNum =1;
    private String url = HttpUrlPre.HTTP_URL_ + "/select/article/detail";

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_audio_detail);
        essayId = getIntent().getStringExtra("id");

        initView();
//        album_view.setZOrderMediaOverlay(true);
        mPlayer = new MediaPlayer();
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

        iv_back.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_playpause.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        if (getLastCustomNonConfigurationInstance() != null) {
            lastIndex = (Integer) getLastCustomNonConfigurationInstance();
        }
    }

    private void loadData(String essayId) {
        new GetAudioData(HomeAudioDetailActivity.this).execute(url,String.valueOf(NewMainActivity.STUDENT_ID),
                String.valueOf(-1), String.valueOf(DensityUtil.getScreenHeight(HomeAudioDetailActivity.this)),
                String.valueOf(DensityUtil.getScreenWidth(HomeAudioDetailActivity.this)),essayId);
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
                break;
            case R.id.iv_collect:
                break;
            case R.id.iv_fullscreen:
                currentIndex = album_view.getCurrentIndex();
                if(isPortrait){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isPortrait = false;
                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isPortrait = true;
                }

                break;
            case R.id.iv_playpause:
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
     * 获取推荐数据
     */
    private static class GetAudioData
            extends WeakAsyncTask<String, Void, String, HomeAudioDetailActivity> {

        protected GetAudioData(HomeAudioDetailActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(HomeAudioDetailActivity activity, String[] strings) {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject object = new JSONObject();
                object.put("studentId", strings[1]);
                object.put("gradeId", strings[2]);
                object.put("width", strings[3]);
                object.put("height", strings[4]);
                object.put("essayId",strings[5]);
                object.put("isShare",0);
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
        protected void onPostExecute(HomeAudioDetailActivity activity, String s) {
            if (s == null) {
//                fragment.errorRecommendData();
            } else {
                activity.analyzeRecommendData(s);
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

        play(mData);

        SpliterImg(mData);


//        if (mPlayer != null) {
//            mPlayer.start();
//            mPlayState = MusicPlayAction.STATE_PLAYING;
            //开始发送消息，执行进度条进度更新
//            handler.sendEmptyMessage(UPDATE_PLAY_PROGRESS_SHOW);
//            if (mListener != null) {
//                mListener.onPlayerStart();
//            }
//            if (mAnimator != null) {
//                mAnimator.resume();
//            }
//        }

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
        }else {
            tv_totalNum.setText(String.valueOf(albumSize/2));
        }


        frontBacks[0] = splitBitmap.get(0);
        frontBacks[1] = splitBitmap.get(0);
        frontBacks[2] = splitBitmap.get(splitBitmap.size()-1);
        frontBacks[3] = splitBitmap.get(splitBitmap.size()-1);
//        pages[0] = splitBitmap.get(1);
//        pages[1] = splitBitmap.get(2);


//        frontBacks[0] = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxin);
//        frontBacks[1] = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxinback);
//        frontBacks[2] = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxin);
//        frontBacks[3] = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxinback);
//        pages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.page);
//        pages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.pageback);



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
                    if(currentIndex == 0){
                        mPlayer.seekTo(0);
                    }else {
                        mPlayer.seekTo(mData.getData().getEssay().getContentList().get(currentIndex-1).getSecond()*1000);

                        Log.e("ssssss",String.valueOf(mPlayer.getDuration()));
                        Log.e("bbbbbb",String.valueOf(mPlayer.getCurrentPosition()));
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
    public void play(AudioArticleBean music) {

//        mPlayingMusic = music;
//        createMediaPlayer();
        try {
            mPlayer.reset();
            //把音频路径传给播放器
            mPlayer.setDataSource(DataEncryption.audioEncode(music.getData().getEssay().getAudio()));
            //准备
            mPlayer.prepareAsync();
            //设置状态为准备中
//            mPlayState = MusicPlayAction.STATE_PREPARING;
            //监听
//            mPlayer.start();
            mPlayer.setOnPreparedListener(mOnPreparedListener);
//            mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
//            mPlayer.setOnCompletionListener(mOnCompletionListener);
//            mPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
//            mPlayer.setOnErrorListener(mOnErrorListener);
//            mPlayer.setOnInfoListener(mOnInfoListener);
            //当播放的时候，需要刷新界面信息
//            if (mListener != null) {
//                mListener.onChange(mPlayingPosition, mPlayingMusic);
//            }
//            if (mOnPlayNumNeedUpdate != null) {
//                mOnPlayNumNeedUpdate.update(mPlayingMusic);
//            }
//            if (mFloatView != null) {
//                RequestOptions options = new RequestOptions()
//                        .placeholder(R.drawable.icon_audio_default)
//                        .error(R.drawable.icon_audio_default)
//                        .transform(new GlideCircleTransform(getApplicationContext()));
//                Glide.with(getApplicationContext())
//                        .load(mPlayingMusic.getImage())
//                        .apply(options)
//                        .into(mMusicView);
//            }
//            if (mAnimator != null) {
//                mAnimator.resume();
//            }
//            updatePlayerList();
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
                    album_view.setCurrentIndex(i+1);
                    tv_currNum.setText(String.valueOf(i+2));
                }
            }
            // 将SeekBar位置设置到当前播放位置
//            seekBar.setProgress(msg.what);
            //获得音乐的当前播放时间
//            currentime.setText(formatime(msg.what));
        }
    };


}
