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

import androidx.appcompat.widget.AppCompatTextView;

import com.dawn.lib_ui.R;

/**
 * 超级 TextView — 支持纯色/渐变背景、圆角、边框，无需额外 drawable XML。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code stv_backgroundSolid} — 纯色背景</li>
 *   <li>{@code stv_cornerRadius} — 圆角半径</li>
 *   <li>{@code stv_borderWidth} — 边框宽度</li>
 *   <li>{@code stv_borderColor} — 边框颜色</li>
 *   <li>{@code stv_gradientAngle} — 渐变角度</li>
 *   <li>{@code stv_gradientType} — 渐变类型（linear/radial/sweep）</li>
 *   <li>{@code stv_gradientColors} — 渐变颜色数组引用</li>
 * </ul>
 */
public class SuperDrawableTextView extends AppCompatTextView {

    private static final int LINEAR = 0;
    private static final int RADIAL = 1;
    private static final int SWEEP = 2;

    private int solidColor = Color.TRANSPARENT;
    private int[] gradientColors;
    private float gradientAngle = 0;
    private int gradientType = LINEAR;
    private float cornerRadius = 0;
    private float borderWidth = 0;
    private int borderColor = Color.TRANSPARENT;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clipPath = new Path();
    private final RectF bgRect = new RectF();

    public SuperDrawableTextView(Context context) {
        super(context);
        init(null);
    }

    public SuperDrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SuperDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        borderPaint.setStyle(Paint.Style.STROKE);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SuperDrawableTextView);
            solidColor = a.getColor(R.styleable.SuperDrawableTextView_stv_backgroundSolid, Color.TRANSPARENT);

            int colorArrayId = a.getResourceId(R.styleable.SuperDrawableTextView_stv_gradientColors, 0);
            if (colorArrayId != 0) {
                TypedArray colors = getResources().obtainTypedArray(colorArrayId);
                gradientColors = new int[colors.length()];
                for (int i = 0; i < colors.length(); i++) {
                    gradientColors[i] = colors.getColor(i, Color.TRANSPARENT);
                }
                colors.recycle();
            }
            gradientAngle = a.getFloat(R.styleable.SuperDrawableTextView_stv_gradientAngle, 0);
            gradientType = a.getInt(R.styleable.SuperDrawableTextView_stv_gradientType, LINEAR);
            cornerRadius = a.getDimension(R.styleable.SuperDrawableTextView_stv_cornerRadius, 0);
            borderWidth = a.getDimension(R.styleable.SuperDrawableTextView_stv_borderWidth, 0);
            borderColor = a.getColor(R.styleable.SuperDrawableTextView_stv_borderColor, Color.TRANSPARENT);
            a.recycle();
        }

        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bgRect.set(borderWidth / 2, borderWidth / 2, w - borderWidth / 2, h - borderWidth / 2);
        updateBackground();
    }

    private void updateBackground() {
        if (gradientColors != null && gradientColors.length > 0) {
            bgPaint.setShader(createGradient());
        } else {
            bgPaint.setShader(null);
            bgPaint.setColor(solidColor);
        }
        clipPath.reset();
        clipPath.addRoundRect(bgRect, cornerRadius, cornerRadius, Path.Direction.CW);
    }

    private Shader createGradient() {
        if (gradientColors == null || gradientColors.length == 0) return null;
        float[] positions = new float[gradientColors.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = (float) i / (positions.length - 1);
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(gradientAngle, bgRect.width() / 2, bgRect.height() / 2);
        switch (gradientType) {
            case RADIAL: {
                float radius = (float) Math.hypot(bgRect.width(), bgRect.height()) / 2;
                if (radius <= 0) radius = 1;
                RadialGradient g = new RadialGradient(
                        bgRect.centerX(), bgRect.centerY(), radius,
                        gradientColors, positions, Shader.TileMode.CLAMP);
                g.setLocalMatrix(matrix);
                return g;
            }
            case SWEEP: {
                SweepGradient g = new SweepGradient(
                        bgRect.centerX(), bgRect.centerY(),
                        gradientColors, positions);
                g.setLocalMatrix(matrix);
                return g;
            }
            default: {
                LinearGradient g = new LinearGradient(
                        0, 0,
                        (float) (bgRect.width() * Math.cos(Math.toRadians(gradientAngle))),
                        (float) (bgRect.height() * Math.sin(Math.toRadians(gradientAngle))),
                        gradientColors, positions, Shader.TileMode.CLAMP);
                g.setLocalMatrix(matrix);
                return g;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (solidColor != Color.TRANSPARENT || gradientColors != null) {
            canvas.save();
            canvas.clipPath(clipPath);
            canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, bgPaint);
            canvas.restore();
        }
        if (borderWidth > 0 && borderColor != Color.TRANSPARENT) {
            canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, borderPaint);
        }
        super.onDraw(canvas);
    }

    // =============== 动态设置 ===============

    public void setSolidBackground(int color) {
        this.solidColor = color;
        this.gradientColors = null;
        updateBackground();
        invalidate();
    }

    public void setGradientBackground(int[] colors, float angle, int type) {
        this.gradientColors = colors;
        this.gradientAngle = angle;
        this.gradientType = type;
        updateBackground();
        invalidate();
    }

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        updateBackground();
        invalidate();
    }

    public void setBorder(float width, int color) {
        this.borderWidth = width;
        this.borderColor = color;
        borderPaint.setStrokeWidth(width);
        borderPaint.setColor(color);
        updateBackground();
        invalidate();
    }
}
