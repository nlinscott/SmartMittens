package com.linscott.smartmitten.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.linscott.smartmitten.fragments.base.BLEScannerFragment;
import com.linscott.smartmitten.tools.Debug;

import java.nio.charset.Charset;
import java.util.UUID;

public class BLEService extends Service {

    public BLEService() {
    }

    private static final int NOTIFICATION_ID = 12;

    private BluetoothAdapter adapter;

    private BluetoothGatt bluetoothGatt;

    private final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final UUID UART_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    public static final String ADDRESS_EXTRA_ID = "address_extra_id";
    public static final String STATE_ID = "state_id";

    public static final String CONNECT_DEVICE = "com.linscott.smartmittens.CONNECT_DEVICE";

    public static final String GESTURE_ACTION = "gesture_action";

    public static final String EXTRA_GESTURE = "gesture";

    public static final String EXTRA_GESTURE_UP = "up";

    public static final String EXTRA_GESTURE_DOWN = "down";

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if(newState == BluetoothProfile.STATE_CONNECTED){
                gatt.discoverServices();
            }

            Intent intent = new Intent();
            intent.putExtra(STATE_ID, newState);
            intent.setAction(BLEScannerFragment.DEVICE_STATUS);
            intent.putExtra(BluetoothDevice.EXTRA_DEVICE, gatt.getDevice());
            sendBroadcast(intent);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            enableRXNotification();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            byte[] data = characteristic.getValue();

            String s = new String(data, Charset.forName("UTF-8"));
            //get a number

            Debug.Log(s);

        }
    };

    private void enableRXNotification()
    {

        BluetoothGattService RxService = bluetoothGatt.getService(UART_SERVICE_UUID);
        if (RxService == null) {
            Debug.Log("RX Service Not found");
            //TODO: notify the device doesnt support uart
            return;
        }

        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
        if (TxChar == null) {
            Debug.Log("RX char Not found");
            //TODO: notify the device doesnt support uart
            return;
        }
        bluetoothGatt.setCharacteristicNotification(TxChar, true);

        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if(RxChar == null){
            Debug.Log("RX Char not found");
            return;
        }
        bluetoothGatt.setCharacteristicNotification(RxChar, true);

        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        if(descriptor == null){
            Debug.Log("TX characteristic's descriptor is null");
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        Debug.Log("mBluetoothGatt closed");
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {

            if(intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)){
                BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
                connect(device);
            }

            return START_STICKY;
        }
        else{
            return 0;
        }
    }



    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final BluetoothDevice device) {

        BluetoothManager mgr = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);

        adapter = mgr.getAdapter();

        if (adapter == null) {
            Debug.Log("BluetoothAdapter not initialized");
            return false;
        }
        if(device == null){
            Debug.Log("device is null");
            return false;
        }


        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        Debug.Log("Trying to create a new connection.");
        buildNotification(device);
        return true;
    }

    private void buildNotification(BluetoothDevice device){
        ConnectedNotification not = new ConnectedNotification(this, NOTIFICATION_ID);
        not.createNotification(device.getName(), device.getAddress());
    }
}


