package com.dawn.lib_ui.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 软键盘工具类 — 显示/隐藏软键盘。
 */
public class KeyboardUtil {

    private KeyboardUtil() {
    }

    /**
     * 显示软键盘。
     */
    public static void showKeyboard(@NonNull EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 隐藏软键盘。
     */
    public static void hideKeyboard(@NonNull Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏软键盘（从指定 View）。
     */
    public static void hideKeyboard(@NonNull View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 切换软键盘状态。
     */
    public static void toggleKeyboard(@NonNull Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    /**
     * 判断软键盘是否正在显示。
     */
    public static boolean isKeyboardVisible(@Nullable Activity activity) {
        if (activity == null) return false;
        View rootView = activity.getWindow().getDecorView();
        int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
        return heightDiff > DensityUtil.dp2px(activity, 200);
    }
}
