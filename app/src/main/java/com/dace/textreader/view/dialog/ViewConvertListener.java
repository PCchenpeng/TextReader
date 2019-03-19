package com.dace.textreader.view.dialog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 自定义对话框中的View的事件监听类
 * Created by 70391 on 2017/9/26.
 */

public abstract class ViewConvertListener implements Parcelable {

    protected abstract void convertView(ViewHolder holder, BaseNiceDialog dialog);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public ViewConvertListener() {
    }

    protected ViewConvertListener(Parcel in) {
    }

    public static final Creator<ViewConvertListener> CREATOR = new Creator<ViewConvertListener>() {
        @Override
        public ViewConvertListener createFromParcel(Parcel source) {
            return new ViewConvertListener(source) {
                @Override
                protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                }
            };
        }

        @Override
        public ViewConvertListener[] newArray(int size) {
            return new ViewConvertListener[size];
        }
    };
}
