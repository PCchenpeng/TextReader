package com.dace.textreader.view.weight.pullrecycler.album;

/**
 * Created by fft123 on 2016/12/6.
 */
public interface OnPageClickListener {
    public void onClickPage(int mCurrentIndex, boolean isFront);

    public boolean validEnableClick(int mCurrentIndex, boolean isFront);
}
