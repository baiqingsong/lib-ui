package com.dawn.lib_ui.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.dawn.lib_ui.util.CrashHandler;
import com.dawn.lib_ui.util.ProcessUtil;

/**
 * Application 基类 — 提供通用初始化流程：主进程判断、崩溃日志、Activity 生命周期管理。
 * <p>
 * 子类覆写 {@link #onMainProcessCreate()} 进行业务初始化。
 * <p>
 * 使用示例：
 * <pre>
 *   public class MyApp extends BaseApplication {
 *       protected void onMainProcessCreate() {
 *           // 初始化网络、数据库等
 *       }
 *   }
 * </pre>
 */
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            if (ProcessUtil.isMainProcess(this)) {
                Log.i(TAG, "Main process started");
                // Activity 栈管理
                ActivityStackManager.getInstance().init(this);
                // 崩溃日志
                CrashHandler.getInstance().init(this);
                // 子类业务初始化
                onMainProcessCreate();
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate failed", e);
        }
    }

    /**
     * 主进程创建时调用，子类覆写进行业务初始化。
     * 此方法在 CrashHandler 和 ActivityStackManager 初始化之后调用。
     */
    protected void onMainProcessCreate() {
        // 子类实现
    }

    /**
     * 获取全局 Application 实例。
     */
    public static BaseApplication getInstance() {
        return instance;
    }

    /**
     * 获取全局 Context。
     */
    public static Context getAppContext() {
        return instance != null ? instance.getApplicationContext() : null;
    }
}
