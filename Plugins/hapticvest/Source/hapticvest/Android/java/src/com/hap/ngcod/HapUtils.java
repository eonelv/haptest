package com.hap.ngcod;

import static com.longjiu.ble.service.ArmorDeviceServiceInterface.MODULES_CLOTHING;

import android.app.Activity;
import android.util.Log;

import com.epicgames.unreal.GameActivity;
import com.longjiu.ble.callback.BleInitCallback;
import com.longjiu.ble.callback.DeviceTransferCallback;
import com.longjiu.ble.callback.ScanCallback;
import com.longjiu.ble.callback.add.LPFN_HS_ArmorMoveResult;
import com.longjiu.ble.callback.add.LPFN_HS_CalibrationCompleteResult;
import com.longjiu.ble.callback.add.LPFN_HS_DeviceStateChanged;
import com.longjiu.ble.callback.add.LPFN_HS_GetPowerResult;
import com.longjiu.ble.callback.add.LPFN_HS_InfraredEntry;
import com.longjiu.ble.callback.add.LPFN_HS_InfraredSustained;
import com.longjiu.ble.callback.add.LPFN_HS_LegMoveResult;
import com.longjiu.ble.callback.add.LPFN_HS_PushButtonClick;
import com.longjiu.ble.callback.add.LPFN_HS_PushButtonStick;
import com.longjiu.ble.model.BleRssiDevice;
import com.longjiu.ble.model.DeviceEnum;
import com.longjiu.ble.service.ArmorDeviceService;
import com.longjiu.ble.service.BleHelper;

import cn.com.heaton.blelibrary.ble.BleLog;

public class HapUtils {
    private static final String TAG = "ngcod";
    private static IHapHandler hapHandler;
    private static Activity gameActivity;

    /**
     * 设置事件
     */
    public static void initEvent(IHapHandler hapHandler) {
        HapUtils.hapHandler = hapHandler;
        ArmorDeviceService.me().HS_Init();

        ArmorDeviceService.me().HS_DeviceStateChanged(new LPFN_HS_DeviceStateChanged() {
            @Override
            public void onResult(String address, boolean state) {
                HapUtils.hapHandler.onDeviceStateChanged(address, state);
                BleLog.e(TAG, "HS_DeviceStateChanged " + state);
            }
        });
        ArmorDeviceService.me().HS_CalibrationCompleteResult(new LPFN_HS_CalibrationCompleteResult() {
            @Override
            public void onResult(int modules) {
                HapUtils.hapHandler.CalibrationCompleteResult(modules);
                BleLog.e(TAG, "HS_CalibrationCompleteResult " + modules);
            }
        });
        ArmorDeviceService.me().HS_PowerResult(new LPFN_HS_GetPowerResult() {
            @Override
            public void onResult(int deviceMode, int power) {
                HapUtils.hapHandler.PowerResult(deviceMode, power);
                BleLog.e(TAG, "HS_PowerResult " + power);
            }
        });
        ArmorDeviceService.me().HS_SetArmorMoveResult(new LPFN_HS_ArmorMoveResult() {
            @Override
            public void onResult(double frontOrBack, double leftOrRight, double around) {
                HapUtils.hapHandler.SetArmorMoveResult(frontOrBack, leftOrRight, around);
                BleLog.e(TAG, "HS_SetArmorMoveResult " + frontOrBack + " " + leftOrRight + " " + around);
            }
        });
        ArmorDeviceService.me().HS_PushButtonClick(new LPFN_HS_PushButtonClick() {
            @Override
            public void onResult(int buttonId, boolean state) {
                HapUtils.hapHandler.PushButtonClick(buttonId, state);
                if (buttonId == DeviceEnum.LeftButton.value) {
                    BleLog.e(TAG, "HS_PushButtonClick 左侧" + buttonId + " " + state);
                } else if (buttonId == DeviceEnum.RightButton.value) {
                    BleLog.e(TAG, "HS_PushButtonClick 右侧" + buttonId + " " + state);
                }
            }
        });
        ArmorDeviceService.me().HS_PushButtonStick(new LPFN_HS_PushButtonStick() {
            @Override
            public void onResult(int buttonId) {
                HapUtils.hapHandler.PushButtonStick(buttonId);
                BleLog.e(TAG, "HS_PushButtonStick " + buttonId);
            }
        });
        ArmorDeviceService.me().HS_InfraredEntry(new LPFN_HS_InfraredEntry() {
            @Override
            public void onResult(int buttonId, boolean state) {
                HapUtils.hapHandler.InfraredEntry(buttonId, state);
                if (buttonId == DeviceEnum.LeftButton.value) {
                    BleLog.e(TAG, "HS_InfraredEntry 左侧" + buttonId + " " + state);
                } else if (buttonId == DeviceEnum.RightButton.value) {
                    BleLog.e(TAG, "HS_InfraredEntry 右侧" + buttonId + " " + state);
                }
            }
        });
        ArmorDeviceService.me().HS_InfraredSustained(new LPFN_HS_InfraredSustained() {
            @Override
            public void onResult(int buttonId) {
                HapUtils.hapHandler.InfraredSustained(buttonId);
                if (buttonId == DeviceEnum.LeftInfrared.value) {
                    BleLog.e(TAG, "HS_InfraredSustained 左侧" + buttonId);
                } else if (buttonId == DeviceEnum.RightInfrared.value) {
                    BleLog.e(TAG, "HS_InfraredSustained 右侧" + buttonId);
                }
            }
        });
        ArmorDeviceService.me().HS_SetLegMoveResult(new LPFN_HS_LegMoveResult() {
            @Override
            public void onResult(int pos, double frontOrBack, double leftOrRight, double around) {
                HapUtils.hapHandler.SetLegMoveResult(pos, frontOrBack, leftOrRight, around);
                if (pos == 0) {
                    BleLog.e(TAG, "HS_SetLegMoveResult 左腿 " + pos + "  " + frontOrBack + " " + leftOrRight + " " + around);
                } else if (pos == 1) {
                    BleLog.e(TAG, "HS_SetLegMoveResult 右腿" + pos + "  " + frontOrBack + " " + leftOrRight + " " + around);
                }
            }
        });
    }

