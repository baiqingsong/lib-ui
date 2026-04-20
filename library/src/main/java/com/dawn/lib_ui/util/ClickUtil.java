package com.dawn.lib_ui.util;

/**
 * 全局快速点击防抖 — 防止 3 秒内重复点击。
 * <p>
 * 使用示例：
 * <pre>
 *   view.setOnClickListener(v -> {
 *       if (ClickUtil.isFastClick()) return;
 *       // 处理点击
 *   });
 * </pre>
 */
public class ClickUtil {

    private static final long DEFAULT_INTERVAL = 3000L;
    private static long lastClickTime;

    /**
     * 判断是否为快速重复点击（默认 3 秒）。
     *
     * @return true 表示快速重复点击，应忽略
     */
    public static boolean isFastClick() {
        return isFastClick(DEFAULT_INTERVAL);
    }

    /**
     * 判断是否为快速重复点击。
     *
     * @param intervalMs 防抖间隔（毫秒）
     * @return true 表示快速重复点击，应忽略
     */
    public static boolean isFastClick(long intervalMs) {
        long now = System.currentTimeMillis();
        long diff = now - lastClickTime;
        if (diff > 0 && diff < intervalMs) {
            return true;
        }
        lastClickTime = now;
        return false;
    }
}
