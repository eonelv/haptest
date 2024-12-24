package com.hap.ngcod;

public interface IHapHandler {
    void onDeviceStateChanged(String address, boolean state);

    void CalibrationCompleteResult(int modules);

    void PowerResult(int deviceMode, int power);

    void SetArmorMoveResult(double frontOrBack, double leftOrRight, double around);

    void PushButtonClick(int buttonId, boolean state);

    void PushButtonStick(int buttonId);

    void InfraredEntry(int buttonId, boolean state);

    void InfraredSustained(int buttonId);

    void SetLegMoveResult(int pos, double frontOrBack, double leftOrRight, double around);

    void onInitSuccess();

    void onInitFail(int code);

    void onScanResult(String address, String name);

    void onConnectionChange(String address, int connectCode);

    void onConnectFailed(String address, int errorCode);

    void onConnectCancel(String address);

    void onNotifySuccess(String address);

    void onWriteSuccess(String address);

    void onWriteFailed(String address, int code);
}
