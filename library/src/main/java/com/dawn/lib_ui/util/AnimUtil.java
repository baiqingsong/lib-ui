package com.dawn.lib_ui.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;

/**
 * 常用动画工具类。
 */
public final class AnimUtil {

    private static final int DEFAULT_DURATION = 300;

    private AnimUtil() {
    }

    /**
     * 淡入。
     */
    public static void fadeIn(@NonNull View view) {
        fadeIn(view, DEFAULT_DURATION);
    }

    public static void fadeIn(@NonNull View view, long duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(duration)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    /**
     * 淡出。
     */
    public static void fadeOut(@NonNull View view) {
        fadeOut(view, DEFAULT_DURATION);
    }

    public static void fadeOut(@NonNull View view, long duration) {
        view.animate().alpha(0f).setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                }).start();
    }

    /**
     * 从下方滑入。
     */
    public static void slideInFromBottom(@NonNull View view) {
        slideInFromBottom(view, DEFAULT_DURATION);
    }

    public static void slideInFromBottom(@NonNull View view, long duration) {
        view.setTranslationY(view.getHeight() > 0 ? view.getHeight() : 500);
        view.setVisibility(View.VISIBLE);
        view.animate().translationY(0).setDuration(duration)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    /**
     * 向下滑出。
     */
    public static void slideOutToBottom(@NonNull View view) {
        slideOutToBottom(view, DEFAULT_DURATION);
    }

    public static void slideOutToBottom(@NonNull View view, long duration) {
        view.animate().translationY(view.getHeight() > 0 ? view.getHeight() : 500)
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        view.setTranslationY(0);
                    }
                }).start();
    }

    /**
     * 缩放弹性进入。
     */
    public static void scaleIn(@NonNull View view) {
        scaleIn(view, DEFAULT_DURATION);
    }

    public static void scaleIn(@NonNull View view, long duration) {
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().scaleX(1f).scaleY(1f).setDuration(duration)
                .setInterpolator(new OvershootInterpolator()).start();
    }

    /**
     * 缩放退出。
     */
    public static void scaleOut(@NonNull View view) {
        scaleOut(view, DEFAULT_DURATION);
    }

    public static void scaleOut(@NonNull View view, long duration) {
        view.animate().scaleX(0f).scaleY(0f).setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        view.setScaleX(1f);
                        view.setScaleY(1f);
                    }
                }).start();
    }

    /**
     * 旋转动画。
     */
    public static void rotate(@NonNull View view, float fromDegrees, float toDegrees, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", fromDegrees, toDegrees);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * 抖动动画（用于错误提示）。
     */
    public static void shake(@NonNull View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }
}
