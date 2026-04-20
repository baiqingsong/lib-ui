package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dawn.lib_ui.R;

/**
 * 通用标题栏 — 左侧返回按钮、中间标题、右侧操作按钮。
 * <p>
 * XML 属性：
 * <ul>
 *   <li>{@code tb_title} — 标题文本</li>
 *   <li>{@code tb_titleColor} — 标题颜色</li>
 *   <li>{@code tb_titleSize} — 标题字号</li>
 *   <li>{@code tb_showBack} — 是否显示返回按钮</li>
 *   <li>{@code tb_rightText} — 右侧文本</li>
 * </ul>
 */
public class TitleBar extends RelativeLayout {

    private ImageView backButton;
    private TextView titleTextView;
    private TextView rightTextView;
    private OnTitleBarClickListener listener;

    public interface OnTitleBarClickListener {
        default void onBackClick() {
        }

        default void onRightClick() {
        }
    }

    public TitleBar(Context context) {
        super(context);
        init(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        int barHeight = (int) (48 * density);
        int padding = (int) (12 * density);

        setMinimumHeight(barHeight);

        // 返回按钮
        backButton = new ImageView(context);
        backButton.setImageResource(android.R.drawable.ic_menu_revert);
        backButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        backButton.setPadding(padding, 0, padding, 0);
        LayoutParams backParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        backParams.addRule(ALIGN_PARENT_START);
        backParams.addRule(CENTER_VERTICAL);
        addView(backButton, backParams);

        // 标题
        titleTextView = new TextView(context);
        titleTextView.setTextSize(18);
        titleTextView.setGravity(Gravity.CENTER);
        LayoutParams titleParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        titleParams.addRule(CENTER_IN_PARENT);
        addView(titleTextView, titleParams);

        // 右侧文本
        rightTextView = new TextView(context);
        rightTextView.setTextSize(14);
        rightTextView.setPadding(padding, 0, padding, 0);
        rightTextView.setGravity(Gravity.CENTER);
        rightTextView.setVisibility(GONE);
        LayoutParams rightParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        rightParams.addRule(ALIGN_PARENT_END);
        rightParams.addRule(CENTER_VERTICAL);
        addView(rightTextView, rightParams);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
            String title = a.getString(R.styleable.TitleBar_tb_title);
            if (title != null) titleTextView.setText(title);
            int titleColor = a.getColor(R.styleable.TitleBar_tb_titleColor, 0xFF333333);
            titleTextView.setTextColor(titleColor);
            float titleSize = a.getDimension(R.styleable.TitleBar_tb_titleSize, 18 * density);
            titleTextView.setTextSize(titleSize / density);
            boolean showBack = a.getBoolean(R.styleable.TitleBar_tb_showBack, true);
            backButton.setVisibility(showBack ? VISIBLE : GONE);
            String rightText = a.getString(R.styleable.TitleBar_tb_rightText);
            if (rightText != null) {
                rightTextView.setText(rightText);
                rightTextView.setVisibility(VISIBLE);
            }
            a.recycle();
        }

        backButton.setOnClickListener(v -> {
            if (listener != null) listener.onBackClick();
        });
        rightTextView.setOnClickListener(v -> {
            if (listener != null) listener.onRightClick();
        });
    }

    public void setTitle(@Nullable CharSequence title) {
        titleTextView.setText(title);
    }

    public void setRightText(@Nullable CharSequence text) {
        rightTextView.setText(text);
        rightTextView.setVisibility(text != null ? VISIBLE : GONE);
    }

    public void setShowBack(boolean show) {
        backButton.setVisibility(show ? VISIBLE : GONE);
    }

    public void setBackIcon(@DrawableRes int resId) {
        backButton.setImageResource(resId);
    }

    public void setOnTitleBarClickListener(@Nullable OnTitleBarClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    public TextView getTitleTextView() {
        return titleTextView;
    }

    @NonNull
    public TextView getRightTextView() {
        return rightTextView;
    }

    @NonNull
    public ImageView getBackButton() {
        return backButton;
    }
}
