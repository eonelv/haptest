package com.longjiu.ble.service;

import static com.longjiu.ble.service.ArmorDeviceServiceInterface.FRAME_BUFFER;

import com.longjiu.ble.callback.add.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;

/**
 * APP 蓝牙交互中心
 */
public class AppBleService {
    private static final String TAG = "AppBleService";
    private volatile static AppBleService instance = null;

    private AppBleService() {
    }

    public static AppBleService me() {
        //第一重判断
        if (instance == null) {
            //锁定代码块
            synchronized (AppBleService.class) {
                //第二重判断
                if (instance == null) {
                    instance = new AppBleService();//创建单例实类
                }
            }
        }
        return instance;
    }

    /**************  声明线程池 *****************/
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
    private final ExecutorService EXECUTOR = new ThreadPoolExecutor(NUMBER_OF_CORES,
            NUMBER_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,
            taskQueue);

    //**********************************************************************************************************************************//


    private LPFN_HS_ArmorMoveResult lpfn_hs_armorMoveResult;
    private LPFN_HS_CalibrationCompleteResult lpfn_hs_calibrationCompleteResult;
    private LPFN_HS_DeviceStateChanged lpfn_hs_deviceStateChanged;
    private LPFN_HS_GetPowerResult lpfn_hs_getPowerResult;
    private LPFN_HS_InfraredEntry lpfn_hs_infraredEntry;
    private LPFN_HS_InfraredSustained lpfn_hs_infraredSustained;
    private LPFN_HS_LegMoveResult lpfn_hs_legMoveResult;
    private LPFN_HS_PushButtonClick lpfn_hs_pushButtonClick;
    private LPFN_HS_PushButtonStick lpfn_hs_pushButtonStick;


    /**
     * 数据处理入口
     *
     * @param data
     */
    public void receiveData(byte[] data) {
        // 粘包处理
        if (data == null) {
            return;
        }

        BleLog.e(TAG,ByteUtils.bytes2HexStr(data));

        if (data.length > FRAME_BUFFER) {
            data = ByteUtils.copyFrom(data, 0, FRAME_BUFFER);
        }
        if (data.length < FRAME_BUFFER) {
            return;
        }
        ArmorDeviceService.me().receiveBleData(data);
    }

    public void submitRunnable(Runnable runnable) {
        EXECUTOR.submit(runnable);
    }

    public void setLpfn_hs_armorMoveResult(LPFN_HS_ArmorMoveResult lpfn_hs_armorMoveResult) {
        this.lpfn_hs_armorMoveResult = lpfn_hs_armorMoveResult;
    }

    public void setLpfn_hs_calibrationCompleteResult(LPFN_HS_CalibrationCompleteResult lpfn_hs_calibrationCompleteResult) {
        this.lpfn_hs_calibrationCompleteResult = lpfn_hs_calibrationCompleteResult;
    }

    public void setLpfn_hs_deviceStateChanged(LPFN_HS_DeviceStateChanged lpfn_hs_deviceStateChanged) {
        this.lpfn_hs_deviceStateChanged = lpfn_hs_deviceStateChanged;
    }

    public void setLpfn_hs_getPowerResult(LPFN_HS_GetPowerResult lpfn_hs_getPowerResult) {
        this.lpfn_hs_getPowerResult = lpfn_hs_getPowerResult;
    }

    public void setLpfn_hs_infraredEntry(LPFN_HS_InfraredEntry lpfn_hs_infraredEntry) {
        this.lpfn_hs_infraredEntry = lpfn_hs_infraredEntry;
    }

    public void setLpfn_hs_infraredSustained(LPFN_HS_InfraredSustained lpfn_hs_infraredSustained) {
        this.lpfn_hs_infraredSustained = lpfn_hs_infraredSustained;
    }

    public void setLpfn_hs_legMoveResult(LPFN_HS_LegMoveResult lpfn_hs_legMoveResult) {
        this.lpfn_hs_legMoveResult = lpfn_hs_legMoveResult;
    }

    public void setLpfn_hs_pushButtonClick(LPFN_HS_PushButtonClick lpfn_hs_pushButtonClick) {
        this.lpfn_hs_pushButtonClick = lpfn_hs_pushButtonClick;
    }

    public void setLpfn_hs_pushButtonStick(LPFN_HS_PushButtonStick lpfn_hs_pushButtonStick) {
        this.lpfn_hs_pushButtonStick = lpfn_hs_pushButtonStick;
    }

    public LPFN_HS_ArmorMoveResult getLpfn_hs_armorMoveResult() {
        return lpfn_hs_armorMoveResult;
    }

    public LPFN_HS_CalibrationCompleteResult getLpfn_hs_calibrationCompleteResult() {
        return lpfn_hs_calibrationCompleteResult;
    }

    public LPFN_HS_DeviceStateChanged getLpfn_hs_deviceStateChanged() {
        return lpfn_hs_deviceStateChanged;
    }

    public LPFN_HS_GetPowerResult getLpfn_hs_getPowerResult() {
        return lpfn_hs_getPowerResult;
    }

    public LPFN_HS_InfraredEntry getLpfn_hs_infraredEntry() {
        return lpfn_hs_infraredEntry;
    }

    public LPFN_HS_InfraredSustained getLpfn_hs_infraredSustained() {
        return lpfn_hs_infraredSustained;
    }

    public LPFN_HS_LegMoveResult getLpfn_hs_legMoveResult() {
        return lpfn_hs_legMoveResult;
    }

    public LPFN_HS_PushButtonClick getLpfn_hs_pushButtonClick() {
        return lpfn_hs_pushButtonClick;
    }

    public LPFN_HS_PushButtonStick getLpfn_hs_pushButtonStick() {
        return lpfn_hs_pushButtonStick;
    }

}
