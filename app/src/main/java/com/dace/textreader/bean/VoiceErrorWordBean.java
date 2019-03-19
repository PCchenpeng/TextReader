package com.dace.textreader.bean;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.bean
 * Created by Administrator.
 * Created time 2019/1/2 0002 下午 6:10.
 * Version   1.0;
 * Describe :  语音测评错字集
 * History:
 * ==============================================================================
 */
public class VoiceErrorWordBean {

    private int index;
    private String word;

    public VoiceErrorWordBean() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
