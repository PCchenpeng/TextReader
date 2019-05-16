package com.dace.textreader.view.gallyviewpager;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hucanhui on 2017/3/31.
 */
public abstract class GallerAdapter extends PagerAdapter {

    public abstract int getGallerSize();
    public abstract View getItemView(int position);

    @Override
    public int getCount() {
        return getGallerSize();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = getItemView(position);
        container.addView(view);
        return view;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }



}
