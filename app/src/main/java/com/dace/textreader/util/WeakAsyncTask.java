package com.dace.textreader.util;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by 70391 on 2017/11/6.
 */

public abstract class WeakAsyncTask<Params, Progress, Result, WeakTarget>
        extends AsyncTask<Params, Progress, Result> {

    protected final WeakReference<WeakTarget> mTarget;


    protected WeakAsyncTask(WeakTarget target) {
        this.mTarget = new WeakReference<WeakTarget>(target);
    }

    @Override
    protected void onPreExecute() {
        final WeakTarget target = mTarget.get();
        if (target != null) {
            this.onPreExecute(target);
        }
    }

    @Override
    protected Result doInBackground(Params... params) {
        final WeakTarget target = mTarget.get();
        if (target != null) {
            return this.doInBackground(target, params);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        final WeakTarget target = mTarget.get();
        if (target != null) {
            this.onPostExecute(target, result);
        }
    }

    protected void onPreExecute(WeakTarget target) {
    }

    protected abstract Result doInBackground(WeakTarget target, Params[] params);

    protected abstract void onPostExecute(WeakTarget target, Result result);
}
