package com.longjiu.ble.callback;

public interface DeviceTransferCallback {
    int CONNECT_CODE_UNCONNECT = 0;
    int CONNECT_CODE_CONNECTING = 1;
    int CONNECT_CODE_CONNECTED = 2;

    /**
     * 连接状态变化
     * 0:unconnect
     * 1:connecting
     * 2:connected
     */
    void onConnectionChanged(String address, int connectCode);

    /**
     * 连接失败回调
     */
    void onConnectFailed(String address, int errorCode);

    /**
     * 连接取消
     */
    void onConnectCancel(String address);

    /**
     * 读通道订阅成功
     */
    void onNotifySuccess(String address);

    /**
     * 写成功
     */
    void onWriteSuccess(String address);

    /**
     * 写失败
     */
    void onWriteFailed(String address, int code);
}
