package com.linscott.smartmitten.adapter;

/**
 * Created by Nic on 8/28/2014.
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linscott.smartmitten.R;

import java.util.ArrayList;

public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private Context context;

    public LeDeviceListAdapter(Context appContext) {
        super();
        context = appContext;
        mLeDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public void removeDevice(int position){
        if(position >= 0 && position <= mLeDevices.size()-1 ) {
            mLeDevices.remove(position);
        }
    }

    public boolean exists(BluetoothDevice device){
        for(int i = 0; i < mLeDevices.size(); ++i){
            if(mLeDevices.get(i).getAddress() == device.getAddress()
                    && mLeDevices.get(i).getName() == device.getName()){
                return true;
            }
        }
        return false;
    }

    public void removeDevice(BluetoothDevice device){
        for(int i = 0; i < mLeDevices.size(); ++i){
            if(mLeDevices.get(i).getAddress().equals( device.getAddress())
                    && mLeDevices.get(i).getName().equals(device.getName())){
                mLeDevices.remove(i);
                break;
            }
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {

            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.device_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView)view.findViewById(R.id.device_address);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();

        viewHolder.deviceAddress.setText(deviceAddress);

        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        }else {
            viewHolder.deviceName.setText("Device");
        }


        return view;
    }

    private class ViewHolder {
        public TextView deviceName, deviceAddress;
    }
}

