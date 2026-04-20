package com.dawn.lib_ui.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 全局崩溃处理器 — 捕获未处理异常，写入日志文件，支持自定义回调。
 * <p>
 * 在 {@link com.dawn.lib_ui.base.BaseApplication} 中自动初始化。
 * 也可手动调用：
 * <pre>
 *   CrashHandler.getInstance().init(context);
 *   CrashHandler.getInstance().init(context, "/sdcard/MyCrash/");
 *   CrashHandler.getInstance().setCrashCallback(errorMsg -> uploadToServer(errorMsg));
 * </pre>
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final long MAX_FOLDER_SIZE = 100 * 1024 * 1024; // 100MB
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private static final CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Context appContext;
    private String crashPath;
    private CrashCallback crashCallback;

    public interface CrashCallback {
        void onCrash(String crashReport);
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化（使用默认路径）。
     */
    public void init(@NonNull Context context) {
        init(context, null);
    }

    /**
     * 初始化（指定崩溃日志目录）。
     */
    public void init(@NonNull Context context, @Nullable String path) {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        appContext = context.getApplicationContext();
        if (path != null && !path.isEmpty()) {
            crashPath = path;
        } else {
            crashPath = getDefaultCrashPath(appContext);
        }
        ensureDirectory(crashPath);
    }

    /**
     * 设置崩溃回调（用于上传至服务器等）。
     */
    public void setCrashCallback(@Nullable CrashCallback callback) {
        this.crashCallback = callback;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        String report = buildCrashReport(thread, ex);

        // 写入文件
        saveCrashFile(report);

        // 回调
        if (crashCallback != null) {
            try {
                crashCallback.onCrash(report);
            } catch (Exception ignore) {
            }
        }

        // 交给系统默认处理器
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private String buildCrashReport(Thread thread, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("Thread: ").append(thread.getName()).append("\n");
        sb.append("Time: ").append(DATE_FORMAT.format(new Date())).append("\n");

        try {
            PackageManager pm = appContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(appContext.getPackageName(), 0);
            sb.append("App Version: ").append(pi.versionName)
                    .append("_").append(pi.versionCode).append("\n");
        } catch (Exception ignore) {
        }

        sb.append("OS Version: ").append(Build.VERSION.RELEASE)
                .append("_").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Vendor: ").append(Build.MANUFACTURER).append("\n");
        sb.append("Model: ").append(Build.MODEL).append("\n");
        sb.append("CPU ABI: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(String.join(", ", Build.SUPPORTED_ABIS));
        }
        sb.append("\n\n");

        // 完整堆栈（包括 cause 链）
        extractFullStackTrace(ex, sb);

        return sb.toString();
    }

    private void extractFullStackTrace(Throwable throwable, StringBuilder output) {
        if (throwable == null) return;
        output.append("Exception: ").append(throwable.getClass().getName()).append("\n");
        output.append("Message: ").append(throwable.getMessage()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            output.append("\tat ").append(element.toString()).append("\n");
        }
        if (throwable.getCause() != null) {
            output.append("\nCaused by: ");
            extractFullStackTrace(throwable.getCause(), output);
        }
        for (Throwable suppressed : throwable.getSuppressed()) {
            output.append("\nSuppressed: ");
            extractFullStackTrace(suppressed, output);
        }
    }

    private void saveCrashFile(String content) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.w(TAG, "External storage not mounted, skip saving crash log");
            return;
        }
        ensureDirectory(crashPath);

        String fileName = crashPath + "crash_" + DATE_FORMAT.format(new Date())
                + "_" + System.currentTimeMillis() + ".trace";
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(fileName));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "Save crash log failed", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static String getDefaultCrashPath(Context context) {
        File dir = context.getExternalFilesDir(null);
        if (dir != null) {
            return dir.getPath() + "/Crash/";
        }
        return context.getFilesDir().getPath() + "/Crash/";
    }

    private static void ensureDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            // 超过 100MB 则清理
            if (getFolderSize(dir) > MAX_FOLDER_SIZE) {
                deleteContents(dir);
            }
        } else {
            dir.mkdirs();
        }
    }

    private static long getFolderSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                size += f.isDirectory() ? getFolderSize(f) : f.length();
            }
        }
        return size;
    }

    private static void deleteContents(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteContents(f);
                }
                f.delete();
            }
        }
    }
}
