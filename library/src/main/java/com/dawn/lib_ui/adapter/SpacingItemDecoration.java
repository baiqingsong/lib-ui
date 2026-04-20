package com.dawn.lib_ui.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView 通用间距装饰 — 自动适配 Linear / Grid / StaggeredGrid。
 * <pre>
 *   recyclerView.addItemDecoration(new SpacingItemDecoration(16));           // 统一间距
 *   recyclerView.addItemDecoration(new SpacingItemDecoration(16, true));    // 包含边缘
 *   recyclerView.addItemDecoration(new SpacingItemDecoration(16, 8));       // 横向/纵向分别指定
 * </pre>
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int horizontalSpacing;
    private final int verticalSpacing;
    private final boolean includeEdge;

    /**
     * 统一间距，不含边缘。
     */
    public SpacingItemDecoration(int spacing) {
        this(spacing, spacing, false);
    }

    /**
     * 统一间距，可选边缘。
     */
    public SpacingItemDecoration(int spacing, boolean includeEdge) {
        this(spacing, spacing, includeEdge);
    }

    /**
     * 横向/纵向不同间距，不含边缘。
     */
    public SpacingItemDecoration(int horizontal, int vertical) {
        this(horizontal, vertical, false);
    }

    /**
     * 完整构造。
     */
    public SpacingItemDecoration(int horizontal, int vertical, boolean includeEdge) {
        this.horizontalSpacing = horizontal;
        this.verticalSpacing = vertical;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position < 0) return;

        int spanCount = getSpanCount(parent);
        int column = position % spanCount;

        if (includeEdge) {
            outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount;
            outRect.right = (column + 1) * horizontalSpacing / spanCount;
            if (position < spanCount) {
                outRect.top = verticalSpacing;
            }
            outRect.bottom = verticalSpacing;
        } else {
            outRect.left = column * horizontalSpacing / spanCount;
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount;
            if (position >= spanCount) {
                outRect.top = verticalSpacing;
            }
        }
    }

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            return ((GridLayoutManager) lm).getSpanCount();
        }
        if (lm instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) lm).getSpanCount();
        }
        return 1;
    }
}
