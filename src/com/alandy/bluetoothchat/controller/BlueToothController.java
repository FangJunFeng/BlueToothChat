package com.alandy.bluetoothchat.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

public class BlueToothController {
	private BluetoothAdapter mAdapter;
	
	public BlueToothController() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	public BluetoothAdapter getAdapter(){
		return mAdapter;
	}

	/**
	 * turn on BlueTooth
	 * @param activity
	 * @param requestCode
	 */
	public void turnOnBlueTooth(Activity activity, int requestCode) {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * Open BlueTooth Enable Visibly
	 * @param context
	 */
	public void enableVisibly(Context context) {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		context.startActivity(discoverableIntent);
	}

	/**
	 * Find BlueToothDevice
	 */
	public void findDevice() {
		assert(mAdapter != null);
		mAdapter.startDiscovery();
	}
	
	
	/**
	 * Get Bonded BlueTooth Devices
	 * @return
	 */
	public ArrayList<BluetoothDevice> getBondedDeviceList(){
		return new ArrayList<>(mAdapter.getBondedDevices());
	}

}
