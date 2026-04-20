package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.dawn.lib_ui.R;

/**
 * 自定义 SeekBar — 完全自绘轨道、滑块、进度条、刻度，支持渐变和垂直模式。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code csb_trackHeight} — 轨道高度</li>
 *   <li>{@code csb_trackCornerRadius} — 轨道圆角</li>
 *   <li>{@code csb_progressColor} — 进度条颜色</li>
 *   <li>{@code csb_trackColor} — 轨道背景颜色</li>
 *   <li>{@code csb_thumbSize} — 滑块大小</li>
 *   <li>{@code csb_thumbColor} — 滑块颜色</li>
 *   <li>{@code csb_thumbBorderWidth} — 滑块边框宽度</li>
 *   <li>{@code csb_thumbBorderColor} — 滑块边框颜色</li>
 *   <li>{@code csb_showTicks} — 是否显示刻度</li>
 *   <li>{@code csb_tickCount} — 刻度数量</li>
 *   <li>{@code csb_tickColor} — 刻度颜色</li>
 *   <li>{@code csb_tickWidth} — 刻度线宽</li>
 * </ul>
 */
public class CustomSeekBar extends AppCompatSeekBar {

    private int mTrackHeight;
    private float mTrackCornerRadius;
    private int mProgressColor;
    private int mBackgroundColor;
    private Shader mProgressShader;

    private int mThumbSize;
    private int mThumbColor;
    private int mThumbBorderWidth;
    private int mThumbBorderColor;

    private boolean mShowTicks;
    private int mTickCount;
    private int mTickColor;
    private int mTickWidth;

    private Paint mProgressPaint;
    private Paint mBackgroundPaint;
    private Paint mTickPaint;
    private Paint mThumbPaint;
    private Paint mThumbBorderPaint;
    private final RectF mTrackRect = new RectF();

    private boolean mIsVertical = false;

    public CustomSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setThumb(null);
        setSplitTrack(false);

