package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.dawn.lib_ui.R;

/**
 * 状态栏占位 View — 高度自动等于系统状态栏高度，用于自定义沉浸式布局。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code sbv_color} — 背景颜色</li>
 * </ul>
 */
public class StatusBarView extends View {

    public StatusBarView(Context context) {
        super(context);
        init(context, null);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int color = Color.TRANSPARENT;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView);
            color = a.getColor(R.styleable.StatusBarView_sbv_color, Color.TRANSPARENT);
            a.recycle();
        }
        setBackgroundColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int statusBarHeight = getStatusBarHeight();
        setMeasuredDimension(
                getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                statusBarHeight);
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
