package com.dace.textreader.bean;

public class MessageEvent {
    private String message;
//    public static String REFRESH
    public  MessageEvent(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
