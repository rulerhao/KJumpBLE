package com.example.kjumpble.ble.cmd.kp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.example.kjumpble.ble.callback.GattCallback;
import com.example.kjumpble.ble.format.HourFormat;
import com.example.kjumpble.ble.format.KP.KPDeviceSetting;
import com.example.kjumpble.ble.format.KP.SenseMode;
import com.example.kjumpble.ble.format.ReminderFormat;
import com.example.kjumpble.ble.format.TemperatureUnitEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class KPCmd {
    public byte[] writeClockTimeCmd = new byte[] {0x03,
            0x08, 0x00, 0x0c, 0x00, 0x10, 0x00, 0x14, 0x00};
    public byte[] changedBytes = new byte[] {0x21, 0x55, (byte) 0xaa};

    public static byte[] getWriteReminderCommand (ArrayList<ReminderFormat> reminders) {
        return KPCmdCalculator.getReminderBytes(reminders);
    }

    public static byte[] getWriteTimeCommand (KPDeviceSetting deviceSetting) {
        return KPCmdCalculator.getTimeBytes(deviceSetting);
    }

    public static byte[] getReadMemoryCommand(int index) {
        return KPCmdCalculator.getReadMemoryBytes(index);
    }

    public static byte[] getReadNumberOfMemoryCommand() {
        return KPCmdCalculator.getReadNumberOfMemoryBytes();
    }

    public static byte[] getStartSenseCommand() {
        return KPCmdCalculator.getStartSenseBytes();
    }

    public static byte[] getStopSenseCommand() {
        return KPCmdCalculator.getStopSenseBytes();
    }

    public static byte[] getChangeModeCommand(SenseMode mode) {
        return KPCmdCalculator.getChangeModeBytes(mode);
    }

    public static byte[] getClearMemoryCommand() {
        return KPCmdCalculator.getClearMemoryBytes();
    }
}
