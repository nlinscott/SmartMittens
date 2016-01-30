package com.linscott.smartmitten;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.linscott.smartmitten.fragments.MarshmallowBleScannerFragment;
import com.linscott.smartmitten.fragments.base.BLEScannerFragment;
import com.linscott.smartmitten.interfaces.UIStateCallback;

public class ScanActivity extends AppCompatActivity {


    public static final int REQUEST_ENABLE_BT = 200;


    private BLEScannerFragment fragment;

    private boolean isScanning = false;


    private UIStateCallback scanCallback = new UIStateCallback() {
        @Override
        public void scanStarted() {
            isScanning = true;
            invalidateOptionsMenu();
        }

        @Override
        public void scanStopped() {
            isScanning = false;
            invalidateOptionsMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragment = new MarshmallowBleScannerFragment();


        fragment.setCallback(scanCallback);

        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
           // Debug.ShowText(getApplicationContext(), getString(R.string.bt_enabled));
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        if (!isScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.scanning_progress);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_scan) {
            fragment.startScan();
            return true;
        }
        if(id == R.id.menu_stop) {
            fragment.stopScan();
            return true;
        }

        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        invalidateOptionsMenu();
        return true;
    }




}

