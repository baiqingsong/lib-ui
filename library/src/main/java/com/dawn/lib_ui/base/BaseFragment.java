package com.dawn.lib_ui.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment 基类 — 提供通用的懒加载、View 绑定、Toast 等能力。
 * <p>
 * 子类必须实现 {@link #getLayoutId()}, {@link #initView(View)}, {@link #initData()}, {@link #addListener()}.
 */
public abstract class BaseFragment extends Fragment {

    protected View rootView;
    private boolean isViewCreated = false;
    private boolean isFirstVisible = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isViewCreated) {
            isViewCreated = true;
            initData();
            initView(view);
            addListener();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstVisible) {
            isFirstVisible = false;
            onLazyLoad();
        }
    }

    // ======================== 子类必须实现 ========================

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(@NonNull View view);

    protected abstract void initData();

    protected abstract void addListener();

    // ======================== 懒加载 ========================

    /**
     * 首次可见时调用。子类可覆写用于延迟加载数据。
     */
    protected void onLazyLoad() {
    }

    // ======================== 便捷方法 ========================

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(int id) {
        if (rootView == null) return null;
        return (T) rootView.findViewById(id);
    }

    protected void toast(String msg) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, TextUtils.isEmpty(msg) ? "" : msg, Toast.LENGTH_SHORT).show();
        }
    }

    protected void toastLong(String msg) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, TextUtils.isEmpty(msg) ? "" : msg, Toast.LENGTH_LONG).show();
        }
    }

    protected void setTextViewStr(TextView textView, String text) {
        if (textView != null) {
            textView.setText(TextUtils.isEmpty(text) ? "" : text);
        }
    }

    protected void visible(View view) {
        if (view != null) view.setVisibility(View.VISIBLE);
    }

    protected void gone(View view) {
        if (view != null) view.setVisibility(View.GONE);
    }

    protected void invisible(View view) {
        if (view != null) view.setVisibility(View.INVISIBLE);
    }
}
