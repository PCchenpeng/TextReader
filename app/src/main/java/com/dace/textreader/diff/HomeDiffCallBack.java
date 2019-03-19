package com.dace.textreader.diff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.dace.textreader.bean.HomeRecommendationBean;

import java.util.List;

/**
 * =============================================================================
 * Copyright (c) 2019 Administrator All rights reserved.
 * Packname com.dace.textreader.diff
 * Created by Administrator.
 * Created time 2019/1/3 0003 下午 4:30.
 * Version   1.0;
 * Describe :
 * History:
 * ==============================================================================
 */
public class HomeDiffCallBack extends DiffUtil.Callback {

    private List<HomeRecommendationBean> mOldData, mNewData;

    public HomeDiffCallBack(
            List<HomeRecommendationBean> mOldData, List<HomeRecommendationBean> mNewData) {
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
        return mOldData.get(i).getCompositionId().equals(mNewData.get(i1).getCompositionId());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        HomeRecommendationBean oldBean = mOldData.get(i);
        HomeRecommendationBean newBean = mNewData.get(i1);

        if (!oldBean.getTitle().equals(newBean.getTitle())) {
            return false;
        }
        if (!oldBean.getContent().equals(newBean.getContent())) {
            return false;
        }
        if (!oldBean.getImage().equals(newBean.getImage())) {
            return false;
        }
        if (!oldBean.getCompositionScore().equals(newBean.getCompositionScore())) {
            return false;
        }
        if (!oldBean.getCompositionPrize().equals(newBean.getCompositionPrize())) {
            return false;
        }
        if (!oldBean.getCompositionAvgScore().equals(newBean.getCompositionAvgScore())) {
            return false;
        }
        if (!oldBean.getViews().equals(newBean.getViews())) {
            return false;
        }
        if (oldBean.getUserId() != newBean.getUserId()) {
            return false;
        }
        if (!oldBean.getUserName().equals(newBean.getUserName())) {
            return false;
        }
        if (!oldBean.getUserImage().equals(newBean.getUserImage())) {
            return false;
        }
        if (!oldBean.getUserGrade().equals(newBean.getUserGrade())) {
            return false;
        }
        return true;
    }

//    @Nullable
//    @Override
//    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//
//        HomeRecommendationBean oldBean = mOldData.get(oldItemPosition);
//        HomeRecommendationBean newBean = mNewData.get(newItemPosition);
//
//        Bundle bundle = new Bundle();
//
//        if (!oldBean.getTitle().equals(newBean.getTitle())) {
//            bundle.putString("key_title", newBean.getTitle());
//        }
//        if (!oldBean.getContent().equals(newBean.getContent())) {
//            bundle.putString("key_content", newBean.getContent());
//        }
//        if (!oldBean.getImage().equals(newBean.getImage())) {
//            bundle.putString("key_image", newBean.getImage());
//        }
//        if (!oldBean.getCompositionScore().equals(newBean.getCompositionScore())) {
//            bundle.putString("key_score", newBean.getCompositionScore());
//        }
//        if (!oldBean.getCompositionPrize().equals(newBean.getCompositionPrize())) {
//            bundle.putString("key_prize", newBean.getCompositionPrize());
//        }
//        if (!oldBean.getCompositionAvgScore().equals(newBean.getCompositionAvgScore())) {
//            bundle.putString("key_avg_score", newBean.getCompositionAvgScore());
//        }
//        if (!oldBean.getViews().equals(newBean.getViews())) {
//            bundle.putString("key_views", newBean.getViews());
//        }
//        if (!oldBean.getUserName().equals(newBean.getUserName())) {
//            bundle.putString("key_user_name", newBean.getUserName());
//        }
//        if (!oldBean.getUserImage().equals(newBean.getUserImage())) {
//            bundle.putString("key_user_image", newBean.getUserImage());
//        }
//        if (!oldBean.getUserGrade().equals(newBean.getUserGrade())) {
//            bundle.putString("key_user_grade", newBean.getUserGrade());
//        }
//
//        if (bundle.size() == 0) {
//            return null;
//        }
//
//        return bundle;
//    }
}
