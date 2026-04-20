package com.dawn.lib_ui.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * View 工具类 — 截图、测量、遍历等。
 */
public final class ViewUtil {

    private ViewUtil() {
    }

    /**
     * View 截图为 Bitmap。
     */
    @Nullable
    public static Bitmap captureView(@NonNull View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        if (bmp != null) {
            bmp = Bitmap.createBitmap(bmp);
        }
        view.setDrawingCacheEnabled(false);
        return bmp;
    }

    /**
     * 获取 View 在屏幕中的坐标。
     */
    @NonNull
    public static int[] getLocationOnScreen(@NonNull View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    /**
     * 获取 View 可见区域。
     */
    @NonNull
    public static Rect getVisibleRect(@NonNull View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }

    /**
     * 测量 View 的宽高（未布局时使用）。
     */
    public static void measureView(@NonNull View view) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
    }

    /**
     * 设置 View 的 margins。
     */
    public static void setMargins(@NonNull View view, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.setMargins(left, top, right, bottom);
            view.setLayoutParams(mlp);
        }
    }

    /**
     * 设置 View 宽高。
     */
    public static void setViewSize(@NonNull View view, int width, int height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(width, height);
        } else {
            lp.width = width;
            lp.height = height;
        }
        view.setLayoutParams(lp);
    }

    /**
     * 判断坐标是否在 View 内。
     */
    public static boolean isTouchInView(@NonNull View view, float rawX, float rawY) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return rawX >= location[0] && rawX <= location[0] + view.getWidth()
                && rawY >= location[1] && rawY <= location[1] + view.getHeight();
    }

    /**
     * 递归遍历 ViewGroup 下所有子 View。
     */
    public static void traverseViewTree(@NonNull View view, @NonNull ViewVisitor visitor) {
        visitor.visit(view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                traverseViewTree(group.getChildAt(i), visitor);
            }
        }
    }

    public interface ViewVisitor {
        void visit(@NonNull View view);
    }
}
