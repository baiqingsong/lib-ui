package com.dawn.lib_ui.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;

/**
 * Activity 栈管理器 — 跟踪所有 Activity 的生命周期，支持一键关闭、获取当前栈顶等操作。
 * <p>
 * 在 {@link BaseApplication} 中自动初始化。也可手动调用 {@link #init(Application)}。
 */
public class ActivityStackManager {

    private static volatile ActivityStackManager instance;

    private final Stack<WeakReference<Activity>> activityStack = new Stack<>();
    private WeakReference<Activity> currentActivityRef = new WeakReference<>(null);

    private ActivityStackManager() {
    }

    public static ActivityStackManager getInstance() {
        if (instance == null) {
            synchronized (ActivityStackManager.class) {
                if (instance == null) {
                    instance = new ActivityStackManager();
                }
            }
        }
        return instance;
    }

    /**
     * 注册 Activity 生命周期回调（在 Application.onCreate 中调用）。
     */
    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedState) {
                addActivity(activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivityRef = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (currentActivityRef.get() == activity) {
                    currentActivityRef.clear();
                }
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                removeActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }
        });
    }

    /**
     * 添加 Activity 到栈中。
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            activityStack.push(new WeakReference<>(activity));
        }
    }

    /**
     * 从栈中移除 Activity。
     */
    public void removeActivity(Activity activity) {
        Iterator<WeakReference<Activity>> it = activityStack.iterator();
        while (it.hasNext()) {
            Activity a = it.next().get();
            if (a == null || a == activity) {
                it.remove();
            }
        }
    }

    /**
     * 获取当前栈顶（前台）Activity。
     */
    @Nullable
    public Activity getCurrentActivity() {
        Activity activity = currentActivityRef.get();
        if (activity != null && !activity.isFinishing()) {
            return activity;
        }
        // fallback: 从栈顶查找
        while (!activityStack.isEmpty()) {
            Activity top = activityStack.peek().get();
            if (top != null && !top.isFinishing()) {
                return top;
            }
            activityStack.pop();
        }
        return null;
    }

    /**
     * 关闭所有 Activity。
     */
    public void finishAllActivities() {
        while (!activityStack.isEmpty()) {
            Activity a = activityStack.pop().get();
            if (a != null && !a.isFinishing()) {
                a.finish();
            }
        }
    }

    /**
     * 关闭除指定 Activity 之外的所有 Activity。
     */
    public void finishAllExcept(Activity except) {
        Iterator<WeakReference<Activity>> it = activityStack.iterator();
        while (it.hasNext()) {
            Activity a = it.next().get();
            if (a == null) {
                it.remove();
            } else if (a != except && !a.isFinishing()) {
                a.finish();
                it.remove();
            }
        }
    }

    /**
     * 关闭指定类型的 Activity。
     */
    public void finishActivity(Class<? extends Activity> clazz) {
        Iterator<WeakReference<Activity>> it = activityStack.iterator();
        while (it.hasNext()) {
            Activity a = it.next().get();
            if (a == null) {
                it.remove();
            } else if (a.getClass() == clazz && !a.isFinishing()) {
                a.finish();
                it.remove();
            }
        }
    }

    /**
     * 获取当前栈中 Activity 数量。
     */
    public int getActivityCount() {
        // 清理无效引用
        activityStack.removeIf(ref -> ref.get() == null);
        return activityStack.size();
    }

    /**
     * 判断是否有指定类型的 Activity 存在。
     */
    public boolean hasActivity(Class<? extends Activity> clazz) {
        for (WeakReference<Activity> ref : activityStack) {
            Activity a = ref.get();
            if (a != null && a.getClass() == clazz && !a.isFinishing()) {
                return true;
            }
        }
        return false;
    }
}
