package com.dawn.lib_ui.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 进程工具类 — 主进程判断、APK 安装、Activity 栈顶检测等。
 */
public class ProcessUtil {

    private ProcessUtil() {
    }

    /**
     * 判断当前是否主进程。
     */
    public static boolean isMainProcess(@NonNull Context context) {
        try {
            String mainProcess = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), 0).processName;
            return isPidOfProcessName(context, Process.myPid(), mainProcess);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    private static boolean isPidOfProcessName(Context context, int pid, String processName) {
        if (processName == null) return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                return processName.equals(info.processName);
            }
        }
        return false;
    }

    /**
     * 检查是否拥有安装未知来源应用的权限。
     */
    public static boolean hasInstallPermission(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 请求安装未知来源应用的权限。
     */
    public static void requestInstallPermission(@NonNull Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri packageUri = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 安装 APK 文件。
     *
     * @param context     上下文
     * @param apkFilePath APK 文件绝对路径
     */
    public static void installApk(@NonNull Context context, @NonNull String apkFilePath) {
        File apkFile = new File(apkFilePath);
        if (!apkFile.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".fileprovider", apkFile);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取当前应用版本名。
     */
    public static String getVersionName(@NonNull Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 获取当前应用版本号。
     */
    public static int getVersionCode(@NonNull Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
