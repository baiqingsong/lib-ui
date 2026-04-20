package com.dawn.lib_ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.dawn.lib_ui.R;

/**
 * 通用 Dialog 基类 — 简化自定义对话框创建流程。
 * <p>
 * 使用方式：继承此类，实现 {@link #getLayoutId()} 和 {@link #initView(View)}。
 * <pre>
 *   public class MyDialog extends BaseDialog {
 *       public MyDialog(Context context) { super(context); }
 *       &#64;Override protected int getLayoutId() { return R.layout.dialog_my; }
 *       &#64;Override protected void initView(View contentView) {
 *           contentView.findViewById(R.id.btn_ok).setOnClickListener(v -> dismiss());
 *       }
 *   }
 * </pre>
 */
public abstract class BaseDialog extends Dialog {

    private float widthRatio = 0.8f;
    private int gravity = Gravity.CENTER;
    private boolean dimEnabled = true;
    private float dimAmount = 0.5f;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
    }

    public BaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null);
        setContentView(contentView);
        initView(contentView);
        applyWindowConfig();
    }

    /**
     * 返回布局资源 ID。
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化视图（找控件、设监听）。
     */
    protected abstract void initView(@NonNull View contentView);

    private void applyWindowConfig() {
        Window window = getWindow();
        if (window == null) return;

        window.setGravity(gravity);

        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        params.width = (int) (dm.widthPixels * widthRatio);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (dimEnabled) {
            params.dimAmount = dimAmount;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        window.setAttributes(params);
    }

    // =============== 链式配置 ===============

    /**
     * 设置宽度比例（相对于屏幕宽度，0~1）。
     */
    public BaseDialog setWidthRatio(float ratio) {
        this.widthRatio = ratio;
        return this;
    }

    /**
     * 设置对话框位置。
     */
    public BaseDialog setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 设置背景变暗。
     */
    public BaseDialog setDimEnabled(boolean enabled) {
        this.dimEnabled = enabled;
        return this;
    }

    /**
     * 设置变暗程度（0~1）。
     */
    public BaseDialog setDimAmount(float amount) {
        this.dimAmount = amount;
        return this;
    }

    /**
     * 设置是否点击外部关闭。
     */
    public BaseDialog setOutsideCancelable(boolean cancelable) {
        setCanceledOnTouchOutside(cancelable);
        return this;
    }
}
