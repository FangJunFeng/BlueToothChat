package com.alandy.bluetoothchat;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

	private List<BluetoothDevice> mData;
	private Context mContext;

	public DeviceAdapter(List<BluetoothDevice> data, Context context) {
		mData = data;
		mContext = context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Reume view, optimize performance
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					android.R.layout.simple_list_item_2, parent, false);
		}
		TextView line1 = (TextView) convertView.findViewById(android.R.id.text1);
		TextView line2 = (TextView) convertView.findViewById(android.R.id.text2);
		
		//To obtain the corresponding bluetooth devices
		BluetoothDevice device = (BluetoothDevice) getItem(position);
		
		//Show name
		line1.setText(device.getName());
		line1.setTextColor(Color.BLACK);
		//Show address
		line2.setText(device.getAddress());
		line2.setTextColor(Color.BLACK);
		return convertView;
	}

	public void refresh(List<BluetoothDevice> data) {
		mData = data;
		notifyDataSetChanged();
	}

}
