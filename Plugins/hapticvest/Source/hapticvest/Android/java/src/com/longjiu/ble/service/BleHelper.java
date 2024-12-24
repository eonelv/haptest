package com.longjiu.ble.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.longjiu.ble.callback.BleInitCallback;
import com.longjiu.ble.callback.DeviceTransferCallback;
import com.longjiu.ble.callback.ScanCallback;
import com.longjiu.ble.model.BleRssiDevice;
import com.longjiu.ble.service.AppBleService;
import com.longjiu.ble.service.MyBleWrapperCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.BleStates;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.ScanRecord;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;
import cn.com.heaton.blelibrary.ble.utils.UuidUtils;

/**
 * 蓝牙帮助类
 */
public class BleHelper {
    private static final String TAG = "@@ BleHelper";
    private static final String SERVICE_UUID = "";
    private static final String CHARACTERISTIC_NOTIFY_UUID = "";
    private static final String CHARACTERISTIC_WRITE_UUID = "";

    private static BleDevice writeBleDevice;
    private static List<BleRssiDevice> searchDevices = new ArrayList<>();

    private static final BleConnectCallback<BleDevice> bleDeviceBleConnectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            Log.e(TAG, "onConnectionChanged: " + device.getConnectionState() + Thread.currentThread().getName());

            if (device.isConnected()) {
                if (deviceTransferCallback != null) {
                    deviceTransferCallback.onConnectionChanged(device.getBleAddress(), DeviceTransferCallback.CONNECT_CODE_CONNECTED);
                }
                if (AppBleService.me().getLpfn_hs_deviceStateChanged() != null) {
                    AppBleService.me().getLpfn_hs_deviceStateChanged().onResult(device.getBleAddress(), true);
                    if (device.getBleName() != null && device.getBleName().contains("Moonseer_Vest")) {
                        writeBleDevice = device;
                        BleLog.e(TAG, "可写设备：" + device.getBleName());

                        try {
                            String deviceAddress = device.getBleAddress().substring(9);
                            // 连接成功盔甲之后，自动连接左腿和右腿
                            for (BleRssiDevice searchDevices : searchDevices) {
                                // 左腿
                                if (searchDevices.getBleAddress().substring(9).equals(deviceAddress) && searchDevices.getBleName().contains("Moonseer_Leg")) {
                                    if ("04".equals(searchDevices.getBleAddress().substring(6, 8))) {
                                        boolean isConnected = false;
                                        // 判断是否已连接
                                        List<BleDevice> connectedDevices = Ble.getInstance().getConnectedDevices();
                                        for (BleDevice connectedDevice : connectedDevices) {
                                            if (connectedDevice.getBleAddress().equals(searchDevices.getBleAddress())) {
                                                isConnected = true;
                                                break;
                                            }
                                        }
                                        if (!isConnected) {
                                            connect(searchDevices.getBleAddress(), deviceTransferCallback);
                                        }
                                        break;
                                    }
                                }
                            }

                            for (BleRssiDevice searchDevices : searchDevices) {
                                if (searchDevices.getBleAddress().substring(9).equals(deviceAddress) && searchDevices.getBleName().contains("Moonseer_Leg")) {
                                    if ("05".equals(searchDevices.getBleAddress().substring(6, 8))) {
                                        boolean isConnected = false;
                                        // 判断是否已连接
                                        List<BleDevice> connectedDevices = Ble.getInstance().getConnectedDevices();
                                        for (BleDevice connectedDevice : connectedDevices) {
                                            if (connectedDevice.getBleAddress().equals(searchDevices.getBleAddress())) {
                                                isConnected = true;
                                                break;
                                            }
                                        }
                                        if (!isConnected) {
                                            connect(searchDevices.getBleAddress(), deviceTransferCallback);
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            BleLog.e(TAG, "自动连接失败：" + Log.getStackTraceString(e));
                        }

                    }
                }
            } else if (device.isConnecting()) {
                if (deviceTransferCallback != null) {
                    deviceTransferCallback.onConnectionChanged(device.getBleAddress(), DeviceTransferCallback.CONNECT_CODE_CONNECTING);
                }
            } else if (device.isDisconnected()) {

                if (writeBleDevice != null && device.getBleAddress().equals(writeBleDevice.getBleAddress())) {
                    writeBleDevice = null;
                }
                if (deviceTransferCallback != null) {
                    deviceTransferCallback.onConnectionChanged(device.getBleAddress(), DeviceTransferCallback.CONNECT_CODE_UNCONNECT);
                }
                if (AppBleService.me().getLpfn_hs_deviceStateChanged() != null) {
                    AppBleService.me().getLpfn_hs_deviceStateChanged().onResult(device.getBleAddress(), false);
                }
            }
        }

        @Override
        public void onConnectFailed(BleDevice device, int errorCode) {
            super.onConnectFailed(device, errorCode);
            if (deviceTransferCallback != null) {
                deviceTransferCallback.onConnectFailed(device.getBleAddress(), errorCode);
            }
            if (AppBleService.me().getLpfn_hs_deviceStateChanged() != null) {
                AppBleService.me().getLpfn_hs_deviceStateChanged().onResult(device.getBleAddress(), false);
            }
            if (writeBleDevice != null && device.getBleAddress().equals(writeBleDevice.getBleAddress())) {
                writeBleDevice = null;
            }
        }

        @Override
        public void onConnectCancel(BleDevice device) {
            super.onConnectCancel(device);
            Log.e(TAG, "onConnectCancel: " + device.getBleName());
            if (deviceTransferCallback != null) {
                deviceTransferCallback.onConnectCancel(device.getBleAddress());
            }
        }

        @Override
        public void onServicesDiscovered(BleDevice device, BluetoothGatt gatt) {
            super.onServicesDiscovered(device, gatt);
        }

        @Override
        public void onReady(BleDevice device) {
            super.onReady(device);
            //连接成功后，设置通知
            Ble.getInstance().enableNotify(device, true, new BleNotifyCallback<BleDevice>() {
                @Override
                public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
//                    UUID uuid = characteristic.getUuid();
//                    BleLog.e(TAG, "onChanged==uuid:" + uuid.toString());
//                    BleLog.e(TAG, "onChanged==data:" + ByteUtils.toHexString(characteristic.getValue()));
//                    if (deviceTransferCallback != null) {
//                        deviceTransferCallback.onCharacteristicChange(characteristic.getValue());
//                    }

                    AppBleService.me().receiveData(characteristic.getValue());
                }

                @Override
                public void onNotifySuccess(BleDevice device) {
                    super.onNotifySuccess(device);
                    BleLog.e(TAG, "onNotifySuccess: " + device.getBleName());
                    if (deviceTransferCallback != null) {
                        deviceTransferCallback.onNotifySuccess(device.getBleAddress());
                    }
                }
            });
        }
    };

    private static ScanCallback scanCallback;

    private static final BleScanCallback<BleDevice> bleScanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            synchronized (Ble.getInstance().getLocker()) {
                BleRssiDevice bleRssiDevice = new BleRssiDevice(device.getBleAddress(), device.getBleName());
                bleRssiDevice.setScanRecord(ScanRecord.parseFromBytes(scanRecord));
                bleRssiDevice.setRssi(rssi);
                if (scanCallback != null) {
                    if (device.getBleName() != null && device.getBleName().length() > 0) {
                        scanCallback.onScanResult(bleRssiDevice);
                        searchDevices.add(bleRssiDevice);
                    }
                }
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            searchDevices.clear();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private static DeviceTransferCallback deviceTransferCallback;
    private static BleInitCallback bleInitCallback;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context, BleInitCallback callback) {
        Ble.options()
                .setLogBleEnable(true)//设置是否输出打印蓝牙日志
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setLogTAG("AndroidBLE")//设置全局蓝牙操作日志TAG
                .setAutoConnect(false)//设置是否自动连接
                .setIgnoreRepeat(false)//设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
                .setConnectFailedRetryCount(3)//连接异常时（如蓝牙协议栈错误）,重新连接次数
                .setConnectTimeout(10 * 1000)//设置连接超时时长
                .setScanPeriod(12 * 1000)//设置扫描时长
                .setMaxConnectNum(7)//最大连接数量
                .setUuidService(UUID.fromString(UuidUtils.uuid16To128("fff0")))//设置主服务的uuid
                .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128("fff2")))//设置可写特征的uuid
//                .setUuidReadCha(UUID.fromString(UuidUtils.uuid16To128("fd02")))//设置可读特征的uuid （选填）
                .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("fff1")))//设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
