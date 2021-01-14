package com.treemeasurer.measurer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity<T extends BasePresenter, V extends BaseView> extends AppCompatActivity implements BaseView{
    protected T presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 定义选择表示层的功能
        presenter = selectPreSenter();
        presenter.attachView((V)this);
    }

    protected abstract T selectPreSenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
