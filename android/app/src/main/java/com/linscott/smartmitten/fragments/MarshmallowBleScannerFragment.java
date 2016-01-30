package com.linscott.smartmitten.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.linscott.smartmitten.R;
import com.linscott.smartmitten.fragments.base.BLEScannerFragment;
import com.linscott.smartmitten.tools.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nic on 12/8/2015.
 */
@TargetApi(23)
public class MarshmallowBleScannerFragment extends BLEScannerFragment {


    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 12;

    private final List<ScanFilter> filters = new ArrayList<>();

    private final ScanSettings settings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build();

    private View rootView;


    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
           /// Debug.Log("devcie found with callback type of: " + callbackType + " " + result.getDevice().getAddress());

            scannedListAdapter.addDevice(result.getDevice());
            scannedListAdapter.notifyDataSetChanged();

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            //Debug.Log("batch scan results are in, found " + results.size());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            //Debug.Log("scan failed: " + errorCode);

        }
    };

    private BluetoothLeScanner scanner;


    @Override
    public void startScan(){
        if(scanner != null) {

            Debug.Log("checking permission");
            checkPermission();

        }
    }

    @Override
    public void stopScan(){

        if(scanner != null) {
            scanner.stopScan(bleScanCallback);
            callback.scanStopped();
        }
    }


    public MarshmallowBleScannerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_device_management, container, false);

        checkCreateState(getActivity());

        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();

        getActivity().unregisterReceiver(deviceConnectedReceiver);

        stopScan();

        checkPauseState();
    }

    @Override
    public void onResume() {
        super.onResume();

        checkBluetoothState(getActivity());

        //get the scanner in onResume instead of OnCreateView
        //scanner will never be set until bluetoothAdapter is null
        //onActivityResult in the host activity will call onResume again
        scanner = bluetoothAdapter.getBluetoothLeScanner();

        getActivity().registerReceiver(deviceConnectedReceiver, deviceConnectedIntentFilter());

        setupLists(rootView);
    }

    private void scan(){
        Debug.Log("scanning");
        Handler handler = new Handler();
        // Stops scanning after a pre-defined scan period.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_PERIOD);

        scanner.startScan(filters, settings, bleScanCallback);
        callback.scanStarted();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    scan();

                } else {
                    Debug.Log("permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }
        }
    }


    private void checkPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{

            Debug.Log("permission already accepted");
            scan();
        }
    }


}
