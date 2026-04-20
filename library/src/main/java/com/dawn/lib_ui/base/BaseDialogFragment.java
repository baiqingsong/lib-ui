package com.dawn.lib_ui.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * DialogFragment 基类 — 比 {@link com.dawn.lib_ui.dialog.BaseDialog} 更适合现代 Android 架构。
 * <p>
 * 生命周期更安全，避免 Activity 重建导致的 Dialog 丢失。
 * <pre>
 *   public class MyDialogFragment extends BaseDialogFragment {
 *       &#64;Override protected int getLayoutId() { return R.layout.dialog_my; }
 *       &#64;Override protected void initView(View view) { }
 *   }
 *   new MyDialogFragment().setWidthRatio(0.85f).show(getSupportFragmentManager());
 * </pre>
 */
public abstract class BaseDialogFragment extends DialogFragment {

    private float widthRatio = 0.8f;
    private int gravity = Gravity.CENTER;
    private boolean dimEnabled = true;
    private float dimAmount = 0.5f;
    private int animStyleRes = 0;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(@NonNull View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || dialog.getWindow() == null) return;
        Window window = dialog.getWindow();

        window.setGravity(gravity);

        DisplayMetrics dm = requireContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (dm.widthPixels * widthRatio);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (dimEnabled) {
            params.dimAmount = dimAmount;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        if (animStyleRes != 0) {
            window.setWindowAnimations(animStyleRes);
        }

        window.setAttributes(params);
    }

    // =============== 链式配置 ===============

    public BaseDialogFragment setWidthRatio(float ratio) {
        this.widthRatio = ratio;
        return this;
    }

    public BaseDialogFragment setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public BaseDialogFragment setDimEnabled(boolean enabled) {
        this.dimEnabled = enabled;
        return this;
    }

    public BaseDialogFragment setDimAmount(float amount) {
        this.dimAmount = amount;
        return this;
    }

    public BaseDialogFragment setAnimStyle(int animStyleRes) {
        this.animStyleRes = animStyleRes;
        return this;
    }

    /**
     * 便捷 show（自动生成 tag）。
     */
    public void show(@NonNull FragmentManager manager) {
        show(manager, getClass().getSimpleName());
    }
}
