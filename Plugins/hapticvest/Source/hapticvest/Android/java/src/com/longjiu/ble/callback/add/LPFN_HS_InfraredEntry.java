package com.longjiu.ble.callback.add;

/**
 * 红外进入(rising edge trigger mode)
 */
public interface LPFN_HS_InfraredEntry {
    void onResult(int buttonId, boolean state);
}
