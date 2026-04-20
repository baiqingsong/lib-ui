package com.dawn.lib_ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView 线条分割线 — 支持颜色、粗细、左右边距。
 * <pre>
 *   recyclerView.addItemDecoration(new ColorDividerDecoration(context, Color.LTGRAY, 1, 16, 16));
 * </pre>
 */
public class ColorDividerDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int dividerHeight;
    private final int marginStart;
    private final int marginEnd;

    /**
     * @param color     分割线颜色
     * @param heightDp  分割线高度 dp
     * @param marginStartDp 左/上边距 dp
     * @param marginEndDp   右/下边距 dp
     */
    public ColorDividerDecoration(@NonNull Context context, @ColorInt int color,
                                  int heightDp, int marginStartDp, int marginEndDp) {
        float density = context.getResources().getDisplayMetrics().density;
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        dividerHeight = (int) (heightDp * density);
        marginStart = (int) (marginStartDp * density);
        marginEnd = (int) (marginEndDp * density);
    }

    public ColorDividerDecoration(@NonNull Context context, @ColorInt int color, int heightDp) {
        this(context, color, heightDp, 0, 0);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position < 0) return;
        int total = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;
        if (position < total - 1) {
            if (isVertical(parent)) {
                outRect.bottom = dividerHeight;
            } else {
                outRect.right = dividerHeight;
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        boolean vertical = isVertical(parent);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            int total = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;
            if (position >= total - 1) continue;

            if (vertical) {
                float left = child.getLeft() + marginStart;
                float right = child.getRight() - marginEnd;
                float top = child.getBottom();
                float bottom = top + dividerHeight;
                c.drawRect(left, top, right, bottom, paint);
            } else {
                float top = child.getTop() + marginStart;
                float bottom = child.getBottom() - marginEnd;
                float left = child.getRight();
                float right = left + dividerHeight;
                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    private boolean isVertical(RecyclerView parent) {
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) lm).getOrientation() == RecyclerView.VERTICAL;
        }
        return true;
    }
}
