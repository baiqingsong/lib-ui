package com.dawn.lib_ui.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences 工具类 — 简化键值对持久化。
 * <pre>
 *   SPUtil.init(context);              // Application 中初始化
 *   SPUtil.put("key", "value");
 *   String v = SPUtil.getString("key", "");
 *   SPUtil.remove("key");
 * </pre>
 */
public final class SPUtil {

    private static final String DEFAULT_SP_NAME = "lib_ui_sp";
    private static SharedPreferences sp;

    private SPUtil() {
    }

    /**
     * 初始化（使用默认名称）。
     */
    public static void init(@NonNull Context context) {
        init(context, DEFAULT_SP_NAME);
    }

    /**
     * 初始化（指定名称）。
     */
    public static void init(@NonNull Context context, @NonNull String name) {
        sp = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getSP() {
        if (sp == null) {
            throw new IllegalStateException("SPUtil not initialized. Call SPUtil.init(context) first.");
        }
        return sp;
    }

    public static void put(@NonNull String key, @Nullable Object value) {
        SharedPreferences.Editor editor = getSP().edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<String> set = (Set<String>) value;
            editor.putStringSet(key, set);
        } else if (value == null) {
            editor.remove(key);
        }
        editor.apply();
    }

    public static String getString(@NonNull String key, @Nullable String defValue) {
        return getSP().getString(key, defValue);
    }

    public static int getInt(@NonNull String key, int defValue) {
        return getSP().getInt(key, defValue);
    }

    public static long getLong(@NonNull String key, long defValue) {
        return getSP().getLong(key, defValue);
    }

    public static float getFloat(@NonNull String key, float defValue) {
        return getSP().getFloat(key, defValue);
    }

    public static boolean getBoolean(@NonNull String key, boolean defValue) {
        return getSP().getBoolean(key, defValue);
    }

    @Nullable
    public static Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defValue) {
        return getSP().getStringSet(key, defValue);
    }

    public static boolean contains(@NonNull String key) {
        return getSP().contains(key);
    }

    public static void remove(@NonNull String key) {
        getSP().edit().remove(key).apply();
    }

    public static void clear() {
        getSP().edit().clear().apply();
    }

    @NonNull
    public static Map<String, ?> getAll() {
        return getSP().getAll();
    }
}
