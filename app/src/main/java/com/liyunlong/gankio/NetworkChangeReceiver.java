package com.liyunlong.gankio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.liyunlong.gankio.listener.OnNetWorkChangeListener;
import com.liyunlong.gankio.utils.NetworkHelper;
import com.liyunlong.gankio.utils.NetworkType;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络状态变化广播接收器
 *
 * @author liyunlong
 * @date 2018/7/11 13:48
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public NetworkType oldType = NetworkType.TYPE_NONE;
    private ConnectivityManager mConnectivityManager;
    public List<OnNetWorkChangeListener> mNetWorkChangeListeners;

    private static class NetworkChangeReceiverHolder {
        private static final NetworkChangeReceiver INSTANCE = new NetworkChangeReceiver();
    }

    public static NetworkChangeReceiver getInstance() {
        return NetworkChangeReceiverHolder.INSTANCE;
    }

    private NetworkChangeReceiver() {
        this.mNetWorkChangeListeners = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (mConnectivityManager == null) {
                mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            if (mConnectivityManager != null) {
                NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();// 获取可用的网络信息
                if (networkInfo == null || !networkInfo.isConnected()) {
                    setNetworkType(false, NetworkType.TYPE_NONE);
                    return;
                }
                setNetworkType(networkInfo.isAvailable(), NetworkHelper.getNetworkType(networkInfo));
            }
        }
    }

    private void setNetworkType(boolean isAvailable, NetworkType networkType) {
        for (OnNetWorkChangeListener listener : mNetWorkChangeListeners) {
            listener.onNetWorkChange(isAvailable, oldType, networkType);
        }
        this.oldType = networkType;
    }

    /**
     * 添加网络状态变化监听
     */
    public void addOnNetWorkChangeListener(OnNetWorkChangeListener listener) {
        if (this.mNetWorkChangeListeners.contains(listener)) {
            return;
        }
        this.mNetWorkChangeListeners.add(listener);
    }

    /**
     * 移除网络状态变化监听
     */
    public void removeOnNetWorkChangeListener(OnNetWorkChangeListener listener) {
        if (this.mNetWorkChangeListeners.contains(listener)) {
            this.mNetWorkChangeListeners.remove(listener);
        }
    }

    /**
     * 清空网络状态变化监听
     */
    public void clearOnNetWorkChangeListener() {
        if (!this.mNetWorkChangeListeners.isEmpty()) {
            this.mNetWorkChangeListeners.clear();
        }
    }

}
