package com.longjiu.ble.callback;

import com.longjiu.ble.model.BleRssiDevice;

public interface ScanCallback {
    void onScanResult(BleRssiDevice bleRssiDevice);
}
