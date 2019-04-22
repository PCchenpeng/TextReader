package com.dace.textreader.view.weight.pullrecycler.mywebview;

public interface WebViewJavascriptBridge {

    public void send(String data);
    public void send(String data, CallBackFunction responseCallback);
}
