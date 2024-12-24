package com.longjiu.ble.callback.add;

/**
 * 按钮按下(rising edge trigger mode)
 */
public interface LPFN_HS_PushButtonClick {
    void onResult(int buttonId, boolean state);
}
