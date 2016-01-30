package com.linscott.smartmitten.fragments.base;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.linscott.smartmitten.R;
import com.linscott.smartmitten.ScanActivity;
import com.linscott.smartmitten.adapter.LeDeviceListAdapter;
import com.linscott.smartmitten.interfaces.UIStateCallback;
import com.linscott.smartmitten.services.BLEService;
import com.linscott.smartmitten.tools.Debug;

import java.util.List;


public abstract class BLEScannerFragment extends Fragment {

    protected BluetoothManager bluetoothManager;
    protected BluetoothAdapter bluetoothAdapter;

    public static final String DEVICE_STATUS = "com.siren.app.action.DEVICE_STATUS";

    protected LeDeviceListAdapter scannedListAdapter;
    protected LeDeviceListAdapter connectedListAdapter;

    /**
     * abstract start for children
     */
    public abstract void startScan();

    /**
     * abstract stop for children
     */
    public abstract void stopScan();

    /**
     * callback used to update acivity's UI
     */
    protected UIStateCallback callback;

    /**
     * sets the callback for UI updates while scanning
     * @param cb
     */
    public void setCallback(final UIStateCallback cb){
        callback = cb;
    }

    protected final long SCAN_PERIOD = 5000;

    /**
     * Uses the context and adapter to see if bluetooth is still connected, enabled etc. and
     * checks if the notification listener is enabled
     * Do this in onResume()
     * @param context
     */
    protected void checkBluetoothState(final Context context){

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableBtIntent, ScanActivity.REQUEST_ENABLE_BT);
        }
    }

    /**
     * checks the created state if any devices are connected, if ble is supported.
     * Launches ControlActivity if there is a device connected
     * @param context
     */
    protected void checkCreateState(final Context context){

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Debug.ShowText(context, "BLE not supported");
            ((Activity)context).finish();
        }

        bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (bluetoothAdapter == null) {
            Debug.ShowText(context, "BT not supported");
            ((Activity)context).finish();
        }
    }

    protected void checkPauseState(){

        if(bluetoothManager != null) {
            List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            if (devices.isEmpty()) {
                Debug.Log("stopping service in onPause");
                Intent intent = new Intent(getActivity(), BLEService.class);
                getActivity().stopService(intent);
            }
        }
    }



    protected BroadcastReceiver deviceConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(DEVICE_STATUS)){

                int state = intent.getIntExtra(BLEService.STATE_ID, 0);

                if(state == BluetoothProfile.STATE_CONNECTED){

                    BluetoothDevice device =  intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                    connectedListAdapter.addDevice(device);
                    scannedListAdapter.removeDevice(device);

                    scannedListAdapter.notifyDataSetChanged();
                    connectedListAdapter.notifyDataSetChanged();
                }else if(state == BluetoothProfile.STATE_DISCONNECTED){
                    //could not connect

                    BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                    connectedListAdapter.removeDevice(device);

                }

            }
        }
    };

    private void connectBleDevice(final BluetoothDevice device){

        Intent intent = new Intent(getActivity(), BLEService.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);

        getActivity().startService(intent);

    }

    protected static IntentFilter deviceConnectedIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DEVICE_STATUS);
        return intentFilter;
    }

    protected void setupLists(final View parentView){

        scannedListAdapter = new LeDeviceListAdapter(getActivity());
        connectedListAdapter = new LeDeviceListAdapter(getActivity());

        checkConnectedDevices();

        ListView deviceList = (ListView)parentView.findViewById(R.id.scannedList);

        ListView connectedList = (ListView)parentView.findViewById(R.id.connectedList);

        connectedList.setAdapter(connectedListAdapter);
        deviceList.setAdapter(scannedListAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                stopScan();

                BluetoothDevice device = scannedListAdapter.getDevice(i);

                scannedListAdapter.removeDevice(i);
                scannedListAdapter.notifyDataSetChanged();
                connectBleDevice(device);
            }
        });
    }

    private void checkConnectedDevices(){


        List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);

        if(!devices.isEmpty()){
            if(connectedListAdapter != null){
                //Debug.Log("found " + devices.size() + " connected devices");
                for(BluetoothDevice device : devices){
                    connectedListAdapter.addDevice(device);
                    scannedListAdapter.removeDevice(device);
                    Debug.Log("removed device from scanned" + device.getAddress());
                }
                connectedListAdapter.notifyDataSetChanged();
                scannedListAdapter.notifyDataSetChanged();
            }
        }else{

            Intent intent = new Intent(getActivity(), BLEService.class);
            getActivity().stopService(intent);

        }
    }
}
