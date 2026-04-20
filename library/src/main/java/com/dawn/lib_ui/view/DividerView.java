package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dawn.lib_ui.R;

/**
 * 通用分割线 View — 支持水平/垂直、实线/虚线、自定义颜色和粗细。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code div_color} — 颜色</li>
 *   <li>{@code div_thickness} — 粗细</li>
 *   <li>{@code div_orientation} — 方向（horizontal / vertical）</li>
 *   <li>{@code div_dashWidth} — 虚线段宽（0=实线）</li>
 *   <li>{@code div_dashGap} — 虚线间距</li>
 * </ul>
 */
public class DividerView extends View {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private int dividerColor = 0xFFE0E0E0;
    private float thickness;
    private int orientation = HORIZONTAL;
    private float dashWidth = 0;
    private float dashGap = 0;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DividerView(Context context) {
        super(context);
        init(context, null);
    }

    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        thickness = density; // 默认 1dp

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DividerView);
            dividerColor = a.getColor(R.styleable.DividerView_div_color, 0xFFE0E0E0);
            thickness = a.getDimension(R.styleable.DividerView_div_thickness, density);
            orientation = a.getInt(R.styleable.DividerView_div_orientation, HORIZONTAL);
            dashWidth = a.getDimension(R.styleable.DividerView_div_dashWidth, 0);
            dashGap = a.getDimension(R.styleable.DividerView_div_dashGap, 0);
            a.recycle();
        }

        paint.setColor(dividerColor);
        paint.setStrokeWidth(thickness);
        paint.setStyle(Paint.Style.STROKE);

        if (dashWidth > 0 && dashGap > 0) {
            paint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, 0));
            setLayerType(LAYER_TYPE_SOFTWARE, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (orientation == HORIZONTAL) {
            int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            setMeasuredDimension(width, (int) Math.ceil(thickness));
        } else {
            int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension((int) Math.ceil(thickness), height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float half = thickness / 2;
        if (orientation == HORIZONTAL) {
            canvas.drawLine(0, half, getWidth(), half, paint);
        } else {
            canvas.drawLine(half, 0, half, getHeight(), paint);
        }
    }
}
