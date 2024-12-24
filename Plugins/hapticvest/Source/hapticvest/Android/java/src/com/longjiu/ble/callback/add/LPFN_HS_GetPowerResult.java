package com.longjiu.ble.callback.add;

/**
 * @name: LPFN_HS_GetPowerResult
 * @description: 电量通知回调
 * @params deviceMode [b8] 返回当前模组ID对应的设备电量，模组ID由DeviceMode枚举值决定
 */
public interface LPFN_HS_GetPowerResult {
    void onResult(int deviceMode, int power);
}