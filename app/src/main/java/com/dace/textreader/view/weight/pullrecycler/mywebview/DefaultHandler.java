package com.dace.textreader.view.weight.pullrecycler.mywebview;

public class DefaultHandler implements BridgeHandler{

    String TAG = "DefaultHandler";

    @Override
    public void handler(String data, CallBackFunction function) {
        if(function != null){
            function.onCallBack("DefaultHandler response data");
        }
    }

}
