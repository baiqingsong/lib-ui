package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dawn.lib_ui.R;

/**
 * 流式布局（标签布局）— 子 View 自动换行排列。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code fl_horizontalSpacing} — 子元素水平间距</li>
 *   <li>{@code fl_verticalSpacing} — 子元素垂直间距</li>
 * </ul>
 */
public class FlowLayout extends ViewGroup {

    private int horizontalSpacing;
    private int verticalSpacing;

    public FlowLayout(Context context) {
        super(context);
        init(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
            horizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_fl_horizontalSpacing,
                    (int) (8 * getResources().getDisplayMetrics().density));
            verticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_fl_verticalSpacing,
                    (int) (8 * getResources().getDisplayMetrics().density));
            a.recycle();
        } else {
            int dp8 = (int) (8 * getResources().getDisplayMetrics().density);
            horizontalSpacing = dp8;
            verticalSpacing = dp8;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int maxWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int lineWidth = 0;
        int lineHeight = 0;
        int totalHeight = 0;
        int maxLineWidth = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth > maxWidth && lineWidth > 0) {
                // 换行
                totalHeight += lineHeight + verticalSpacing;
                maxLineWidth = Math.max(maxLineWidth, lineWidth - horizontalSpacing);
                lineWidth = 0;
                lineHeight = 0;
            }

            lineWidth += childWidth + horizontalSpacing;
            lineHeight = Math.max(lineHeight, childHeight);
        }
        // 最后一行
        totalHeight += lineHeight;
        maxLineWidth = Math.max(maxLineWidth, lineWidth - horizontalSpacing);

        int measuredWidth = widthMode == MeasureSpec.EXACTLY ? widthSize
                : maxLineWidth + getPaddingLeft() + getPaddingRight();
        int measuredHeight = totalHeight + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(
                resolveSize(measuredWidth, widthMeasureSpec),
                resolveSize(measuredHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int maxWidth = r - l - getPaddingLeft() - getPaddingRight();
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int lineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (x - getPaddingLeft() + childWidth > maxWidth && x > getPaddingLeft()) {
                // 换行
                y += lineHeight + verticalSpacing;
                x = getPaddingLeft();
                lineHeight = 0;
            }

            child.layout(x, y, x + childWidth, y + childHeight);
            x += childWidth + horizontalSpacing;
            lineHeight = Math.max(lineHeight, childHeight);
        }
    }

    public void setSpacing(int horizontal, int vertical) {
        this.horizontalSpacing = horizontal;
        this.verticalSpacing = vertical;
        requestLayout();
    }
}
