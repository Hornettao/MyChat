package com.hornettao.mychat.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 弱引用Handler，防止内存泄露
 * Created by hornettao on 15/7/9.
 */
public abstract class WeakReferenceHandler<T> extends Handler {

    private static final String TAG = "WeakReferenceHandler";
    private WeakReference<T> mReference;

    public WeakReferenceHandler(T reference) {
        mReference = new WeakReference<T>(reference);
    }

    @Override
    public final void handleMessage(Message msg) {
        T t = mReference.get();
        if (t == null)
            return;
        handleMessage(t, msg);
    }

    protected abstract void handleMessage(T reference, Message msg);
}
