package com.dawn.lib_ui.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dawn.lib_ui.base.BaseApplication;

/**
 * 线程安全的 Toast 工具 — 可在任意线程调用。
 * <pre>
 *   ToastUtil.show("提示信息");
 *   ToastUtil.showLong("长提示");
 * </pre>
 */
public final class ToastUtil {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static Toast currentToast;

    private ToastUtil() {
    }

    public static void show(@Nullable CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void showLong(@Nullable CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

    public static void show(@Nullable CharSequence text, int duration) {
        if (text == null || text.length() == 0) return;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showInternal(text, duration);
        } else {
            MAIN_HANDLER.post(() -> showInternal(text, duration));
        }
    }

    private static void showInternal(@NonNull CharSequence text, int duration) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(BaseApplication.getAppContext(), text, duration);
        currentToast.show();
    }

    /**
     * 取消当前 Toast。
     */
    public static void cancel() {
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
    }
}
