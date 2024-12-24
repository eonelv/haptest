package com.longjiu.ble.callback;


public interface BleInitCallback {
    void onSuccess();
    void onFail(int code);
}