//                .setFactory(new BleFactory<BleRssiDevice>() {//实现自定义BleDevice时必须设置
//                    @Override
//                    public BleRssiDevice create(String address, String name) {
//                        return new BleRssiDevice(address, name);//自定义BleDevice的子类
//                    }
//                })
                .setBleWrapperCallback(new MyBleWrapperCallback())
                .create(context, new Ble.InitCallback() {
                    @Override
                    public void success() {
                        BleLog.e("MainApplication", "初始化成功");
                        callback.onSuccess();
                    }

                    @Override
                    public void failed(int failedCode) {
                        BleLog.e("MainApplication", "初始化失败：" + failedCode);
                        callback.onFail(failedCode);
                    }
                });

    }


    /**
     * 开始扫描
     *
     * @param callback
     */
    public static void startScan(ScanCallback callback) {
        scanCallback = callback;
        Ble.getInstance().startScan(bleScanCallback);
    }

    /**
     * 停止扫描
     */
    public static void stopScan() {
        Ble.getInstance().stopScan();
    }

    /**
     * 连接设备
     *
     * @param address
     * @param callback
     */
    public static void connect(String address, DeviceTransferCallback callback) {
        deviceTransferCallback = callback;
        Ble.getInstance().connect(address, bleDeviceBleConnectCallback);
    }

    /**
     * 断开连接
     */
    public static void disConnect() {
        Ble.getInstance().disconnectAll();
    }

    /**
     * 写入数据
     */
    public static void writeData(byte[] data) {
        if (writeBleDevice != null) {
            Ble.getInstance().write(writeBleDevice, data, new BleWriteCallback<BleDevice>() {
                @Override
                public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
                    if (deviceTransferCallback != null) {
                        deviceTransferCallback.onWriteSuccess(device.getBleAddress());
                    }
                }

                @Override
                public void onWriteFailed(BleDevice device, int failedCode) {
                    super.onWriteFailed(device, failedCode);
                    if (deviceTransferCallback != null) {
                        deviceTransferCallback.onWriteFailed(device.getBleAddress(), failedCode);
                    }
                }
            });
        } else {
            if (deviceTransferCallback != null) {
                deviceTransferCallback.onWriteFailed("", BleStates.ConnectException);
            }
        }
    }
}
