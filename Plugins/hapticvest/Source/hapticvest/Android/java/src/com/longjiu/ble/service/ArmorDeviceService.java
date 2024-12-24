package com.longjiu.ble.service;

import static com.longjiu.ble.utils.MathUtils.Degree2Radian;

import android.util.Log;

import com.longjiu.ble.callback.add.LPFN_HS_ArmorMoveResult;
import com.longjiu.ble.callback.add.LPFN_HS_CalibrationCompleteResult;
import com.longjiu.ble.callback.add.LPFN_HS_DeviceStateChanged;
import com.longjiu.ble.callback.add.LPFN_HS_GetPowerResult;
import com.longjiu.ble.callback.add.LPFN_HS_InfraredEntry;
import com.longjiu.ble.callback.add.LPFN_HS_InfraredSustained;
import com.longjiu.ble.callback.add.LPFN_HS_LegMoveResult;
import com.longjiu.ble.callback.add.LPFN_HS_PushButtonClick;
import com.longjiu.ble.callback.add.LPFN_HS_PushButtonStick;
import com.longjiu.ble.model.DeviceEnum;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;

public class ArmorDeviceService implements ArmorDeviceServiceInterface {

    private volatile static ArmorDeviceService instance = null;
    private static final String TAG = "ArmorDeviceServiceImpl";

    private ArmorDeviceService() {
    }

    public static ArmorDeviceService me() {
        //第一重判断
        if (instance == null) {
            //锁定代码块
            synchronized (ArmorDeviceService.class) {
                //第二重判断
                if (instance == null) {
                    instance = new ArmorDeviceService();//创建单例实类
                }
            }
        }
        return instance;
    }

    class ShakeMotor {
        // 索引:0-11
        int motorIndex;
        // 震动时间 0-0x80
        int shakeTime;
        // 震动幅度 0-0x64
        int shakePower;
    }


    class EulerAngle {
        int Roll;
        int Pitch;
        int Yaw;
        int RollAcc;
        int PitchAcc;
        int YawAcc;
    }

    class EulerAngleAcc {
        int frameIndex;
        int RollAcc;
        int PitchAcc;
        int YawAcc;
    }

    class CalibrationResult {
        /**
         * 欧拉角基准值
         * (去掉零点偏移量)
         */
        EulerAngle m_EulerAngleBase = new EulerAngle();

        /**
         * 标准偏差
         * 当前标定值与1.0的绝对值误差
         */
        EulerAngle m_EulerStandDeviation = new EulerAngle();
        ;

        /**
         * 容器
         */
        List<EulerAngle> m_ArmorEulerAngleArr = new ArrayList<>();

        /**
         * 标定精度
         */
        int m_Precision = 20;

        /**
         * 滚转限制锁定和上一帧滚转值
         */
        double m_fLimitRoll = 5;
        double m_fRollLast = 0;

        boolean m_bLock = false;

    }

    class DeviceStateItem {
        int CheckCounts = 0;
        boolean State;
        int DeviceID;
    }

    // size 3
    CalibrationResult[] calibrationResult = new CalibrationResult[3];

    {
        calibrationResult[0] = new CalibrationResult();
        calibrationResult[1] = new CalibrationResult();
        calibrationResult[2] = new CalibrationResult();
    }

    boolean[] CalibrationState = {false, false, false};
    boolean[] ButtonState = {false, false};
    boolean[] InfraredState = {false, false};
    DeviceStateItem[] DeviceState = new DeviceStateItem[DEVICE_COUNTS];

    ShakeMotor[] shakeMotor = new ShakeMotor[16];

    {
        for (int i = 0; i < shakeMotor.length; i++) {
            shakeMotor[i] = new ShakeMotor();
            shakeMotor[i].motorIndex = i;
        }
    }

    @Override
    public void HS_Init() {

    }


    @Override
    public void HS_DeviceStateChanged(LPFN_HS_DeviceStateChanged _fn) {
        AppBleService.me().setLpfn_hs_deviceStateChanged(_fn);
    }

    // 标定重试时间
    int MAX_TIME = 20 * 1000;
    private static final int CALIBRATION_INTERVAL = 10;

