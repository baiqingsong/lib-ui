package com.dawn.lib_ui.util;

import android.os.SystemClock;
import android.view.View;

import java.util.WeakHashMap;

/**
 * 控件维度的点击防抖工厂 — 每个 View 独立计时，防止同一控件短时间内重复点击。
 * <p>
 * 使用示例：
 * <pre>
 *   button.setOnClickListener(ClickFactory.wrap(v -> {
 *       // 处理点击
 *   }));
 * </pre>
 */
public class ClickFactory {

    private static final WeakHashMap<View, Long> LAST_CLICK_MAP = new WeakHashMap<>();
    private static final long DEFAULT_INTERVAL_MS = 500L;

    /**
     * 判断此次点击是否应当处理。
     *
     * @param view       被点击的控件
     * @param intervalMs 间隔毫秒数，<=0 则使用默认值
     * @return true 表示可以处理
     */
    public static boolean shouldHandleClick(View view, long intervalMs) {
        if (view == null) return false;
        long now = SystemClock.uptimeMillis();
        long interval = intervalMs > 0 ? intervalMs : DEFAULT_INTERVAL_MS;
        Long last = LAST_CLICK_MAP.get(view);
        if (last == null || (now - last) >= interval) {
            LAST_CLICK_MAP.put(view, now);
            return true;
        }
        return false;
    }

    /**
     * 将 OnClickListener 包装为具备防抖能力的监听器。
     *
     * @param origin     原始点击回调
     * @param intervalMs 防抖间隔
     * @return 包装后的监听器
     */
    public static View.OnClickListener wrap(View.OnClickListener origin, long intervalMs) {
        return v -> {
            if (origin != null && shouldHandleClick(v, intervalMs)) {
                origin.onClick(v);
            }
        };
    }

    /**
     * 使用默认间隔包装。
     */
    public static View.OnClickListener wrap(View.OnClickListener origin) {
        return wrap(origin, DEFAULT_INTERVAL_MS);
    }
}
