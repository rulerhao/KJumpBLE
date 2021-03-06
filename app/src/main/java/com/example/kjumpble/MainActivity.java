package com.example.kjumpble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kjumpble.ble.BLEService;
import com.example.kjumpble.ble.cmd.BLE_CLIENT_CMD;
import com.example.kjumpble.ble.CheckBLEScan;
import com.example.kjumpble.ble.CheckLocationStatus;
import com.example.kjumpble.ble.callback.OnProgressListener;
import com.example.kjumpble.ble.format.HourFormat;
import com.example.kjumpble.ble.format.KP.KPSettings;
import com.example.kjumpble.ble.format.KP.SenseMode;
import com.example.kjumpble.ble.format.LeftRightHand;
import com.example.kjumpble.ble.format.ReminderFormat;
import com.example.kjumpble.ble.format.TemperatureUnit;
import com.example.kjumpble.ble.timeFormat.DeviceTimeFormat;
import com.example.kjumpble.ble.timeFormat.ReminderTimeFormat;
import com.example.kjumpble.util.DeviceRegex;
import com.example.kjumpble.util.MyPermissions;
import com.example.kjumpble.permission.PermissionRequestCode;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private BLEService bleService;

    private Button scanDeviceButton;
    private Button getUserIndexButton;
    private Button getNumberOfDataButton;
    private Button readLatestMemoryButton;
    private Button readAllMemoryButton;
    private Button clearAllDataButton;

    private Button writeClockTimeAndFlagButton;

    private Button writeReminderClockTimeAndFlagButton;

    private Button setDeviceButton;

    private Button writeTemperatureUnitButton;


    private TextView bleStatusTextView;

    private Activity activity;

    private Intent bleServiceIntent;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        activity = this;

        scanDeviceButton = findViewById(R.id.ScanDeviceButton);
        scanDeviceButton.setOnClickListener(scanDeviceButtonOnClickListener);

        getUserIndexButton = findViewById(R.id.GetUserIndexButton);
        getUserIndexButton.setOnClickListener(getUserIndexOnClickListener);

        getNumberOfDataButton = findViewById(R.id.GetNumberOfDataButton);
        getNumberOfDataButton.setOnClickListener(getNumberOfDataButtonOnClickListener);

        readLatestMemoryButton = findViewById(R.id.ReadLatestMemoryButton);
        readLatestMemoryButton.setOnClickListener(readLatestMemoryButtonOnClickListener);

        readAllMemoryButton = findViewById(R.id.ReadAllMemoryButton);
        readAllMemoryButton.setOnClickListener(readAllMemoryButtonOnClickListener);

        clearAllDataButton = findViewById(R.id.ClearAllDataButton);
        clearAllDataButton.setOnClickListener(clearAllDataButtonOnClickListener);

        // Clock
        writeClockTimeAndFlagButton = findViewById(R.id.WriteClockTimeAndFlagButton);
        writeClockTimeAndFlagButton.setOnClickListener(writeClockTimeAndFlagButtonOnClickListener);

        // Reminder clock
        writeReminderClockTimeAndFlagButton = findViewById(R.id.WriteReminderClickTimeAndFlagButton);
        writeReminderClockTimeAndFlagButton.setOnClickListener(writeReminderClockTimeAndFlagButtonOnClickListener);

        // init device
        setDeviceButton = findViewById(R.id.SetDeviceButton);
        setDeviceButton.setOnClickListener(setDeviceButtonOnClickListener);

        // Temperature Unit
        writeTemperatureUnitButton = findViewById(R.id.writeTemperatureUnit);
        writeTemperatureUnitButton.setOnClickListener(writeTemperatureUnitOnClickListener);




        bleStatusTextView = findViewById(R.id.bleStatusTextView);

        MyPermissions myPermissions = new MyPermissions();
        String[] permissions = myPermissions.getPermissions();

        if (permissions.length != 0)
            ActivityCompat.requestPermissions(this, permissions, PermissionRequestCode.ON_CREATE_REQUEST_PERMISSIONS);
    }

    @Override
    protected void onResume () {
        super.onResume();

        Log.d("test8360", "onResume");

        bleServiceIntent = new Intent(this, BLEService.class);
        bindService(bleServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE );
    }

    @Override
    protected void onPause () {
        super.onPause();

        Log.d("test8360", "onPause");
        unbindService(mServiceConnection);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();

        Log.d("test8360", "onDestroy");
    }

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected (ComponentName arg0) {
            Log.d("test8360", "onServiceDisconnected");
            mServiceConnection = null;
        }

        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            Log.d("test8360", "onServiceConnected");
            bleService = ((BLEService.MsgBinder)service).getServiceInstance();
            bleService.setOnProgressListener(onProgressListener);
        }
    };

    private final View.OnClickListener scanDeviceButtonOnClickListener = v -> {
        if (CheckBLEScan.ability(this.getApplicationContext()) && CheckLocationStatus.checkLocationForBLE(this.getApplicationContext())) {
            bleService.scanLeDevice();
        }
        else {
            CheckBLEScan.checkBLEToast(this.getApplicationContext());
        }
    };


    private final View.OnClickListener getUserIndexOnClickListener = v -> {
        bleService.writeCommand(BLE_CLIENT_CMD.ReadUserAndMemoryCmd);
    };

    private final View.OnClickListener getNumberOfDataButtonOnClickListener = v -> {
        bleService.writeCommand(BLE_CLIENT_CMD.ReadNumberOfDataCmd);
    };

    private final View.OnClickListener readLatestMemoryButtonOnClickListener = v -> {
        bleService.writeCommand(BLE_CLIENT_CMD.ReadLatestMemoryCmd);
    };

    private final View.OnClickListener readAllMemoryButtonOnClickListener = v -> {
        bleService.writeCommand(BLE_CLIENT_CMD.ReadAllMemoryCmd);
    };

    private final View.OnClickListener clearAllDataButtonOnClickListener = v -> {
        bleService.writeCommand(BLE_CLIENT_CMD.ClearAllDataCmd);
    };

    private static final DeviceTimeFormat testClockTime = new DeviceTimeFormat(2003, 11, 30, 23, 58, 55);
    private static final ReminderTimeFormat testReminderClockTime = new ReminderTimeFormat(21, 17);
    private static final TemperatureUnit testTemperatureUnit = TemperatureUnit.F;
    private static final boolean testEnable = true;
    // Clock
    private final View.OnClickListener writeClockTimeAndFlagButtonOnClickListener = v -> {
        bleService.writeClockTimeAndShowFlag(testClockTime, testEnable);
    };

    // Reminder
    private final View.OnClickListener writeReminderClockTimeAndFlagButtonOnClickListener = v -> {
        bleService.writeReminderClockTimeAndEnabled(testReminderClockTime, testEnable);
    };

    // Init
    private final View.OnClickListener setDeviceButtonOnClickListener = v -> {
        bleService.writeSetDevice();
    };

    // Unit
    private final View.OnClickListener writeTemperatureUnitOnClickListener = v -> {
        bleService.writeTemperatureUnit(testTemperatureUnit);
    };

    /**
     * KP Series
     */
    // Write Device Setting
    KPSettings deviceSetting = new KPSettings(reminder, true, testTemperatureUnit, hourFormat, clockShowFlag);

    // Write reminder time
    private static final ArrayList<ReminderFormat> reminder = new ArrayList<> () {{
            add(new ReminderFormat(true, new ReminderTimeFormat(9, 35)));
            add(new ReminderFormat(true, new ReminderTimeFormat(13,47)));
            add(new ReminderFormat(true, new ReminderTimeFormat(17,21)));
            add(new ReminderFormat(true, new ReminderTimeFormat(23,58)));
        }
    };

    // Write time
    private static final HourFormat hourFormat = HourFormat.is12;
    private static final boolean ambient = true;
    private static final boolean clockShowFlag = true;

    private final View.OnClickListener setKPDeviceOnClickListener = v -> {
        bleService.setDevice(deviceSetting);
    };


    private final View.OnClickListener readNumberOfMemoryOnClickListener = v -> {
        bleService.readKPNumberOfMemory();
    };

    private final int dataIndex = 19;
    private final View.OnClickListener readMemoryOnClickListener = v -> {
        bleService.readKPMemory(dataIndex);
    };

    private final View.OnClickListener kpStartSenseOnClickListener = v -> {
        bleService.kpStartSense();
    };

    private final View.OnClickListener kpStopSenseOnClickListener = v -> {
        bleService.kpStopSense();
    };

    private final View.OnClickListener kpClearMemoryOnClickListener = v -> {
        bleService.kpClearMemory();
    };

    private final SenseMode mode = SenseMode.KP;
    private final View.OnClickListener kpChangeModeOnClickListener = v -> {
        bleService.kpChangeMode(mode);
    };

    private final OnProgressListener onProgressListener = new OnProgressListener() {
        @Override
        public void onScanMotionFailed () {
            CheckBLEScan.checkBLEEnabledToast(activity);
        }

        @Override
        public void onStartScan () {
            activity.runOnUiThread(() -> scanDeviceButton.setText(R.string.stop_scan));
        }

        @Override
        public void onStopScan () {
            activity.runOnUiThread(() -> scanDeviceButton.setText(R.string.start_scan));
        }

        @Override
        public void onConnected (BluetoothGatt gatt) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    bleStatusTextView.setText(R.string.connecting);

                    String deviceName = gatt.getDevice().getName();
                    if (Pattern.matches(DeviceRegex.KPSeries, deviceName)) {
                        kpViewInit(activity);
                    }
                    else if (Pattern.matches(DeviceRegex.KI8180, deviceName)) {

                    }
                    else if (Pattern.matches(DeviceRegex.KI8186, deviceName)) {

                    }
                    else if (Pattern.matches(DeviceRegex.KI8360, deviceName)) {

                    }
                    else if (Pattern.matches(DeviceRegex.KG517x, deviceName)) {

                    }
                    else if (Pattern.matches(DeviceRegex.KD2070, deviceName)) {
                        kd2070ViewInit(activity);
                    }
                    else if (Pattern.matches(DeviceRegex.KD2161, deviceName)) {

                    }
                }
            });
        }

        @Override
        public void onDisConnected () {
            activity.runOnUiThread(() -> bleStatusTextView.setText(R.string.do_nothing));
        }
    };

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
            }
            else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
            }
            else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            }
        }
    };

    private void kpViewInit (Activity activity) {
        activity.setContentView(R.layout.kplayout);
        Button readNumberOfMemoryButton;
        Button readMemoryButton;
        Button setKPDeviceButton;
        Button kpStartSenseButton;
        Button kpStopSenseButton;
        Button kpClearMemoryButton;
        Button kpChangeModeButton;

        readNumberOfMemoryButton = findViewById(R.id.readNumberOfMemoryButton);
        readNumberOfMemoryButton.setOnClickListener(readNumberOfMemoryOnClickListener);

        readMemoryButton = findViewById(R.id.readMemoryButton);
        readMemoryButton.setOnClickListener(readMemoryOnClickListener);

        setKPDeviceButton = findViewById(R.id.setDeviceButton);
        setKPDeviceButton.setOnClickListener(setKPDeviceOnClickListener);

        kpStartSenseButton = findViewById(R.id.startSenseButton);
        kpStartSenseButton.setOnClickListener(kpStartSenseOnClickListener);

        kpStopSenseButton = findViewById(R.id.stopSenseButton);
        kpStopSenseButton.setOnClickListener(kpStopSenseOnClickListener);

        kpClearMemoryButton = findViewById(R.id.kpclearMemoryButton);
        kpClearMemoryButton.setOnClickListener(kpClearMemoryOnClickListener);

        kpChangeModeButton = findViewById(R.id.changeModeButton);
        kpChangeModeButton.setOnClickListener(kpChangeModeOnClickListener);
    }

    private void kd2070ViewInit (Activity activity) {
        activity.setContentView(R.layout.kd2070layout);
        Button readNumberOfMemoryButton;
        Button readIndexMemoryButton;
        Button setDeviceButton;
        Button writeHandButton;
        Button writeUnitButton;
        Button clearDataButton;
        Button writeClockButton;
        Button readSettingsButton;

        EditText editTextNumberEditText;
        editTextNumberEditText = findViewById(R.id.editTextNumber);
        EditText editTextHand;
        editTextHand = findViewById(R.id.editTextHand);
        EditText editTextUnit;
        editTextUnit = findViewById(R.id.editTextUnit);

        readNumberOfMemoryButton = findViewById(R.id.KD2070ReadNumberOfMemoryButton);
        readNumberOfMemoryButton.setOnClickListener(v -> bleService.kd2070ReadNumberOfMemory());

        readIndexMemoryButton = findViewById(R.id.KD2070ReadMemoryButton);
        readIndexMemoryButton.setOnClickListener(v -> bleService.kd2070ReadDataAtIndex(Integer.parseInt(editTextNumberEditText.getText().toString())));

        setDeviceButton = findViewById(R.id.KD2070SetDeviceButton);
        setDeviceButton.setOnClickListener(v -> bleService.kd2070ReadSettings());

        writeHandButton = findViewById(R.id.KD2070WriteHand);
        writeHandButton.setOnClickListener(v -> bleService.kd2070WriteHand(editTextHand.getText().toString().equals("L") ? LeftRightHand.Left : LeftRightHand.Right));

        writeUnitButton = findViewById(R.id.KD2070WriteUnit);
        writeUnitButton.setOnClickListener(v -> bleService.kd2070WriteUnit(editTextUnit.getText().toString().equals("F") ? TemperatureUnit.F : TemperatureUnit.C));

        clearDataButton = findViewById(R.id.KD2070ClearMemoryButton);
        clearDataButton.setOnClickListener(v -> bleService.kd2070ClearData());

        DeviceTimeFormat clockTime = new DeviceTimeFormat(2015,5,13,17,25,31);
        writeClockButton = findViewById(R.id.KD2070WriteTime);
        writeClockButton.setOnClickListener(v -> bleService.kd2070WriteClock(clockTime));

        readSettingsButton = findViewById(R.id.KD2070ReadSettings);
        readSettingsButton.setOnClickListener(v -> bleService.kd2070ReadSettings());
    }
    private void kpOnClickListenerInit () {

    }
}