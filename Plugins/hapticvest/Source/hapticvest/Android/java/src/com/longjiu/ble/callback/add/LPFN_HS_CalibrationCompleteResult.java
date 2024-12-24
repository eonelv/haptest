package com.longjiu.ble.callback.add;

/**
 * 标定完成回调
 * 参数：modules  0: 盔甲,1 左脚，2 右脚
 */
public interface LPFN_HS_CalibrationCompleteResult {
    void onResult(int modules);
}
