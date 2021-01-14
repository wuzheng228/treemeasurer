package com.treemeasurer.measurer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<T extends BasePresenter, V extends BaseView> extends Fragment {
    protected T presenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        presenter = selectPreSenter();
        presenter.attachView((V)this);
    }

    protected abstract T selectPreSenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
