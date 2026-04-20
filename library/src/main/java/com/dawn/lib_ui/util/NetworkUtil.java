package com.dawn.lib_ui.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;

/**
 * 网络工具类 — 检测网络连接状态（不需要 INTERNET 权限，只需 ACCESS_NETWORK_STATE）。
 */
public final class NetworkUtil {

    public enum NetworkType {
        WIFI, CELLULAR, ETHERNET, NONE
    }

    private NetworkUtil() {
    }

    /**
     * 网络是否可用。
     */
    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            return caps != null && (
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
    }

    /**
     * 是否 WiFi 连接。
     */
    public static boolean isWifiConnected(@NonNull Context context) {
        return getNetworkType(context) == NetworkType.WIFI;
    }

    /**
     * 是否移动网络。
     */
    public static boolean isMobileConnected(@NonNull Context context) {
        return getNetworkType(context) == NetworkType.CELLULAR;
    }

    /**
     * 获取当前网络类型。
     */
    @NonNull
    public static NetworkType getNetworkType(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return NetworkType.NONE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) return NetworkType.NONE;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps == null) return NetworkType.NONE;
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return NetworkType.WIFI;
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return NetworkType.CELLULAR;
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return NetworkType.ETHERNET;
        } else {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isConnected()) return NetworkType.NONE;
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_WIFI) return NetworkType.WIFI;
            if (type == ConnectivityManager.TYPE_MOBILE) return NetworkType.CELLULAR;
            if (type == ConnectivityManager.TYPE_ETHERNET) return NetworkType.ETHERNET;
        }
        return NetworkType.NONE;
    }
}
