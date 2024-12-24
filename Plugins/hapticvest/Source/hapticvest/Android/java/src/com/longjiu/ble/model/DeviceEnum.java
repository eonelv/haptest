package com.longjiu.ble.model;

public enum DeviceEnum {
    RightInfrared(1),
    LeftInfrared(2),
    RightButton(4),
    LeftButton(8);

    public int value;
    DeviceEnum(int i) {
        this.value = i;
    }

}
