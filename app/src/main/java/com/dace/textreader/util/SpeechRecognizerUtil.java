package com.dace.textreader.util;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;

/**
 * =============================================================================
 * Copyright (c) 2018 Administrator All rights reserved.
 * Packname com.dace.textreader.util
 * Created by Administrator.
 * Created time 2018/12/20 0020 下午 4:04.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class SpeechRecognizerUtil {

    private static SpeechRecognizerUtil util;

    private SpeechRecognizer mIat;

    public static SpeechRecognizerUtil getInstance() {
        if (util == null) {
            util = new SpeechRecognizerUtil();
        }
        return util;
    }

    public void init(Context context, InitListener mInitListener) {
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParams("0");
    }

    /**
     * 设置参数
     *
     * @param ptt 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
     */
    public void setParams(String ptt) {
        if (mIat == null) {
            return;
        }
        mIat.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        mIat.setParameter(SpeechConstant.SUBJECT, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "3000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "2000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, ptt);
    }

    /**
     * 开始语音识别
     *
     * @param mRecognizerListener
     * @return ErrorCode.SUCCESS正在听写, else听写失败
     */
    public int startVoice(RecognizerListener mRecognizerListener) {
        return mIat.startListening(mRecognizerListener);
    }

    /**
     * 停止语音识别
     */
    public void stopVoice() {
        if (mIat.isListening()) {
            mIat.stopListening();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (null != mIat) {
            if (mIat.isListening()) {
                mIat.stopListening();
            }
            // 退出时释放连接
            mIat.destroy();
        }
    }

}