        mTrackHeight = dpToPx(4);
        mTrackCornerRadius = dpToPx(2);
        mProgressColor = Color.parseColor("#FF4081");
        mBackgroundColor = Color.parseColor("#E0E0E0");
        mThumbSize = dpToPx(16);
        mThumbColor = Color.WHITE;
        mThumbBorderWidth = 0;
        mThumbBorderColor = Color.TRANSPARENT;
        mShowTicks = false;
        mTickCount = 5;
        mTickColor = Color.parseColor("#757575");
        mTickWidth = dpToPx(1);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);
            mTrackHeight = a.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_trackHeight, mTrackHeight);
            mTrackCornerRadius = a.getDimension(R.styleable.CustomSeekBar_csb_trackCornerRadius, mTrackCornerRadius);
            mProgressColor = a.getColor(R.styleable.CustomSeekBar_csb_progressColor, mProgressColor);
            mBackgroundColor = a.getColor(R.styleable.CustomSeekBar_csb_trackColor, mBackgroundColor);
            mThumbSize = a.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_thumbSize, mThumbSize);
            mThumbColor = a.getColor(R.styleable.CustomSeekBar_csb_thumbColor, mThumbColor);
            mThumbBorderWidth = a.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_thumbBorderWidth, 0);
            mThumbBorderColor = a.getColor(R.styleable.CustomSeekBar_csb_thumbBorderColor, Color.TRANSPARENT);
            mShowTicks = a.getBoolean(R.styleable.CustomSeekBar_csb_showTicks, false);
            mTickCount = a.getInteger(R.styleable.CustomSeekBar_csb_tickCount, 5);
            mTickColor = a.getColor(R.styleable.CustomSeekBar_csb_tickColor, mTickColor);
            mTickWidth = a.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_tickWidth, mTickWidth);
            a.recycle();
        }
        initPaints();
    }

    private void initPaints() {
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(mProgressColor);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);

        mTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(mTickWidth);
        mTickPaint.setColor(mTickColor);

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setStyle(Paint.Style.FILL);
        mThumbPaint.setColor(mThumbColor);
        mThumbPaint.setShadowLayer(dpToPx(4), 0, dpToPx(2), Color.argb(50, 0, 0, 0));

        mThumbBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbBorderPaint.setStyle(Paint.Style.STROKE);
        mThumbBorderPaint.setStrokeWidth(mThumbBorderWidth);
        mThumbBorderPaint.setColor(mThumbBorderColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateTrackRect();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void updateTrackRect() {
        float halfThumb = mThumbSize / 2f;
        if (mIsVertical) {
            mTrackRect.set(
                    getWidth() / 2f - mTrackHeight / 2f, halfThumb,
                    getWidth() / 2f + mTrackHeight / 2f, getHeight() - halfThumb);
        } else {
            mTrackRect.set(
                    halfThumb, getHeight() / 2f - mTrackHeight / 2f,
                    getWidth() - halfThumb, getHeight() / 2f + mTrackHeight / 2f);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (mIsVertical) {
            canvas.rotate(-90);
            canvas.translate(-getHeight(), 0);
        }
        drawTrackBackground(canvas);
        drawProgress(canvas);
        drawTicks(canvas);
        drawThumb(canvas);
    }

    private void drawTrackBackground(Canvas canvas) {
        canvas.drawRoundRect(mTrackRect, mTrackCornerRadius, mTrackCornerRadius, mBackgroundPaint);
    }

    private void drawProgress(Canvas canvas) {
        float progress = getProgress() / (float) getMax();
        RectF progressRect = new RectF(mTrackRect);
        if (mIsVertical) {
            progressRect.top = progressRect.bottom - progressRect.height() * progress;
        } else {
            progressRect.right = progressRect.left + progressRect.width() * progress;
        }
        if (mProgressShader != null) {
            mProgressPaint.setShader(mProgressShader);
        } else {
            mProgressPaint.setShader(null);
            mProgressPaint.setColor(mProgressColor);
        }
        canvas.drawRoundRect(progressRect, mTrackCornerRadius, mTrackCornerRadius, mProgressPaint);
    }

    private void drawTicks(Canvas canvas) {
        if (!mShowTicks || mTickCount < 2) return;
        float trackLen = mIsVertical ? mTrackRect.height() : mTrackRect.width();
        float interval = trackLen / (mTickCount - 1);
        float tickLength = dpToPx(8);
        for (int i = 0; i < mTickCount; i++) {
            if (mIsVertical) {
                float y = mTrackRect.top + interval * i;
                canvas.drawLine(mTrackRect.centerX() - tickLength / 2, y,
                        mTrackRect.centerX() + tickLength / 2, y, mTickPaint);
            } else {
                float x = mTrackRect.left + interval * i;
                canvas.drawLine(x, mTrackRect.centerY() - tickLength / 2,
                        x, mTrackRect.centerY() + tickLength / 2, mTickPaint);
            }
        }
    }

    private void drawThumb(Canvas canvas) {
        float progress = getProgress() / (float) getMax();
        float thumbPos = mIsVertical
                ? mTrackRect.bottom - mTrackRect.height() * progress
                : mTrackRect.left + mTrackRect.width() * progress;
        float cx = mIsVertical ? mTrackRect.centerY() : thumbPos;
        float cy = mIsVertical ? thumbPos : mTrackRect.centerY();

        if (mThumbBorderWidth > 0) {
            canvas.drawCircle(cx, cy, mThumbSize / 2f, mThumbBorderPaint);
        }
        canvas.drawCircle(cx, cy, mThumbSize / 2f - mThumbBorderWidth, mThumbPaint);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsVertical) {
            int minWidth = mThumbSize + getPaddingLeft() + getPaddingRight();
            int width = resolveSize(minWidth, widthMeasureSpec);
            setMeasuredDimension(width, getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        } else {
            int minHeight = mThumbSize + getPaddingTop() + getPaddingBottom();
            int height = resolveSize(minHeight, heightMeasureSpec);
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
        }
    }

    // =============== 动态设置 ===============

    public void setVertical(boolean vertical) {
        mIsVertical = vertical;
        updateTrackRect();
        requestLayout();
    }

    public void setProgressGradient(int startColor, int endColor) {
        mProgressShader = new LinearGradient(
                mTrackRect.left, mTrackRect.top, mTrackRect.right, mTrackRect.bottom,
                startColor, endColor, Shader.TileMode.CLAMP);
        invalidate();
    }

    public void setThumbBorder(int widthDp, int color) {
        mThumbBorderWidth = dpToPx(widthDp);
        mThumbBorderColor = color;
        mThumbBorderPaint.setStrokeWidth(mThumbBorderWidth);
        mThumbBorderPaint.setColor(color);
        invalidate();
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }
}
