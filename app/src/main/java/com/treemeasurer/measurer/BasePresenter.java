package com.treemeasurer.measurer;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<T extends BaseView> {
    // 1.持有view接口
    protected WeakReference<T> view;

    protected T specView;

    public void attachView(T view) {
        this.view = new WeakReference<>(view);
    }

    public void detachView() {
        if (view != null) {
            view.clear();
            view = null;
        }
    }

    /**
     * @return 当前presenter对应view的context
     */
    protected T getContext() {
        return (T) view.get();
    }

    protected String getTag() {
        return ((Activity)getContext()).getLocalClassName();
    }

}
