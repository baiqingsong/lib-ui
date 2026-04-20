package com.dawn.lib_ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dawn.lib_ui.R;

/**
 * 空状态/加载状态 View — 显示加载中、空数据、错误三种状态。
 * <pre>
 *   stateView.showLoading();
 *   stateView.showEmpty("暂无数据");
 *   stateView.showError("加载失败", v -> reload());
 *   stateView.hide();
 * </pre>
 */
public class StateView extends FrameLayout {

    private ProgressBar progressBar;
    private ImageView stateIcon;
    private TextView stateText;
    private TextView retryButton;

    public StateView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float density = getResources().getDisplayMetrics().density;
        int dp16 = (int) (16 * density);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        LayoutParams containerParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(container, containerParams);

        // 加载指示器
        progressBar = new ProgressBar(context);
        progressBar.setVisibility(GONE);
        container.addView(progressBar, new LinearLayout.LayoutParams(
                (int) (48 * density), (int) (48 * density)));

        // 状态图标
        stateIcon = new ImageView(context);
        stateIcon.setVisibility(GONE);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (80 * density), (int) (80 * density));
        iconParams.bottomMargin = dp16;
        container.addView(stateIcon, iconParams);

        // 状态文本
        stateText = new TextView(context);
        stateText.setTextSize(14);
        stateText.setTextColor(0xFF999999);
        stateText.setGravity(Gravity.CENTER);
        stateText.setVisibility(GONE);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.bottomMargin = dp16;
        container.addView(stateText, textParams);

        // 重试按钮
        retryButton = new TextView(context);
        retryButton.setTextSize(14);
        retryButton.setTextColor(0xFF2196F3);
        retryButton.setText("重试");
        retryButton.setPadding(dp16 * 2, dp16 / 2, dp16 * 2, dp16 / 2);
        retryButton.setVisibility(GONE);
        container.addView(retryButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setVisibility(GONE);
    }

    public void showLoading() {
        showLoading("加载中...");
    }

    public void showLoading(@Nullable String message) {
        setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
        stateIcon.setVisibility(GONE);
        stateText.setVisibility(message != null ? VISIBLE : GONE);
        if (message != null) stateText.setText(message);
        retryButton.setVisibility(GONE);
    }

    public void showEmpty(@Nullable String message) {
        showEmpty(message, 0);
    }

    public void showEmpty(@Nullable String message, @DrawableRes int iconRes) {
        setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        if (iconRes != 0) {
            stateIcon.setImageResource(iconRes);
            stateIcon.setVisibility(VISIBLE);
        } else {
            stateIcon.setVisibility(GONE);
        }
        stateText.setText(message != null ? message : "暂无数据");
        stateText.setVisibility(VISIBLE);
        retryButton.setVisibility(GONE);
    }

    public void showError(@Nullable String message, @Nullable OnClickListener retryListener) {
        showError(message, 0, retryListener);
    }

    public void showError(@Nullable String message, @DrawableRes int iconRes,
                          @Nullable OnClickListener retryListener) {
        setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        if (iconRes != 0) {
            stateIcon.setImageResource(iconRes);
            stateIcon.setVisibility(VISIBLE);
        } else {
            stateIcon.setVisibility(GONE);
        }
        stateText.setText(message != null ? message : "加载失败");
        stateText.setVisibility(VISIBLE);
        if (retryListener != null) {
            retryButton.setVisibility(VISIBLE);
            retryButton.setOnClickListener(retryListener);
        } else {
            retryButton.setVisibility(GONE);
        }
    }

    public void hide() {
        setVisibility(GONE);
    }
}
