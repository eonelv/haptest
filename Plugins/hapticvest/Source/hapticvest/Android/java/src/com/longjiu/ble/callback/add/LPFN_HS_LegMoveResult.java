package com.longjiu.ble.callback.add;

/**
 * 腿部移动回调
 * 参数：pos：0左腿。1右腿
 * 参数：frontOrBack ∈ [-1,0) 前移动, 0 静止, (0, +1] 后移动
 * 参数：leftOrRight ∈ [-1,0) 左移动, 0 静止, (0, +1] 右移动
 * 参数：around ∈ [-1,1]
 */
public interface LPFN_HS_LegMoveResult {
    void onResult(int pos, double frontOrBack, double leftOrRight, double around);
}
