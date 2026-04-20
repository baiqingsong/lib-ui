package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.dawn.lib_ui.R;

/**
 * 圆角 ImageView — 支持统一圆角和四角独立设置、圆形模式、边框。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code riv_cornerRadius} — 统一圆角半径</li>
 *   <li>{@code riv_topLeftRadius} — 左上角</li>
 *   <li>{@code riv_topRightRadius} — 右上角</li>
 *   <li>{@code riv_bottomLeftRadius} — 左下角</li>
 *   <li>{@code riv_bottomRightRadius} — 右下角</li>
 *   <li>{@code riv_borderWidth} — 边框宽度</li>
 *   <li>{@code riv_borderColor} — 边框颜色</li>
 *   <li>{@code riv_isCircle} — 是否圆形</li>
 * </ul>
 */
public class RoundImageView extends AppCompatImageView {

    private float cornerRadius = 0;
    private float topLeftRadius = 0;
    private float topRightRadius = 0;
    private float bottomLeftRadius = 0;
    private float bottomRightRadius = 0;
    private float borderWidth = 0;
    private int borderColor = Color.TRANSPARENT;
    private boolean isCircle = false;

    private final Path clipPath = new Path();
    private final RectF drawRect = new RectF();
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RoundImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        borderPaint.setStyle(Paint.Style.STROKE);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
            cornerRadius = a.getDimension(R.styleable.RoundImageView_riv_cornerRadius, 0);
            topLeftRadius = a.getDimension(R.styleable.RoundImageView_riv_topLeftRadius, 0);
            topRightRadius = a.getDimension(R.styleable.RoundImageView_riv_topRightRadius, 0);
            bottomLeftRadius = a.getDimension(R.styleable.RoundImageView_riv_bottomLeftRadius, 0);
            bottomRightRadius = a.getDimension(R.styleable.RoundImageView_riv_bottomRightRadius, 0);
            borderWidth = a.getDimension(R.styleable.RoundImageView_riv_borderWidth, 0);
            borderColor = a.getColor(R.styleable.RoundImageView_riv_borderColor, Color.TRANSPARENT);
            isCircle = a.getBoolean(R.styleable.RoundImageView_riv_isCircle, false);
            a.recycle();
        }

        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateClipPath();
    }

    private void updateClipPath() {
        float half = borderWidth / 2;
        drawRect.set(half, half, getWidth() - half, getHeight() - half);
        clipPath.reset();

        if (isCircle) {
            float radius = Math.min(drawRect.width(), drawRect.height()) / 2;
            clipPath.addCircle(drawRect.centerX(), drawRect.centerY(), radius, Path.Direction.CW);
        } else {
            float tl = topLeftRadius > 0 ? topLeftRadius : cornerRadius;
            float tr = topRightRadius > 0 ? topRightRadius : cornerRadius;
            float bl = bottomLeftRadius > 0 ? bottomLeftRadius : cornerRadius;
            float br = bottomRightRadius > 0 ? bottomRightRadius : cornerRadius;
            float[] radii = {tl, tl, tr, tr, br, br, bl, bl};
            clipPath.addRoundRect(drawRect, radii, Path.Direction.CW);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean hasClip = cornerRadius > 0 || topLeftRadius > 0 || topRightRadius > 0
                || bottomLeftRadius > 0 || bottomRightRadius > 0 || isCircle;

        if (hasClip) {
            canvas.save();
            canvas.clipPath(clipPath);
            super.onDraw(canvas);
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }

        if (borderWidth > 0 && borderColor != Color.TRANSPARENT) {
            if (isCircle) {
                float radius = Math.min(drawRect.width(), drawRect.height()) / 2;
                canvas.drawCircle(drawRect.centerX(), drawRect.centerY(), radius, borderPaint);
            } else {
                float tl = topLeftRadius > 0 ? topLeftRadius : cornerRadius;
                float tr = topRightRadius > 0 ? topRightRadius : cornerRadius;
                float bl = bottomLeftRadius > 0 ? bottomLeftRadius : cornerRadius;
                float br = bottomRightRadius > 0 ? bottomRightRadius : cornerRadius;
                float[] radii = {tl, tl, tr, tr, br, br, bl, bl};
                Path borderPath = new Path();
                borderPath.addRoundRect(drawRect, radii, Path.Direction.CW);
                canvas.drawPath(borderPath, borderPaint);
            }
        }
    }

    // =============== 动态设置 ===============

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        updateClipPath();
        invalidate();
    }

    public void setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        this.topLeftRadius = topLeft;
        this.topRightRadius = topRight;
        this.bottomRightRadius = bottomRight;
        this.bottomLeftRadius = bottomLeft;
        updateClipPath();
        invalidate();
    }

    public void setCircle(boolean circle) {
        this.isCircle = circle;
        updateClipPath();
        invalidate();
    }

    public void setBorder(float width, int color) {
        this.borderWidth = width;
        this.borderColor = color;
        borderPaint.setStrokeWidth(width);
        borderPaint.setColor(color);
        updateClipPath();
        invalidate();
    }
}
