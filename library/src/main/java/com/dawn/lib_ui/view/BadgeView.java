package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dawn.lib_ui.R;

/**
 * 圆形小红点 / 数字角标 View。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code bv_badgeColor} — 角标颜色（默认红色）</li>
 *   <li>{@code bv_textColor} — 文本颜色（默认白色）</li>
 *   <li>{@code bv_textSize} — 文本大小</li>
 * </ul>
 * <pre>
 *   badgeView.setCount(5);   // 显示数字
 *   badgeView.showDot();     // 仅显示圆点
 *   badgeView.hide();        // 隐藏
 * </pre>
 */
public class BadgeView extends View {

    private int badgeColor = Color.RED;
    private int textColor = Color.WHITE;
    private float textSize;
    private int count = 0;
    private boolean showDot = false;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BadgeView(Context context) {
        super(context);
        init(context, null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        textSize = 10 * getResources().getDisplayMetrics().scaledDensity;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
            badgeColor = a.getColor(R.styleable.BadgeView_bv_badgeColor, Color.RED);
            textColor = a.getColor(R.styleable.BadgeView_bv_textColor, Color.WHITE);
            textSize = a.getDimension(R.styleable.BadgeView_bv_textSize, textSize);
            a.recycle();
        }

        bgPaint.setColor(badgeColor);
        bgPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!showDot && count <= 0) return;

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(cx, cy);

        canvas.drawCircle(cx, cy, radius, bgPaint);

        if (!showDot && count > 0) {
            String text = count > 99 ? "99+" : String.valueOf(count);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float textY = cy - (fm.top + fm.bottom) / 2;
            canvas.drawText(text, cx, textY, textPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dp = (int) getResources().getDisplayMetrics().density;
        int size;
        if (showDot) {
            size = 8 * dp;
        } else if (count > 0) {
            String text = count > 99 ? "99+" : String.valueOf(count);
            int minSize = text.length() > 1 ? 20 * dp : 16 * dp;
            size = minSize;
        } else {
            size = 0;
        }
        int w = resolveSize(size, widthMeasureSpec);
        int h = resolveSize(size, heightMeasureSpec);
        int s = Math.max(w, h);
        setMeasuredDimension(s, s);
    }

    public void setCount(int count) {
        this.count = Math.max(0, count);
        this.showDot = false;
        setVisibility(count > 0 ? VISIBLE : GONE);
        requestLayout();
        invalidate();
    }

    public void showDot() {
        this.count = 0;
        this.showDot = true;
        setVisibility(VISIBLE);
        requestLayout();
        invalidate();
    }

    public void hide() {
        this.count = 0;
        this.showDot = false;
        setVisibility(GONE);
    }

    public int getCount() {
        return count;
    }
}
