package com.dace.textreader.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.dace.textreader.R;
import com.dace.textreader.util.DensityUtil;
import com.dace.textreader.view.weight.pullrecycler.mywebview.BridgeCustomWebview;
import com.dace.textreader.view.weight.pullrecycler.mywebview.CallBackFunction;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticleDetailActivityTest extends AppCompatActivity {

//    private BridgeCustomWebview webView;
    private RecyclerView lv_test;
    private Adapter adapter;
    private Adapter1 adapter1;

    String url = "https://check.pythe.cn/1readingModule/pyReadDetail0.html?platForm=android&fontSize=18px&readModule=1&py=1&studentId=8429&gradeId=142&lineHeight=2.4&isShare=0&version=3.2.6&backgroundColor=FFFFFF&essayId=10032979";
//    String url = "https://www.baidu.com";
//    String url = "https://check.pythe.cn/1readingModule/pyReadDetail0.html?platForm=ios&fontSize=21px&readModule=1&py=1&studentId=8428&gradeId=151&lineHeight=2.6&isShare=0&version=3.2.6&backgroundColor=FFFBE9&essayId=10345373";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articledetail_test);

//        webView = findViewById(R.id.web_test);
        lv_test = findViewById(R.id.lv_test);
//        lv_test.setHasFixedSize(true);
        adapter1 = new Adapter1(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        lv_test.setLayoutManager(linearLayoutManager);
        lv_test.setAdapter(adapter1);

//        adapter = new Adapter(this);
//        lv_test.setAdapter(adapter);
//        lv_test.addHeaderView();
    }
//        initWebSettings();

//        webView.setWebViewClient(new WebViewClient() {
//
//
//
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                    int errorCode = error.getErrorCode();
//                    if (errorCode != 200) {
//                    }
//                }
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
////                super.onReceivedSslError(view, handler, error);
//                handler.proceed();
//            }
//        });

//        JSONObject params = new JSONObject();
//        try {
//            params.put("screen_height",DensityUtil.px2dip(this,DensityUtil.getScreenHeight(this)));
//            params.put("screen_width",DensityUtil.px2dip(this,DensityUtil.getScreenWidth(this)));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        webView.callHandler("getPhoneSize", params.toString(), new CallBackFunction() {
//            @Override
//            public void onCallBack(String data) {
//            }
//        });
//
//
//        webView.loadUrl(url);
//    }



//    private void initWebSettings() {
//        WebSettings webSettings = webView.getSettings();
//        //5.0以上开启混合模式加载
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        //允许js代码
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        //禁用放缩
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setBuiltInZoomControls(false);
//        //禁用文字缩放
//        webSettings.setTextZoom(100);
//        //自动加载图片
//        webSettings.setLoadsImagesAutomatically(true);
//    }

    class Adapter extends BaseAdapter{

        private Context mContext;
        public Adapter(Context context){
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_article_detail_test, null);
                viewHolder = new ViewHolder();
                viewHolder.webView = convertView.findViewById(R.id.webview);

                WebSettings webSettings = viewHolder.webView.getSettings();
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

                JSONObject params = new JSONObject();
                try {
                    params.put("screen_height",DensityUtil.px2dip(mContext,DensityUtil.getScreenHeight(mContext)));
                    params.put("screen_width",DensityUtil.px2dip(mContext,DensityUtil.getScreenWidth(mContext)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewHolder.webView.callHandler("getPhoneSize", params.toString(), new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                    }
                });
                viewHolder.webView.loadUrl(url);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        class ViewHolder{
            BridgeCustomWebview webView;
        }
    }

    class Adapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private ArticleDetailActivityTest context;
        public Adapter1(ArticleDetailActivityTest context){
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if(i == 0){
                View view = LayoutInflater.from(context).inflate(R.layout.item_article_detail_test_top,null);
                return new TopHolder(view);
            }else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_article_detail_test,null);
                return new ItemViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            int type = getItemViewType(i);
            if (type == 0){
                TxVideoPlayerController controller = new TxVideoPlayerController(context);
                ViewGroup.LayoutParams params = ((TopHolder)viewHolder).view_video.getLayoutParams();
                params.width = getResources().getDisplayMetrics().widthPixels; // 宽度为屏幕宽度
                params.height = (int) (params.width * 9f / 16f);    // 高度为宽度的9/16
                ((TopHolder)viewHolder).view_video.setLayoutParams(params);

                controller.setTitle("hahahh");
                controller.setLenght(1);
                Glide.with(context)
                        .load("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg")
                        .into(controller.imageView());
                ((TopHolder)viewHolder).view_video.setUp("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4", null);
                ((TopHolder)viewHolder).view_video.setController(controller);


            }else {
                WebSettings webSettings = ((ItemViewHolder)viewHolder).webView.getSettings();
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
//
//                JSONObject params = new JSONObject();
//                try {
//                    params.put("screen_height",DensityUtil.px2dip(context,DensityUtil.getScreenHeight(context)));
//                    params.put("screen_width",DensityUtil.px2dip(context,DensityUtil.getScreenWidth(context)));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                ((ItemViewHolder)viewHolder).webView.callHandler("getPhoneSize", params.toString(), new CallBackFunction() {
//                    @Override
//                    public void onCallBack(String data) {
//                    }
//                });
                ((ItemViewHolder)viewHolder).webView.loadUrl("https://www.baidu.com");
            }

        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position){
            if(position == 1)
            return 0;
            else
                return 1;
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            WebView webView;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                webView = itemView.findViewById(R.id.webview);
            }
        }

        class TopHolder extends RecyclerView.ViewHolder{
            NiceVideoPlayer view_video;
            public TopHolder(@NonNull View itemView) {
                super(itemView);
                view_video = itemView.findViewById(R.id.view_video);
            }
        }
    }



    public interface OnActivityLifeListener{
        void onStart();
        void onStop();
        void onDestroy();
    }

    OnActivityLifeListener onActivityLifeListener;

    public void setOnActivityLifeListener(OnActivityLifeListener onActivityLifeListener){
        this.onActivityLifeListener = onActivityLifeListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(onActivityLifeListener != null)
        onActivityLifeListener.onStop();

        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(onActivityLifeListener != null)
        onActivityLifeListener.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(onActivityLifeListener != null)
        onActivityLifeListener.onStop();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }


}
