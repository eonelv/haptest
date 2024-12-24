package com.longjiu.ble.service;

import com.longjiu.ble.callback.add.*;

public interface ArmorDeviceServiceInterface {
    int WRITE_BUFFER = 128;
    int READ_BUFFER = 32;
    int ANTI_SHAKE = 5;
    double PI = 3.1415926;

    int PARITY = 0;
    int BUAD_RATE = 115200;
    int STOP_BITS = 1;
    int DATA_BITS = 8;

    int DATA_BUFFER = 2 * READ_BUFFER;
    int FRAME_BUFFER = 32;

    int DEVICE_COUNTS = 32;
    // 盔甲IMU
    byte FRAME_ARMOR = (byte) 0x10;
    // 左腿IMU
    byte FRAME_LLEG_IMU = (byte) 0x04;
    // 右腿IMU
    byte FRAME_RLEG_IMU = (byte) 0x05;

    // 震动马达个数
    int SHAKE_MOTOR_NUM = 16;
    // 数据未
    byte FRAME_IMU_DATA = (byte) 0x01;
    // 控制位
    byte FRAME_CTRL = (byte) 0x02;
    // 语音识别
    byte FRAME_VOICE = (byte) 0x06;

    int ANTI_ACCELERATION = 30;

    byte[] HexTable256 =
            {
                    (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F,
                    (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1A, (byte) 0x1B, (byte) 0x1C, (byte) 0x1D, (byte) 0x1E, (byte) 0x1F,
                    (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x23, (byte) 0x24, (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2A, (byte) 0x2B, (byte) 0x2C, (byte) 0x2D, (byte) 0x2E, (byte) 0x2F,
                    (byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x3A, (byte) 0x3B, (byte) 0x3C, (byte) 0x3D, (byte) 0x3E, (byte) 0x3F,
                    (byte) 0x40, (byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4A, (byte) 0x4B, (byte) 0x4C, (byte) 0x4D, (byte) 0x4E, (byte) 0x4F,
                    (byte) 0x50, (byte) 0x51, (byte) 0x52, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59, (byte) 0x5A, (byte) 0x5B, (byte) 0x5C, (byte) 0x5D, (byte) 0x5E, (byte) 0x5F,
                    (byte) 0x60, (byte) 0x61, (byte) 0x62, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x6A, (byte) 0x6B, (byte) 0x6C, (byte) 0x6D, (byte) 0x6E, (byte) 0x6F,
                    (byte) 0x70, (byte) 0x71, (byte) 0x72, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x7A, (byte) 0x7B, (byte) 0x7C, (byte) 0x7D, (byte) 0x7E, (byte) 0x7F,
                    (byte) 0x80, (byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8A, (byte) 0x8B, (byte) 0x8C, (byte) 0x8D, (byte) 0x8E, (byte) 0x8F,
                    (byte) 0x90, (byte) 0x91, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9A, (byte) 0x9B, (byte) 0x9C, (byte) 0x9D, (byte) 0x9E, (byte) 0x9F,
                    (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3, (byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7, (byte) 0xA8, (byte) 0xA9, (byte) 0xAA, (byte) 0xAB, (byte) 0xAC, (byte) 0xAD, (byte) 0xAE, (byte) 0xAF,
                    (byte) 0xB0, (byte) 0xB1, (byte) 0xB2, (byte) 0xB3, (byte) 0xB4, (byte) 0xB5, (byte) 0xB6, (byte) 0xB7, (byte) 0xB8, (byte) 0xB9, (byte) 0xBA, (byte) 0xBB, (byte) 0xBC, (byte) 0xBD, (byte) 0xBE, (byte) 0xBF,
                    (byte) 0xC0, (byte) 0xC1, (byte) 0xC2, (byte) 0xC3, (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7, (byte) 0xC8, (byte) 0xC9, (byte) 0xCA, (byte) 0xCB, (byte) 0xCC, (byte) 0xCD, (byte) 0xCE, (byte) 0xCF,
                    (byte) 0xD0, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xDB, (byte) 0xDC, (byte) 0xDD, (byte) 0xDE, (byte) 0xDF,
                    (byte) 0xE0, (byte) 0xE1, (byte) 0xE2, (byte) 0xE3, (byte) 0xE4, (byte) 0xE5, (byte) 0xE6, (byte) 0xE7, (byte) 0xE8, (byte) 0xE9, (byte) 0xEA, (byte) 0xEB, (byte) 0xEC, (byte) 0xED, (byte) 0xEE, (byte) 0xEF,
                    (byte) 0xF0, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3, (byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8, (byte) 0xF9, (byte) 0xFA, (byte) 0xFB, (byte) 0xFC, (byte) 0xFD, (byte) 0xFE, (byte) 0xFF
            };


    // 初始化
    void HS_Init();



    // 标定
    // 参数：modules  0: 盔甲,1 左脚，2 右脚
    int MODULES_CLOTHING = 0;
    int MODULES_LEFT_FOOT = 1;
    int MODULES_RIGHT_FOOT = 2;

    // 标定
    void HS_Calibration(int modules);

    // 设置标定精度
    void HS_SetPrecision(int modules, int precision);

    // 设置标定完成回调函数
    void HS_CalibrationCompleteResult(LPFN_HS_CalibrationCompleteResult _fn);

    // 设备状态发生变化
    // 状态变化为在线（DeviceState::Connected）和离线 (DeviceState::Disconnected)
    void HS_DeviceStateChanged(LPFN_HS_DeviceStateChanged _fn);

    // 获取Power
    void HS_PowerResult(LPFN_HS_GetPowerResult _fn);

    // 设置盔甲移动
    void HS_SetArmorMoveResult(LPFN_HS_ArmorMoveResult _fn);

    // 按钮按下时触发
    void HS_PushButtonClick(LPFN_HS_PushButtonClick _fn);

    // 按钮持续触摸时触发
    void HS_PushButtonStick(LPFN_HS_PushButtonStick _fn);

    // 红外进入时触发
    void HS_InfraredEntry(LPFN_HS_InfraredEntry _fn);

    // 红外持续时触发
    void HS_InfraredSustained(LPFN_HS_InfraredSustained _fn);

    // 设置脚步移动
    void HS_SetLegMoveResult(LPFN_HS_LegMoveResult _fn);

    // 释放资源
    void HS_UnInit();

    // 震动设置
    // motorIndex 取值: 0 - 11
    // shakeTime 时间: 每一个值代表100ms，超过0x80自动截取为0x80, 范围∈[1,128]
    // shakePower 振动幅度 取值范围∈ [1,100]
    void HS_SetArmorShake(int motorIndex, int shakeTime, int shakePower);

    // 震动
    void HS_ArmorShake();


}