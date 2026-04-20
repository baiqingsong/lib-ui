package com.dawn.lib_ui.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * 资源访问工具类 — 简化 Context 获取颜色、Drawable、尺寸等操作。
 */
public final class ResourceUtil {

    private ResourceUtil() {
    }

    @ColorInt
    public static int getColor(@NonNull Context context, @ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int drawableRes) {
        return ContextCompat.getDrawable(context, drawableRes);
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes int stringRes) {
        return context.getString(stringRes);
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes int stringRes, Object... args) {
        return context.getString(stringRes, args);
    }

    public static float getDimension(@NonNull Context context, @DimenRes int dimenRes) {
        return context.getResources().getDimension(dimenRes);
    }

    public static int getDimensionPixelSize(@NonNull Context context, @DimenRes int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }

    @NonNull
    public static String[] getStringArray(@NonNull Context context, @ArrayRes int arrayRes) {
        return context.getResources().getStringArray(arrayRes);
    }

    @NonNull
    public static int[] getIntArray(@NonNull Context context, @ArrayRes int arrayRes) {
        return context.getResources().getIntArray(arrayRes);
    }

    /**
     * 获取主题属性的颜色值。
     */
    @ColorInt
    public static int getThemeColor(@NonNull Context context, int attrRes) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, value, true);
        return value.data;
    }
}
