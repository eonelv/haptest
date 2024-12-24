package com.longjiu.ble.callback.add;

/**
 * 盔甲移动回调
 * 参数：frontOrBack ∈ [-1,0) 前移动, 0 静止, (0, +1] 后移动
 * 参数：leftOrRight ∈ [-1,0) 左移动, 0 静止, (0, +1] 右移动
 * 参数: around   ∈ [-1,0) 左转, 0 静止, (0, +1] 右转
 */
public interface LPFN_HS_ArmorMoveResult {
    void onResult(double frontOrBack, double leftOrRight, double around);
}