    /**
     * 初始化
     */
    public static void initBle(Activity gameActivity) {
        HapUtils.gameActivity = gameActivity;
        gameActivity.runOnUiThread(() -> {
            BleHelper.init(gameActivity, new BleInitCallback() {
                @Override
                public void onSuccess() {
                    HapUtils.hapHandler.onInitSuccess();
                    BleLog.d(TAG, "初始化成功 ");
                }

                @Override
                public void onFail(int code) {
                    HapUtils.hapHandler.onInitFail(code);
                    BleLog.e(TAG, "初始化失败 " + code);
                }
            });
        });
    }

    /**
     * 开始扫描
     */
    public static void startScan() {
        Log.e(TAG, "调用BleHelper.startScan");
        BleHelper.startScan(new ScanCallback() {
            @Override
            public void onScanResult(BleRssiDevice bleRssiDevice) {
                Log.e(TAG, "搜索到设备: address:" + bleRssiDevice.getBleAddress() + " name:" + bleRssiDevice.getBleName());
                HapUtils.hapHandler.onScanResult(bleRssiDevice.getBleAddress(), bleRssiDevice.getBleName());
            }
        });
    }

    /**
     * 停止扫描
     */
    public static void stopScan() {
        BleHelper.stopScan();
    }

    /**
     * 连接
     * @param deviceID 设备ID, 应该是扫描到的设备ID。demo中是写死的.
     */
    public static void connect(String deviceID) {
        HapUtils.gameActivity.runOnUiThread(() -> {
            BleHelper.connect(deviceID, new DeviceTransferCallback() {
                @Override
                public void onConnectionChanged(String address, int connectCode) {
                    HapUtils.hapHandler.onConnectionChange(address, connectCode);
                    Log.e(TAG, "onConnectionChanged : " + connectCode + " address :" + address);
                }

                @Override
                public void onConnectFailed(String address, int errorCode) {
                    HapUtils.hapHandler.onConnectFailed(address, errorCode);
                    Log.e(TAG, "onConnectFailed  " + " address :" + address);
                }

                @Override
                public void onConnectCancel(String address) {
                    HapUtils.hapHandler.onConnectCancel(address);
                    Log.e(TAG, "onConnectCancel  ");
                }

                @Override
                public void onNotifySuccess(String address) {
                    HapUtils.hapHandler.onNotifySuccess(address);
                    Log.e(TAG, "onNotifySuccess  ");
                }

                @Override
                public void onWriteSuccess(String address) {
                    HapUtils.hapHandler.onWriteSuccess(address);
                    Log.e(TAG, "onWriteSuccess");
                }

                @Override
                public void onWriteFailed(String address, int code) {
                    HapUtils.hapHandler.onWriteFailed(address, code);
                    Log.e(TAG, "onWriteFailed : " + code);
                }
            });
        });

    }

    /**
     * 断开连接
     */
    public static void disconnect() {
        BleHelper.disConnect();
    }

    public static void sendData() {

    }

    /**
     * @param  motorIndex ：胸部模块力反馈是通过埋在盔甲中的震动马达震动触发
     * 反馈，4.0 版本盔甲预留了 12 个马达，分别是 M1，M2, …. M12 编号，共 12 个。
     * 编号为 0 - 11。
     * @param time 震动时间，为0停止震动
     */
    public static void shakeEngine(int motorIndex, int time) {
        ArmorDeviceService.me().HS_SetArmorShake(motorIndex, time, 50);
        // 1号马达停止震动
        ArmorDeviceService.me().HS_SetArmorShake(motorIndex, 0, 50);
        ArmorDeviceService.me().HS_ArmorShake();
    }

//    public static void test() {
//        ArmorDeviceService.me().HS_Init();
//        BleLog.e(TAG, "HS_Calibration");
//        ArmorDeviceService.me().HS_Calibration(0);
//        ArmorDeviceService.me().HS_Calibration(1);
//        ArmorDeviceService.me().HS_Calibration(2);
//    }

    /**
     * // 设置标定精度
     */
    public static void SetPrecision(int precision) {
        ArmorDeviceService.me().HS_SetPrecision(MODULES_CLOTHING,precision);
    }

    /**
     * 标定盔甲
     */
    public static void SetCalibration() {
        ArmorDeviceService.me().HS_Calibration(MODULES_CLOTHING);
    }
}
