package com.dawn.lib_ui.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity 基类 — 提供通用生命周期管理、全屏/状态栏、权限请求、页面跳转、Toast 等能力。
 * <p>
 * 子类必须实现 {@link #getLayoutId()}, {@link #initView()}, {@link #initData()}, {@link #addListener()}.
 * <p>
 * 使用示例：
 * <pre>
 *   public class HomeActivity extends BaseActivity {
 *       protected int getLayoutId() { return R.layout.activity_home; }
 *       protected void initView() { ... }
 *       protected void initData() { ... }
 *       protected void addListener() { ... }
 *   }
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActivityStackManager.getInstance().addActivity(this);
        setContentView(getLayoutId());
        initData();
        initView();
        addListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackManager.getInstance().removeActivity(this);
    }

    // ======================== 子类必须实现 ========================

    /** 返回布局资源 ID */
    @LayoutRes
    protected abstract int getLayoutId();

    /** 初始化数据（在 setContentView 之后、initView 之前调用） */
    protected abstract void initData();

    /** 初始化控件 */
    protected abstract void initView();

    /** 添加监听器 */
    protected abstract void addListener();

    // ======================== 全屏/状态栏 ========================

    /**
     * 设置半透明状态栏 + 白色状态栏背景。
     */
    protected void setTranslucentStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.WHITE);
        decorViewGroup.addView(statusBarView);
    }

    /**
     * 设置半透明状态栏 + 自定义颜色。
     */
    protected void setTranslucentStatusBar(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        decorViewGroup.addView(statusBarView);
    }

    /**
     * 隐藏虚拟导航栏，全屏沉浸式。
     */
    protected void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * 设置全屏窗口属性（无状态栏、充满屏幕）。
     */
    protected void setupFullScreen() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0f;
        window.setAttributes(params);
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }

    /**
     * 设置状态栏亮色模式（深色图标，API 23+）。
     */
    protected void setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 设置状态栏暗色模式（亮色图标）。
     */
    protected void setDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 保持屏幕常亮。
     */
    protected void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 获取状态栏高度。
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    // ======================== 页面跳转 ========================

    protected void jumpToActivity(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    protected void jumpToActivity(Class<?> clazz, Bundle extras) {
        startActivity(new Intent(this, clazz).putExtras(extras));
    }

    protected void jumpToActivity(Class<?> clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    protected void jumpToActivity(Class<?> clazz, Bundle extras, int requestCode) {
        startActivityForResult(new Intent(this, clazz).putExtras(extras), requestCode);
    }

    protected void jumpToActivity(String className) throws ClassNotFoundException {
        jumpToActivity(Class.forName(className));
    }

    protected void jumpToActivity(String className, Bundle extras) throws ClassNotFoundException {
        jumpToActivity(Class.forName(className), extras);
    }

    // ======================== Toast ========================

    protected void toast(String msg) {
        runOnUiThread(() ->
                Toast.makeText(this, TextUtils.isEmpty(msg) ? "" : msg, Toast.LENGTH_SHORT).show());
    }

    protected void toastLong(String msg) {
        runOnUiThread(() ->
                Toast.makeText(this, TextUtils.isEmpty(msg) ? "" : msg, Toast.LENGTH_LONG).show());
    }

    // ======================== View 便捷方法 ========================

    protected void setTextViewStr(TextView textView, String text) {
        if (textView != null) {
            textView.setText(TextUtils.isEmpty(text) ? "" : text);
        }
    }

    protected void visible(View view) {
        if (view != null) view.post(() -> view.setVisibility(View.VISIBLE));
    }

    protected void gone(View view) {
        if (view != null) view.post(() -> view.setVisibility(View.GONE));
    }

    protected void invisible(View view) {
        if (view != null) view.post(() -> view.setVisibility(View.INVISIBLE));
    }

    public void closeActivity(View view) {
        finish();
    }

    // ======================== 权限请求 ========================

    private int mRequestCode;

    protected void requestPermission(String[] permissions, int requestCode) {
        this.mRequestCode = requestCode;
        if (checkPermissions(permissions)) {
            onPermissionGranted(requestCode);
        } else {
            List<String> denied = getDeniedPermissions(permissions);
            ActivityCompat.requestPermissions(this,
                    denied.toArray(new String[0]), requestCode);
        }
    }

    private boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                list.add(permission);
            }
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                onPermissionGranted(requestCode);
            } else {
                onPermissionDenied(requestCode);
            }
        }
    }

    /**
     * 权限已授予回调。子类按需覆写。
     */
    protected void onPermissionGranted(int requestCode) {
        Log.d(TAG, "Permission granted: " + requestCode);
    }

    /**
     * 权限被拒绝回调。子类按需覆写。
     */
    protected void onPermissionDenied(int requestCode) {
        Log.d(TAG, "Permission denied: " + requestCode);
    }

    /**
     * 跳转到应用设置页面。
     */
    protected void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    // ======================== 退出确认 ========================

    protected void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("确定退出吗？")
                .setPositiveButton("确定", (d, w) ->
                        ActivityStackManager.getInstance().finishAllActivities())
                .setNegativeButton("取消", null)
                .show();
    }

    // ======================== dp/px ========================

    protected int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
