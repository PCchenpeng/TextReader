package com.dace.textreader.view.weight.pullrecycler.mywebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;


import com.dace.textreader.util.MyToastUtil;
import com.shuyu.action.web.ActionSelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeCustomWebview extends WebView implements WebViewJavascriptBridge {

    private float startx;
    private float starty;
    private float offsetx;
    private float offsety;
    private final String TAG = "BridgeWebView";
    private BridgeWebViewClient bridgeWebViewClient;

    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private List<Message> startupMessage = new ArrayList<Message>();

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    ActionMode mActionMode;

    List<String> mActionList = new ArrayList<>();

    ActionSelectListener mActionSelectListener;

    private String content;

    public BridgeCustomWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeCustomWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeCustomWebview(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("onPageFinished","onDraw");
    }

    /**
     *
     * @param handler
     *            default handler,handle messages send by js without assigned handler name,
     *            if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    private void init() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.setWebViewClient(generateBridgeWebViewClient());
    }

    protected BridgeWebViewClient generateBridgeWebViewClient() {
        BridgeWebViewClient bridgeWebViewClient=  new BridgeWebViewClient(this);
        bridgeWebViewClient.setOnPageFinished(new BridgeWebViewClient.OnPageFinished() {
            @Override
            public void onPageFinished() {
                if(onPageFinished != null)
                    onPageFinished.onPageFinished();
//                MyToastUtil.showToast(getContext(),"加载完毕");
            }
        });
        return bridgeWebViewClient;
    }

    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }


    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null){
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }


    /**
     * 处理item，处理点击
     * 这里本该处理item的点击事件的
     * 但是现在的需求不需要菜单（需要自定义的弹出界面）
     * 所以去掉了menu，变成了文字选中事件的监听
     *
     * @param actionMode
     */
    private ActionMode resolveData(ActionMode actionMode) {
        getContent();
        if (actionMode != null) {

//            final Menu menu = actionMode.getMenu();
//            mActionMode = actionMode;
//            menu.clear();
//
//            //弹窗
//            getSelectedData("");
//        }
//        mActionMode = actionMode;
//        return actionMode;




            Menu menu = actionMode.getMenu();
            this.mActionMode = actionMode;
            menu.clear();

            int i;
            for(i = 0; i < this.mActionList.size(); ++i) {
                menu.add((CharSequence)this.mActionList.get(i));
            }

            for(i = 0; i < menu.size(); ++i) {
                MenuItem menuItem = menu.getItem(i);
                menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        BridgeCustomWebview.this.getSelectedData((String)item.getTitle());
//                        BridgeCustomWebview.this.getContent();
                        BridgeCustomWebview.this.releaseAction();
                        return true;
                    }
                });
            }
        }

        this.mActionMode = actionMode;
        return actionMode;


    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActionMode actionMode = super.startActionMode(callback);
        return resolveData(actionMode);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        ActionMode actionMode = super.startActionMode(callback, type);
        return resolveData(actionMode);
    }

    private void releaseAction() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    /**
     * 点击的时候，获取网页中选择的文本，回掉到原生中的js接口
     *
     * @param title 传入点击的item文本，一起通过js返回给原生接口
     */
    private void getSelectedData(String title) {

        String js = "(function getSelectedText() {" +
                "var txt;" +
                "var title = \"" + title + "\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "JSInterface.callback(txt,title);" +
                "})()";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            evaluateJavascript("javascript:" + js, null);
//        } else {
            loadUrl("javascript:" + js);
//        }
    }

    public void linkJSInterface() {
        addJavascriptInterface(new ActionSelectInterface(this), "JSInterface");
    }



//    public static String  getContentJs = "(function getContent() {" +
//            "var content = document.getElementById(\"originTxt\").innerText;"+
//            "JSInterface.getContent(content);"
//            +
//            "})()";

    private void getContent(){

        String js = "(function getContent() {" +
                "var content = document.getElementById(\"originTxt\").innerText;"+
                "JSInterface.getContent(content);"
                        +
                        "})()";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:" + js, null);
        } else {
            loadUrl("javascript:" + js);
        }

    }

    /**
     * 设置弹出action列表
     *
     * @param actionList
     */
    public void setActionList(List<String> actionList) {
        mActionList = actionList;
    }

    /**
     * 设置点击回掉
     *
     * @param actionSelectListener
     */
    public void setActionSelectListener(ActionSelectListener actionSelectListener) {
        this.mActionSelectListener = actionSelectListener;
    }

    /**
     * js选中的回掉接口
     */
    private class ActionSelectInterface {

        BridgeCustomWebview mContext;

        ActionSelectInterface(BridgeCustomWebview c) {
            mContext = c;
        }

        @JavascriptInterface
        public void callback(final String value, final String title) {
            if (mActionSelectListener != null) {
                mActionSelectListener.onClick(title, value);
            }
        }

        @JavascriptInterface
        public void getContent(String content){
            Log.e("addJavascriptInterface",content);
            if(contentListener != null){
                contentListener.getContent(content);
                Log.e("addJavascriptInterface",content);
            }
        }

    }


    public interface ContentListener{
        void getContent(String content);
    }

    public void setContentListener(ContentListener contentListener){
        this.contentListener = contentListener;
    }

    private ContentListener contentListener;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                startx = event.getX();
                starty = event.getY();
                Log.e("MotionEvent", "webview按下");
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                Log.e("MotionEvent", "webview滑动");
                offsetx = Math.abs(event.getX() - startx);
                offsety = Math.abs(event.getY() - starty);
                Log.e("MotionEvent", "starty = "+String.valueOf(starty));
                Log.e("MotionEvent", "event.getY() = "+String.valueOf(event.getY()));
                Log.e("MotionEvent", "offsety = "+String.valueOf(offsety));
                if (offsetx > offsety ) {
                    getParent().requestDisallowInterceptTouchEvent(true);
//                    Log.e("MotionEvent", "屏蔽了父控件");
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
//                    Log.e("MotionEvent", "事件传递给父控件");
                }

            default:
                break;
        }
        return super.onTouchEvent(event);
    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev){
//        return super.onInterceptTouchEvent(ev);
//    }

    public interface OnPageFinished{
        void onPageFinished();
    }

    private OnPageFinished onPageFinished;

    public void setOnPageFinished(OnPageFinished onPageFinished){
        this.onPageFinished = onPageFinished;
    }

}
