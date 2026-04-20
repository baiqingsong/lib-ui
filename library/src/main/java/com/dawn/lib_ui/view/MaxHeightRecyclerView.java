package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import com.dawn.lib_ui.R;

/**
 * 限制最大高度的 RecyclerView — 超出后内部滚动。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code mhrv_maxHeight} — 最大高度</li>
 * </ul>
 */
public class MaxHeightRecyclerView extends RecyclerView {

    private int maxHeight = Integer.MAX_VALUE;

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView);
            maxHeight = a.getDimensionPixelSize(R.styleable.MaxHeightRecyclerView_mhrv_maxHeight, Integer.MAX_VALUE);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight != Integer.MAX_VALUE) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }
}
