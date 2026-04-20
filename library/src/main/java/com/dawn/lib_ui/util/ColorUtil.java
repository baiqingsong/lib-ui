package com.dawn.lib_ui.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

/**
 * 颜色工具类 — 颜色混合、亮度调整、透明度设置等。
 */
public final class ColorUtil {

    private ColorUtil() {
    }

    /**
     * 两种颜色混合。
     *
     * @param ratio color1 的比例（0~1）
     */
    @ColorInt
    public static int blendColors(@ColorInt int color1, @ColorInt int color2,
                                  @FloatRange(from = 0, to = 1) float ratio) {
        float inverseRatio = 1f - ratio;
        int a = (int) (Color.alpha(color1) * ratio + Color.alpha(color2) * inverseRatio);
        int r = (int) (Color.red(color1) * ratio + Color.red(color2) * inverseRatio);
        int g = (int) (Color.green(color1) * ratio + Color.green(color2) * inverseRatio);
        int b = (int) (Color.blue(color1) * ratio + Color.blue(color2) * inverseRatio);
        return Color.argb(clamp(a), clamp(r), clamp(g), clamp(b));
    }

    /**
     * 设置颜色的透明度。
     *
     * @param alpha 0~255
     */
    @ColorInt
    public static int setAlpha(@ColorInt int color, int alpha) {
        return Color.argb(clamp(alpha), Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * 设置颜色透明度（百分比）。
     */
    @ColorInt
    public static int setAlphaPercent(@ColorInt int color,
                                      @FloatRange(from = 0, to = 1) float percent) {
        return setAlpha(color, (int) (255 * percent));
    }

    /**
     * 使颜色变暗。
     *
     * @param factor 0~1，0=黑色，1=原色
     */
    @ColorInt
    public static int darken(@ColorInt int color,
                             @FloatRange(from = 0, to = 1) float factor) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * factor);
        int g = (int) (Color.green(color) * factor);
        int b = (int) (Color.blue(color) * factor);
        return Color.argb(a, clamp(r), clamp(g), clamp(b));
    }

    /**
     * 使颜色变亮。
     *
     * @param factor 0~1，0=原色，1=白色
     */
    @ColorInt
    public static int lighten(@ColorInt int color,
                              @FloatRange(from = 0, to = 1) float factor) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) + (255 - Color.red(color)) * factor);
        int g = (int) (Color.green(color) + (255 - Color.green(color)) * factor);
        int b = (int) (Color.blue(color) + (255 - Color.blue(color)) * factor);
        return Color.argb(a, clamp(r), clamp(g), clamp(b));
    }

    /**
     * 判断颜色是否偏暗。
     */
    public static boolean isDarkColor(@ColorInt int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    /**
     * 颜色转十六进制字符串 #AARRGGBB。
     */
    @NonNull
    public static String toHexString(@ColorInt int color) {
        return String.format("#%08X", color);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