    @Override
    public void HS_Calibration(int modules) {
        if (modules < 0 || modules >= 3) {
            return;
        }
        CalibrationState[modules] = true;

        AppBleService.me().submitRunnable(new Runnable() {
            @Override
            public void run() {
                BleLog.e(TAG, "OnArmorCalibration Thread start! \r\n");
                BleLog.e(TAG, modules + " module Thread start! \r\n");
                int totalTime = 0;
                while (calibrationResult[modules].m_ArmorEulerAngleArr.size() < calibrationResult[modules].m_Precision) {
                    try {
                        totalTime += CALIBRATION_INTERVAL;
                        if (totalTime > MAX_TIME) {
                            BleLog.e(TAG, "标定失败 标定时间超时 20s");
                            return;
                        }
                        Thread.sleep(CALIBRATION_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                int pitchBaseValue = 0;
                int yawBaseValue = 0;
                int rollBaseValue = 0;

                if (calibrationResult[modules].m_ArmorEulerAngleArr.size() < 0) {
                    CalibrationState[modules] = false;
                    return;
                }

                // 需要用m_nSamplingPrecision 个欧拉角进行标定坐标系
                // 目前陀螺仪标定（校准）采用去掉零点偏移量，采集一定数据，求平均值，
                // 这个平均值就是零点偏移，后续所读的陀螺仪欧拉角数据减去零偏即可
                // 求pitchArr的算术平均值
                for (EulerAngle i : calibrationResult[modules].m_ArmorEulerAngleArr) {
                    pitchBaseValue = pitchBaseValue + i.Pitch;
                    yawBaseValue = yawBaseValue + i.Yaw;
                    rollBaseValue = rollBaseValue + i.Roll;
                }

                calibrationResult[modules].m_EulerAngleBase.Pitch = pitchBaseValue / calibrationResult[modules].m_ArmorEulerAngleArr.size();
                calibrationResult[modules].m_EulerAngleBase.Yaw = yawBaseValue / calibrationResult[modules].m_ArmorEulerAngleArr.size();
                calibrationResult[modules].m_EulerAngleBase.Roll = rollBaseValue / calibrationResult[modules].m_ArmorEulerAngleArr.size();
                // 计算零飘修正
                calibrationResult[modules].m_EulerStandDeviation.Pitch = -calibrationResult[modules].m_EulerAngleBase.Pitch;
                calibrationResult[modules].m_EulerStandDeviation.Yaw = -calibrationResult[modules].m_EulerAngleBase.Yaw;
                calibrationResult[modules].m_EulerStandDeviation.Roll = -calibrationResult[modules].m_EulerAngleBase.Roll;

                CalibrationState[modules] = false;

                calibrationResult[modules].m_ArmorEulerAngleArr.clear();

                if (AppBleService.me().getLpfn_hs_calibrationCompleteResult() != null) {
                    AppBleService.me().getLpfn_hs_calibrationCompleteResult().onResult(modules);
                }

                BleLog.e(TAG, String.format("OnArmorCalibration Thread Finished!, CV=%d,%d,%d ", calibrationResult[modules].m_EulerStandDeviation.Pitch,
                        calibrationResult[modules].m_EulerStandDeviation.Yaw, calibrationResult[modules].m_EulerStandDeviation.Roll));

            }
        });
    }

    @Override
    public void HS_SetPrecision(int modules, int precision) {
        if (modules < 0 || modules >= 3) {
            return;
        }

        if (precision <= 0 || precision > 15000) {
            precision = 30;
        }

        calibrationResult[modules].m_Precision = precision;
    }

    @Override
    public void HS_CalibrationCompleteResult(LPFN_HS_CalibrationCompleteResult _fn) {
        AppBleService.me().setLpfn_hs_calibrationCompleteResult(_fn);
    }

    @Override
    public void HS_PowerResult(LPFN_HS_GetPowerResult _fn) {
        AppBleService.me().setLpfn_hs_getPowerResult(_fn);
    }

    @Override
    public void HS_SetArmorMoveResult(LPFN_HS_ArmorMoveResult _fn) {
        AppBleService.me().setLpfn_hs_armorMoveResult(_fn);
    }

    @Override
    public void HS_PushButtonClick(LPFN_HS_PushButtonClick _fn) {
        AppBleService.me().setLpfn_hs_pushButtonClick(_fn);
    }

    @Override
    public void HS_PushButtonStick(LPFN_HS_PushButtonStick _fn) {
        AppBleService.me().setLpfn_hs_pushButtonStick(_fn);
    }

    @Override
    public void HS_InfraredEntry(LPFN_HS_InfraredEntry _fn) {
        AppBleService.me().setLpfn_hs_infraredEntry(_fn);
    }

    @Override
    public void HS_InfraredSustained(LPFN_HS_InfraredSustained _fn) {
        AppBleService.me().setLpfn_hs_infraredSustained(_fn);
    }

    @Override
    public void HS_SetLegMoveResult(LPFN_HS_LegMoveResult _fn) {
        AppBleService.me().setLpfn_hs_legMoveResult(_fn);
    }

    @Override
    public void HS_UnInit() {

    }

    @Override
    public void HS_SetArmorShake(int motorIndex, int shakeTime, int shakePower) {
        if (motorIndex >= 0 && motorIndex < SHAKE_MOTOR_NUM) {
            shakeMotor[motorIndex].shakeTime = shakeTime;
            shakeMotor[motorIndex].shakePower = shakePower;
        }
    }

    @Override
    public void HS_ArmorShake() {
        byte[] shakeData = new byte[40];
        byte[] head = {(byte) 0xFD, (byte) 0xDA, (byte) 0x27, (byte) 0x13};
        System.arraycopy(head, 0, shakeData, 0, head.length);
        shakeData[36] = (byte)0x00;
        shakeData[37] = (byte)0x01;
        shakeData[38] = (byte)0x00;
        shakeData[39] = (byte)0xFE;
        for (int i = 0; i < SHAKE_MOTOR_NUM; ++i) {
            // 构建震动
            shakeData[5 + i * 2] = HexTable256[shakeMotor[i].shakeTime];
            shakeData[5 + i * 2 + 1] = HexTable256[shakeMotor[i].shakePower];
        }

        for (int i = 0; i < SHAKE_MOTOR_NUM; ++i) {
            // 清除震动
            shakeMotor[i].shakeTime = 0;
            shakeMotor[i].shakePower = 0;
        }
        BleHelper.writeData(shakeData);
    }

    public void receiveBleData(byte[] frame) {
        if (frame == null) {
            return;
        }

        if (frame[0] == (byte) 0xFD && frame[1] == (byte) 0XDA && frame[3] == (byte) 0x11) {
            resultIMU(frame);
        } else if (frame[0] == (byte) 0xFD && frame[1] == (byte) 0XDA && frame[3] == (byte) 0x12) {
            resultCtl(frame);
        }

    }

    private void resultLegIMU(byte[] frame) {
        BleLog.e(TAG, "resultLegIMU " + ByteUtils.bytes2HexStr(frame));

        EulerAngle ag = new EulerAngle();
//        ag.Yaw = (int) ((frame[5] | (frame[6] << 8)) / 100.0D); // pitch
//        ag.Pitch = (int) ((frame[7] | (frame[8] << 8)) / 100.0D); // roll
//        ag.Roll = (int) ((frame[9] | (frame[10] << 8)) / 100.0D); // yaw

        ag.Yaw = ByteUtils.bytes2Short2(new byte[]{frame[5], frame[6]}) / 100;
        ag.Pitch = ByteUtils.bytes2Short2(new byte[]{frame[7], frame[8]}) / 100;
        ag.Roll = ByteUtils.bytes2Short2(new byte[]{frame[9], frame[10]}) / 100;

        ag.YawAcc = ByteUtils.bytes2Short2(new byte[]{frame[11], frame[12]});
        ag.PitchAcc = ByteUtils.bytes2Short2(new byte[]{frame[13], frame[14]});
        ag.RollAcc = ByteUtils.bytes2Short2(new byte[]{frame[15], frame[16]});

        int device = 0;
        if (frame[3] == FRAME_LLEG_IMU) {
            device = 0;
        } else if (frame[3] == FRAME_RLEG_IMU) {
            device = 1;
        }
        int pos = 1 + device;
        // 标定通知
        if (CalibrationState[pos]) {
            // 标定armor
            if (calibrationResult[pos].m_ArmorEulerAngleArr.size() >= calibrationResult[pos].m_Precision) {
                return;
            } else {
                calibrationResult[pos].m_ArmorEulerAngleArr.add(ag);
                return;
            }
        }

        // 腿部电量通知
        if (AppBleService.me().getLpfn_hs_getPowerResult() != null) {
            AppBleService.me().getLpfn_hs_getPowerResult().onResult(pos, ByteUtils.byte2int(frame[30]));
        }

        ag.Pitch = ag.Pitch + calibrationResult[pos].m_EulerStandDeviation.Pitch;
        ag.Yaw = ag.Yaw + calibrationResult[pos].m_EulerStandDeviation.Yaw;
        ag.Roll = ag.Roll + calibrationResult[pos].m_EulerStandDeviation.Roll;

        ag.PitchAcc = ag.PitchAcc + calibrationResult[pos].m_EulerStandDeviation.PitchAcc;
        ag.YawAcc = ag.YawAcc + calibrationResult[pos].m_EulerStandDeviation.YawAcc;
        ag.RollAcc = ag.RollAcc + calibrationResult[pos].m_EulerStandDeviation.RollAcc;


        double yaw = 0.0, pitch = 0.0, roll = 0.0;
        double yawacc = 0.0, pitchacc = 0.0, rollacc = 0.0;
        // 俯仰角
        if (ag.Pitch >= -ANTI_SHAKE && ag.Pitch <= ANTI_SHAKE) {
            pitch = 0;
        } else {
            pitch = ag.Pitch;
            // 峰值修正
            if (ag.Pitch < -90.0) {
                pitch = -90.0;
            }

            if (ag.Pitch > 90.0) {
                pitch = 90;
            }

        }

        if (ag.Yaw >= -ANTI_SHAKE && ag.Yaw <= ANTI_SHAKE) {
            yaw = 0;
        } else {
            yaw = ag.Yaw;

            if (ag.Yaw < -90.0) {
                yaw = -90.0;
            }

            // 峰值修正
            if (ag.Yaw > 90.0) {
                yaw = 90;
            }


        }


        if (ag.Roll >= -ANTI_SHAKE && ag.Roll <= ANTI_SHAKE) {
            roll = 0;
        } else {
            roll = ag.Roll;

            if (ag.Roll < -90.0) {
                roll = -90.0;
            }

            // 峰值修正
            if (ag.Roll > 90.0) {
                roll = 90;
            }


        }

        pitchacc = ag.PitchAcc;
        yawacc = ag.YawAcc;
        rollacc = ag.RollAcc;

        if (ag.PitchAcc >= -ANTI_ACCELERATION && ag.PitchAcc <= ANTI_ACCELERATION) {
            pitchacc = 0;
        }

        if (ag.RollAcc >= -ANTI_ACCELERATION && ag.RollAcc <= ANTI_ACCELERATION) {
            rollacc = 0;
        }

        if (ag.YawAcc >= -ANTI_ACCELERATION && ag.YawAcc <= ANTI_ACCELERATION) {
            yawacc = 0;
        }

        Log.e("@@_LEG_pitch " + device, "pitch : " + pitch + " source data : " + ByteUtils.bytes2HexStr(new byte[]{frame[8], frame[7]}) + " value :" + (ByteUtils.bytes2Short2(new byte[]{frame[7], frame[8]}) / 100) + " radian : " + Degree2Radian(pitch) + " sin :" + Math.sin(Degree2Radian(pitch)));
        Log.e("@@_LEG_yaw " + device, "yaw : " + yaw + " source data : " + ByteUtils.bytes2HexStr(new byte[]{frame[6], frame[5]}) + " value :" + (ByteUtils.bytes2Short2(new byte[]{frame[5], frame[6]}) / 100) + " radian : " + Degree2Radian(yaw) + " sin :" + Math.sin(Degree2Radian(yaw)));


        if (AppBleService.me().getLpfn_hs_legMoveResult() != null) {
            AppBleService.me().getLpfn_hs_legMoveResult().onResult(device, Math.sin(Degree2Radian(pitch)), Math.sin(Degree2Radian(yaw)), Math.sin(Degree2Radian(roll)));
        }
    }

    private void resultCtl(byte[] frame) {
        BleLog.e(TAG, "resultCtl " + ByteUtils.bytes2HexStr(frame));
        BitSet ctrl = BitSet.valueOf(new byte[]{frame[4]});
        boolean oldState = InfraredState[0];
        InfraredState[0] = ctrl.get(0);
        if (ctrl.get(0))    // 0x01
        {
            // 上升沿触发
            if (oldState != ctrl.get(0) && AppBleService.me().getLpfn_hs_infraredEntry() != null) {
                AppBleService.me().getLpfn_hs_infraredEntry().onResult(DeviceEnum.RightInfrared.value, true);
            }

            // 水平触发
            if (AppBleService.me().getLpfn_hs_infraredSustained() != null) {
                AppBleService.me().getLpfn_hs_infraredSustained().onResult(DeviceEnum.RightInfrared.value);
            }
        } else if (oldState && !ctrl.get(0) && AppBleService.me().getLpfn_hs_infraredEntry() != null) {
            AppBleService.me().getLpfn_hs_infraredEntry().onResult(DeviceEnum.RightInfrared.value, false);
        }

        oldState = InfraredState[1];
        InfraredState[1] = ctrl.get(1);
        if (ctrl.get(1))    // 0x02
        {
            // 上升沿触发 et
            if (oldState != ctrl.get(1) && AppBleService.me().getLpfn_hs_infraredEntry() != null) {
                AppBleService.me().getLpfn_hs_infraredEntry().onResult(DeviceEnum.LeftInfrared.value, true);
            }

            // 水平触发  lt
            if (AppBleService.me().getLpfn_hs_infraredSustained() != null) {
                AppBleService.me().getLpfn_hs_infraredSustained().onResult(DeviceEnum.RightInfrared.value);
            }
        } else if (oldState && !ctrl.get(1) && AppBleService.me().getLpfn_hs_infraredEntry() != null) {
            AppBleService.me().getLpfn_hs_infraredEntry().onResult(DeviceEnum.LeftInfrared.value, false);
        }

        oldState = ButtonState[0];
        ButtonState[0] = ctrl.get(2);
        if (ctrl.get(2))    // 0x04  RightButton
        {
            // 上升沿触发
            if (oldState != ctrl.get(2) && AppBleService.me().getLpfn_hs_pushButtonClick() != null) {
                AppBleService.me().getLpfn_hs_pushButtonClick().onResult(DeviceEnum.RightButton.value, true);
            }

            // 水平触发
            if (AppBleService.me().getLpfn_hs_pushButtonStick() != null) {
                AppBleService.me().getLpfn_hs_pushButtonStick().onResult(DeviceEnum.RightButton.value);
            }
        } else if (oldState && !ctrl.get(2) && AppBleService.me().getLpfn_hs_pushButtonClick() != null) {
            AppBleService.me().getLpfn_hs_pushButtonClick().onResult(DeviceEnum.RightButton.value, false);
        }


        oldState = ButtonState[1];
        ButtonState[1] = ctrl.get(3);
        if (ctrl.get(3))    // 0x08  LeftButton
        {
            // 上升沿触发
            if (oldState != ctrl.get(3) && AppBleService.me().getLpfn_hs_pushButtonClick() != null) {
                AppBleService.me().getLpfn_hs_pushButtonClick().onResult(DeviceEnum.LeftButton.value, true);
            }

            // 水平触发
            if (AppBleService.me().getLpfn_hs_pushButtonStick() != null) {
                AppBleService.me().getLpfn_hs_pushButtonStick().onResult(DeviceEnum.LeftButton.value);
            }
        } else if (oldState && !ctrl.get(3) && AppBleService.me().getLpfn_hs_pushButtonClick() != null) {
            AppBleService.me().getLpfn_hs_pushButtonClick().onResult(DeviceEnum.LeftButton.value, false);
        }
    }

    private void resultIMU(byte[] frame) {
        BleLog.e(TAG, "resultIMU " + ByteUtils.bytes2HexStr(frame));

        EulerAngle ag = new EulerAngle();
        EulerAngleAcc acc = new EulerAngleAcc();

//        ag.Yaw = (int) ((frame[5] | (frame[6] << 8)) ); // pitch
//        ag.Pitch = (int) ((frame[7] | (frame[8] << 8)) / 100.0); // roll
//        ag.Roll = (int) ((frame[9] | (frame[10] << 8)) / 100.0); // yaw
        ag.Yaw = ByteUtils.bytes2Short2(new byte[]{frame[4], frame[5]}) / 100;
        ag.Pitch = ByteUtils.bytes2Short2(new byte[]{frame[6], frame[7]}) / 100;
        ag.Roll = ByteUtils.bytes2Short2(new byte[]{frame[8], frame[9]}) / 100;

        // 标定通知
        if (CalibrationState[0]) {
            // 标定armor
            if (calibrationResult[0].m_ArmorEulerAngleArr.size() >= calibrationResult[0].m_Precision) {
                return;
            } else {

                ag.YawAcc = ByteUtils.bytes2Short2(new byte[]{frame[11], frame[12]});
                ag.PitchAcc = ByteUtils.bytes2Short2(new byte[]{frame[13], frame[14]});
                ag.RollAcc = ByteUtils.bytes2Short2(new byte[]{frame[15], frame[16]});

                calibrationResult[0].m_ArmorEulerAngleArr.add(ag);
                return;
            }
        }

        if (AppBleService.me().getLpfn_hs_getPowerResult() != null) {
            AppBleService.me().getLpfn_hs_getPowerResult().onResult(0, ByteUtils.byte2int(frame[30]));
        }

        ag.Pitch = ag.Pitch + calibrationResult[0].m_EulerStandDeviation.Pitch;
        ag.Yaw = ag.Yaw + calibrationResult[0].m_EulerStandDeviation.Yaw;
        ag.Roll = ag.Roll + calibrationResult[0].m_EulerStandDeviation.Roll;


        double yaw = 0.0, pitch = 0.0, roll = 0.0;
        double yawacc = 0.0, pitchacc = 0.0, rollacc = 0.0;

        // 俯仰角
        if (ag.Pitch >= -ANTI_SHAKE && ag.Pitch <= ANTI_SHAKE) {
            pitch = 0;
        } else {
            pitch = ag.Pitch;

            if (ag.Pitch < -90.0) {
                pitch = -90.0;
            }

            // 峰值修正
            if (ag.Pitch > 90.0) {
                pitch = 90;
            }
        }

        if (ag.Yaw >= -ANTI_SHAKE && ag.Yaw <= ANTI_SHAKE) {
            yaw = 0;
        } else {
            yaw = ag.Yaw;

            if (ag.Yaw < -90.0) {
                yaw = -90.0;
            }

            // 峰值修正
            if (ag.Yaw > 90.0) {
                yaw = 90;
            }

        }

        // 俯仰角
        if (ag.Roll >= -ANTI_SHAKE && ag.Roll <= ANTI_SHAKE) {
            roll = 0;
        } else {
            roll = ag.Roll;

            if (ag.Roll < -90.0) {
                roll = -90.0;
            }

            // 峰值修正
            if (ag.Roll > 90.0) {
                roll = 90;
            }
        }

        pitchacc = 0;
        yawacc = 0;
        rollacc = 0;

        // 加速度计算
        // 各个分量和重力进行对比
//        if (ag.Pitch - CalibrationGravity[0] <= -ANTI_ACCELERATION || ag.Pitch - CalibrationGravity[0] >= ANTI_ACCELERATION)
//        {
//            printf("%d, %d \r\n", ag.Pitch, ag.Pitch - CalibrationGravity[0]);
//        }

//        System.out.println("pitch : " + Math.sin(Degree2Radian(pitch)));
//        System.out.println("yaw   : " + Math.sin(Degree2Radian(yaw)));
//        System.out.println("roll  : " + Math.sin(Degree2Radian(roll)));

        BleLog.e("@@-pitch", "pitch : " + pitch + " source data : " + ByteUtils.bytes2HexStr(new byte[]{frame[8], frame[7]}) + " value :" + (ByteUtils.bytes2Short2(new byte[]{frame[7], frame[8]}) / 100) + " radian : " + Degree2Radian(pitch) + " sin :" + Math.sin(Degree2Radian(pitch)));
        BleLog.e("@@-yaw", "yaw : " + yaw + " source data : " + ByteUtils.bytes2HexStr(new byte[]{frame[6], frame[5]}) + " value :" + (ByteUtils.bytes2Short2(new byte[]{frame[5], frame[6]}) / 100) + " radian : " + Degree2Radian(yaw) + " sin :" + Math.sin(Degree2Radian(yaw)));

        if (AppBleService.me().getLpfn_hs_armorMoveResult() != null) {
            AppBleService.me().getLpfn_hs_armorMoveResult().onResult(Math.sin(Degree2Radian(pitch)), Math.sin(Degree2Radian(yaw)), Math.sin(Degree2Radian(roll)) * -1);
        }
    }

}
