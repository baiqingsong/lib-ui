package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.dawn.lib_ui.R;

/**
 * 可绘制 View — 支持纯色/渐变背景（线性、径向、扫描）、圆角、边框。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code dv_solidColor} — 纯色背景</li>
 *   <li>{@code dv_cornerRadius} — 圆角半径</li>
 *   <li>{@code dv_borderWidth} — 边框宽度</li>
 *   <li>{@code dv_borderColor} — 边框颜色</li>
 * </ul>
 */
public class DrawableView extends View {

    private int solidColor = Color.TRANSPARENT;
    private int[] gradientColors;
    private float gradientAngle = 0;
    private int gradientType = 0; // 0-linear, 1-radial, 2-sweep
    private float cornerRadius = 0;
    private float borderWidth = 0;
    private int borderColor = Color.TRANSPARENT;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clipPath = new Path();
    private final RectF bgRect = new RectF();
    private final Matrix gradientMatrix = new Matrix();

    public DrawableView(Context context) {
        super(context);
        init(null);
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        borderPaint.setStyle(Paint.Style.STROKE);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DrawableView);
            solidColor = a.getColor(R.styleable.DrawableView_dv_solidColor, Color.TRANSPARENT);
            cornerRadius = a.getDimension(R.styleable.DrawableView_dv_cornerRadius, 0);
            borderWidth = a.getDimension(R.styleable.DrawableView_dv_borderWidth, 0);
            borderColor = a.getColor(R.styleable.DrawableView_dv_borderColor, Color.TRANSPARENT);
            a.recycle();
        }
        updatePaints();
    }

    private void updatePaints() {
        if (gradientColors != null && gradientColors.length > 0) {
            bgPaint.setShader(createGradient());
        } else {
            bgPaint.setShader(null);
            bgPaint.setColor(solidColor);
        }
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float half = borderWidth / 2;
        bgRect.set(half, half, w - half, h - half);
        clipPath.reset();
        clipPath.addRoundRect(bgRect, cornerRadius, cornerRadius, Path.Direction.CW);
        gradientMatrix.reset();
        gradientMatrix.setRotate(gradientAngle, bgRect.centerX(), bgRect.centerY());
        updatePaints();
    }

    private Shader createGradient() {
        if (gradientColors == null || gradientColors.length == 0) return null;
        float[] positions = new float[gradientColors.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = (float) i / (positions.length - 1);
        }
        switch (gradientType) {
            case 1: {
                float radius = Math.max(bgRect.width(), bgRect.height()) / 2;
                if (radius <= 0) radius = 1;
                RadialGradient g = new RadialGradient(
                        bgRect.centerX(), bgRect.centerY(), radius,
                        gradientColors, positions, Shader.TileMode.CLAMP);
                g.setLocalMatrix(gradientMatrix);
                return g;
            }
            case 2: {
                SweepGradient g = new SweepGradient(
                        bgRect.centerX(), bgRect.centerY(),
                        gradientColors, positions);
                g.setLocalMatrix(gradientMatrix);
                return g;
            }
            default: {
                LinearGradient g = new LinearGradient(
                        0, 0,
                        (float) (bgRect.width() * Math.cos(Math.toRadians(gradientAngle))),
                        (float) (bgRect.height() * Math.sin(Math.toRadians(gradientAngle))),
                        gradientColors, positions, Shader.TileMode.CLAMP);
                g.setLocalMatrix(gradientMatrix);
                return g;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 背景
        if (solidColor != Color.TRANSPARENT || gradientColors != null) {
            int save = canvas.save();
            canvas.clipPath(clipPath);
            canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, bgPaint);
            canvas.restoreToCount(save);
        }
        // 边框
        if (borderWidth > 0 && borderColor != Color.TRANSPARENT) {
            canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, borderPaint);
        }
    }

    // =============== 动态设置 ===============

    public void setSolidColor(int color) {
        this.solidColor = color;
        this.gradientColors = null;
        updatePaints();
        invalidate();
    }

    public void setGradient(int[] colors, float angle, int type) {
        this.gradientColors = colors;
        this.gradientAngle = angle;
        this.gradientType = type;
        updatePaints();
        invalidate();
    }

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        float half = borderWidth / 2;
        clipPath.reset();
        clipPath.addRoundRect(bgRect, cornerRadius, cornerRadius, Path.Direction.CW);
        invalidate();
    }

    public void setBorder(float width, int color) {
        this.borderWidth = width;
        this.borderColor = color;
        updatePaints();
        invalidate();
    }
}
