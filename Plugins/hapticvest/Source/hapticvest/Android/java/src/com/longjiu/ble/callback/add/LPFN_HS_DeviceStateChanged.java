package com.longjiu.ble.callback.add;

/**
 * 状态发生变化
 */
public interface LPFN_HS_DeviceStateChanged {
    void onResult(String address, boolean state);
}
