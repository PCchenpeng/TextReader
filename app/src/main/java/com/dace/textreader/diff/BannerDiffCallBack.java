package com.dace.textreader.diff;

import android.support.v7.util.DiffUtil;

import com.dace.textreader.bean.BannerBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.diff
 * Created by Administrator.
 * Created time 2019/1/3 0003 下午 5:58.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class BannerDiffCallBack extends DiffUtil.Callback {

    private List<BannerBean> mOldData, mNewData;

    public BannerDiffCallBack(List<BannerBean> mOldData, List<BannerBean> mNewData) {
        this.mOldData = mOldData;
        this.mNewData = mNewData;
    }

    @Override
    public int getOldListSize() {
        return mOldData != null ? mOldData.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewData != null ? mNewData.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        boolean same = false;
        BannerBean oldBean = mOldData.get(i);
        BannerBean newBean = mNewData.get(i);
        if (oldBean.getName().equals(newBean.getName())
                && oldBean.getImagePath().equals(newBean.getImagePath())
                && oldBean.getTitle().equals(newBean.getTitle())) {
            same = true;
        }
        return same;
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        BannerBean oldBean = mOldData.get(i);
        BannerBean newBean = mNewData.get(i);
        if (!oldBean.getName().equals(newBean.getName())) {
            return false;
        }
        if (!oldBean.getImagePath().equals(newBean.getImagePath())) {
            return false;
        }
        if (!oldBean.getTitle().equals(newBean.getTitle())) {
            return false;
        }
        return true;
    }
}
