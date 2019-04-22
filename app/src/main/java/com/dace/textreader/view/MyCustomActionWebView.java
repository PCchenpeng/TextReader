package com.dace.textreader.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.shuyu.action.web.ActionSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MyCustomActionWebView extends WebView {

    ActionMode mActionMode;

    List<String> mActionList = new ArrayList<>();

    ActionSelectListener mActionSelectListener;

    public MyCustomActionWebView(Context context) {
        super(context);
    }

    public MyCustomActionWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomActionWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        if (actionMode != null) {

            final Menu menu = actionMode.getMenu();
            mActionMode = actionMode;
            menu.clear();

            //弹窗
            getSelectedData("");
        }
        mActionMode = actionMode;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:" + js, null);
        } else {
            loadUrl("javascript:" + js);
        }
    }

    public void linkJSInterface() {
        addJavascriptInterface(new ActionSelectInterface(this), "JSInterface");
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

        MyCustomActionWebView mContext;

        ActionSelectInterface(MyCustomActionWebView c) {
            mContext = c;
        }

        @JavascriptInterface
        public void callback(final String value, final String title) {
            if (mActionSelectListener != null) {
                mActionSelectListener.onClick(title, value);
            }
        }
    }
}
